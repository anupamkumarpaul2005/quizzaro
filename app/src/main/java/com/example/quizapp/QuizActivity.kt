package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizapp.databinding.ActivityQuizBinding

class QuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding
    private lateinit var list: ArrayList<QuestionModel>
    private var count: Int = 0
    private var score: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityQuizBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        list = ArrayList<QuestionModel>()
        list.add(QuestionModel("When did India gain Independence?", "1945", "1947", "1950", "1962", 2))
        list.add(QuestionModel("What is the capital of France?", "Berlin", "Madrid", "Paris", "Rome", 3))
        list.add(QuestionModel("Which planet is known as the Red Planet?", "Earth", "Mars", "Jupiter", "Venus", 2))
        list.add(QuestionModel("Who wrote 'Romeo and Juliet'?", "Shakespeare", "Dickens", "Tolstoy", "Hemingway", 1))
        list.add(QuestionModel("What is the square root of 64?", "6", "7", "8", "9", 3))

        nextData(0)
        binding.option1.setOnClickListener {
            nextData(1)
        }
        binding.option2.setOnClickListener {
            nextData(2)
        }
        binding.option3.setOnClickListener {
            nextData(3)
        }
        binding.option4.setOnClickListener {
            nextData(4)
        }

    }

    fun nextData(prevOp: Int) {
        if(prevOp!=0) {
            if(list[count-1].correctAnswer==prevOp)
                score++
        }
        if(count>=list.size) {
            val intent= Intent(this, ScoreActivity::class.java)
            intent.putExtra("SCORE",score)
            startActivity(intent)
            finish()
        }
        else {
            binding.questionNo.text = "${count + 1}/10"
            binding.Question.text = list[count].question
            binding.option1.text = list[count].option1
            binding.option2.text = list[count].option2
            binding.option3.text = list[count].option3
            binding.option4.text = list[count].option4
            count++
        }
    }
}