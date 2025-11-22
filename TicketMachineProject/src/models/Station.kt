package models

data class Station(
    val name: String,
    var singlePrice: Double,
    var returnPrice: Double,
    var salesCount: Int = 0
) {
    fun getPrice(type: TicketType): Double {
        // TODO: return price based on type
        return 0.0
    }

    fun increaseSales() {
        // TODO: increment salesCount
    }
}
