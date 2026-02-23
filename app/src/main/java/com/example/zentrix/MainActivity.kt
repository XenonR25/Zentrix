package com.example.zentrix

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.zentrix.domain.model.Product
import com.example.zentrix.domain.model.Screen
import com.example.zentrix.features.MainScaffold
import com.example.zentrix.features.auth.AuthState
import com.example.zentrix.features.auth.AuthViewModel
import com.example.zentrix.features.landing.home.HomeScreen
import com.example.zentrix.features.auth.login.SignInScreen
import com.example.zentrix.features.auth.signup.SignupScreen
import com.example.zentrix.features.cart.CartScreen
import com.example.zentrix.features.favorites.FavoritesScreen
import com.example.zentrix.features.product.ProductDetailScreen
import com.example.zentrix.ui.theme.ZentrixTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            ZentrixTheme {

                val authViewModel :AuthViewModel = hiltViewModel()
                val authState by authViewModel.authState.collectAsStateWithLifecycle()
                val windowSizeClass = calculateWindowSizeClass(this)


                val initialScreen = when(authState){
                    is AuthState.Authenticated -> Screen.Home
                    is AuthState.Unauthenticated -> Screen.Login
                }
                val navStack = remember(initialScreen) { mutableStateListOf(initialScreen) }


                LaunchedEffect(authState) {
                    when(authState){
                        is AuthState.Authenticated -> {
                            if(navStack.last() != Screen.Home) {
                                navStack.clear()
                                navStack.add(Screen.Home)
                            }
                        }
                        is AuthState.Unauthenticated -> {
                            navStack.clear()
                            navStack.add(Screen.Login)
                        }
                    }
                }

                NavDisplay(
                    backStack = navStack,
                    onBack = {
                        if(navStack.size > 1) navStack.removeLast()
                        else finish() // Close app if on the last screen
                    },
                    entryProvider = {screen ->
                        when(screen){
                            is Screen.Login -> NavEntry(screen) {
                                SignInScreen(
                                    onNavigateToSignup = {navStack.add(Screen.Signup)},
                                    onAuthSuccess = {
                                        authViewModel.checkAuthState()
                                    }
                                )
                            }
                            is Screen.Signup -> NavEntry(screen){
                                SignupScreen(
                                    onNavigateToLogin = {navStack.removeLast()},
                                    onNavigateBack = {navStack.removeLast()},
                                    onAuthSuccess = {
                                        authViewModel.checkAuthState()
                                    }
                                )
                            }

                            is Screen.Home -> NavEntry(screen){
                                MainScaffold(windowSize = windowSizeClass.widthSizeClass
                                ,currentScreen = screen
                                    ,onNavigate = {destination->
                                        if(destination != screen){
                                            navStack.clear()
                                            navStack.add(destination)
                                        }
                                    }
                                ) {_,hazeState ->
                                    HomeScreen(hazeState , onProductClick = {
                                        product ->
                                        navStack.add(
                                            Screen.ProductDetails(
                                                productId = product.id,
                                                name = product.name,
                                                brand = product.brand,
                                                price = product.price,
                                                originalPrice = product.originalPrice,
                                                rating = product.rating,
                                                isFavorite = product.isFavorite,
                                                imageUrl = product.imageUrl,
                                                isNew = product.isNew,
                                                discount = product.discount
                                            )
                                        )
                                    }, onSignOut = {authViewModel.signOut()})
                                }
                            }
                            is Screen.ProductDetails -> NavEntry(screen) {
                                val product = Product(
                                    name = screen.name,
                                    brand = screen.brand,
                                    price = screen.price,
                                    originalPrice = screen.originalPrice,
                                    rating = screen.rating,
                                    imageUrl = screen.imageUrl,
                                    isFavorite = screen.isFavorite,
                                    isNew = screen.isNew,
                                    discount = screen.discount
                                )
                                ProductDetailScreen(product,onNavigateBack = {navStack.removeLast()})
                            }
                            is Screen.Cart -> NavEntry(screen) {
                                MainScaffold(windowSize = windowSizeClass.widthSizeClass
                                    ,currentScreen = screen
                                    ,onNavigate = {destination->
                                    if(destination != screen){
                                        navStack.clear()
                                        navStack.add(destination)
                                    }
                                    }) { _, hazeState ->
                                    CartScreen(hazeState = hazeState)

                            }
                            }
                            is Screen.Favorites -> NavEntry(screen) {
                             MainScaffold(windowSize = windowSizeClass.widthSizeClass,
                                 currentScreen = screen,
                                 onNavigate = {destination ->
                                     if(destination != screen){
                                         navStack.clear()
                                         navStack.add(destination)
                                     }
                                 }
                             ) { _,hazeState ->
                                 FavoritesScreen(hazeState = hazeState,
                                     onProductClick= {product->
                                         navStack.add(
                                             Screen.ProductDetails(
                                                 productId = product.id,
                                                 name = product.name,
                                                 brand = product.brand,
                                                 price = product.price,
                                                 originalPrice = product.originalPrice,
                                                 rating = product.rating,
                                                 isFavorite = product.isFavorite,
                                                 imageUrl = product.imageUrl,
                                                 isNew = product.isNew,
                                                 discount = product.discount
                                             )
                                         )
                                     })
                             }
                            }
                            else -> NavEntry(screen){
                                Text("Unknown Destination")
                            }
                        }
                    }
                )
            }
        }
    }
}