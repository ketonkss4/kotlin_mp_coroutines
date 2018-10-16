package co.happybits.mpcompanion.authentication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.happybits.mpcompanion.R
import co.happybits.mpcompanion.authentication.dependencies.DaggerAuthComponent
import javax.inject.Inject

class AuthActivity : AppCompatActivity() {

    @Inject lateinit var authActivityViewModel: AuthActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DaggerAuthComponent.builder().build().inject(this)
        authActivityViewModel.authenticateLogin()
    }

}
