package com.example.dailyhealthwellness.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns//email validation check
import android.widget.Button
import android.widget.EditText
import android.widget.Toast//short popup messages
import androidx.appcompat.app.AppCompatActivity
import com.example.dailyhealthwellness.R

class Register : AppCompatActivity() {

    private lateinit var btnRegister: Button
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etEmail = findViewById(R.id.et_register_email)
        etPassword = findViewById(R.id.et_register_password)
        etConfirmPassword = findViewById(R.id.et_register_confirm_password)
        btnRegister = findViewById(R.id.btn_register)

        btnRegister.setOnClickListener {
            val email = etEmail.text.toString().trim()//email password string vidiyt extra space remove karala save karanva
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (email.isEmpty()) {
                etEmail.error = "Email required"
                Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener//click event stop
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {//email format ek weradinm
                etEmail.error = "Enter valid email"
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                etPassword.error = "Password required"
                Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 6) {
                etPassword.error = "Password must be at least 6 characters"
                Toast.makeText(this, "Password too short", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (confirmPassword != password) {
                etConfirmPassword.error = "Passwords do not match"
                Toast.makeText(this, "Passwords must match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save to SharedPreferences
            val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)// user data shared preference ekt save karanva. app ekt vitryi access karann puluvam - private
            prefs.edit().apply {
                putString("user_email", email)
                putString("user_name", email.substringBefore("@")) // default name from email-> @ mark ekat kalin thiyena name ek gannva
                apply()//save changes
            }

            // Move to Login page
            val intent = Intent(this, Login::class.java)
            startActivity(intent)

            Toast.makeText(this, "Registered successfully! Please login.", Toast.LENGTH_LONG).show()
        }
    }
}
