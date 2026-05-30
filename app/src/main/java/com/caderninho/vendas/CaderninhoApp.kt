package com.caderninho.vendas

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.caderninho.vendas.di.ApplicationScope
import com.caderninho.vendas.widget.WidgetRefreshObserver
import com.caderninho.vendas.widget.WidgetRefreshWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltAndroidApp
class CaderninhoApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var widgetObserver: WidgetRefreshObserver
    @Inject @ApplicationScope lateinit var applicationScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        // Widget refresh touches Glance and WorkManager; defer so cold start stays responsive
        // on slow devices and software-only emulators (no KVM).
        applicationScope.launch {
            delay(3_000)
            WidgetRefreshWorker.enqueue(this@CaderninhoApp)
            widgetObserver.start()
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
