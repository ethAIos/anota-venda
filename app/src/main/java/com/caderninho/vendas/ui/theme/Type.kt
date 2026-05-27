package com.caderninho.vendas.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.caderninho.vendas.R

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

private val NunitoFont = GoogleFont("Nunito")
private val CaveatFont = GoogleFont("Caveat")

val NunitoFamily = FontFamily(
    Font(NunitoFont, provider, FontWeight.Medium),
    Font(NunitoFont, provider, FontWeight.SemiBold),
    Font(NunitoFont, provider, FontWeight.Bold),
    Font(NunitoFont, provider, FontWeight.ExtraBold),
    Font(NunitoFont, provider, FontWeight.Black),
)

val CaveatFamily = FontFamily(
    Font(CaveatFont, provider, FontWeight.Medium),
    Font(CaveatFont, provider, FontWeight.SemiBold),
    Font(CaveatFont, provider, FontWeight.Bold),
)

val CaveatStyle = TextStyle(
    fontFamily = CaveatFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 22.sp,
    letterSpacing = 0.3.sp,
)

val LocalCaveatStyle = compositionLocalOf { CaveatStyle }

val CaderninhoTypography: Typography = run {
    val base = Typography()
    Typography(
        displayLarge = base.displayLarge.copy(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold),
        displayMedium = base.displayMedium.copy(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold),
        displaySmall = base.displaySmall.copy(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold),
        headlineLarge = base.headlineLarge.copy(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold),
        headlineMedium = base.headlineMedium.copy(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold),
        headlineSmall = base.headlineSmall.copy(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold),
        titleLarge = base.titleLarge.copy(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold),
        titleMedium = base.titleMedium.copy(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold),
        titleSmall = base.titleSmall.copy(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold),
        bodyLarge = base.bodyLarge.copy(fontFamily = NunitoFamily, fontWeight = FontWeight.Medium),
        bodyMedium = base.bodyMedium.copy(fontFamily = NunitoFamily, fontWeight = FontWeight.Medium),
        bodySmall = base.bodySmall.copy(fontFamily = NunitoFamily, fontWeight = FontWeight.Medium),
        labelLarge = base.labelLarge.copy(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold),
        labelMedium = base.labelMedium.copy(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold),
        labelSmall = base.labelSmall.copy(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, letterSpacing = 0.6.sp),
    )
}
