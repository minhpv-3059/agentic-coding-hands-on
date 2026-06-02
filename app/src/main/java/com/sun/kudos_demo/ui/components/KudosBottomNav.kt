package com.sun.kudos_demo.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.sun.kudos_demo.navigation.NavRoutes
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import com.sun.kudos_demo.ui.theme.KudosContainer
import com.sun.kudos_demo.ui.theme.KudosDivider
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosGray

enum class BottomNavTab(val label: String, val icon: ImageVector, val route: String) {
    Saa2025("SAA 2025", Icons.Default.Home, NavRoutes.HOME),
    Awards("Awards", Icons.Default.Star, NavRoutes.AWARDS),
    Kudos("Kudos", Icons.Default.Favorite, NavRoutes.KUDOS_FEED),
    Profile("Profile", Icons.Default.Person, NavRoutes.PROFILE_ME)
}

@Composable
fun KudosBottomNav(
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = KudosContainer,
        modifier = modifier
    ) {
        BottomNavTab.entries.forEach { tab ->
            val selected = tab == selectedTab
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label
                    )
                },
                label = {
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = KudosGold,
                    selectedTextColor = KudosGold,
                    unselectedIconColor = KudosGray,
                    unselectedTextColor = KudosGray,
                    indicatorColor = KudosDivider
                )
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF101417)
@Composable
private fun KudosBottomNavPreview() {
    KudosAppTheme {
        KudosBottomNav(
            selectedTab = BottomNavTab.Kudos,
            onTabSelected = {}
        )
    }
}
