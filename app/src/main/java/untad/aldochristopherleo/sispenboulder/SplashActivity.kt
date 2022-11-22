package untad.aldochristopherleo.sispenboulder

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import untad.aldochristopherleo.sispenboulder.databinding.ActivitySplashBinding
import untad.aldochristopherleo.sispenboulder.util.ConnectionLiveData

class SplashActivity : AppCompatActivity() {

    private lateinit var splashActivityBinding : ActivitySplashBinding
    private lateinit var mAuth : FirebaseAuth
    private lateinit var connectionLiveData: ConnectionLiveData
    private var backPressedTime: Long = 0
    private lateinit var backToast: Toast


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        connectionLiveData = ConnectionLiveData(this)
        connectionLiveData.observe(this){ status ->
            if (status == false){
                Toast.makeText(this, "hola",Toast.LENGTH_LONG).show()

                mAuth = FirebaseAuth.getInstance()
                val user = mAuth.currentUser

                Handler().postDelayed({
                    if (user!=null){
                        val dashboardIntent = Intent(this, MainActivity::class.java)
                        dashboardIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(dashboardIntent)
                        finish()
                    } else {
                        val signInIntent = Intent(this, StartActivity::class.java)
                        signInIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(signInIntent)
                        finish()
                    }
                }, 2000)
            } else{

                Toast.makeText(this, "hello",Toast.LENGTH_LONG).show()
                mAuth = FirebaseAuth.getInstance()
                val user = mAuth.currentUser

                Handler().postDelayed({
                    if (user!=null){
                        val dashboardIntent = Intent(this, MainActivity::class.java)
                        dashboardIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(dashboardIntent)
                        finish()
                    } else {
                        val signInIntent = Intent(this, StartActivity::class.java)
                        signInIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(signInIntent)
                        finish()
                    }
                }, 2000)
            }

        }


        splashActivityBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashActivityBinding.root)


    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (backPressedTime + 1000 > System.currentTimeMillis()){
            backToast.cancel()
            finish()
            super.onBackPressed()
            return
        } else {
            backToast = Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT)
            backToast.show()
        }

        backPressedTime = System.currentTimeMillis()

    }
}