package co.happybits.mpcompanion

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import co.happybits.hbmx.mp.ApplicationIntf

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ApplicationIntf.getRestApi()?.execGenericRequest()
    }
}
