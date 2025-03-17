package com.example.lockscreen_stylednotification.lockscreen.digital_clock

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date

class ClockViewModel: ViewModel() {
    private val dateFormat: DateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM)
    private val timeFormat: DateFormat = DateFormat.getTimeInstance(DateFormat.SHORT)

    private val _time = MutableStateFlow(timeFormat.format(Date()))
    val time: StateFlow<String> = _time.asStateFlow()

    private val _date = MutableStateFlow(dateFormat.format(Date()))
    val date: StateFlow<String> = _date.asStateFlow()

    init {
        CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                val now = Date()
                _time.value = timeFormat.format(now)
                _date.value = dateFormat.format(now)
                delay(1000)
            }
        }
    }
}