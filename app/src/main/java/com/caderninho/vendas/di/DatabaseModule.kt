package com.caderninho.vendas.di

import android.content.Context
import androidx.room.Room
import com.caderninho.vendas.data.db.CaderninhoDatabase
import com.caderninho.vendas.data.db.SeedData
import com.caderninho.vendas.data.prefs.UserPrefsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        prefs: UserPrefsRepository,
    ): CaderninhoDatabase {
        val db = Room.databaseBuilder(
            context.applicationContext,
            CaderninhoDatabase::class.java,
            CaderninhoDatabase.NAME,
        ).build()
        // seed lazily on first access
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            SeedData.populateIfEmpty(db, prefs)
        }
        return db
    }

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
}
