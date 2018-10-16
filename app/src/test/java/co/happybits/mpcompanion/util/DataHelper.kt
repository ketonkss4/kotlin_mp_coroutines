package co.happybits.mpcompanion.util

import co.happybits.mpcompanion.data.Conversation
import co.happybits.mpcompanion.data.Messages

fun buildTestConversation(
        group: Boolean = false,
        title: String = "",
        conversation_id: String = "0"
): Conversation {
    return Conversation(group,
            title,
            "",
            conversation_id,
            "",
            Messages(emptyList()),
            emptyList())
}