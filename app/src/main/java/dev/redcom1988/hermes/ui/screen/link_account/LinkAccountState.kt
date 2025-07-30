package dev.redcom1988.hermes.ui.screen.link_account

data class LinkAccountScreenState(
    val basicLinkState: LinkState = LinkState(),
    val googleLinkState: LinkState = LinkState(),
) {
    data class LinkState(
        val linked: Boolean = false,
        val loading: Boolean = false,
        val errorMessage: String? = null,
    )
}