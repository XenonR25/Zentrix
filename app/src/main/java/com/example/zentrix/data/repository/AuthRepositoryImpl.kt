package com.example.zentrix.data.repository

import com.example.zentrix.domain.repository.AuthRepository
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository{
    override suspend fun signUp(email: String, pass: String): Result<AuthResult> = try{
        val result = firebaseAuth.createUserWithEmailAndPassword(email,pass).await()
        Result.success(result)
    } catch (e: Exception){
        Result.failure(e)
    }

    override suspend fun signIn(email: String, pass: String): Result<AuthResult> = try{
        val result = firebaseAuth.signInWithEmailAndPassword(email,pass).await()

        Result.success(result)
    } catch (e: Exception){
        Result.failure(e)
    }
}