package com.example.anotacoesdeprodutos.presentation.formatter


import android.os.Build
import androidx.annotation.RequiresApi
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

val locale: Locale = Locale.Builder().setLanguage("pt").setRegion("BR").build()
val currencyFormatter: NumberFormat = NumberFormat.getCurrencyInstance(locale)


@RequiresApi(Build.VERSION_CODES.O)
fun Long.toBrazilianDate(): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("pt", "BR"))

    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}