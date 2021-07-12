package net.centralabastos.ashgrim

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import net.centralabastos.ashgrim.datos.AppDatabase
import net.centralabastos.ashgrim.datos.Repositorio

class PreciosApplication:Application() {
     private val applicationScope= CoroutineScope(SupervisorJob())
     val repositorio by lazy { Repositorio(database.precioDao()) }
    private val database by lazy { AppDatabase.getBase(this,applicationScope) }

    private val CHANNEL_ID="net.centralabastos.ashgrim"
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(applicationContext) {}
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE ) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}