package dev.redcom1988.hermes.ui.util

import cafe.adriel.voyager.navigator.Navigator
import dev.redcom1988.hermes.ui.components.ResultScreen

fun Navigator.popWithResult(data: Map<String, Any?>) {
    val prev = if (items.size < 2) null else items[items.size - 2] as? ResultScreen
    prev?.arguments = HashMap(data)
    pop()
}

// Convenience overload for easier usage
fun Navigator.popWithResult(vararg pairs: Pair<String, Any?>) {
    popWithResult(mapOf(*pairs))
}