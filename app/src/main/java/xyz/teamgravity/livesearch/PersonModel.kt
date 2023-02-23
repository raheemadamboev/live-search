package xyz.teamgravity.livesearch

data class PersonModel(
    val firstName: String,
    val lastName: String,
) {
    fun matches(query: String): Boolean {
        // combinations
        return listOf(
            "$firstName$lastName",
            "$firstName $lastName",
            "$${firstName.firstOrNull() ?: ""}${lastName.firstOrNull() ?: ""}"
        ).any { value ->
            value.contains(query, ignoreCase = true)
        }
    }

    fun express(): String {
        return "$firstName $lastName"
    }
}
