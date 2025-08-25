/*
 * MainActivity.kt
 * BabylAI Android Example App
 *
 * Created by Ahmed Raad on 08/08/2025.
 */

package iq.aau.babylai.android.BabylAI

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import iq.aau.babylai.android.BabylAI.ui.theme.BabylAITheme

/**
 * Main activity for BabylAI Example App
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BabylAITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BabylAIExampleView()
                }
            }
        }
    }
}
