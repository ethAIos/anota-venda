package com.caderninho.vendas.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "caderninho_prefs")

@Singleton
class UserPrefsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object Keys {
        val OnboardingDone = booleanPreferencesKey("onboarding_done")
        val ChargeTemplate = stringPreferencesKey("charge_template")
        val DemoSeeded = booleanPreferencesKey("demo_seeded")
    }

    val onboardingDone: Flow<Boolean> = context.dataStore.data.map {
        it[Keys.OnboardingDone] ?: false
    }

    val chargeTemplate: Flow<String> = context.dataStore.data.map {
        it[Keys.ChargeTemplate] ?: DEFAULT_TEMPLATE
    }

    suspend fun setOnboardingDone(done: Boolean) {
        context.dataStore.edit { it[Keys.OnboardingDone] = done }
    }

    suspend fun setChargeTemplate(template: String) {
        context.dataStore.edit { it[Keys.ChargeTemplate] = template }
    }

    suspend fun isDemoSeeded(): Boolean =
        context.dataStore.data.first()[Keys.DemoSeeded] ?: false

    suspend fun setDemoSeeded(value: Boolean) {
        context.dataStore.edit { it[Keys.DemoSeeded] = value }
    }

    companion object {
        const val DEFAULT_TEMPLATE =
            "Oi {nome}, tudo bem? 🙂 Passando aqui pra lembrar da sua compra ({produto}, {valor}) que vence em {data}. Qualquer coisa me avisa!"
    }
}
