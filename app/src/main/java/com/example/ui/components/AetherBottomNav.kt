package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PanelBorder
import com.example.ui.theme.PanelDark
import com.example.ui.theme.PlasmaCyan
import com.example.ui.theme.SpaceBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

data class NavDestination(
    val title: String,
    val icon: ImageVector,
    val testTag: String
)

val NAV_ITEMS = listOf(
    NavDestination("Swarm", Icons.Default.Public, "nav_singularity"),
    NavDestination("Fusion", Icons.Default.Hub, "nav_tokamak"),
    NavDestination("Orbital", Icons.Default.SatelliteAlt, "nav_orbital"),
    NavDestination("Quantum", Icons.Default.Memory, "nav_quantum"),
    NavDestination("MNC", Icons.Default.Apartment, "nav_mnc")
)

@Composable
fun AetherBottomNav(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = PanelBorder)
            .windowInsetsPadding(WindowInsets.navigationBars),
        containerColor = SpaceBlack,
        tonalElevation = 8.dp
    ) {
        NAV_ITEMS.forEachIndexed { index, item ->
            val isSelected = selectedTab == index
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) PlasmaCyan else TextMuted
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) TextPrimary else TextMuted
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = PlasmaCyan.copy(alpha = 0.2f),
                    selectedIconColor = PlasmaCyan,
                    unselectedIconColor = TextMuted,
                    selectedTextColor = TextPrimary,
                    unselectedTextColor = TextMuted
                )
            )
        }
    }
}
