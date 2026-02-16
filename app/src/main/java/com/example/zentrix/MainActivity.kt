package com.example.zentrix

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.zentrix.domain.model.Screen
import com.example.zentrix.features.auth.home.HomeScreen
import com.example.zentrix.features.auth.home.MainScaffold
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
                val windowSizeClass = calculateWindowSizeClass(this)
                val navStack = remember { mutableStateListOf<Screen>(Screen.Login) }

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
                                        navStack.clear()
                                        navStack.add(Screen.Home)
                                    }
                                )
                            }
                            is Screen.Signup -> NavEntry(screen){
                                SignupScreen(
                                    onNavigateBack = {navStack.removeLast()},
                                    onAuthSuccess = {
                                        navStack.clear()
                                        navStack.add(Screen.Home)
                                    }
                                )
                            }

                            is Screen.Home -> NavEntry(screen){
                                MainScaffold(windowSize = windowSizeClass.widthSizeClass) {_,hazeState ->
                                    HomeScreen(hazeState)
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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ZentrixTheme {
        Greeting("Android")
    }
}