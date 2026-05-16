package com.caderninho.vendas.data.db

import androidx.room.TypeConverter
import com.caderninho.vendas.data.model.PaymentMode
import com.caderninho.vendas.ui.components.AvatarTone
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun localDateToEpoch(value: LocalDate?): Long? = value?.toEpochDay()

    @TypeConverter
    fun epochToLocalDate(value: Long?): LocalDate? = value?.let(LocalDate::ofEpochDay)

    @TypeConverter
    fun paymentModeToString(value: PaymentMode): String = value.name

    @TypeConverter
    fun stringToPaymentMode(value: String): PaymentMode = PaymentMode.valueOf(value)

    @TypeConverter
    fun avatarToneToString(value: AvatarTone): String = value.name

    @TypeConverter
    fun stringToAvatarTone(value: String): AvatarTone = AvatarTone.valueOf(value)
}
