package com.tpkits.presentation.base

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tpkits.domain.model.User
import com.tpkits.presentation.account.AccountScreen
import com.tpkits.presentation.bookmark.BookmarkScreen
import com.tpkits.presentation.search.SearchScreen
import com.tpkits.presentation.home.HomeScreen

enum class BottomNavigationItem(
    val title: String,
    val route: String,
    val icon: ImageVector
) {
    HOME("홈", "home", Icons.Default.Home),
    SEARCH("장소 검색", "search", Icons.Default.Search),
    BOOKMARK("북마크", "bookmark", Icons.Default.AddCircle),
    ACCOUNT("계정", "account", Icons.Default.AccountCircle)
}

@Composable
fun BaseScreen(
    user: User,
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {}
) {
    val navController = rememberNavController()
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavigationItem.HOME.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavigationItem.HOME.route) {
                HomeScreen(user = user)
            }
            composable(BottomNavigationItem.SEARCH.route) {
                SearchScreen()
            }
            composable(BottomNavigationItem.BOOKMARK.route) {
                BookmarkScreen()
            }
            composable(BottomNavigationItem.ACCOUNT.route) {
                AccountScreen(user = user, onLogout = onLogout)
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    NavigationBar {
        BottomNavigationItem.entries.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
} 