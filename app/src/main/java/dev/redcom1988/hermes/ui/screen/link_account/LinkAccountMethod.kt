package dev.redcom1988.hermes.ui.screen.link_account

sealed interface LinkAccountMethod {
    data class Basic(
        val email: String,
        val password: String,
    ): LinkAccountMethod
    data object Google: LinkAccountMethod
}