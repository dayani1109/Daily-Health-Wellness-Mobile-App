package com.example.dailyhealthwellness.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dailyhealthwellness.ui.activities.Home
import com.example.dailyhealthwellness.R

class Login : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.et_login_email)
        passwordEditText = findViewById(R.id.et_login_password)
        loginButton = findViewById(R.id.btn_login)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email & password", Toast.LENGTH_SHORT).show()
            } else if (email == "admin@gmail.com" && password == "123456") {
                Toast.makeText(this, "Login Successful ✅", Toast.LENGTH_SHORT).show()

                // Save login details
                val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)// save user info locally using shared preference
                prefs.edit().apply {
                    putString("user_email", email)
                    putString("user_name", "Admin") // default username
                    apply()
                }

                val intent = Intent(this, Home::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK//old screen histroy clear venva
                startActivity(intent)
                finish()

            } else {
                Toast.makeText(this, "Invalid Credentials ❌", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
