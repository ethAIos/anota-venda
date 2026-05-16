package com.caderninho.vendas.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import com.caderninho.vendas.data.repo.SalesRepository
import com.caderninho.vendas.di.ApplicationScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetRefreshObserver @Inject constructor(
    @ApplicationContext private val context: Context,
    @ApplicationScope private val scope: CoroutineScope,
    private val repo: SalesRepository,
) {
    fun start() {
        scope.launch {
            repo.observeOpenInstallments()
                .map { list -> list.map { it.id to it.paidAt } }
                .distinctUntilChanged()
                .drop(1)
                .collect {
                    A1Widget().updateAll(context)
                    A2Widget().updateAll(context)
                    A3Widget().updateAll(context)
                }
        }
    }
}
