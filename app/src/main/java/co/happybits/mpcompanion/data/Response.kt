package co.happybits.mpcompanion.data

data class Response(
        val sync: String,
        val `continue`: String,
        val conversations: List<Conversation>
)