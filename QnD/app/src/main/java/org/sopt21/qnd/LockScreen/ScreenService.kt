package org.sopt21.qnd.LockScreen

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder

import org.sopt21.qnd.R

class ScreenService : Service() {

    private var mReceiver: ScreenReceiver? = null

    override fun onBind(intent: Intent): IBinder? {

        return null
    }

    override fun onCreate() {
        super.onCreate()

        mReceiver = ScreenReceiver()
        val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        registerReceiver(mReceiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        startForeground(1, Notification())
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = Notification.Builder(applicationContext)
                .setContentText("")
                .setContentTitle("")
                .setTicker("")
                .setSmallIcon(R.drawable.appicon)
                .setContentIntent(null)
        val notification = builder.build()

        if (intent != null) {
            if (intent.action == null) {
                if (mReceiver == null) {
                    mReceiver = ScreenReceiver()
                    val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
                    registerReceiver(mReceiver, filter)
                }
            }
        }
        return Service.START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        //        mReceiver.reenableKeyguard(); // 기본 잠금화면이 나타남

        if (mReceiver != null) {
            unregisterReceiver(mReceiver)
        }
    }
}
