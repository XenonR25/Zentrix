package com.example.zentrix.features.auth.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zentrix.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignInViewModel  @Inject constructor(
    private val repository: AuthRepository
) : ViewModel(){
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun onSignInClicked(onSuccess : ()-> Unit){
        if(email.isBlank()){
            errorMessage = "Please fill in all fields"
            return
        }
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            //we reuse the signIn method from the repository
            repository.signIn(email,password)
                .onSuccess {
                    onSuccess()
                }
                .onFailure { e ->
                    errorMessage = e.localizedMessage ?: "Login failed"
                }

            isLoading = false
        }
    }
}