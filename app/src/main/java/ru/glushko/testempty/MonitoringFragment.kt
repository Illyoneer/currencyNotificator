package ru.glushko.testempty

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment

class MonitoringFragment : Fragment(), View.OnClickListener {
    private lateinit var startServiceButton: Button
    private lateinit var stopServiceButton: Button
    private lateinit var currencyValueInput: EditText
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.currency_monitoring_fragment, container, false)

        startServiceButton = view.findViewById(R.id.start_service_button)
        startServiceButton.setOnClickListener(this)
        stopServiceButton = view.findViewById(R.id.stop_service_button)
        stopServiceButton.setOnClickListener(this)

        currencyValueInput = view.findViewById(R.id.currency_value_input)

        return view
    }

    override fun onClick(view: View?) {

            when(view) {
                startServiceButton ->
                    if(currencyValueInput.text.toString() != "") {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                            requireActivity().startForegroundService(Intent(requireContext(), CurrencyNotificatorService().javaClass)
                                .putExtra("userValue", currencyValueInput.text.toString())) //Запуск сервиса для Android O и выше.
                        else
                            requireActivity().startService(Intent(requireContext(), CurrencyNotificatorService().javaClass)
                                .putExtra("userValue", currencyValueInput.text.toString())) //Запуск сервиса для Android ниже O.
                    }
                stopServiceButton -> requireActivity().stopService(Intent(requireContext(), CurrencyNotificatorService().javaClass)) //Остановка сервиса.
            }
        }
    }