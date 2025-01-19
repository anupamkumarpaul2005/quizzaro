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


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
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

        binding.button.setOnClickListener {
            Firebase.auth.createUserWithEmailAndPassword(binding.Email.editText?.text.toString(),
                binding.Password.editText?.text.toString()).addOnCompleteListener {
                    if(it.isSuccessful) {
                        val user = User(FirebaseAuth.getInstance().currentUser?.uid, binding.Name.editText?.text.toString(), binding.Email.editText?.text.toString(), 0)
                        addUserToFirestore(user)
                        saveUserData(this, binding.Name.editText?.text.toString(), 0)
                        Toast.makeText(this,"User Created!!!",Toast.LENGTH_LONG).show()
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else {
                        Toast.makeText(this,"User NOT Created!!!",Toast.LENGTH_LONG).show()
                    }
            }
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