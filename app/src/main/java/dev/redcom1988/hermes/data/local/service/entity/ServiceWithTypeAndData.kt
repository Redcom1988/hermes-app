package dev.redcom1988.hermes.data.local.service.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ServiceWithTypeAndData(
    @Embedded val service: ServiceEntity,

    @Relation(
        parentColumn = "serviceTypeId",
        entityColumn = "serviceTypeId",
        entity = ServiceTypeEntity::class
    )
    val serviceType: ServiceTypeEntity,

    @Relation(
        entity = ServiceTypeFieldEntity::class,
        parentColumn = "serviceTypeId",
        entityColumn = "serviceTypeId"
    )
    val fields: List<FieldWithValue>
)
