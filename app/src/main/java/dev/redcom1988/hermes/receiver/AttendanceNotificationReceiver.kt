package dev.redcom1988.hermes.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import dev.redcom1988.hermes.core.util.extension.injectLazy
import dev.redcom1988.hermes.domain.attendance.AttendanceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * BroadcastReceiver that handles notification actions for attendance tracking.
 * Primarily handles the "Check Out" action from the notification.
 */
class AttendanceNotificationReceiver : BroadcastReceiver() {

    private val attendanceRepository: AttendanceRepository by injectLazy()
    private val receiverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        private const val TAG = "AttendanceNotifReceiver"
        const val ACTION_CHECK_OUT = "dev.redcom1988.hermes.ACTION_CHECK_OUT"
        const val EXTRA_EMPLOYEE_ID = "employee_id"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive: action=${intent.action}")

        when (intent.action) {
            ACTION_CHECK_OUT -> {
                val employeeId = intent.getIntExtra(EXTRA_EMPLOYEE_ID, -1)
                if (employeeId != -1) {
                    handleCheckOut(context, employeeId)
                } else {
                    Log.e(TAG, "Invalid employee ID for check out")
                }
            }
        }
    }

    private fun handleCheckOut(context: Context, employeeId: Int) {
        Log.d(TAG, "Handling check out for employee: $employeeId")

        // Use goAsync to allow background work
        val pendingResult = goAsync()

        receiverScope.launch {
            try {
                // Finish attendance
                attendanceRepository.finishAttendance(
                    employeeId = employeeId,
                    endTime = formatDateTime(LocalDateTime.now()),
                    taskIds = emptyList()
                )

                // Stop the foreground service by sending stop intent
                val serviceIntent = Intent(context, Class.forName("dev.redcom1988.hermes.service.AttendanceNotificationService")).apply {
                    action = "stop_service"
                }
                context.startService(serviceIntent)

                // Show success toast on main thread
                launch(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Checked out successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                Log.d(TAG, "Check out completed successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error during check out", e)

                // Show error toast on main thread
                launch(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Failed to check out: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun formatDateTime(dateTime: LocalDateTime): String {
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "Z"
    }
}
