package dev.redcom1988.hermes.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import dev.redcom1988.hermes.data.sync.SyncDataJob
import dev.redcom1988.hermes.ui.components.AppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormLayout(
    title: String,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current.applicationContext
    val scope = rememberCoroutineScope()
    val isSyncing by SyncDataJob.isSyncing(context, scope).collectAsState()

    Scaffold(
        topBar = {
            AppBar(
                title = title,
                navigateUp = onBack,
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isSyncing,
            onRefresh = { /* Display only - no user refresh allowed */ }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding)
            ) {
                content()
            }
        }
    }
}