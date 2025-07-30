package dev.redcom1988.hermes.data.local.division.entity

import androidx.room.Entity

@Entity(primaryKeys = ["divisionId", "accessId"])
data class DivisionAccessCrossRef(
    val divisionId: Int,
    val accessId: Int
)