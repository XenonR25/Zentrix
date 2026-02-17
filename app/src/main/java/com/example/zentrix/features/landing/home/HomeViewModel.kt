package com.example.zentrix.features.landing.home

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {
    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    init {
        fetchUserName()
    }

    private fun fetchUserName(){
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        Firebase.firestore.collection("users").document(uid).get()
            .addOnSuccessListener {doc ->
                _userName.value = doc.getString("name")?.split(" ")?.firstOrNull() ?: ""
            }

    }
}