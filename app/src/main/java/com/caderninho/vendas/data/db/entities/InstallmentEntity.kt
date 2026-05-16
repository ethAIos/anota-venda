package com.caderninho.vendas.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "installments",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("orderId"), Index("dueDate"), Index("paidAt")]
)
data class InstallmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: Long,
    val number: Int,
    val ofTotal: Int,
    val dueDate: LocalDate,
    val amountCents: Long,
    val paidAt: LocalDate? = null,
)
