package com.argentspirit.quickvault.activities

import android.net.http.SslCertificate.saveState
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.argentspirit.quickvault.R
import com.argentspirit.quickvault.ui.theme.QuickVaultTheme
import com.argentspirit.quickvault.views.PasswordsView
import com.argentspirit.quickvault.views.ServicesView
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickVaultTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    val bottomNavItems = listOf(ScreenView.Authenticator, ScreenView.Services, ScreenView.Menu)
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(navController, bottomNavItems)
        }
    ){ innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "/services",
            modifier = Modifier.padding(innerPadding)
        ){
            composable("/services") { ServicesView(navController) }
            composable("/services/{serviceId}",
                arguments = listOf(navArgument("serviceId"){type = NavType.LongType})
            ) {
                PasswordsView(navController)
            }
        }
    }
}
sealed class ScreenView(val route: String, val label: String, val iconResId: Int){
    object Authenticator : ScreenView("/services", "Authenticator", R.drawable.encrypted_24dp)
    object Services : ScreenView("/services", "Passwords", R.drawable.key_24dp)
    object Menu : ScreenView("/services", "Menu", R.drawable.menu_24dp)
}
@Composable
fun BottomNavigationBar(navController: NavHostController, items: List<ScreenView>){
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEach { screen ->
            NavigationBarItem(
                // Use painterResource with the icon resource ID
                icon = { Icon(painter = painterResource(id = screen.iconResId), contentDescription = screen.label) },
                label = { Text(screen.label) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    if (currentDestination?.route != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id){
                                saveState = true
                            }
//                            popUpTo(navController.s) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}