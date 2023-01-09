package online.daliyq.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import online.daliyq.AuthManager
import online.daliyq.R
import online.daliyq.ui.base.BaseActivity
import online.daliyq.ui.login.LoginActivity
import online.daliyq.ui.main.MainActivity

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch {
            delay(1000)

            if(AuthManager.accessToken.isNullOrEmpty()){
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }

            finish()
        }
    }
}