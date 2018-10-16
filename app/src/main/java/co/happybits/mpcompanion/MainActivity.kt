package co.happybits.mpcompanion

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.happybits.mpcompanion.data.getMyUserId
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GlobalScope.launch(Dispatchers.Main) {
            async(Dispatchers.Default) {
                val loginManager = LoginManager.getInstance()
                loginManager.auth()
                loginManager.login("6125018293", "US")
                Log.v(MainActivity::class.simpleName, "Api token: " + loginManager.apiToken)
                MpCompanion.instance.authToken = loginManager.apiToken
            }.await()

            val response = MpCompanion.appComponent.service().requestConversationSync().await()
            val conversation = response.conversations.first()
            Toast.makeText(
                    this@MainActivity,
                    "Convo Id = ${conversation.conversation_id}. " +
                            "Unwatched Count = ${conversation
                                    .messages
                                    .entries
                                    .asSequence()
                                    .filter {
                                        val myUserId = conversation.members.getMyUserId()
                                        !it.viewers.viewerIds.contains(myUserId)
                                    }
                                    .count()} ",
                    Toast.LENGTH_LONG
            ).show()
        }
    }
}
