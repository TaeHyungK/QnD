package org.sopt21.qnd.LockScreen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val i = Intent(context, ScreenService::class.java)
            context.startService(i)
        }
    }
}
