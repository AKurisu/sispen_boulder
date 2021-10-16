package untad.aldochristopherleo.sispenboulder

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import untad.aldochristopherleo.sispenboulder.databinding.ActivitySplashBinding
import untad.aldochristopherleo.sispenboulder.util.ConnectionLiveData

class SplashActivity : AppCompatActivity() {

    private lateinit var splashActivityBinding : ActivitySplashBinding
    private lateinit var mAuth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        splashActivityBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashActivityBinding.root)

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        Handler().postDelayed({
            if (user!=null){
                val dashboardIntent = Intent(this, MainActivity::class.java)
                startActivity(dashboardIntent)
                finish()
            } else {
                val signInIntent = Intent(this, StartActivity::class.java)
                startActivity(signInIntent)
                finish()
            }
        }, 2000)
    }
}