package untad.aldochristopherleo.sispenboulder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import untad.aldochristopherleo.sispenboulder.adapter.ListEventsAdapter
import untad.aldochristopherleo.sispenboulder.databinding.ActivityMainBinding
import untad.aldochristopherleo.sispenboulder.util.ConnectionLiveData
import untad.aldochristopherleo.sispenboulder.util.MainViewModel

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var activityMainBinding: ActivityMainBinding
    private var backPressedTime: Long = 0
    private lateinit var backToast: Toast
    private lateinit var connectionLiveData: ConnectionLiveData
    private val viewModel: MainViewModel by viewModels()
    private lateinit var fabIntent : Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(activityMainBinding.root)

        viewModel.user.observe(this){ user ->
            fabIntent = if (user.type == "Juri"){
                Intent(this@MainActivity, AddEventActivity::class.java)
            } else {
                Intent(this@MainActivity, SignUpActivity::class.java)
            }
        }

        connectionLiveData = ConnectionLiveData(this)
        connectionLiveData.observe(this){ status ->
            if (status == false){
//                Toast.makeText(this, "hola",Toast.LENGTH_SHORT).show()
            } else {
//                Toast.makeText(this, "hello",Toast.LENGTH_SHORT).show()
            }
        }

        activityMainBinding.bottomNavView.background = null
        activityMainBinding.bottomNavView.menu.getItem(2).isEnabled = false
        val navController = findNavController(R.id.nav_fragment)
        activityMainBinding.bottomNavView.setupWithNavController(navController)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment, R.id.listFragment ,R.id.profileFragment, R.id.settingFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)
        activityMainBinding.fab.setOnClickListener(this)
    }

    override fun onClick(v: View?){
        when (v?.id){
            R.id.fab -> {
                startActivity(fabIntent)
            }
        }
    }

    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()){
            backToast.cancel()
            super.onBackPressed()
            return
        } else {
            backToast = Toast.makeText(this, "Press back again on exit",Toast.LENGTH_SHORT)
            backToast.show()
        }

        backPressedTime = System.currentTimeMillis()

    }
}