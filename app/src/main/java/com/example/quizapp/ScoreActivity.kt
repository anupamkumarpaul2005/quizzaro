package com.example.quizapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizapp.databinding.ActivityQuizBinding
import com.example.quizapp.databinding.ActivityScoreBinding

class ScoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScoreBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var score: Int = intent.getIntExtra("SCORE",0)
        binding.newScore.text = score.toString()


        val (username, maxScore) = getUserData(this)
        binding.maxScore.text = maxScore.toString()

        if(score<5)
            binding.successImg.visibility = View.INVISIBLE
        else
            binding.failImg.visibility = View.INVISIBLE


        binding.playAgainButton.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.homeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
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