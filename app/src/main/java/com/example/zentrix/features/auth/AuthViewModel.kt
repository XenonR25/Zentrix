package com.example.zentrix.features.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

sealed class AuthState{
//    data object Loading : AuthState()
    data object Unauthenticated : AuthState()
    data class Authenticated(val uid : String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {
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