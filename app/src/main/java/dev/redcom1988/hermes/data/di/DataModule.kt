package dev.redcom1988.hermes.data.di

import androidx.room.Room
import dev.redcom1988.hermes.BuildConfig
import dev.redcom1988.hermes.data.auth.datasource.AnonymousAuthDataSource
import dev.redcom1988.hermes.data.auth.datasource.BasicAuthDataSource
import dev.redcom1988.hermes.data.auth.datasource.GoogleAuthDataSource
import dev.redcom1988.hermes.data.auth.repository.AuthRepositoryImpl
import dev.redcom1988.hermes.data.backup.BackupRepositoryImpl
import dev.redcom1988.hermes.data.backup.BackupService
import dev.redcom1988.hermes.data.local.HermesDatabase
import dev.redcom1988.hermes.data.local.category.dao.CategoryDao
import dev.redcom1988.hermes.data.local.category.repository.CategoryRepositoryImpl
import dev.redcom1988.hermes.data.local.subscription.dao.NotificationDao
import dev.redcom1988.hermes.data.local.subscription.dao.SubscriptionDao
import dev.redcom1988.hermes.data.local.subscription.repository.SubscriptionRepositoryImpl
import dev.redcom1988.hermes.domain.auth.datasource.AuthDataSource
import dev.redcom1988.hermes.domain.auth.AuthRepository
import dev.redcom1988.hermes.domain.backup.repository.BackupRepository
import dev.redcom1988.hermes.domain.category.repository.CategoryRepository
import dev.redcom1988.hermes.domain.subscription.repository.SubscriptionRepository
import io.minio.MinioClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
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
    single<CategoryDao> { get<HermesDatabase>().categoryDao() }
    single<NotificationDao> { get<HermesDatabase>().notificationDao() }
    single<SubscriptionDao> { get<HermesDatabase>().subscriptionDao() }


    // repositories
    single<AuthRepository> {
        AuthRepositoryImpl(
            context = androidContext(),
            anonymousAuthDataSource = get<AuthDataSource>(named("anonymous")),
            basicAuthDataSource = get<AuthDataSource>(named("basic")),
            googleAuthDataSource = get<AuthDataSource>(named("google")),
            database = get<HermesDatabase>(),
        )
    }
    single<CategoryRepository> {
        CategoryRepositoryImpl(
            categoryDao = get(),
            subscriptionDao = get(),
        )
    }
    single<SubscriptionRepository> {
        SubscriptionRepositoryImpl(
            subscriptionDao = get(),
            notificationDao = get(),
        )
    }
}