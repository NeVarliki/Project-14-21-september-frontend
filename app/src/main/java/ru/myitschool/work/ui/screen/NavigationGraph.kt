package ru.myitschool.work.ui.screen

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.myitschool.work.data.repo.AuthRepository
import ru.myitschool.work.domain.auth.IsAuthorizedUseCase
import ru.myitschool.work.ui.nav.AppDestination
import ru.myitschool.work.ui.nav.AuthScreenDestination
import ru.myitschool.work.ui.nav.HomeScreenDestination
import ru.myitschool.work.ui.screen.auth.AuthScreen
import ru.myitschool.work.ui.screen.home.HomeScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val start: AppDestination = if (IsAuthorizedUseCase(AuthRepository)()) HomeScreenDestination else AuthScreenDestination
    NavHost(
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        navController = navController,
        startDestination = start,
    ) {
        composable<AuthScreenDestination> { AuthScreen(navController = navController) }
        composable<HomeScreenDestination> { HomeScreen(navController = navController) }
    }
}
