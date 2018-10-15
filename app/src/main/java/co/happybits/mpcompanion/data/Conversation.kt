package co.happybits.mpcompanion.data


data class Conversation(val group: Boolean,
                        val title: String,
                        val conversation_id: Int,
                        val modified_at: String,
                        val admins: List<Int>,
                        val members: List<Member>,
                        val messages: List<Message>,
                        val sync: String,
                        val `continue`: Boolean)