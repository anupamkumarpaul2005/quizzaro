package com.example.quizapp

import android.content.Intent
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

        }
    }
}