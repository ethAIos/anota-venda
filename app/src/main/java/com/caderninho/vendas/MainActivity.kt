package com.caderninho.vendas

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.rememberNavController
import com.caderninho.vendas.data.prefs.UserPrefsRepository
import com.caderninho.vendas.demo.DemoLockPolicy
import com.caderninho.vendas.demo.DemoLockState
import com.caderninho.vendas.nav.CaderninhoNavGraph
import com.caderninho.vendas.nav.Destinations
import com.caderninho.vendas.ui.screens.expired.ExpiredScreen
import com.caderninho.vendas.ui.theme.CaderninhoTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface RootState {
    data object Loading : RootState
    data class Ready(val onboardingDone: Boolean) : RootState
    data object Expired : RootState
}

@HiltViewModel
class RootViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val prefs: UserPrefsRepository,
    private val demoLockPolicy: DemoLockPolicy,
) : ViewModel() {

    val state: StateFlow<RootState> = combine(
        prefs.onboardingDone,
        flow { emit(demoLockState()) },
    ) { onboardingDone, lockState ->
        when (lockState) {
            is DemoLockState.Expired -> RootState.Expired
            else -> RootState.Ready(onboardingDone)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, RootState.Loading)

    fun completeOnboarding() {
        viewModelScope.launch { prefs.setOnboardingDone(true) }
    }

    private fun demoLockState(): DemoLockState {
        if (!demoLockPolicy.isEnabled()) return DemoLockState.Disabled
        return runCatching {
            val pm = appContext.packageManager
            val pi = pm.getPackageInfo(appContext.packageName, 0)
            demoLockPolicy.evaluate(pi.firstInstallTime)
        }.getOrElse { DemoLockState.Disabled }
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
                val state by rootVm.state.collectAsStateWithLifecycle()
                when (val s = state) {
                    is RootState.Expired -> ExpiredScreen()
                    is RootState.Ready -> {
                        val navController = rememberNavController()
                        val start = if (s.onboardingDone) Destinations.PayingToday else Destinations.Onboarding
                        CaderninhoNavGraph(
                            navController = navController,
                            startDestination = start,
                            onOnboardingComplete = { rootVm.completeOnboarding() },
                        )
                    }
                    RootState.Loading -> {}
                }
            }
        }
    }
}
