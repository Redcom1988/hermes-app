package dev.redcom1988.hermes.data.local.meeting.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import dev.redcom1988.hermes.data.local.client.entity.ClientEntity
import dev.redcom1988.hermes.data.local.user.entity.UserEntity

data class MeetingWithUsersAndClients(
    @Embedded val meeting: MeetingEntity,

    @Relation(
        parentColumn = "meetingId",
        entityColumn = "userId",
        entity = UserEntity::class,
    )
    val users: List<UserEntity>,

    @Relation(
        parentColumn = "meetingId",
        entityColumn = "clientId",
        entity = ClientEntity::class,
    )
    val clients: List<ClientEntity>
)