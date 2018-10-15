package co.happybits.mpcompanion

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GlobalScope.launch(Dispatchers.Default) {
            val loginManager = LoginManager.getInstance()
            loginManager.auth()
            loginManager.login("6125018293", "US")
            Log.v(MainActivity::class.simpleName, "Api token: " + loginManager.apiToken)
        }
    }
}
