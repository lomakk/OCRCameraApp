package com.vision.scantexter.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.vision.scantexter.android.screen.ocr.OCRScreen
import com.vision.scantexter.android.theme.ScanTexterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ScanTexterTheme {
                BaseActivityScreenContainer {
                    OCRScreen(
                        showOCRState = remember { mutableStateOf(true) },
                        onCopyText = {

                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BaseActivityScreenContainer(
    content: @Composable (navController: NavHostController) -> Unit
) {
    val navController = rememberNavController()
    val systemUiController = rememberSystemUiController()

    ProvideWindowInsets {
        SideEffect {
            systemUiController.setStatusBarColor(
                color = Color.Transparent,
                darkIcons = true
            )
            systemUiController.setNavigationBarColor(
                color = Color.Transparent,
                darkIcons = false
            )
        }

        content(navController)
    }
}

