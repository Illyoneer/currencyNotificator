package ru.glushko.testempty

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.app.Notification
import android.os.Build
import androidx.core.app.NotificationCompat
import android.app.NotificationManager
import android.graphics.Color
import androidx.annotation.RequiresApi
import android.app.NotificationChannel
import kotlinx.coroutines.*
import java.util.*
import androidx.core.app.NotificationManagerCompat
import android.net.ConnectivityManager

open class CurrencyNotificatorService : Service()
{
    private val contentManipulationManager = ContentManipulator()
    private var currentDateCalendar = Calendar.getInstance()
    private var currentValue = ""
    private var localValue = ""
    private var dayOfStartService = currentDateCalendar.get(Calendar.DAY_OF_MONTH)
    private var canBeRunning: Boolean = true

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        CoroutineScope(Dispatchers.IO).launch {
            scanValueWithDate() //Получение валюты на день запуска сервиса.
            do {
                val currentCalendarOnDevice = Calendar.getInstance()
                val currentDayOnDevice = currentCalendarOnDevice.get(Calendar.DAY_OF_MONTH)

                if(isOnline()) {
                        CoroutineScope(Dispatchers.IO).launch {
                            localValue = contentManipulationManager.getDataFormSiteWithSingleDate(currentCalendarOnDevice)
                        }.join() //Получение валюты на период обновления данных.

                    if(dayOfStartService != currentDayOnDevice) {
                        Log.i("1service", "Local value: $localValue")
                        if(localValue != "" && localValue != currentValue) {
                            showNotificationOfValueChange(localValue) //Отображение уведомления об изменении курса.
                            Log.i("1service", "Day is change")
                            currentValue = localValue
                            dayOfStartService = currentDayOnDevice
                        }
                    }
                }else
                    showNotificationOfEthernetState() //Отображение уведомления об отсутствии интернета.
                delay(600000) //TODO: 600000 Заменить на ContentResolver.
            } while (canBeRunning)
        }
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            launchServiceAndNotificationForAndroidOAndL()
        else
            launchServiceAndNotificationForAndroidLOLLIPOP()
    }

    @RequiresApi(Build.VERSION_CODES.O and Build.VERSION_CODES.R)
    private fun launchServiceAndNotificationForAndroidOAndL() {
        val CHANNEL_ID = "ru.glushko.testempty"
        val CHANNEL_NAME = "CurrencyNotificatorService Channel"
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel =
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setSmallIcon(R.drawable.service_icon)
            .setContentTitle("Мониторинг валюты запущен")
            .setContentText("Обновление каждые 10 минут")
            .setColor(Color.MAGENTA)
            .setOngoing(true)

        startForeground(2, notificationBuilder.build())
    } //Запуск служебного уведомления для Android O и выше.

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun launchServiceAndNotificationForAndroidLOLLIPOP() {
        val CHANNEL_ID = "ru.glushko.testempty"
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.service_icon)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentTitle("Мониторинг валюты запущен")
            .setContentText("Обновление каждые 10 минут")
            .setColor(Color.MAGENTA)
            .setOngoing(true)
        startForeground(1, notificationBuilder.build())
    } //Запуск служебного уведомления для Android LOLLIPOP и выше.

    private fun showNotificationOfValueChange(currentValue:String) {
        val CHANNEL_ID = "ru.glushko.testempty"
        val builder: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Курс валюты изменился!")
            .setContentText("Текущий курс: $currentValue")
            .setSmallIcon(R.drawable.service_icon)
            .setColor(Color.MAGENTA)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setWhen(System.currentTimeMillis())
            .setChannelId(CHANNEL_ID)
            .build()

        val notificationManagerCompat = NotificationManagerCompat.from(this)
        notificationManagerCompat.notify(3, builder)
    } //Отображение уведомления при изменении курса.

    private fun showNotificationOfEthernetState() {
        val CHANNEL_ID = "ru.glushko.testempty"
        val builder: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Нет подключения к интернету!")
            .setContentText("Перезапустите сервис после подключения к интернету.")
            .setSmallIcon(R.drawable.service_icon)
            .setColor(Color.MAGENTA)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setWhen(System.currentTimeMillis())
            .setChannelId(CHANNEL_ID)
            .build()

        val notificationManagerCompat = NotificationManagerCompat.from(this)
        notificationManagerCompat.notify(4, builder)
        stopSelf()
    } //Отображение уведомления при изменении курса.

    override fun onDestroy() {
        super.onDestroy()
        canBeRunning = false
        stopSelf()
        Log.i("1service", "Service stopped.")
    }

    private suspend fun scanValueWithDate() {
        CoroutineScope(Dispatchers.IO).launch {
            val value:String = contentManipulationManager.getDataFormSiteWithSingleDate(currentDateCalendar)
            currentValue = value
            Log.i("1service", "Current value from service: $currentValue")
        }.join()
    }  //Получение валюты на момент вызова.

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun isOnline(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null
    } //Проверка доступности сети перед загрузкой валюты.
}