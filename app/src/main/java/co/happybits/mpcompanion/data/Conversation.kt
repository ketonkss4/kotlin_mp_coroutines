package co.happybits.mpcompanion.data


data class Conversation(val group: Boolean,
                        val title: String,
                        val count_records: String,
                        val conversation_id: String,
                        val modified_at: String,
                        val messages: Messages,
                        val members: List<Member>)

fun List<Member>.getMyUserId(): String? {
    forEach {
        if (it.isMyId()) {
            return it.user_id
        }
    }
    return null
}