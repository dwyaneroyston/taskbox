package com.example.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.todo.ui.theme.TodoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoTheme {
                TodoTheme(darkTheme = themeViewModel.isDarkTheme.value) {

                    val navController = rememberNavController()

                    Surface(modifier = Modifier.fillMaxSize()) {
                        NavHost(navController = navController, startDestination = "category") {

                            // Category screen
                            composable("category") {
                                CategoryScreen(
                                    onCategoryClick = { category ->
                                        navController.navigate("todo/${category.id}")
                                    },

                                    themeViewModel = themeViewModel
                                )
                            }

                            // Todo screen with category ID passed
                            composable(
                                route = "todo/{categoryId}",
                                arguments = listOf(navArgument("categoryId") {
                                    type = NavType.IntType
                                })
                            ) { backStackEntry ->
                                val categoryId = backStackEntry.arguments?.getInt("categoryId")
                                    ?: return@composable
                                TodoScreen(
                                    categoryId,
                                    onBackClick = { navController.popBackStack() })
                            }
                        }
                    }
                }
            }
        }
    }
}
