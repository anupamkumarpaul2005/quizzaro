package com.example.quizapp

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizapp.databinding.ActivityHomeBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val (username, maxScore) = getUserData(this)

        binding.greeting.text = "Hello, $username"
        binding.maxScore.text = maxScore.toString()


        binding.playButton.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
        }
    }

    fun getUserData(context: Context): Pair<String?, Int> {
        // Get the SharedPreferences instance
        val sharedPref: SharedPreferences = context.getSharedPreferences("userPrefs", Context.MODE_PRIVATE)

        // Retrieve the saved username and maxScore
        val username = sharedPref.getString("username", "User") // Default to "User" if not found
        val maxScore = sharedPref.getInt("maxScore", 0) // Default to 0 if not found

        // Return the data as a pair
        return Pair(username, maxScore)
    }

}