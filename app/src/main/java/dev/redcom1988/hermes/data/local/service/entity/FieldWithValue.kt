package dev.redcom1988.hermes.data.local.service.entity

import androidx.room.Embedded
import androidx.room.Relation

data class FieldWithValue(
    @Embedded val field: ServiceTypeFieldEntity,

    @Relation(
        parentColumn = "fieldId",
        entityColumn = "fieldId",
        associateBy = androidx.room.Junction(
            value = ServiceTypeDataEntity::class,
            parentColumn = "fieldId",
            entityColumn = "fieldId"
        )
    )
    val values: List<ServiceTypeDataEntity>
)
