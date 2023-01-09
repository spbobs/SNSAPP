package online.daliyq.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import online.daliyq.AuthManager
import online.daliyq.R
import online.daliyq.databinding.ActivityLoginBinding
import online.daliyq.ui.base.BaseActivity
import online.daliyq.ui.main.MainActivity

class LoginActivity : BaseActivity() {
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.password.setOnEditorActionListener { textView, i, keyEvent ->
            when(i){
                EditorInfo.IME_ACTION_DONE -> {
                    login()
                    return@setOnEditorActionListener true
                }
                EditorInfo.IME_ACTION_UNSPECIFIED -> {
                    if(keyEvent.action == KeyEvent.ACTION_DOWN && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER){
                        login()
                        return@setOnEditorActionListener true
                    }
                }
            }
            false
        }

        binding.login.setOnClickListener {
            login()
        }
    }

    fun validateUidAndPassword(uid: String, password: String) : Boolean{
        binding.userIdLayout.error = null
        binding.passwordLayout.error = null

        if(uid.length < 5){
            binding.userIdLayout.error = getString(R.string.error_uid_too_short)
            return false
        }
        if(password.length < 8){
            binding.passwordLayout.error = getString(R.string.error_password_too_short)
            return false
        }
        val numberRegex = "[0-9]".toRegex()
        if(!numberRegex.containsMatchIn(password)){
            binding.userIdLayout.error = getString(R.string.error_password_must_contain_number)
            return false
        }

        return true
    }

    fun login(){
        if(binding.progress.isInvisible){
            return
        }

        val uid = binding.userId.text?.trim().toString()
        val password = binding.password.text?.trim().toString()

        if(validateUidAndPassword(uid, password)){
            binding.progress.isInvisible = true

            lifecycleScope.launch{
                val authTokenResponse = api.login(uid, password)
                if(authTokenResponse.isSuccessful){
                    val authToken = authTokenResponse.body()

                    AuthManager.uid = uid
                    AuthManager.accessToken = authToken?.accessToken
                    AuthManager.refreshToken = authToken?.refreshToken

                    val messagingToken = FirebaseMessaging.getInstance().token.await()
                    api.registerPushToken(messagingToken)

                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    binding.progress.isVisible = false
                    Toast.makeText(this@LoginActivity,R.string.error_login_failed,Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}