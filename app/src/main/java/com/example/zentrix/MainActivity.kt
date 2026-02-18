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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.zentrix.domain.model.Screen
import com.example.zentrix.features.MainScaffold
import com.example.zentrix.features.auth.AuthState
import com.example.zentrix.features.auth.AuthViewModel
import com.example.zentrix.features.landing.home.HomeScreen
import com.example.zentrix.features.auth.login.SignInScreen
import com.example.zentrix.features.auth.signup.SignupScreen
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
                                MainScaffold(windowSize = windowSizeClass.widthSizeClass) {_,hazeState ->
                                    HomeScreen(hazeState , onSignOut = {authViewModel.signOut()})
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