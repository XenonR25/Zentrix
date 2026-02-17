package com.example.zentrix.domain.usecase

import com.example.zentrix.domain.repository.AuthRepository
import com.google.firebase.auth.AuthResult
import javax.inject.Inject

class ValidateSignupUseCase @Inject constructor(
    private val repository: AuthRepository
){
    private val passwordPattern = Regex("^(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-za-z\\d@$!%*?&]{8,}$")

    suspend operator fun invoke(name:String, email: String, pass:String,confirmPass: String): Result<AuthResult>{
        return when{
            email.isBlank() -> Result.failure(Exception("Email is required"))
            !passwordPattern.matches(pass) -> Result.failure(Exception("Password too weak: Use Uppercase, Lowercase, Number, and Special Char (8+ chars)"))
            pass != confirmPass -> Result.failure(Exception("Passwords do not match"))
            else -> repository.signUp(name,email,pass)
        }
    }
}