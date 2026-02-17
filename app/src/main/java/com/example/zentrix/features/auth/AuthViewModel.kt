package com.example.zentrix.features.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class AuthState{
//    data object Loading : AuthState()
    data object Unauthenticated : AuthState()
    data class Authenticated(val uid : String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

//    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState


    init{
        checkAuthState()
    }

     fun checkAuthState(){
        val user = auth.currentUser
        _authState.value = if(user!= null){
            AuthState.Authenticated(user.uid)} else{
                AuthState.Unauthenticated
            }


    }
    fun signOut(){
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

}