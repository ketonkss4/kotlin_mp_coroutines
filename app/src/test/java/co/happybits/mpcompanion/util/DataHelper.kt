package co.happybits.mpcompanion.util

import co.happybits.mpcompanion.data.Conversation

fun buildTestConversation(
        group: Boolean = false,
        title: String = "",
        conversation_id: Int = 0
): Conversation {
    return Conversation(group,
            title,
            conversation_id,
            "",
            emptyList(),
            emptyList(),
            emptyList(),
            "",
            false)
}