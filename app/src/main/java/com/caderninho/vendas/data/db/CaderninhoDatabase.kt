package com.caderninho.vendas.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.caderninho.vendas.data.db.dao.CustomerDao
import com.caderninho.vendas.data.db.dao.InstallmentDao
import com.caderninho.vendas.data.db.dao.OrderDao
import com.caderninho.vendas.data.db.entities.CustomerEntity
import com.caderninho.vendas.data.db.entities.InstallmentEntity
import com.caderninho.vendas.data.db.entities.OrderEntity

@Database(
    entities = [CustomerEntity::class, OrderEntity::class, InstallmentEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class CaderninhoDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun orderDao(): OrderDao
    abstract fun installmentDao(): InstallmentDao

    companion object {
        const val NAME = "caderninho.db"
    }
}
