package com.smartwareafrica.smsprinter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val smsReceived = "android.provider.Telephony.SMS_RECEIVED";
            val tag = "SMSBroadcastReceiver";

            Log.i(tag, "Intent received: " + intent!!.action)

            if (intent.action === smsReceived) {
                val bundle = intent.extras
                if (bundle != null) {
                    val pdus = bundle["pdus"] as Array<*>?
                    val messages: Array<SmsMessage?> = arrayOfNulls(pdus!!.size)
                    for (i in pdus.indices) {
                        messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                    }
                    if (messages.size > -1) {
                        Log.i(tag, "Message received: " + (messages[0]?.messageBody))
                    }
                }
            }
        }
    }

}
