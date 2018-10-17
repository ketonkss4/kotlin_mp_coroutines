package co.happybits.mpcompanion.data

data class Response(
        val sync: String,
        val `continue`: Boolean,
        val conversations: List<Conversation>
)