package com.caderninho.vendas.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.caderninho.vendas.data.model.PaymentMode
import java.time.LocalDate

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("customerId")]
)
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long,
    val what: String,
    val createdAt: LocalDate,
    val totalCents: Long,
    val paymentMode: PaymentMode,
    val observation: String? = null,
)
