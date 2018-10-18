package co.happybits.mpcompanion.widget

import java.io.Serializable

data class PoloWidget(val conversationId: String,
                      val title: String,
                      val unwatchedCount: String,
                      val imgUrl: String?) : Serializable