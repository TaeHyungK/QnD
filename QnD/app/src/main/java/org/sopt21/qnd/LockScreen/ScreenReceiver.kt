package org.sopt21.qnd.LockScreen

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.widget.Toast

class ScreenReceiver : BroadcastReceiver() {

    private var telephonyManager: TelephonyManager? = null
    private var isPhoneldle = true

    private val phoneStateListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            when (state) {
                TelephonyManager.CALL_STATE_IDLE  // 아무 행동이 없는 상태
                -> isPhoneldle = true
                TelephonyManager.CALL_STATE_RINGING // 벨소리
                -> isPhoneldle = false
                TelephonyManager.CALL_STATE_OFFHOOK // 전화를 걸거나, 전화중인 상태
                -> isPhoneldle = false
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == Intent.ACTION_SCREEN_OFF) {
            if (telephonyManager == null) {
                telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                telephonyManager!!.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
            }

            if (isPhoneldle) {
                val i = Intent(context, LockScreenActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(i)
            }

        }
    }
}