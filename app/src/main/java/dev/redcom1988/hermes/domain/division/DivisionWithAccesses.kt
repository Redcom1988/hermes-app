package dev.redcom1988.hermes.domain.division

import dev.redcom1988.hermes.domain.access.Access

data class DivisionWithAccess (
    val division: Division,
    val accesses: List<Access>,
)