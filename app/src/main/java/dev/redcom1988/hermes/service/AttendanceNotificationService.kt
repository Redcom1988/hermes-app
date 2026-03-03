package dev.redcom1988.hermes.service

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import dev.redcom1988.hermes.R
import dev.redcom1988.hermes.core.notification.NotificationHelper
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.data.local.auth.UserPreference
import dev.redcom1988.hermes.domain.account_data.DivisionRepository
import dev.redcom1988.hermes.domain.attendance.AttendanceRepository
import dev.redcom1988.hermes.receiver.AttendanceNotificationReceiver
import dev.redcom1988.hermes.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Foreground service that shows a persistent notification while user is checked in.
 * Updates elapsed time every minute and provides a "Check Out" action button.
 */
class AttendanceNotificationService : Service() {

    private val attendanceRepository: AttendanceRepository by injectLazy()
    private val divisionRepository: DivisionRepository by injectLazy()
    private val userPreference: UserPreference by injectLazy()
    private val notificationHelper by lazy { NotificationHelper(this) }
    
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var employeeId: Int = -1
    
    companion object {
        private const val TAG = "AttendanceNotifService"
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "attendance_tracking"
        
        private const val EXTRA_EMPLOYEE_ID = "employee_id"
        private const val ACTION_STOP_SERVICE = "stop_service"
        
        /**
         * Start the attendance notification service
         */
        fun start(context: Context, employeeId: Int) {
            val intent = Intent(context, AttendanceNotificationService::class.java).apply {
                putExtra(EXTRA_EMPLOYEE_ID, employeeId)
            }
            context.startForegroundService(intent)
            Log.d(TAG, "Service start requested for employee: $employeeId")
        }
        
        /**
         * Stop the attendance notification service
         */
        fun stop(context: Context) {
            val intent = Intent(context, AttendanceNotificationService::class.java).apply {
                action = ACTION_STOP_SERVICE
            }
            context.startService(intent)
            Log.d(TAG, "Service stop requested")
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand called")
        
        // Handle stop action
        if (intent?.action == ACTION_STOP_SERVICE) {
            Log.d(TAG, "Stop action received")
            stopForegroundService()
            return START_NOT_STICKY
        }
        
        // Get employee ID from intent
        employeeId = intent?.getIntExtra(EXTRA_EMPLOYEE_ID, -1) ?: -1
        
        if (employeeId == -1) {
            Log.e(TAG, "Invalid employee ID, stopping service")
            stopSelf()
            return START_NOT_STICKY
        }
        
        Log.d(TAG, "Starting foreground service for employee: $employeeId")
        
        // Create initial notification and start foreground
        try {
            val notification = createNotification(
                timeText = "Calculating...",
                todayTotalHours = 0,
                requiredHours = 8
            )
            
            startForeground(NOTIFICATION_ID, notification)
            Log.d(TAG, "Foreground service started successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start foreground service", e)
            throw e
        }
        
        // Start monitoring and updating notification
        startMonitoring()
        
        return START_STICKY // Service will restart if killed by system
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        Log.d(TAG, "Service destroyed")
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun startMonitoring() {
        serviceScope.launch {
            // Wait a bit before first update to let foreground service initialize
            delay(2_000)
            
            while (isActive) {
                try {
                    updateNotification()
                    checkForDayChange()
                } catch (e: Exception) {
                    Log.e(TAG, "Error in monitoring loop", e)
                }
                
                // Update every minute
                delay(60_000)
            }
        }
    }

    private suspend fun updateNotification() {
        try {
            val activeAttendance = attendanceRepository.observeActiveAttendanceForEmployee(employeeId).firstOrNull()
            
            if (activeAttendance == null) {
                Log.d(TAG, "No active attendance found, stopping service")
                stopForegroundService()
                return
            }
            
            // Calculate elapsed time
            val startTime = parseDateTime(activeAttendance.startTime)
            val now = LocalDateTime.now()
            val duration = Duration.between(startTime, now)
            val totalMinutes = duration.toMinutes()
            
            val hours = totalMinutes / 60
            val mins = totalMinutes % 60
            val timeText = if (hours > 0) {
                "${hours}h ${mins}m"
            } else {
                "${mins}m"
            }
            
            // Get today's total work hours and required hours
            val todayTotalMinutes = getTodayTotalWorkMinutes()
            val todayTotalHours = todayTotalMinutes / 60
            val requiredHours = getRequiredWorkHours()
            
            // Update notification
            val notification = createNotification(
                timeText = timeText,
                todayTotalHours = todayTotalHours.toInt(),
                requiredHours = requiredHours
            )
            notificationHelper.notify(NOTIFICATION_ID, notification)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error updating notification", e)
        }
    }

    private suspend fun checkForDayChange() {
        try {
            val activeAttendance = attendanceRepository.observeActiveAttendanceForEmployee(employeeId).firstOrNull()
            
            if (activeAttendance != null) {
                val startTime = parseDateTime(activeAttendance.startTime)
                val now = LocalDateTime.now()
                
                // Check if it's a different day
                if (startTime.toLocalDate() != now.toLocalDate()) {
                    Log.d(TAG, "Day change detected, auto-finishing attendance")
                    
                    // Calculate midnight of start day
                    val midnight = startTime.toLocalDate().plusDays(1).atStartOfDay()
                    
                    // Auto check-out at midnight
                    attendanceRepository.finishAttendance(
                        employeeId = employeeId,
                        endTime = formatDateTime(midnight),
                        taskIds = emptyList()
                    )
                    
                    // Service will stop on next update when no active attendance is found
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking for day change", e)
        }
    }

    private fun createNotification(
        timeText: String,
        todayTotalHours: Int,
        requiredHours: Int
    ): android.app.Notification {
        // Create intent to open app to attendance screen
        val openAppIntent = notificationHelper.createActivityPendingIntent(
            requestCode = 100,
            activityClass = MainActivity::class.java,
            context = this
        )
        
        // Create "Check Out" action intent
        val checkOutIntent = Intent(this, AttendanceNotificationReceiver::class.java).apply {
            action = AttendanceNotificationReceiver.ACTION_CHECK_OUT
            putExtra(AttendanceNotificationReceiver.EXTRA_EMPLOYEE_ID, employeeId)
        }
        val checkOutPendingIntent = PendingIntent.getBroadcast(
            this,
            101,
            checkOutIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val checkOutAction = NotificationCompat.Action.Builder(
            0, // No icon
            "Check Out",
            checkOutPendingIntent
        ).build()
        
        // Build notification title and text
        val title = "🕐 Attendance Active"
        val text = "Session: $timeText\nToday: ${todayTotalHours}h / ${requiredHours}h required"
        
        return notificationHelper.createNotification(
            channelId = CHANNEL_ID,
            title = title,
            text = text,
            smallIconResId = R.mipmap.ic_launcher,
            context = this,
            pendingIntent = openAppIntent,
            onGoing = true,
            autoCancel = false,
            priority = NotificationCompat.PRIORITY_DEFAULT,
            actions = listOf(checkOutAction)
        )
    }

    private fun stopForegroundService() {
        Log.d(TAG, "Stopping foreground service")
        serviceScope.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
    
    private suspend fun getTodayTotalWorkMinutes(): Long {
        return try {
            val today = LocalDate.now()
            val allAttendances = attendanceRepository.getVisibleAttendances().firstOrNull() ?: emptyList()
            
            val todayAttendances = allAttendances.filter { attendance ->
                attendance.employeeId == employeeId && runCatching {
                    parseDateTime(attendance.createdAt).toLocalDate() == today
                }.getOrDefault(false)
            }
            
            var totalMinutes = 0L
            todayAttendances.forEach { attendance ->
                val start = parseDateTime(attendance.startTime)
                val end = if (attendance.endTime != null) {
                    parseDateTime(attendance.endTime)
                } else {
                    LocalDateTime.now() // Current time for active attendance
                }
                
                val minutes = Duration.between(start, end).toMinutes()
                totalMinutes += minutes
            }
            
            totalMinutes
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating today's total work minutes", e)
            0L
        }
    }
    
    private suspend fun getRequiredWorkHours(): Int {
        return try {
            val divisionName = userPreference.divisionType().get()
            divisionRepository.getDivisionWorkHoursByName(divisionName) ?: 8
        } catch (e: Exception) {
            Log.e(TAG, "Error getting required work hours", e)
            8 // Default to 8 hours
        }
    }

    private fun parseDateTime(dateTimeString: String): LocalDateTime {
        return try {
            // Try parsing ISO format first (e.g., "2024-01-15T08:30:00Z")
            LocalDateTime.parse(dateTimeString.replace("Z", ""), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        } catch (e: Exception) {
            try {
                // Try parsing with custom format
                LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            } catch (e2: Exception) {
                Log.e(TAG, "Error parsing datetime: $dateTimeString", e2)
                LocalDateTime.now()
            }
        }
    }

    private fun formatDateTime(dateTime: LocalDateTime): String {
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z"
    }
}
