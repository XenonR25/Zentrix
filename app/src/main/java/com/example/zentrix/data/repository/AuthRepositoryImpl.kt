package com.example.zentrix.data.repository

import com.example.zentrix.data.remote.UserModel
import com.example.zentrix.domain.repository.AuthRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository{
    override suspend fun signUp(name:String, email: String, pass: String): Result<AuthResult> = try{
        val result = firebaseAuth.createUserWithEmailAndPassword(email,pass)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    val userId = it.result?.user?.uid

                    val userModel = UserModel(name,email,userId!!)

                    Firebase.firestore.collection("users").document(userId)
                        .set(userModel)
                        .addOnCompleteListener { res->
                            if(res.isSuccessful){
                                println("User added to firestore")
                            }else{
                                println("User not added to firestore")
                            }
                        }
                }
            }
            .await()
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