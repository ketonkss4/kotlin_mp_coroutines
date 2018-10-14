package co.happybits.mpcompanion.data

data class Message(val creator: String,
                   val created_at: String,
                   val video: String,
                   val test: String,
                   val viewers: List<Viewer>)