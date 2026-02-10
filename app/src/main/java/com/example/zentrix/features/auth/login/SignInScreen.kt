package com.example.zentrix.features.auth.login

import android.graphics.Canvas
import android.widget.Space
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.zentrix.core.designsystem.GlassCard
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@Composable
fun SignInScreen(
    viewModel: SignInViewModel = hiltViewModel(),
    onNavigateToSignup: () -> Unit,
    onAuthSuccess: () -> Unit
) {
    val hazeState = remember { HazeState() }
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A))){
        //Background with moving style circles
        Canvas(modifier = Modifier.fillMaxSize().hazeSource(state = hazeState)) {
            drawCircle(Color(0xFF3B82F6).copy(0.4f), radius = 500f,center = Offset(200f,200f))
            drawCircle(Color(0xFF8B5CF6).copy(0.3f), radius = 400f,center = Offset(size.width - 200f, size.height - 200f))
        }

        Column (
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){// Using the generic card
            GlassCard(hazeState = hazeState) {
                Text(
                    text = "Welcome Back",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Sign in to continue shopping",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = viewModel.email,
                    onValueChange = { viewModel.email = it},
                    label = {Text("Email",color = Color.White.copy(alpha = 0.6f))},
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.3f)
                    )
                )
                OutlinedTextField(
                    value = viewModel.password,
                    onValueChange = { viewModel.password = it },
                    label = { Text("Password", color = Color.White.copy(alpha = 0.6f)) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                    )
                )

                if(viewModel.errorMessage != null) {
                      Text(
                          text = viewModel.errorMessage!!,
                          color = Color.Red.copy(alpha = 0.9f),
                          style = MaterialTheme.typography.bodySmall,
                          modifier = Modifier.padding(top = 8.dp)
                      )
                }
                Button(
                    onClick = {viewModel.onSignInClicked(onAuthSuccess)},
                    enabled = !viewModel.isLoading,
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                    shape = RoundedCornerShape(12.dp)
                    ){
                    if(viewModel.isLoading){
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else
                    {
                        Text("Sign In")
                    }
                    TextButton(onClick = onNavigateToSignup,
                        modifier = Modifier.padding(top = 8.dp)) {
                        Text("Don't have an account? Sign up", color = Color.White.copy(alpha = 0.8f))
                    }
                }

            }

        }
    }
}