package dev.redcom1988.hermes.ui.main

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.stack.StackEvent
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorDisposeBehavior
import cafe.adriel.voyager.transitions.ScreenTransition
import dev.redcom1988.hermes.domain.auth.AuthRepository
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.ui.screen.home.HomeScreen
import dev.redcom1988.hermes.ui.screen.login.LoginScreen
import dev.redcom1988.hermes.ui.theme.HermesTheme
import soup.compose.material.motion.animation.materialSharedAxisX
import soup.compose.material.motion.animation.rememberSlideDistance


class MainActivity : ComponentActivity() {

    private val authRepository: AuthRepository by injectLazy()
    private var isReady = false
//    private var initialScreen: Screen = HomeScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    // Check whether the initial data is ready.
                    return if (isReady) {
                        // The content is ready. Start drawing.
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        // The content isn't ready. Suspend.
                        false
                    }
                }
            }
        )

        enableEdgeToEdge()
        setContent {
            HermesTheme {
                val globalAuthScreenModel = remember { GlobalAuthScreenModel(authRepository) }
                val authState by globalAuthScreenModel.authState.collectAsState()
                LaunchedEffect(Unit) {
                    globalAuthScreenModel.initialize()
                }

                if (authState.isInitialized) {
                    val initialScreen: Screen = if (authState.isLoggedIn) {
                        HomeScreen
                    } else {
                        LoginScreen
                    }

                    val slideDistance = rememberSlideDistance()
                    Navigator(
                        screen = initialScreen,
                        disposeBehavior = NavigatorDisposeBehavior(
                            disposeNestedNavigators = false,
                            disposeSteps = true,
                        )
                    ) { navigator ->
                        ScreenTransition(
                            modifier = Modifier.fillMaxSize(),
                            navigator = navigator,
                            transition = {
                                materialSharedAxisX(
                                    forward = navigator.lastEvent != StackEvent.Pop,
                                    slideDistance = slideDistance,
                                )
                            },
                        )
                    }
                }
                LaunchedEffect(authState.isInitialized) {
                    isReady = true
                }
            }
        }
    }
}