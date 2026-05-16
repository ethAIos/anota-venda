package com.caderninho.vendas.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.caderninho.vendas.data.db.entities.InstallmentEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface InstallmentDao {
    @Query("SELECT * FROM installments WHERE orderId = :orderId ORDER BY number ASC")
    fun observeByOrder(orderId: Long): Flow<List<InstallmentEntity>>

    @Query("""
        SELECT i.* FROM installments i
        INNER JOIN orders o ON o.id = i.orderId
        WHERE o.customerId = :customerId
        ORDER BY i.dueDate ASC
    """)
    fun observeByCustomer(customerId: Long): Flow<List<InstallmentEntity>>

    @Query("SELECT * FROM installments WHERE paidAt IS NULL ORDER BY dueDate ASC")
    fun observeOpen(): Flow<List<InstallmentEntity>>

    @Query("SELECT * FROM installments WHERE paidAt IS NULL AND dueDate = :date ORDER BY id ASC")
    fun observeDueOn(date: LocalDate): Flow<List<InstallmentEntity>>

    @Query("""
        SELECT * FROM installments
        WHERE paidAt IS NULL AND dueDate BETWEEN :startDate AND :endDate
        ORDER BY dueDate ASC, id ASC
    """)
    fun observeOpenDueBetween(startDate: LocalDate, endDate: LocalDate): Flow<List<InstallmentEntity>>

    @Query("SELECT * FROM installments WHERE id = :id")
    suspend fun getById(id: Long): InstallmentEntity?

    @Query("SELECT * FROM installments WHERE orderId = :orderId ORDER BY number ASC")
    suspend fun getByOrder(orderId: Long): List<InstallmentEntity>

    @Query("SELECT * FROM installments ORDER BY dueDate ASC, id ASC")
    suspend fun getAll(): List<InstallmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(installments: List<InstallmentEntity>)

    @Query("UPDATE installments SET paidAt = :date WHERE id = :id")
    suspend fun markPaid(id: Long, date: LocalDate)

    @Query("UPDATE installments SET dueDate = :newDate WHERE id = :id")
    suspend fun postpone(id: Long, newDate: LocalDate)

    @Query("UPDATE installments SET paidAt = NULL WHERE id = :id")
    suspend fun unmarkPaid(id: Long)

    @Query("UPDATE installments SET amountCents = :cents WHERE id = :id")
    suspend fun updateAmount(id: Long, cents: Long)
}
