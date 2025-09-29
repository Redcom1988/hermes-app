package dev.redcom1988.hermes.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.redcom1988.hermes.data.local.account_data.dao.AccessDao
import dev.redcom1988.hermes.data.local.account_data.entity.AccessEntity
import dev.redcom1988.hermes.data.local.attendance.AttendanceDao
import dev.redcom1988.hermes.data.local.attendance.entity.AttendanceEntity
import dev.redcom1988.hermes.data.local.attendance.entity.AttendanceTaskCrossRefEntity
import dev.redcom1988.hermes.data.local.client.ClientDao
import dev.redcom1988.hermes.data.local.client.entity.ClientEntity
import dev.redcom1988.hermes.data.local.account_data.dao.DivisionDao
import dev.redcom1988.hermes.data.local.account_data.entity.DivisionAccessCrossRefEntity
import dev.redcom1988.hermes.data.local.account_data.entity.DivisionEntity
import dev.redcom1988.hermes.data.local.account_data.dao.EmployeeDao
import dev.redcom1988.hermes.data.local.account_data.entity.EmployeeEntity
import dev.redcom1988.hermes.data.local.account_data.entity.EmployeeTaskCrossRefEntity
import dev.redcom1988.hermes.data.local.meeting.MeetingDao
import dev.redcom1988.hermes.data.local.meeting.entity.MeetingClientCrossRefEntity
import dev.redcom1988.hermes.data.local.meeting.entity.MeetingEntity
import dev.redcom1988.hermes.data.local.meeting.entity.MeetingUserCrossRefEntity
import dev.redcom1988.hermes.data.local.service.ServiceDao
import dev.redcom1988.hermes.data.local.service.entity.ServiceEntity
import dev.redcom1988.hermes.data.local.service.entity.ServiceTypeDataCrossRefEntity
import dev.redcom1988.hermes.data.local.service.entity.ServiceTypeEntity
import dev.redcom1988.hermes.data.local.service.entity.ServiceTypeFieldEntity
import dev.redcom1988.hermes.data.local.task.TaskDao
import dev.redcom1988.hermes.data.local.task.entity.TaskEntity
import dev.redcom1988.hermes.data.local.account_data.dao.UserDao
import dev.redcom1988.hermes.data.local.account_data.entity.UserEntity
import dev.redcom1988.hermes.data.local.attendance.AttendanceTaskDao
import dev.redcom1988.hermes.data.local.client.entity.ClientDataEntity
import dev.redcom1988.hermes.data.local.meeting.MeetingClientDao
import dev.redcom1988.hermes.data.local.meeting.MeetingUserDao
import dev.redcom1988.hermes.data.local.task.EmployeeTaskDao
import dev.redcom1988.hermes.data.local.workhour_plan.WorkhourPlanDao
import dev.redcom1988.hermes.data.local.workhour_plan.entity.WorkhourPlanEntity

@Database(
    entities = [
        AccessEntity::class,
        AttendanceEntity::class,
        AttendanceTaskCrossRefEntity::class,
        ClientEntity::class,
        ClientDataEntity::class,
        DivisionEntity::class,
        DivisionAccessCrossRefEntity::class,
        EmployeeEntity::class,
        MeetingEntity::class,
        MeetingUserCrossRefEntity::class,
        MeetingClientCrossRefEntity::class,
        ServiceEntity::class,
        ServiceTypeEntity::class,
        ServiceTypeFieldEntity::class,
        ServiceTypeDataCrossRefEntity::class,
        TaskEntity::class,
        EmployeeTaskCrossRefEntity::class,
        UserEntity::class,
        WorkhourPlanEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class HermesDatabase : RoomDatabase() {
    abstract fun accessDao(): AccessDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun attendanceTaskDao(): AttendanceTaskDao
    abstract fun clientDao(): ClientDao
    abstract fun divisionDao(): DivisionDao
    abstract fun employeeDao(): EmployeeDao
    abstract fun employeeTaskDao(): EmployeeTaskDao
    abstract fun meetingDao(): MeetingDao
    abstract fun meetingClientDao(): MeetingClientDao
    abstract fun meetingUserDao(): MeetingUserDao
    abstract fun serviceDao(): ServiceDao
    abstract fun taskDao(): TaskDao
    abstract fun userDao(): UserDao
    abstract fun workhourPlanDao(): WorkhourPlanDao
}
