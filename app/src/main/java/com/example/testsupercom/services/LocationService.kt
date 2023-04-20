package com.example.testsupercom.services

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.testsupercom.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import com.example.testsupercom.R
import org.jetbrains.anko.doAsync
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Inject


private val NOTIFICATION_ID = 1
private val CHANNEL_ID = "LocationServiceChannel"

@AndroidEntryPoint
class LocationService : Service() {


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        startService()
        return START_STICKY
    }

    private fun startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Location Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun startLocationUpdates() {

    }



    private fun stopService() {
        stopForeground(true)
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        // Create a NotificationCompat.Builder object to build the notification
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Location Service")
            .setContentText("Running...")
            .setSmallIcon(R.drawable.baseline_my_location_24)
            .setContentIntent(pendingIntent)
            .setOngoing(true)

        // Return the built notification
        return builder.build()
    }


}
