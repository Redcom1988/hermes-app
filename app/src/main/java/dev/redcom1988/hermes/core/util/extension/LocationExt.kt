package dev.redcom1988.hermes.core.util.extension

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@SuppressLint("MissingPermission")
suspend fun getCurrentLocation(context: Context): Pair<Double, Double>? {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    return suspendCancellableCoroutine { cont ->
        val token = CancellationTokenSource()

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            token.token
        ).addOnSuccessListener { location ->
            if (location != null) {
                cont.resume(Pair(location.latitude, location.longitude))
            } else {
                cont.resume(null)
            }
        }.addOnFailureListener {
            cont.resume(null)
        }

        cont.invokeOnCancellation {
            token.cancel()
        }
    }
}

private val TEST_LOCATION = Pair(-8.781, 115.186) // Denpasar Location
private val OFFICE_LOCATION = Pair(-8.662, 115.214) // Asanka Location
private const val VICINITY_RADIUS_METERS = 100f


suspend fun isInOfficeLocation(
    context: Context,
    officeLatitude: Double = OFFICE_LOCATION.first,
    officeLongitude: Double = OFFICE_LOCATION.second,
    radiusInMeters: Float = VICINITY_RADIUS_METERS
): Boolean {
    val currentLocation = getCurrentLocation(context) ?: return false
    val results = FloatArray(1)

    android.location.Location.distanceBetween(
        currentLocation.first,
        currentLocation.second,
        officeLatitude,
        officeLongitude,
        results
    )

    return results[0] <= radiusInMeters
}
