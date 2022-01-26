package ru.anasoft.weather.view

import android.content.*
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import ru.anasoft.weather.R
import ru.anasoft.weather.databinding.ActivityMainBinding
import ru.anasoft.weather.view.main.MainFragment

private const val IS_RUSSIAN_KEY = "IS_RUSSIAN_KEY"

var isRussianSavedPreference = true

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val myPreferences: SharedPreferences by lazy { getPreferences(MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(savedInstanceState == null){
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commit()
        }

        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkStateReceiver, filter)

        isRussianSavedPreference = myPreferences.getBoolean(IS_RUSSIAN_KEY, true)

    }

    private var networkStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "android.net.conn.CONNECTIVITY_CHANGE") {
                Toast
                    .makeText(context,
                        "Изменилось состояние подключения к Интернет",
                        Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

//    override fun onPause() {
//        super.onPause()
//        myPreferences.edit()
//            .putBoolean(IS_RUSSIAN_KEY, isRussianSavedPreference)
//            .apply()
//    }

    override fun onDestroy() {
        super.onDestroy()
        myPreferences.edit()
            .putBoolean(IS_RUSSIAN_KEY, isRussianSavedPreference)
            .apply()
    }

}