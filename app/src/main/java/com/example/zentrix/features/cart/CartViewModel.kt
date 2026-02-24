package com.example.zentrix.features.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zentrix.data.repository.FirestoreRepository
import com.example.zentrix.domain.model.CartItem
import com.example.zentrix.domain.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject   constructor(
    private  val repository: FirestoreRepository
) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _hasNewState = MutableStateFlow(false)
    val hasNewState : StateFlow<Boolean> = _hasNewState.asStateFlow()


    init{
        loadCartFromFirebase()
    }

    //Firebase cart item access
    private fun loadCartFromFirebase(){
        viewModelScope.launch {
            _isLoading.value = true
            repository.loadCartItems()
                .onSuccess{items->
                    _cartItems.value = items
                    calculateTotalPrice()
                }
                .onFailure {e->
                    // Handle error
                println("Error loading cart items: ${e.message}")

                }
            _isLoading.value = false
        }
    }


    fun addToCart(product: Product){
        val currentItems = _cartItems.value.toMutableList()
        val existingItem = currentItems.find {it.product.id == product.id}

        if(existingItem != null){
            //Increased Quantity
            val index = currentItems.indexOf(existingItem)
            currentItems[index] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            // Add new item
            currentItems.add(CartItem(product = product,1))
        }
        _hasNewState.value = true
        _cartItems.value = currentItems
        calculateTotalPrice()
        syncToFireStore()

    }



    fun removeFromCart(productId : String){
        _cartItems.value = _cartItems.value.filter { it.product.id != productId }
        calculateTotalPrice()
        syncToFireStore()
    }

    fun updateQuantity(productId: String, newQuantity : Int){
        if(newQuantity<=0){
            removeFromCart(productId)
            return
        }

        val currentItems = _cartItems.value.toMutableList()
        val index = currentItems.indexOfFirst { it.product.id == productId }

        if(index != -1){
            currentItems[index] = currentItems[index].copy(quantity = newQuantity)
            _cartItems.value = currentItems
            calculateTotalPrice()
            syncToFireStore()
        }

    }
    fun clearCart(){
        _cartItems.value = emptyList()
        _totalPrice.value = 0.0
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    fun isInCart (productId:String) : Boolean {
        return _cartItems.value.any { it.product.id == productId }
    }

    fun markCartAsViewed(){
        _hasNewState.value = false
    }

    private fun calculateTotalPrice() {
        val total = _cartItems.value.sumOf {item ->
        val priceString = item.product.price.replace("£","").replace(",","")
        val price = priceString.toDoubleOrNull() ?: 0.0
        price * item.quantity
        }
        _totalPrice.value = total
    }

    private fun syncToFireStore() {
        viewModelScope.launch {
            repository.saveCartItems(_cartItems.value)
                .onFailure{e->
                    println("Error syncing cart: ${e.message}")
                }
        }
    }

}