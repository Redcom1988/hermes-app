package dev.redcom1988.hermes.data.local.division.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import dev.redcom1988.hermes.data.local.access.entity.AccessEntity
import dev.redcom1988.hermes.data.local.access.toDomain
import dev.redcom1988.hermes.data.local.division.toDomain
import dev.redcom1988.hermes.domain.division.DivisionWithAccess

data class DivisionWithAccesses(
    @Embedded val division: DivisionEntity,
    @Relation(
        parentColumn = "divisionId",
        entityColumn = "accessId",
        associateBy = Junction(DivisionAccessCrossRef::class)
    )
    val accesses: List<AccessEntity>
)

fun DivisionWithAccesses.toDomain(): DivisionWithAccess {
    return DivisionWithAccess(
        division = this.division.toDomain(),
        accesses = this.accesses.map { it.toDomain() }
    )
}