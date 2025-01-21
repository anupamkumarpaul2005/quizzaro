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
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.JsonObjectRequest
import com.example.quizapp.databinding.ActivityQuizBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore


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


        val url = "https://opentdb.com/api.php?amount=10&category=9&type=multiple"
        fetchQuestions(url)

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

    private fun nextData(prevOp: Int) {
        if(prevOp==0) {
            resetOptionColors()
            updateQuestion()
            count++
            return
        }
        setOptionsEnabled(false)
        val isCorrect = list[count - 1].correctAnswer == prevOp

        val selectedButton = when (prevOp) {
            1 -> binding.option1
            2 -> binding.option2
            3 -> binding.option3
            4 -> binding.option4
            else -> null
        }

        selectedButton?.setBackgroundColor(
            if (isCorrect) getColor(R.color.correct)
            else getColor(R.color.wrong)
        )

        if (isCorrect) {
            score++
        }

        selectedButton?.postDelayed({
            resetOptionColors() // Reset colors of all options
            setOptionsEnabled(true)
            if (count >= list.size) {
                // End of quiz
                val (username, maxScore) = getUserData(this)
                if (score > maxScore) {
                    val userId = Firebase.auth.currentUser?.uid.toString()
                    updateMaxScore(userId)
                    saveUserData(this, username, score)
                }
                val intent = Intent(this, ScoreActivity::class.java)
                intent.putExtra("SCORE", score)
                startActivity(intent)
                finish()
            } else {
                // Update the UI for the next question
                updateQuestion()
                count++
            }
        }, 1000) // 1-second delay
    }

    private fun updateQuestion() {
        binding.questionNo.text = "${count + 1}/10"
        binding.Question.text = list[count].question
        binding.option1.text = list[count].option1
        binding.option2.text = list[count].option2
        binding.option3.text = list[count].option3
        binding.option4.text = list[count].option4
    }

    private fun resetOptionColors() {
        val defaultColor = getColor(R.color.highlight)
        binding.option1.setBackgroundColor(defaultColor)
        binding.option2.setBackgroundColor(defaultColor)
        binding.option3.setBackgroundColor(defaultColor)
        binding.option4.setBackgroundColor(defaultColor)
    }

    private fun updateMaxScore(userId: String?) {
        Firebase.firestore.collection("userBase")
            .whereEqualTo("userId", userId)
            .get()
            .addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    for (document in result.result!!) {
                        // Update the maxScore field for the current user document
                        Firebase.firestore.collection("userBase")
                            .document(document.id)
                            .update("maxScore", score)
                            .addOnSuccessListener {
                                Log.d(TAG, "MaxScore updated successfully")
                            }
                            .addOnFailureListener { exception ->
                                Log.e(TAG, "Error updating maxScore: ", exception)
                            }
                    }
                } else {
                    Log.e(TAG, "Error fetching documents: ", result.exception)
                }
            }
    }

    fun saveUserData(context: Context, username: String?, maxScore: Int) {
        // Get the SharedPreferences instance
        val sharedPref: SharedPreferences = context.getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        // Save the username and maxScore
        editor.putString("username", username)
        editor.putInt("maxScore", maxScore)

        // Commit the changes
        editor.apply()
    }

    private fun fetchQuestions(url: String) {
        val requestQueue = Volley.newRequestQueue(this)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                // Parse the questions and populate the list
                list = parseQuestions(response)

                // Log the questions to verify
                list.forEach { question ->
                    Log.d("QUESTION_MODEL", question.toString())
                }
                if (list.isNotEmpty()) {
                    nextData(0) // Call only after data is fetched and list is populated
                } else {
                    Log.e("FETCH_ERROR", "No questions fetched from the API.")
                }
            },
            { error ->
                // Log the error
                Log.e("API_ERROR", "Error: ${error.message}")
            }
        )

        // Add the request to the queue
        requestQueue.add(jsonObjectRequest)
    }

    private fun parseQuestions(response: org.json.JSONObject): ArrayList<QuestionModel> {
        val questionList = ArrayList<QuestionModel>()

        try {
            val results = response.getJSONArray("results")
            for (i in 0 until results.length()) {
                val questionObject = results.getJSONObject(i)
                val question = questionObject.getString("question")
                val correctAnswer = questionObject.getString("correct_answer")
                val incorrectAnswers = questionObject.getJSONArray("incorrect_answers")

                // Combine correct and incorrect answers
                val options = mutableListOf<String>()
                for (j in 0 until incorrectAnswers.length()) {
                    options.add(incorrectAnswers.getString(j))
                }
                options.add(correctAnswer)
                options.shuffle() // Shuffle the options

                // Determine the index of the correct answer (1-based index for compatibility)
                val correctAnswerIndex = options.indexOf(correctAnswer) + 1

                // Map to QuestionModel
                val questionModel = QuestionModel(
                    question = question,
                    option1 = options.getOrNull(0),
                    option2 = options.getOrNull(1),
                    option3 = options.getOrNull(2),
                    option4 = options.getOrNull(3),
                    correctAnswer = correctAnswerIndex
                )
                questionList.add(questionModel)
            }
        } catch (e: Exception) {
            Log.e("PARSE_ERROR", "Error parsing JSON: ${e.message}")
        }

        return questionList
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

    private fun setOptionsEnabled(enabled: Boolean) {
        binding.option1.isEnabled = enabled
        binding.option2.isEnabled = enabled
        binding.option3.isEnabled = enabled
        binding.option4.isEnabled = enabled
    }
}