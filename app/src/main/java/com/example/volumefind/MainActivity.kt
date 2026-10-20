package com.example.volumefind

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.volumefind.data.repository.AppSettings
import com.example.volumefind.ui.home.VolumeFindViewModel
import com.example.volumefind.ui.navigation.VolumeFindApp
import com.example.volumefind.ui.theme.VolumeFindTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: VolumeFindViewModel = viewModel()
            val settings by viewModel.settingsFlow.collectAsState()

            val isDarkTheme = when (settings.themeMode) {
                AppSettings.THEME_LIGHT -> false
                AppSettings.THEME_DARK -> true
                else -> isSystemInDarkTheme()
            }

            VolumeFindTheme(
                darkTheme = isDarkTheme,
                dynamicColor = settings.dynamicColor
            ) {
                VolumeFindApp(viewModel = viewModel)
            }
        }
    }
}
