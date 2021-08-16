package ru.glushko.testempty

import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.util.*

class MainActivity : AppCompatActivity(R.layout.activity_main), View.OnClickListener
{
    private val contentManipulator = ContentManipulator()
    private var list: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startService_button.setOnClickListener(this) //Установка
        stopService_button.setOnClickListener(this) //слушателей
    }

    override fun onStart() {
        super.onStart()
        if(isOnline()) {
            CoroutineScope(Dispatchers.IO).launch()
            {
                val currentCalendarOnDevice = Calendar.getInstance()
                val month:Int = currentCalendarOnDevice.get(Calendar.MONTH)
                val year:Int = currentCalendarOnDevice.get(Calendar.YEAR)
                list = contentManipulator.getDataFormSiteWithDateRange(currentCalendarOnDevice) //Получение курса доллара списком.
                setDataToList(list)
            }
        }
    }

    private fun setDataToList(list:MutableList<String>) {
        val listAdapter: ArrayAdapter<String> = ArrayAdapter(applicationContext, R.layout.listview_custom_textcolor, list)
        runOnUiThread(Runnable { currency_listView.adapter = listAdapter })
    } //Метод для инициализации ListView + Adapter

    override fun onPause() {
        super.onPause()
        list.removeAll(list) //Удаление элементов из списка после утраты видимости view
    }

    override fun onClick(view: View?) {
        when(view) {
            startService_button ->
                if(isOnline()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        startForegroundService(Intent(this, CurrencyNotificatorService().javaClass)) //Запуск сервиса для Android O и выше.
                    else
                        startService(Intent(this, CurrencyNotificatorService().javaClass)) //Запуск сервиса для Android ниже O.
                }
            stopService_button -> stopService(Intent(this, CurrencyNotificatorService().javaClass)) //Остановка сервиса.
        }
    }

    private fun isOnline(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null
    } //Проверка доступности сети перед загрузкой валюты.
}