package dev.redcom1988.hermes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SidebarItem(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector? = null,
    selected: Boolean,
    isVisible: Boolean = true,
    onClick: () -> Unit
) {
    val indicatorColor = MaterialTheme.colorScheme.primary
    val textColor = if (selected) indicatorColor else MaterialTheme.colorScheme.onBackground
    val backgroundColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else Color.Transparent

    if (isVisible) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .background(backgroundColor)
                .padding(vertical = 16.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
        ) {
            Icon(
                imageVector = icon ?: Icons.Default.Home,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = textColor
            )
        }
    }
}
