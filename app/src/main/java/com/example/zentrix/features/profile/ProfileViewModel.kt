package com.example.zentrix.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class UserProfile(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val photoUrl: String = ""
)

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _updateStatus = MutableStateFlow<UpdateStatus>(UpdateStatus.Idle)
    val updateStatus: StateFlow<UpdateStatus> = _updateStatus.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val uid = auth.currentUser?.uid ?: return@launch
                val email = auth.currentUser?.email ?: ""

                val document = firestore.collection("users")
                    .document(uid)
                    .get()
                    .await()

                _userProfile.value = UserProfile(
                    name = document.getString("name") ?: "",
                    email = email,
                    phone = document.getString("phone") ?: "",
                    photoUrl = document.getString("photoUrl") ?: ""
                )
            } catch (e: Exception) {
                println("Error loading profile: ${e.message}")
            }
        }
    }

    fun updateProfile(name: String, phone: String) {
        viewModelScope.launch {
            try {
                _updateStatus.value = UpdateStatus.Loading

                val uid = auth.currentUser?.uid ?: return@launch

                firestore.collection("users")
                    .document(uid)
                    .update(
                        mapOf(
                            "name" to name,
                            "phone" to phone
                        )
                    )
                    .await()

                _userProfile.value = _userProfile.value.copy(
                    name = name,
                    phone = phone
                )

                _updateStatus.value = UpdateStatus.Success("Profile updated successfully")
            } catch (e: Exception) {
                _updateStatus.value = UpdateStatus.Error(e.message ?: "Failed to update profile")
            }
        }
    }

    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            try {
                _updateStatus.value = UpdateStatus.Loading

                val user = auth.currentUser ?: return@launch
                val email = user.email ?: return@launch

                // Re-authenticate user
                val credential = EmailAuthProvider.getCredential(email, oldPassword)
                user.reauthenticate(credential).await()

                // Update password
                user.updatePassword(newPassword).await()

                _updateStatus.value = UpdateStatus.Success("Password changed successfully")
            } catch (e: Exception) {
                _updateStatus.value = UpdateStatus.Error(
                    when {
                        e.message?.contains("password") == true -> "Incorrect old password"
                        else -> "Failed to change password"
                    }
                )
            }
        }
    }
}

sealed class UpdateStatus {
    data object Idle : UpdateStatus()
    data object Loading : UpdateStatus()
    data class Success(val message: String) : UpdateStatus()
    data class Error(val message: String) : UpdateStatus()
}