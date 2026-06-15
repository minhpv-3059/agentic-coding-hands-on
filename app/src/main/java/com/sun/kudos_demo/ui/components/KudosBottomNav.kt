package com.sun.kudos_demo.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.sun.kudos_demo.R
import com.sun.kudos_demo.navigation.NavRoutes
import com.sun.kudos_demo.ui.theme.KudosAppTheme
import androidx.compose.ui.graphics.Color
import com.sun.kudos_demo.ui.theme.KudosContainer
import com.sun.kudos_demo.ui.theme.KudosGold
import com.sun.kudos_demo.ui.theme.KudosGray

enum class BottomNavTab(val label: String, @DrawableRes val icon: Int, val route: String) {
    Saa2025("SAA 2025", R.drawable.ic_nav_home, NavRoutes.HOME),
    Awards("Awards", R.drawable.ic_nav_awards, NavRoutes.AWARDS),
    Kudos("Kudos", R.drawable.ic_nav_kudos, NavRoutes.KUDOS_FEED),
    Profile("Profile", R.drawable.ic_nav_profile, NavRoutes.PROFILE_ME)
}

@Composable
fun KudosBottomNav(
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = KudosContainer,
        modifier = modifier.navigationBarsPadding()
    ) {
        BottomNavTab.entries.forEach { tab ->
            val selected = tab == selectedTab
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        painter = painterResource(tab.icon),
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
                    indicatorColor = Color.Transparent
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
