data class Ticket(
    val origin: String,
    val destination: Station,
    val type: TicketType,
    val price: Double
) {
    fun formatted(): String {
        // Format price with two decimals
        val priceStr = String.format("%.2f", price)
        return """
            ***
            [$origin]
            to
            [${destination.name}]
            Price: $priceStr [${type}]
            ***
        """.trimIndent()
    }
}
