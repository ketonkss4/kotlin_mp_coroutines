package co.happybits.mpcompanion.data

data class Member(
        val user_id: String,
        val phone: String,
        val first_name: String,
        val last_name: String
) {
    fun isMyId(): Boolean {
        return phone.contains("6125018293")
    }
}