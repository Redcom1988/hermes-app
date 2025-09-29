package dev.redcom1988.hermes.data.di

import androidx.room.Room
import dev.redcom1988.hermes.BuildConfig
import dev.redcom1988.hermes.domain.auth.AuthRepository
import dev.redcom1988.hermes.data.local.auth.AuthRepositoryImpl
import dev.redcom1988.hermes.data.local.HermesDatabase
import dev.redcom1988.hermes.data.local.account_data.AccountRepositoryImpl
import dev.redcom1988.hermes.data.local.account_data.DivisionRepositoryImpl
import dev.redcom1988.hermes.data.local.account_data.EmployeeRepositoryImpl
import dev.redcom1988.hermes.data.local.account_data.UserRepositoryImpl
import dev.redcom1988.hermes.data.local.account_data.dao.AccessDao
import dev.redcom1988.hermes.data.local.attendance.AttendanceDao
import dev.redcom1988.hermes.data.local.attendance.AttendanceRepositoryImpl
import dev.redcom1988.hermes.data.local.client.ClientDao
import dev.redcom1988.hermes.data.local.client.ClientRepositoryImpl
import dev.redcom1988.hermes.data.local.account_data.dao.DivisionDao
import dev.redcom1988.hermes.data.local.account_data.dao.EmployeeDao
import dev.redcom1988.hermes.data.local.meeting.MeetingDao
import dev.redcom1988.hermes.data.local.meeting.MeetingRepositoryImpl
import dev.redcom1988.hermes.data.local.service.ServiceDao
import dev.redcom1988.hermes.data.local.service.ServiceRepositoryImpl
import dev.redcom1988.hermes.data.local.task.TaskDao
import dev.redcom1988.hermes.data.local.task.TaskRepositoryImpl
import dev.redcom1988.hermes.data.local.account_data.dao.UserDao
import dev.redcom1988.hermes.data.local.attendance.AttendanceTaskDao
import dev.redcom1988.hermes.data.local.meeting.MeetingClientDao
import dev.redcom1988.hermes.data.local.meeting.MeetingUserDao
import dev.redcom1988.hermes.data.local.task.EmployeeTaskDao
import dev.redcom1988.hermes.data.local.workhour_plan.WorkhourPlanDao
import dev.redcom1988.hermes.data.local.workhour_plan.WorkhourPlanRepositoryImpl
import dev.redcom1988.hermes.data.remote.api.AccountApi
import dev.redcom1988.hermes.data.remote.api.AttendanceApi
import dev.redcom1988.hermes.data.remote.api.AuthApi
import dev.redcom1988.hermes.data.remote.api.BulkSyncApi
import dev.redcom1988.hermes.data.remote.api.ClientApi
import dev.redcom1988.hermes.data.remote.api.MeetingApi
import dev.redcom1988.hermes.data.remote.api.ServiceApi
import dev.redcom1988.hermes.data.remote.api.TaskApi
import dev.redcom1988.hermes.data.remote.api.WorkhourPlanApi
import dev.redcom1988.hermes.data.remote.api.impl.AccountApiImpl
import dev.redcom1988.hermes.data.remote.api.impl.AttendanceApiImpl
import dev.redcom1988.hermes.data.remote.api.impl.AuthApiImpl
import dev.redcom1988.hermes.data.remote.api.impl.BulkSyncApiImpl
import dev.redcom1988.hermes.data.remote.api.impl.ClientApiImpl
import dev.redcom1988.hermes.data.remote.api.impl.MeetingApiImpl
import dev.redcom1988.hermes.data.remote.api.impl.ServiceApiImpl
import dev.redcom1988.hermes.data.remote.api.impl.TaskApiImpl
import dev.redcom1988.hermes.data.remote.api.impl.WorkhourPlanApiImpl
import dev.redcom1988.hermes.data.sync.SyncRepositoryImpl
import dev.redcom1988.hermes.domain.account_data.AccountRepository
import dev.redcom1988.hermes.domain.account_data.DivisionRepository
import dev.redcom1988.hermes.domain.account_data.EmployeeRepository
import dev.redcom1988.hermes.domain.account_data.UserRepository
import dev.redcom1988.hermes.domain.attendance.AttendanceRepository
import dev.redcom1988.hermes.domain.auth.SyncRepository
import dev.redcom1988.hermes.domain.client.ClientRepository
import dev.redcom1988.hermes.domain.meeting.MeetingRepository
import dev.redcom1988.hermes.domain.service.ServiceRepository
import dev.redcom1988.hermes.domain.task.TaskRepository
import dev.redcom1988.hermes.domain.workhour_plan.WorkhourPlanRepository
import io.minio.MinioClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    // minio
    single {
        MinioClient.builder()
            .endpoint(BuildConfig.MINIO_ENDPOINT)
            .credentials(
                BuildConfig.MINIO_ACCESS_KEY,
                BuildConfig.MINIO_SECRET_KEY
            )
            .build()
    }

    // database
    single<HermesDatabase> {
        Room
            .databaseBuilder(
                androidContext(),
                HermesDatabase::class.java,
                "hermes_database"
            )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    // dao
    single<AccessDao> { get<HermesDatabase>().accessDao() }
    single<AttendanceDao> { get<HermesDatabase>().attendanceDao() }
    single<AttendanceTaskDao> { get<HermesDatabase>().attendanceTaskDao() }
    single<ClientDao> { get<HermesDatabase>().clientDao() }
    single<DivisionDao> { get<HermesDatabase>().divisionDao() }
    single<EmployeeDao> { get<HermesDatabase>().employeeDao() }
    single<EmployeeTaskDao> { get<HermesDatabase>().employeeTaskDao() }
    single<MeetingDao> { get<HermesDatabase>().meetingDao() }
    single<MeetingClientDao> { get<HermesDatabase>().meetingClientDao() }
    single<MeetingUserDao> { get<HermesDatabase>().meetingUserDao() }
    single<ServiceDao> { get<HermesDatabase>().serviceDao() }
    single<TaskDao> { get<HermesDatabase>().taskDao() }
    single<UserDao> { get<HermesDatabase>().userDao() }
    single<WorkhourPlanDao> { get<HermesDatabase>().workhourPlanDao() }

    // apis
    single<AuthApi> {
        AuthApiImpl(
            networkHelper = get()
        )
    }

    single<AccountApi> {
        AccountApiImpl(
            networkHelper = get()
        )
    }

    single<AttendanceApi> {
        AttendanceApiImpl(
            networkHelper = get()
        )
    }

    single<ClientApi> {
        ClientApiImpl(
            networkHelper = get()
        )
    }

    single<MeetingApi> {
        MeetingApiImpl(
            networkHelper = get()
        )
    }

    single<ServiceApi> {
        ServiceApiImpl(
            networkHelper = get()
        )
    }

    single<TaskApi> {
        TaskApiImpl(
            networkHelper = get()
        )
    }

    single<WorkhourPlanApi> {
        WorkhourPlanApiImpl(
            networkHelper = get()
        )
    }

    single<BulkSyncApi> {
        BulkSyncApiImpl(
            networkHelper = get()
        )
    }

    // repositories
    single<SyncRepository> {
        SyncRepositoryImpl(
            api = get(),
            userPreference = get(),
            db = get()
        )
    }

    single<AuthRepository> {
        AuthRepositoryImpl(
            authApi = get(),
            syncRepository = get(),
            authPreference = get(),
            userPreference = get(),
            db = get()
        )
    }

    single<AccountRepository> {
        AccountRepositoryImpl(
            userDao = get(),
            employeeDao = get(),
            divisionDao = get(),
            accessDao = get(),
            api = get()
        )
    }

    single<DivisionRepository> {
        DivisionRepositoryImpl(
            divisionDao = get(),
        )
    }

    single<EmployeeRepository> {
        EmployeeRepositoryImpl(
            employeeDao = get(),
        )
    }

    single<UserRepository> {
        UserRepositoryImpl(
            userDao = get(),
        )
    }

    single<AttendanceRepository> {
        AttendanceRepositoryImpl(
            attendanceDao = get(),
            api = get(),
            attendanceTaskDao = get()
        )
    }

    single<ClientRepository> {
        ClientRepositoryImpl(
            clientDao = get(),
            serviceDao = get(),
            api = get()
        )
    }

    single<MeetingRepository> {
        MeetingRepositoryImpl(
            meetingDao = get(),
            meetingClientDao = get(),
            meetingUserDao = get(),
            api = get()
        )
    }

    single<ServiceRepository> {
        ServiceRepositoryImpl(
            serviceDao = get(),
            api = get()
        )
    }

    single<TaskRepository> {
        TaskRepositoryImpl(
            taskDao = get(),
            employeeTaskDao = get()
        )
    }

    single<WorkhourPlanRepository> {
        WorkhourPlanRepositoryImpl(
            workhourPlanDao = get(),
            api = get()
        )
    }

}