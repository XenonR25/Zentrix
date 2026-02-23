package com.example.zentrix.data.repository

import com.example.zentrix.domain.model.CartItem
import com.example.zentrix.domain.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FirestoreRepository @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getUserId(): String? = auth.currentUser?.uid

    //Cart Operations

    suspend fun saveCartItems(cartItems: List<CartItem>): Result<Unit> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not authenticated"))

            val cartData = cartItems.map { item ->
                hashMapOf(
                    "productId" to item.product.id,
                    "name" to item.product.name,
                    "brand" to item.product.brand,
                    "price" to item.product.price,
                    "originalPrice" to item.product.originalPrice,
                    "rating" to item.product.rating,
                    "imageUrl" to item.product.imageUrl,
                    "isNew" to item.product.isNew,
                    "discount" to item.product.discount,
                    "quantity" to item.quantity
                )

            }
            firestore.collection("users").document(userId).collection("cart").document("items").set(hashMapOf("items" to cartData)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loadCartItems(): Result<List<CartItem>> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not authenticated"))

            val document = firestore.collection("users").document(userId).collection("cart").document("items").get().await()

            val items = document.get("items") as? List<Map<String, Any>> ?: emptyList()

            val cartItems = items.mapNotNull{item ->
                try {
                    val product = Product(
                        name = item["name"] as? String ?: "",
                    brand = item["brand"] as? String ?: "",
                    price = item["price"] as? String ?: "",
                    originalPrice = item["originalPrice"] as? String,
                    rating = (item["rating"] as? Number)?.toFloat() ?: 0f,
                    imageUrl = item["imageUrl"] as? String ?: "",
                    isFavorite = false,
                    isNew = item["isNew"] as? Boolean ?: false,
                    discount = item["discount"] as? String
                    )
                    val quantity = (item["quantity"] as? Number)?.toInt() ?: 1

                CartItem(product, quantity )
                } catch (e: Exception){null}
            }

            Result.success(cartItems)
        } catch (e: Exception) {
            Result.failure(e)
        }

    }
    suspend fun clearCart(): Result<Unit>{
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not authenticated"))

            firestore.collection("users")
                .document(userId)
                .collection("cart")
                .document("items")
                .delete().await()

            Result.success(Unit)
        } catch(e: Exception){
            Result.failure(e)
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // Favorites Operations
    // ─────────────────────────────────────────────────────────────────────

    suspend fun saveFavorites(favorites: List<Product>): Result<Unit> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            val favoritesData = favorites.map { product ->
                hashMapOf(
                    "productId" to product.id,
                    "name" to product.name,
                    "brand" to product.brand,
                    "price" to product.price,
                    "originalPrice" to product.originalPrice,
                    "rating" to product.rating,
                    "imageUrl" to product.imageUrl,
                    "isNew" to product.isNew,
                    "discount" to product.discount
                )
            }

            firestore.collection("users")
                .document(userId)
                .collection("favorites")
                .document("items")
                .set(hashMapOf("items" to favoritesData))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loadFavorites(): Result<List<Product>> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            val document = firestore.collection("users")
                .document(userId)
                .collection("favorites")
                .document("items")
                .get()
                .await()

            val items = document.get("items") as? List<Map<String, Any>> ?: emptyList()

            val favorites = items.mapNotNull { item ->
                try {
                    Product(
                        name = item["name"] as? String ?: "",
                        brand = item["brand"] as? String ?: "",
                        price = item["price"] as? String ?: "",
                        originalPrice = item["originalPrice"] as? String,
                        rating = (item["rating"] as? Number)?.toFloat() ?: 0f,
                        imageUrl = item["imageUrl"] as? String ?: "",
                        isFavorite = true,
                        isNew = item["isNew"] as? Boolean ?: false,
                        discount = item["discount"] as? String
                    )
                } catch (e: Exception) {
                    null
                }
            }

            Result.success(favorites)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addFavorite(product: Product): Result<Unit> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            val productData = hashMapOf(
                "productId" to product.id,
                "name" to product.name,
                "brand" to product.brand,
                "price" to product.price,
                "originalPrice" to product.originalPrice,
                "rating" to product.rating,
                "imageUrl" to product.imageUrl,
                "isNew" to product.isNew,
                "discount" to product.discount,
                "addedAt" to System.currentTimeMillis()
            )

            firestore.collection("users")
                .document(userId)
                .collection("favorites")
                .document(product.id)
                .set(productData)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFavorite(productId: String): Result<Unit> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))

            firestore.collection("users")
                .document(userId)
                .collection("favorites")
                .document(productId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}