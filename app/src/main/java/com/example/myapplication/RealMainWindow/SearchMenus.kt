package com.example.myapplication.RealMainWindow

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.myapplication.databinding.MainMenuBinding

class SearchMenus : ComponentActivity() {
    lateinit var binding: MainMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainMenuBinding.inflate(layoutInflater)

        binding.searchbutton.setText("인기순")
        binding.mycocktail.setText("베이스 별")

        setContentView(binding.root)
    }
}