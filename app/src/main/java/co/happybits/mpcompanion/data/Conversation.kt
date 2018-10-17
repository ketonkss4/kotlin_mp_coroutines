package co.happybits.mpcompanion.data


data class Conversation(val group: Boolean,
                        val title: String,
                        val count_records: String,
                        val conversation_id: String,
                        val modified_at: String,
                        val messages: Messages,
                        val members: List<Member>,
                        val creator_id: String)

fun List<Member>.getMyUserId(): String? {
    forEach {
        if (it.isMyId()) {
            return it.user_id
        }
    }
    return null
}

fun Conversation.getConversationTitle(): String {
    if (!title.isNullOrEmpty()) {
        return title
    } else {
        if (group) {
            members.forEach {
                if (it.user_id == creator_id) {
                    return "${it.first_name}'s Group"
                }
            }
            return "Group"
        } else {
            val member = members.find { !it.isMyId() } as Member
            return "${member.first_name} ${member.last_name}"
        }
    }
}

fun Conversation.getUnwatchedCount(): String {
    val myUserId = members.getMyUserId()
    val entries = messages.entries
    var count = 0
    entries.filter { it.isVideo() }
            .filter { !it.creator.isMyId() }
            .forEach { entry ->
        if (entry.viewers == null || !entry.viewers.viewerIds.contains(myUserId)) count++
    }
    return count.toString()
}