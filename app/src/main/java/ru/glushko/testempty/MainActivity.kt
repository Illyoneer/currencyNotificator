package ru.glushko.testempty

import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class MainActivity : AppCompatActivity(R.layout.activity_main)
{
    private lateinit var fragmentManager:FragmentTransaction
    private lateinit var alertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val toolbar:Toolbar? = findViewById(R.id.toolbar_actionbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = null

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, DynamicFragment())
            .commit() //Установка фрагмента "DynamicFragment" в качесте стартового.

        toolbarTextTitle.text = "Динамика курса доллара"

    }

    override fun onStart() {
        nav_view.setOnItemSelectedListener {
                item ->
            fragmentManager = supportFragmentManager.beginTransaction()
            when (item.itemId) {
                R.id.currency_dynamic -> {
                    if(isOnline()) {
                        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, DynamicFragment()).commit()
                        toolbarTextTitle.text = "Динамика курса доллара"
                    } else
                        showCancelableNetworkError()
                }

                R.id.currency_monitoring ->
                {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, MonitoringFragment()).commit()
                    toolbarTextTitle.text = "Сервис мониторинга курса"
                }
            }
            true
        }
        super.onStart()
    }

    private fun isOnline(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null
    } //Проверка доступности сети перед загрузкой валюты.

    private fun showCancelableNetworkError()
    {
        val add_inflater = LayoutInflater.from(this) //Создание Инфлэйтора
        val network_error_dialog = add_inflater.inflate(R.layout.network_error_dialog, null) //Получение View фрагмента.

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Нет соединения с интернетом!") //Добавление заголовка.
            .setView(network_error_dialog) //Присвоение View полученного ранее.
            .setPositiveButton("Закрыть")
            {
                    alertDialog, _ -> alertDialog.cancel()
            }

        alertDialog = builder.create() //Подтверждение создания.
        alertDialog.show() //Вызывается для отображения диалогового окна.
    }
}