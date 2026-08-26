package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.EasyTransportApp
import com.example.ui.EasyTransportViewModel
import com.example.ui.theme.EasyTransportTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            EasyTransportTheme {
                val viewModel: EasyTransportViewModel = viewModel()
                EasyTransportApp(viewModel = viewModel)
            }
        }
    }
}
