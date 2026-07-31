package com.vincorp.dejavu

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vincorp.dejavu.screens.splash.SplashScreen
import com.vincorp.dejavu.screens.step.StepPhase
import com.vincorp.dejavu.screens.step.StepScreen
import com.vincorp.dejavu.screens.word.WordScreen
import com.vincorp.dejavu.screens.wordcount.WordCountScreen
import com.vincorp.dejavu.theme.Background
import com.vincorp.dejavu.theme.DejaVuTheme
import kotlinx.serialization.Serializable

@Serializable
object SplashDestination

@Serializable
object WordCountDestination

@Serializable
object Step1Destination

@Serializable
object Step2Destination

@Serializable
object WordDestination

@Composable
fun App() {
    DejaVuTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Background
        ) {
            val navController: NavHostController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = SplashDestination,
                modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                composable<SplashDestination> {
                    SplashScreen(
                        onFinished = {
                            navController.navigate(WordCountDestination) {
                                popUpTo(SplashDestination) { inclusive = true }
                            }
                        }
                    )
                }
                composable<WordCountDestination> {
                    WordCountScreen(
                        onNavigateToStep1 = {
                            navController.navigate(Step1Destination)
                        },
                        onNavigateToWord = {
                            navController.navigate(WordDestination)
                        }
                    )
                }
                composable<Step1Destination> {
                    StepScreen(
                        phase = StepPhase.Step1,
                        onNavigateToStep2 = {
                            navController.navigate(Step2Destination) {
                                popUpTo(Step1Destination) { inclusive = true }
                            }
                        },
                        onNavigateToWord = {}
                    )
                }
                composable<Step2Destination> {
                    StepScreen(
                        phase = StepPhase.Step2,
                        onNavigateToStep2 = {},
                        onNavigateToWord = {
                            navController.navigate(WordDestination) {
                                popUpTo(Step2Destination) { inclusive = true }
                            }
                        }
                    )
                }
                composable<WordDestination> {
                    WordScreen()
                }
            }
        }
    }
}
