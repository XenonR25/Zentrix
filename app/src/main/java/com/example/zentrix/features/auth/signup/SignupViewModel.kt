package com.example.zentrix.features.auth.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zentrix.domain.usecase.ValidateSignupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val validateSignupUseCase : ValidateSignupUseCase
) : ViewModel( ){
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun onSignupClicked(onSuccess: ()-> Unit){
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            validateSignupUseCase(email,password,confirmPassword)
                .onSuccess { onSuccess() }
                .onFailure { errorMessage = it.message }
        }
    }
}