package dev.redcom1988.hermes.ui.main

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MiscellaneousServices
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.data.local.auth.UserPreference
import dev.redcom1988.hermes.data.sync.SyncDataJob
import dev.redcom1988.hermes.ui.components.AppBar
import dev.redcom1988.hermes.ui.components.LoadingScreen
import dev.redcom1988.hermes.ui.components.SidebarItem
import dev.redcom1988.hermes.ui.screen.attendance.AttendanceScreen
import dev.redcom1988.hermes.ui.screen.client.ClientScreen
import dev.redcom1988.hermes.ui.screen.division.DivisionScreen
import dev.redcom1988.hermes.ui.screen.employee.EmployeeScreen
import dev.redcom1988.hermes.ui.screen.home.HomeScreen
import dev.redcom1988.hermes.ui.screen.meeting.MeetingScreen
import dev.redcom1988.hermes.ui.screen.service.ServiceScreen
import dev.redcom1988.hermes.ui.screen.settings.SettingsScreen
import dev.redcom1988.hermes.ui.screen.task.TaskScreen
import dev.redcom1988.hermes.ui.screen.user.UserScreen
import dev.redcom1988.hermes.ui.screen.workplan.WorkplanScreen
import kotlinx.coroutines.launch

val localUserPreference = staticCompositionLocalOf<UserPreference> { error ("No UserPreference provided") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenLayout(
    screen: Screen,
    title: String,
    ignoreTopPadding: Boolean = false,
    isLoading: Boolean = false,
    loadingText: String? = null,
    floatingActionButton: @Composable () -> Unit = {},
    appBarActions: @Composable RowScope.() -> Unit = {},
    content: @Composable () -> Unit
) {
    val navigator = LocalNavigator.currentOrThrow
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current.applicationContext
    val isSyncing by SyncDataJob.isSyncing(context, scope).collectAsState()

    @SuppressLint("ConfigurationScreenWidthHeight")
    val viewportSize = LocalConfiguration.current.screenWidthDp.dp

    val userPreference by remember { injectLazy<UserPreference>() }

    CompositionLocalProvider(
        localUserPreference provides userPreference
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.width(minOf(viewportSize * 0.6f, 320.dp))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "HERMES",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        HorizontalDivider(
                            thickness = DividerDefaults.Thickness,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        )

                        // Sidebar navigation items
                        SidebarItem(
                            title = "Home",
                            icon = Icons.Default.Home,
                            selected = screen == HomeScreen,
                            onClick = {
                                navigator.push(HomeScreen)
                                scope.launch { drawerState.close() }
                            }
                        )
                        SidebarItem(
                            title = "Attendance",
                            icon = Icons.Default.Event,
                            selected = screen == AttendanceScreen,
                            onClick = {
                                navigator.push(AttendanceScreen)
                                scope.launch { drawerState.close() }
                            }
                        )
                        SidebarItem(
                            title = "Tasks",
                            icon = Icons.Default.Task,
                            selected = screen == TaskScreen,
                            onClick = {
                                navigator.push(TaskScreen)
                                scope.launch { drawerState.close() }
                            }
                        )
                        SidebarItem(
                            title = "Users",
                            icon = Icons.Default.SupervisorAccount,
                            selected = screen == UserScreen,
                            onClick = {
                                navigator.push(UserScreen)
                                scope.launch { drawerState.close() }
                            }
                        )
                        SidebarItem(
                            title = "Employees",
                            icon = Icons.Default.AccountBox,
                            selected = screen == EmployeeScreen,
                            onClick = {
                                navigator.push(EmployeeScreen)
                                scope.launch { drawerState.close() }
                            }
                        )
                        SidebarItem(
                            title = "Divisions",
                            icon = Icons.Default.AccountTree,
                            selected = screen == DivisionScreen,
                            onClick = {
                                navigator.push(DivisionScreen)
                                scope.launch { drawerState.close() }
                            }
                        )
                        SidebarItem(
                            title = "Workhour Plan",
                            icon = Icons.Default.CalendarMonth,
                            selected = screen == WorkplanScreen,
                            onClick = {
                                navigator.push(WorkplanScreen)
                                scope.launch { drawerState.close() }
                            }
                        )
                        SidebarItem(
                            title = "Clients",
                            icon = Icons.Default.AccountTree,
                            selected = screen == ClientScreen,
                            onClick = {
                                navigator.push(ClientScreen)
                                scope.launch { drawerState.close() }
                            }
                        )
                        SidebarItem(
                            title = "Services",
                            icon = Icons.Default.MiscellaneousServices,
                            selected = screen == ServiceScreen,
                            onClick = {
                                navigator.push(ServiceScreen)
                                scope.launch { drawerState.close() }
                            }
                        )
                        SidebarItem(
                            title = "Meetings",
                            icon = Icons.Default.MeetingRoom,
                            selected = screen == MeetingScreen,
                            onClick = {
                                navigator.push(MeetingScreen)
                                scope.launch { drawerState.close() }
                            }
                        )
                        SidebarItem(
                            title = "Settings",
                            icon = Icons.Default.Settings,
                            selected = screen == SettingsScreen,
                            onClick = {
                                navigator.push(SettingsScreen)
                                scope.launch { drawerState.close() }
                            }
                        )
                    }
                }
            }
        ) {
            PullToRefreshBox(
                isRefreshing = isSyncing,
                onRefresh = { SyncDataJob.start(context)}
            ) {
                Scaffold(
                    topBar = {
                        AppBar(
                            title = title,
                            navigateUp = {
                                scope.launch { drawerState.open() }
                            },
                            navigationIcon = Icons.Default.Menu,
                            actions = appBarActions
                        )
                    },
                    floatingActionButton =  {
                        floatingActionButton()
                    }
                ) { padding ->
                    val contentPadding = if (ignoreTopPadding) {
                        // When ignoring top padding, still pad to the AppBar but don't add extra padding
                        PaddingValues(
                            top = padding.calculateTopPadding(),
                            bottom = padding.calculateBottomPadding(),
                            start = padding.calculateStartPadding(LocalLayoutDirection.current),
                            end = padding.calculateEndPadding(LocalLayoutDirection.current)
                        )
                    } else {
                        // Normal padding behavior
                        padding
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(contentPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            LoadingScreen(text = loadingText)
                        } else {
                            content()
                        }
                    }
                }
            }
        }
    }
}