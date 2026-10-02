package com.example.scontatore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.scontatore.ui.CalculatorScreen
import com.example.scontatore.ui.theme.ScontatoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScontatoreTheme {
                CalculatorScreen()
            }
        }
    }
}
