package com.example.anotacoesdeprodutos.presentation.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.YearMonth
import java.time.ZoneId

object MonthStartAndEnd {

    @RequiresApi(Build.VERSION_CODES.O)
    fun currentMonth(): DateRange {
        val zone = ZoneId.systemDefault()

        val start = YearMonth.now()
            .atDay(1)
            .atStartOfDay(zone)
            .toInstant()
            .toEpochMilli()

        val nextMonth = YearMonth.now()
            .plusMonths(1)
            .atDay(1)
            .atStartOfDay(zone)
            .toInstant()
            .toEpochMilli()

        return DateRange(start, nextMonth)
    }
}

data class DateRange(
    val start: Long,
    val end: Long
)