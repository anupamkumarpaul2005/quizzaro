package com.example.quizapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizapp.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import android.content.Context
import android.content.SharedPreferences
import android.view.View


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var isLoginMode = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        updateUIForMode()

        binding.signUpButton.setOnClickListener {
            val email = binding.Email.editText?.text.toString()
            val password = binding.Password.editText?.text.toString()
            val name = binding.Name.editText?.text.toString()

            if (isLoginMode) {
                logInUser(email, password)
            } else {
                signUpUser(name, email, password)
            }
        }

        binding.toggleLoginSignup.setOnClickListener {
            isLoginMode = !isLoginMode
            updateUIForMode()
        }

    }

    private fun updateUIForMode() {
        if (isLoginMode) {
            binding.Name.visibility = View.GONE
            binding.signUpButton.text = "Log In"
            binding.toggleLoginSignup.text = "Don’t have an account? Sign Up"
        } else {
            binding.Name.visibility = View.VISIBLE
            binding.signUpButton.text = "Sign Up"
            binding.toggleLoginSignup.text = "Already have an account? Log In"
        }
    }

    private fun logInUser(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    fetchUserDataAndUpdatePrefs(userId)
                } else {
                    Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show()
                }
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Login failed: ${it.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun signUpUser(name: String, email: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val user = User(FirebaseAuth.getInstance().currentUser?.uid, name, email, 0)
                addUserToFirestore(user)
                saveUserData(this, name, 0)
                Toast.makeText(this, "User created successfully!", Toast.LENGTH_LONG).show()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Signup failed: ${it.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun fetchUserDataAndUpdatePrefs(userId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("userBase")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        val username = document.getString("username") ?: "Unknown"
                        val maxScore = document.getLong("maxScore")?.toInt() ?: 0

                        // Update shared preferences
                        saveUserData(this, username, maxScore)

                        Toast.makeText(this, "Welcome back, $username!", Toast.LENGTH_SHORT).show()

                        // Navigate to Home Activity
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Toast.makeText(this, "User not found in Firestore", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching user data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


    fun saveUserData(context: Context, username: String, maxScore: Int) {
        // Get the SharedPreferences instance
        val sharedPref: SharedPreferences = context.getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        // Save the username and maxScore
        editor.putString("username", username)
        editor.putInt("maxScore", maxScore)

        // Commit the changes
        editor.apply()
    }


    fun addUserToFirestore(user: User) {
        val db = FirebaseFirestore.getInstance()

        // Create a Map to store QuestionModel data
        val questionMap = hashMapOf(
            "userId" to user.userId,
            "username" to user.username,
            "emailId" to user.emailId,
            "maxScore" to user.maxScore
        )

        // Add the data to a Firestore collection
        db.collection("userBase")
            .add(questionMap)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding document", e)
            }
    }

}