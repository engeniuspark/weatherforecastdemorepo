package com.snapwork.weatherapp.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class LocationTracker @Inject constructor(
    private val locationClient: FusedLocationProviderClient,
    @ApplicationContext private val context: Context
) {

    suspend fun getCurrentLocation(): Location? {
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!hasCoarseLocationPermission && !hasFineLocationPermission) {
            return null
        }

        if (!isGpsEnabled) {
            return null
        }

        // 1. Try to get cached last location first for speed
        val cachedLocation = suspendCancellableCoroutine<Location?> { cont ->
            try {
                locationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        cont.resume(location)
                    }
                    .addOnFailureListener {
                        cont.resume(null)
                    }
                    .addOnCanceledListener {
                        cont.resume(null)
                    }
            } catch (e: SecurityException) {
                cont.resume(null)
            }
        }

        if (cachedLocation != null) {
            return cachedLocation
        }

        // 2. If cached location is null (common on first launch after granting permission), request fresh location
        return suspendCancellableCoroutine { cont ->
            try {
                val priority = if (hasFineLocationPermission) {
                    Priority.PRIORITY_HIGH_ACCURACY
                } else {
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY
                }

                val cts = CancellationTokenSource()

                locationClient.getCurrentLocation(priority, cts.token)
                    .addOnSuccessListener { location: Location? ->
                        cont.resume(location)
                    }
                    .addOnFailureListener {
                        cont.resume(null)
                    }
                    .addOnCanceledListener {
                        cont.resume(null)
                    }

                cont.invokeOnCancellation {
                    cts.cancel()
                }
            } catch (e: SecurityException) {
                cont.resume(null)
            }
        }
    }
}
