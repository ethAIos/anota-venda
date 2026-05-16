package com.caderninho.vendas.widget

import android.content.Context
import com.caderninho.vendas.data.repo.SalesRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun salesRepository(): SalesRepository
}

fun Context.widgetRepo(): SalesRepository =
    EntryPointAccessors.fromApplication(applicationContext, WidgetEntryPoint::class.java).salesRepository()
