package ru.anasoft.weather.view

import android.content.*
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import ru.anasoft.weather.R
import ru.anasoft.weather.databinding.ActivityMainBinding
import ru.anasoft.weather.view.contacts.ContactsFragment
import ru.anasoft.weather.view.history.HistoryFragment
import ru.anasoft.weather.view.main.MainFragment
import ru.anasoft.weather.view.map.MapsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_screen_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_history -> {

                val fragmentHistory = supportFragmentManager.findFragmentByTag("HISTORY")
                if (fragmentHistory==null) {
                    supportFragmentManager.apply {
                        beginTransaction()
                            .replace(R.id.container, HistoryFragment.newInstance(), "HISTORY")
                            .addToBackStack("")
                            .commitAllowingStateLoss()
                    }
                }
                true
            }
            R.id.menu_contacts -> {

                val fragmentContacts = supportFragmentManager.findFragmentByTag("CONTACTS")
                if (fragmentContacts==null) {
                    supportFragmentManager.apply {
                        beginTransaction()
                            .replace(R.id.container, ContactsFragment.newInstance(), "CONTACTS")
                            .addToBackStack("")
                            .commitAllowingStateLoss()
                    }
                }
                true
            }
            R.id.menu_google_maps -> {

                val fragmentGMap = supportFragmentManager.findFragmentByTag("GMAP")
                if (fragmentGMap==null) {
                    supportFragmentManager.apply {
                        beginTransaction()
                            .replace(R.id.container, MapsFragment(), "GMAP")
                            .addToBackStack("")
                            .commitAllowingStateLoss()
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}