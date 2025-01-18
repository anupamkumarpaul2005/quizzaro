package com.example.quizapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quizapp.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

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
                        Toast.makeText(this,"User Created!!!",Toast.LENGTH_LONG).show()
                    }
                    else {
                        Toast.makeText(this,"User NOT Created!!!",Toast.LENGTH_LONG).show()
                    }
            }
        }
    }
}