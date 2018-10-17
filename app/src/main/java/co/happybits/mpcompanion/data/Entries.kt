package co.happybits.mpcompanion.data


data class Entries(
        val message_id: String,
        val viewers: Viewers?,
        val video: Video?,
        val creator: Member
)

fun Entries.isVideo(): Boolean {
    return (video != null)
}