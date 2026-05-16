package com.caderninho.vendas.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.caderninho.vendas.ui.components.AvatarTone

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String? = null,
    val avatarTone: AvatarTone = AvatarTone.DEFAULT,
)
