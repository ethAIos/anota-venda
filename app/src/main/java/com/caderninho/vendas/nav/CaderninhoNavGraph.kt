package com.caderninho.vendas.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navArgument
import com.caderninho.vendas.ui.screens.customer.CustomerDetailScreen
import com.caderninho.vendas.ui.screens.newsale.NewSaleScreen
import com.caderninho.vendas.ui.screens.onboarding.OnboardingScreen
import com.caderninho.vendas.ui.screens.order.OrderDetailScreen
import com.caderninho.vendas.ui.screens.payingtoday.PayingTodayScreen
import com.caderninho.vendas.ui.screens.settings.SettingsScreen

@Composable
fun CaderninhoNavGraph(
    navController: NavHostController,
    startDestination: String,
    onOnboardingComplete: () -> Unit,
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Destinations.Onboarding) {
            OnboardingScreen(onFinish = {
                onOnboardingComplete()
                navController.navigate(Destinations.PayingToday) {
                    popUpTo(Destinations.Onboarding) { inclusive = true }
                }
            })
        }

        composable(
            route = Destinations.PayingToday,
            deepLinks = listOf(navDeepLink { uriPattern = "caderninho://payingtoday" }),
        ) {
            PayingTodayScreen(
                onOpenCustomer = { id -> navController.navigate(Destinations.customer(id)) },
                onOpenNewSale = { navController.navigate(Destinations.newSale()) },
                onOpenSettings = { navController.navigate(Destinations.Settings) },
            )
        }

        composable(
            route = Destinations.Receive,
            arguments = listOf(navArgument(Destinations.ArgId) { type = NavType.LongType }),
            deepLinks = listOf(navDeepLink { uriPattern = "caderninho://receive/{${Destinations.ArgId}}" }),
        ) { entry ->
            val installmentId = entry.arguments?.getLong(Destinations.ArgId)
            PayingTodayScreen(
                initialReceiveInstallmentId = installmentId,
                onOpenCustomer = { id -> navController.navigate(Destinations.customer(id)) },
                onOpenNewSale = { navController.navigate(Destinations.newSale()) },
                onOpenSettings = { navController.navigate(Destinations.Settings) },
            )
        }

        composable(
            route = Destinations.NewSale,
            arguments = listOf(navArgument(Destinations.ArgCustomerId) { type = NavType.LongType; defaultValue = -1L }),
            deepLinks = listOf(navDeepLink { uriPattern = "caderninho://newsale" }),
        ) {
            NewSaleScreen(
                onClose = { navController.popBackStack() },
                onOpenCustomer = { id ->
                    navController.popBackStack()
                    navController.navigate(Destinations.customer(id))
                },
            )
        }

        composable(
            route = Destinations.Customer,
            arguments = listOf(navArgument(Destinations.ArgId) { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = "caderninho://customer/{${Destinations.ArgId}}" }),
        ) {
            CustomerDetailScreen(
                onBack = { navController.popBackStack() },
                onOpenOrder = { id -> navController.navigate(Destinations.order(id)) },
                onNewSaleForCustomer = { id -> navController.navigate(Destinations.newSale(id)) },
                onCustomerDeleted = {
                    navController.popBackStack(Destinations.PayingToday, inclusive = false)
                },
            )
        }

        composable(
            route = Destinations.Order,
            arguments = listOf(navArgument(Destinations.ArgId) { type = NavType.StringType }),
            deepLinks = listOf(navDeepLink { uriPattern = "caderninho://order/{${Destinations.ArgId}}" }),
        ) {
            OrderDetailScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = Destinations.Settings,
            deepLinks = listOf(navDeepLink { uriPattern = "caderninho://settings" }),
        ) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
