package com.example.myapplication.RealMainWindow

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.myapplication.MainWindow.MainActivity
import com.example.myapplication.R
import com.example.myapplication.RemoteRatingView.RemoteRatingActivity
import com.example.myapplication.databinding.MainMenuBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroActivity : ComponentActivity() {
    lateinit var binding : MainMenuBinding

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = MainMenuBinding.inflate(layoutInflater)
            binding.materialToolbar3.inflateMenu(R.menu.main_menu)
            binding.materialToolbar3.title = "칵테일"

            binding.mycocktail.setOnClickListener {
                startActivity(Intent(this, MainActivity::class.java))
            }

            binding.searchbutton.setOnClickListener {
                startActivity(Intent(this, RemoteRatingActivity::class.java))
            }

            setContentView(binding.root)
        }
}