package ru.glushko.testempty

import android.annotation.SuppressLint
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

open class ContentManipulator
{
    @SuppressLint("SimpleDateFormat")
    private val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
    private val contentManager = ContentDownloadManager()

    suspend fun getDataFormSiteWithDateRange(calendar:Calendar) = coroutineScope()
    {
        return@coroutineScope async { contentManager.parseXMLFromWebsiteWithDateRange(getDateOfPreviousMonth(calendar), getCurrentDate(calendar)) }
    }.await() //Получение динамики курса доллара списком.

    suspend fun getDataFormSiteWithSingleDate(calendar:Calendar) = coroutineScope()
    {
        return@coroutineScope async { contentManager.parseXMLFromWebsiteWithSingleDate(getCurrentDate(calendar), getCurrentDate(calendar)) }
    }.await() //Получение курса доллара на определенную дату.


    private fun getCurrentDate(calendar:Calendar) : String {
        return simpleDateFormat.format(calendar.time)
    } //Получение даты текущего дня.

    private fun getDateOfPreviousMonth(calendar:Calendar): String {
        val day:Int = calendar.get(Calendar.DAY_OF_MONTH)
        val month:Int = calendar.get(Calendar.MONTH)
        val year:Int = calendar.get(Calendar.YEAR)
        val lastMonthDate = "$day/$month/$year"
        return simpleDateFormat.format(simpleDateFormat.parse(lastMonthDate))
    } //Получение даты прошлого месяца.

}
