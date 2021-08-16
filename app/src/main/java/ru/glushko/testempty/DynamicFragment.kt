package ru.glushko.testempty


import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class DynamicFragment : Fragment() {

    private val contentManipulator = ContentManipulator()
    private var list: MutableList<String> = mutableListOf()
    private lateinit var currencyListView: ListView
    private var alertDialog: AlertDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.currency_dynamic_fragment, container, false)
        currencyListView = view.findViewById(R.id.currency_listView)
        return view
    }

    override fun onStart() {
        if(isOnline()) {
            CoroutineScope(Dispatchers.IO).launch()
            {
                val currentCalendarOnDevice = Calendar.getInstance()
                list =
                    contentManipulator.getDataFormSiteWithDateRange(currentCalendarOnDevice) //Получение курса доллара списком.
                setDataToList(list)
            }
            if(alertDialog?.isShowing == true)
                alertDialog!!.cancel()
        }else
            showNetworkError()
        super.onStart()
    }


    private fun setDataToList(list:MutableList<String>) {
        val listAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), R.layout.listview_custom_textcolor, list)
        activity?.runOnUiThread(kotlinx.coroutines.Runnable { currencyListView.adapter = listAdapter })
    } //Метод для инициализации ListView + Adapter

    override fun onPause() {
        super.onPause()
        list.removeAll(list)
    }

    private fun isOnline(): Boolean {
        val connectivityManager = requireActivity().getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null
    } //Проверка доступности сети перед загрузкой валюты.

    private fun showNetworkError()
    {
        val layoutInflater = LayoutInflater.from(requireContext()) //Создание Инфлэйтора
        val errorDialogView = layoutInflater.inflate(R.layout.network_error_dialog, null) //Получение View фрагмента.

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Нет соединения с интернетом!") //Добавление заголовка.
            .setView(errorDialogView) //Присвоение View полученного ранее.
            .setCancelable(false) //Запрет закрытия диалога при нажатии на кнопку "назад".

        alertDialog = builder.create() //Подтверждение создания.
        alertDialog!!.setCanceledOnTouchOutside(false) //Запрет закрытия диалога при нажатии за его границами.
        alertDialog!!.show() //Вызывается для отображения диалогового окна.
    }
}