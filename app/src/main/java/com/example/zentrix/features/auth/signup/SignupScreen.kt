package com.example.zentrix.features.auth.signup

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.zentrix.core.designsystem.GlassCard
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.hazeSource

// ui/auth/SignupScreen.kt
@Composable
fun SignupScreen(
    viewModel: SignupViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onAuthSuccess: () -> Unit
) {
    val hazeState = remember { HazeState() }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A))) {

        // Background: Mark this as the SOURCE for the blur
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(state = hazeState) // Use hazeSource here
        ) {
            drawCircle(Color.Magenta.copy(0.4f), radius = 400f, center = center)
            drawCircle(Color.Cyan.copy(0.3f), radius = 300f, center = Offset(size.width, 0f))
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Generic Glass Card usage
            GlassCard(hazeState = hazeState) {
                Text(
                    text = "Create Account",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = viewModel.email,
                    onValueChange = { viewModel.email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = viewModel.password,
                    onValueChange = { viewModel.password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )

                OutlinedTextField(
                    value = viewModel.confirmPassword,
                    onValueChange = { viewModel.confirmPassword = it },
                    label = { Text("Confirm Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )

                if (viewModel.errorMessage != null) {
                    Text(
                        text = viewModel.errorMessage!!,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Button(
                    onClick = { viewModel.onSignupClicked(onAuthSuccess) },
                    enabled = !viewModel.isLoading,
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
                ) {
                    if (viewModel.isLoading) CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                    else Text("Sign Up")
                }
            }
        }
    }
}