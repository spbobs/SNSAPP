package online.daliyq.ui.base

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import online.daliyq.R
import online.daliyq.Settings
import online.daliyq.api.ApiService
import online.daliyq.db.AppDatabase
import online.daliyq.ui.image.ImageViewerActivity

abstract class BaseActivity : AppCompatActivity() {
    val api: ApiService by lazy { ApiService.getInstance() }
    val db: AppDatabase by lazy { AppDatabase.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Settings.theme == Settings.Theme.Dark) {
            if (this !is ImageViewerActivity) {
                setTheme(R.style.Theme_DaliyQ_Dark)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}