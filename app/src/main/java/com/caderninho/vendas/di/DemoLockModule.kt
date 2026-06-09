package com.caderninho.vendas.di

import com.caderninho.vendas.BuildConfig
import com.caderninho.vendas.demo.DemoLockPolicy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Duration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DemoLockModule {

    @Provides
    @Singleton
    fun provideDemoLockPolicy(): DemoLockPolicy =
        DemoLockPolicy(lockDuration = Duration.ofMillis(BuildConfig.DEMO_LOCK_MILLIS))
}
