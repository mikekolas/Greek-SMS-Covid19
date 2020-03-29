package com.example.govsmscovid19

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsMessage
import android.widget.Toast


class SMSReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val extras = intent.extras

        if (extras != null) {
            val sms = extras.get("pdus") as Array<*>
            for (i in sms.indices) {
                val format = extras.getString("format")
                val smsMessage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    SmsMessage.createFromPdu(sms[i] as ByteArray, format)
                } else {
                    SmsMessage.createFromPdu(sms[i] as ByteArray)
                }
                val phoneNumber = smsMessage.originatingAddress
                val messageText = smsMessage.messageBody.toString()

                if (phoneNumber == GOV_NUMBER_TO_SEND) {
                    Toast.makeText(
                        context,
                        "phoneNumber: $phoneNumber\nmessageText: $messageText",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
    }
}
