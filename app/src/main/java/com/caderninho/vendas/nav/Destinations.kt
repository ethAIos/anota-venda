package com.caderninho.vendas.nav

object Destinations {
    const val Onboarding = "onboarding"
    const val PayingToday = "payingToday"
    const val Receive = "receive/{id}"
    const val NewSale = "newsale?customerId={customerId}"
    fun newSale(customerId: Long? = null): String =
        "newsale?customerId=${customerId ?: -1L}"
    const val Customer = "customer/{id}"
    fun receive(id: Long) = "receive/$id"
    fun customer(id: Long) = "customer/$id"
    const val Order = "order/{id}"
    fun order(id: Long) = "order/$id"
    const val Settings = "settings"

    const val ArgCustomerId = "customerId"
    const val ArgId = "id"
}
