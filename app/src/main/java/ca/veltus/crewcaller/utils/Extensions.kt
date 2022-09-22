package ca.veltus.crewcaller.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.veltus.crewcaller.base.BaseRecyclerViewAdapter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


const val HOUR_IN_MILLISECONDS = 3600000
const val DAY_IN_MILLISECONDS = 86400000
const val EIGHT_HOURS_IN_MILLISECONDS = 28800000
const val TWELVE_HALF_HOURS_IN_MILLISECONDS = 43200000

// Extension function to setup the RecyclerView
fun <T> RecyclerView.setup(
    adapter: BaseRecyclerViewAdapter<T>, isGridLayout: Boolean = false
) {
    this.apply {
        layoutManager = if (isGridLayout) {
            GridLayoutManager(this.context, 7)

        } else {
            LinearLayoutManager(this.context)
        }
        this.adapter = adapter
    }
}

// Animate changing the view visibility
fun View.fadeIn() {
    this.visibility = View.VISIBLE
    this.alpha = 0f
    this.animate().alpha(1f).setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            this@fadeIn.alpha = 1f
        }
    })
}

// Animate changing the view visibility
fun View.fadeOut() {
    this.animate().alpha(0f).setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            this@fadeOut.alpha = 1f
            this@fadeOut.visibility = View.GONE
        }
    })
}


fun calculateTimeDifferences(start: String, end: String): Long {
    var difference: Long?
    val format = SimpleDateFormat("HH:mm", Locale.US)
    val startDate: Date = format.parse(start) as Date
    val endDate: Date = format.parse(end) as Date

    difference = if (endDate.before(startDate) || endDate == startDate) {
        DAY_IN_MILLISECONDS - startDate.time + endDate.time

    } else {
        endDate.time - startDate.time
    }

    return difference
}

fun convertHourRateToMilliseconds(hourRate: String): Double {
    return (hourRate.toDouble() / HOUR_IN_MILLISECONDS)
}

fun convertMillisecondsToHours(vararg milliseconds: Int): String {
    var sum = 0
    for (millisecond in milliseconds) sum += millisecond

    val hours = TimeUnit.MILLISECONDS.toHours(sum.toLong())
    val minutes = TimeUnit.MILLISECONDS.toMinutes(sum.toLong()) % 60

    return ("$hours Hours\n$minutes Minutes")
}

fun getTimeStringFromLong(time: Long): String {
    val seconds = (time / 1000) % 60
    val minutes = (time / (1000 * 60) % 60)
    val hours = (time / (1000 * 60 * 60) % 24)

    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

fun isLocationPermissionsEnabled(context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(
        context,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        context,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

}

fun isLocationEnabled(activity: Activity): Boolean {
    val locationManager: LocationManager =
        activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
        LocationManager.NETWORK_PROVIDER
    )
}

fun calculateEarnings(
    workRate: String,
    timeWorked: Long? = null,
    startTime: String? = null,
    endTime: String? = null
): Pair<Double, String> {
    var millisecondsWorked: Long = timeWorked ?: 0
    var overtimeAndHalfPay: Double = 0.0
    var overtimeDoublePay: Double = 0.0
    var regularPay: Double = 0.0
    var overtimeAndHalfHours: Long = 0
    var overtimeDoubleHours: Long = 0

    if (!startTime.isNullOrEmpty() && !endTime.isNullOrEmpty()) {
        millisecondsWorked = calculateTimeDifferences(startTime, endTime)

    }

    regularPay = convertHourRateToMilliseconds(workRate)

    if (millisecondsWorked > EIGHT_HOURS_IN_MILLISECONDS) {
        overtimeAndHalfHours = millisecondsWorked - EIGHT_HOURS_IN_MILLISECONDS
        overtimeAndHalfPay = overtimeAndHalfHours * (1.5 * (regularPay))

    }
    if (millisecondsWorked > TWELVE_HALF_HOURS_IN_MILLISECONDS) {
        overtimeDoubleHours = millisecondsWorked - TWELVE_HALF_HOURS_IN_MILLISECONDS
        overtimeDoublePay = overtimeDoubleHours * ((2 * (regularPay)))

    }

    regularPay = (millisecondsWorked * regularPay)

    val totalPay: Double = regularPay + overtimeAndHalfPay + overtimeDoublePay

    val totalHoursWorked: String = convertMillisecondsToHours(millisecondsWorked.toInt())

    return Pair(totalPay, totalHoursWorked)

}

fun hasNetworkConnection(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Android 10+
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            ?.let { networkCapabilities ->
                return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            }
    } else {
        return connectivityManager.activeNetworkInfo?.isConnectedOrConnecting == true
    }

    return false
}



