package dev.redcom1988.hermes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.redcom1988.hermes.core.util.SyncStatusConverter
import dev.redcom1988.hermes.data.local.access.AccessDao
import dev.redcom1988.hermes.data.local.access.entity.AccessEntity
import dev.redcom1988.hermes.data.local.attendance.AttendanceDao
import dev.redcom1988.hermes.data.local.attendance.entity.AttendanceEntity
import dev.redcom1988.hermes.data.local.attendance.entity.AttendanceTaskCrossRef
import dev.redcom1988.hermes.data.local.client.ClientDao
import dev.redcom1988.hermes.data.local.client.entity.ClientEntity
import dev.redcom1988.hermes.data.local.division.DivisionDao
import dev.redcom1988.hermes.data.local.division.entity.DivisionAccessCrossRef
import dev.redcom1988.hermes.data.local.division.entity.DivisionEntity
import dev.redcom1988.hermes.data.local.employee.EmployeeDao
import dev.redcom1988.hermes.data.local.employee.entity.EmployeeEntity
import dev.redcom1988.hermes.data.local.meeting.MeetingDao
import dev.redcom1988.hermes.data.local.meeting.entity.MeetingClients
import dev.redcom1988.hermes.data.local.meeting.entity.MeetingEntity
import dev.redcom1988.hermes.data.local.meeting.entity.MeetingUsers
import dev.redcom1988.hermes.data.local.service.ServiceDao
import dev.redcom1988.hermes.data.local.service.entity.ServiceEntity
import dev.redcom1988.hermes.data.local.service.entity.ServiceTypeDataEntity
import dev.redcom1988.hermes.data.local.service.entity.ServiceTypeEntity
import dev.redcom1988.hermes.data.local.service.entity.ServiceTypeFieldEntity
import dev.redcom1988.hermes.data.local.task.entity.TaskEntity
import dev.redcom1988.hermes.data.local.task.TaskDao
import dev.redcom1988.hermes.data.local.user.UserDao
import dev.redcom1988.hermes.data.local.user.entity.UserEntity
import dev.redcom1988.hermes.data.local.user.entity.UserTaskCrossRef

@Database(
    entities = [
        AccessEntity::class,
        AttendanceEntity::class,
        AttendanceTaskCrossRef::class,
        ClientEntity::class,
        DivisionEntity::class,
        DivisionAccessCrossRef::class,
        EmployeeEntity::class,
        MeetingEntity::class,
        MeetingUsers::class,
        MeetingClients::class,
        ServiceEntity::class,
        ServiceTypeEntity::class,
        ServiceTypeFieldEntity::class,
        ServiceTypeDataEntity::class,
        TaskEntity::class,
        UserTaskCrossRef::class,
        UserEntity::class,
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(SyncStatusConverter::class)
abstract class HermesDatabase : RoomDatabase() {
    abstract fun accessDao(): AccessDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun clientDao(): ClientDao
    abstract fun divisionDao(): DivisionDao
    abstract fun employeeDao(): EmployeeDao
    abstract fun meetingDao(): MeetingDao
    abstract fun serviceDao(): ServiceDao
    abstract fun taskDao(): TaskDao
    abstract fun userDao(): UserDao
}
