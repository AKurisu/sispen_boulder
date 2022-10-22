package untad.aldochristopherleo.sispenboulder

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
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
            fabIntent = if (user.type == "Panitia"){
                Intent(this@MainActivity, AddEventActivity::class.java)
            } else if (user.type == "Manajer") {
                Intent(this@MainActivity, SignUpActivity::class.java)
            } else {
                Intent()
            }
            if (user.type == "Juri Lapangan" || user.type == "Presiden Juri"){
                activityMainBinding.fab.visibility = View.GONE
            }
        }

        connectionLiveData = ConnectionLiveData(this)
//        connectionLiveData.observe(this){ status ->
//            if (status == false){
////                Toast.makeText(this, "hola",Toast.LENGTH_SHORT).show()
//            } else {
////                Toast.makeText(this, "hello",Toast.LENGTH_SHORT).show()
//            }
//        }

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

    @Deprecated("Deprecated in Java")
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