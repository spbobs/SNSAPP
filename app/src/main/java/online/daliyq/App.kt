package online.daliyq

import android.app.Application
import online.daliyq.api.ApiService

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        AuthManager.init(this)
        ApiService.init(this)
        Notifier.init(this)
        Settings.init(this)
    }
}