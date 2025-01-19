package com.example.quizapp

data class QuestionModel(
    val question: String? = null,
    val option1: String? = null,
    val option2: String? = null,
    val option3: String? = null,
    val option4: String? = null,
    val correctAnswer: Int? = 0
)
