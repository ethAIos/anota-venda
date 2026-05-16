package com.caderninho.vendas.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caderninho.vendas.data.prefs.UserPrefsRepository
import com.caderninho.vendas.data.repo.SalesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: UserPrefsRepository,
    private val repo: SalesRepository,
) : ViewModel() {

    val template: StateFlow<String> = prefs.chargeTemplate
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserPrefsRepository.DEFAULT_TEMPLATE)

    fun saveTemplate(value: String) {
        viewModelScope.launch { prefs.setChargeTemplate(value) }
    }

    fun clearAllData(onDone: () -> Unit) {
        viewModelScope.launch {
            repo.clearAllData()
            onDone()
        }
    }

    suspend fun exportCsvFile(context: Context): File = withContext(Dispatchers.IO) {
        val dir = File(context.cacheDir, "exports").also { it.mkdirs() }
        val stamp = DateTimeFormatter.ofPattern("yyyyMMdd-HHmm").format(LocalDateTime.now())
        File(dir, "caderninho-vendas-$stamp.csv").also { file ->
            file.writeText(repo.exportCsv(), Charsets.UTF_8)
        }
    }
}
