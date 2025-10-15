package com.example.dailyhealthwellness.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dailyhealthwellness.HomeFragment
import com.example.dailyhealthwellness.R

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // home fragment ek load karanva
        if (savedInstanceState == null) {//me activity ek 1 time open veddi vitrak code ek run venna
            supportFragmentManager.beginTransaction()//fragment ek load/replace karann transaction start karanva
                .replace(R.id.homeactivity_fragment_container, HomeFragment())//homefragment ek replace karanva container ekt
                .commit()// fragment ek screen ekt add venva
        }
    }
}

