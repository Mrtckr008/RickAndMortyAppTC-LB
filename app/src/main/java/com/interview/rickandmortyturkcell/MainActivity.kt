package com.interview.rickandmortyturkcell

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.interview.rickandmortyturkcell.navigation.AppNavHost
import com.interview.rickandmortyturkcell.ui.common.theme.RickAndMortyTurkcellTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RickAndMortyTurkcellTheme {
                AppNavHost()
            }
        }
    }
}
