package dev.redcom1988.hermes.ui.main

import android.Manifest
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.stack.StackEvent
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorDisposeBehavior
import cafe.adriel.voyager.transitions.ScreenTransition
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.data.local.auth.UserPreference
import dev.redcom1988.hermes.domain.attendance.AttendanceRepository
import dev.redcom1988.hermes.domain.auth.AuthRepository
import dev.redcom1988.hermes.service.AttendanceNotificationService
import dev.redcom1988.hermes.ui.screen.home.HomeScreen
import dev.redcom1988.hermes.ui.screen.login.LoginScreen
import dev.redcom1988.hermes.ui.theme.HermesTheme
import dev.redcom1988.hermes.ui.util.rememberMultiplePermissionsState
import dev.redcom1988.hermes.ui.util.rememberNotificationPermissionState
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
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
        
        // Check for active attendance and restart service if needed (after reboot/app kill)
        lifecycleScope.launch {
            try {
                val userPreference: UserPreference by injectLazy()
                val attendanceRepository: AttendanceRepository by injectLazy()
                
                val employeeId = userPreference.employeeId().get().takeIf { it != -1 }
                if (employeeId != null) {
                    val activeAttendance = attendanceRepository.observeActiveAttendanceForEmployee(employeeId).firstOrNull()
                    if (activeAttendance != null) {
                        // Restart service
                        AttendanceNotificationService.start(this@MainActivity, employeeId)
                    }
                }
            } catch (e: Exception) {
                // Silently handle errors
            }
        }
        
        setContent {
            HermesTheme {
                // Request required permissions on startup sequentially
                val locationPermissions = rememberMultiplePermissionsState(
                    permissions = listOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
                
                val notificationPermission = rememberNotificationPermissionState()
                
                // Request permissions sequentially - only show one dialog at a time
                LaunchedEffect(locationPermissions.isAllPermissionsGranted(), notificationPermission.isGranted.value) {
                    // First request location permissions
                    if (!locationPermissions.isAllPermissionsGranted()) {
                        locationPermissions.requestPermissions()
                    } 
                    // Only request notification permission after location permissions are granted
                    else if (!notificationPermission.isGranted.value) {
                        notificationPermission.requestPermission()
                    }
                }
                
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