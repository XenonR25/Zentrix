package com.example.zentrix.domain.repository

import com.google.firebase.auth.AuthResult

interface AuthRepository{
    suspend fun signIn(email: String , pass : String) : Result<AuthResult>
    suspend fun signUp(name:String, email: String, pass : String) : Result<AuthResult>
}