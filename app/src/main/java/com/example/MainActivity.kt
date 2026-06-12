package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.DriverAppUI
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.DriverViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable premium modern Edge-To-Edge content overlay
        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                // Initialize modern View State with context attachment
                val driverViewModel: DriverViewModel = viewModel()
                
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    DriverAppUI(
                        viewModel = driverViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
