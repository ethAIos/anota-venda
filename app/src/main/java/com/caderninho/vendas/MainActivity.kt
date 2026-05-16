package com.caderninho.vendas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.rememberNavController
import com.caderninho.vendas.data.prefs.UserPrefsRepository
import com.caderninho.vendas.nav.CaderninhoNavGraph
import com.caderninho.vendas.nav.Destinations
import com.caderninho.vendas.ui.theme.CaderninhoTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    private val prefs: UserPrefsRepository,
) : ViewModel() {
    val onboardingDone: StateFlow<Boolean?> = prefs.onboardingDone
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun completeOnboarding() {
        viewModelScope.launch { prefs.setOnboardingDone(true) }
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CaderninhoTheme {
                val rootVm: RootViewModel = hiltViewModel()
                val onboardingDone by rootVm.onboardingDone.collectAsStateWithLifecycle()
                val navController = rememberNavController()
                if (onboardingDone != null) {
                    val start = if (onboardingDone == true) Destinations.PayingToday else Destinations.Onboarding
                    CaderninhoNavGraph(
                        navController = navController,
                        startDestination = start,
                        onOnboardingComplete = { rootVm.completeOnboarding() },
                    )
                }
            }
        }
    }
}
