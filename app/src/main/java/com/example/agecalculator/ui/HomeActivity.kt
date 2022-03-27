package com.example.agecalculator.ui

import android.os.Bundle
import android.util.Log
import com.example.agecalculator.AppBaseActivity
import com.example.agecalculator.databinding.ActivityHomeBinding

private const val TAG = "AppBase_Act"

class HomeActivity : AppBaseActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "onCreate: HomeActivity.")

    }


}