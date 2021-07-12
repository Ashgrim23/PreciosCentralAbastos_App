package net.centralabastos.ashgrim.worker


import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import net.centralabastos.ashgrim.MainActivity
import net.centralabastos.ashgrim.PreciosApplication
import net.centralabastos.ashgrim.R
import java.lang.Exception

class PreciosWorker(context: Context, workerParams:WorkerParameters ):
    CoroutineWorker(context,workerParams)  {
    private val CHANNEL_ID="net.centralabastos.ashgrim"

    val app: PreciosApplication get()=applicationContext as PreciosApplication

    override suspend fun doWork(): Result {
        val app=(applicationContext as PreciosApplication)

        try {

            if (app.repositorio.loadPrecios()) {
                val intent= Intent(app,MainActivity::class.java).apply {
                    flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val pendingIntent:PendingIntent = PendingIntent.getActivity(app,0,intent,0)

                val builder=NotificationCompat.Builder(app,CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.ic_dialog_info )
                    .setContentTitle(app.getString(R.string.string_notification_title))
                    .setContentText(app.getString(R.string.string_notification_content))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)

                with(NotificationManagerCompat.from(app)) {
                    notify(0,builder.build())
                }
            }


            return Result.success()
        } catch (e: Exception) {
            Log.e("daff",e.stackTraceToString())
            return Result.retry()
        }
    }


}