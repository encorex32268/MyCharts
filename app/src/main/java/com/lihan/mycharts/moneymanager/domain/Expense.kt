package com.lihan.mycharts.moneymanager.domain

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.DateFormat
import java.time.LocalDateTime
import java.time.Period
import java.time.YearMonth
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

data class Expense(
    val cost: String,
    val timestamp: Long,
    val isIncome: Boolean
)

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
fun main(args: Array<String>) {
    monthlyExpenses.map {
        val dateTimeFormatter = DateTimeFormatter
            .ofPattern("MM/dd")
        val localDatetime = LocalDateTime.ofEpochSecond(it.timestamp,0,ZoneOffset.UTC)
        println(dateTimeFormatter.format(localDatetime))
    }

}
val monthlyExpenses = run {
    val today = YearMonth.now()
    val startOfMonth = today.atDay(1)
    val endOfMonth = today.atEndOfMonth()

    val expenseList = mutableListOf<Expense>()
    var currentDate = startOfMonth

    while (!currentDate.isAfter(endOfMonth)){
        expenseList.add(
            Expense(
                cost = Random.nextInt(0,9999).toString(),
                timestamp = currentDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC),
                isIncome = false
            )
        )
        currentDate = currentDate.plusDays(1)
    }

    expenseList.toList()
}

fun Long.toDateString(): String {
    val dateTimeFormatter = DateTimeFormatter
        .ofPattern("dd")
    val localDatetime = LocalDateTime.ofEpochSecond(this,0,ZoneOffset.UTC)
    return dateTimeFormatter.format(localDatetime)
}

