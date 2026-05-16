package com.caderninho.vendas

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.caderninho.vendas.widget.WidgetRefreshObserver
import com.caderninho.vendas.widget.WidgetRefreshWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class CaderninhoApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var widgetObserver: WidgetRefreshObserver

    override fun onCreate() {
        super.onCreate()
        WidgetRefreshWorker.enqueue(this)
        widgetObserver.start()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
