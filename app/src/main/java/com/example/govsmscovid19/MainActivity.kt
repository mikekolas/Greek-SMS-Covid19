package com.example.govsmscovid19

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.telephony.SmsManager
import android.util.Log
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private var numberToSend: String = "13033"
    private var textToSend: String = ""
    private val SMS_REQUEST_CODE = 101
    private var interval: Long = 5000

    private var permissions: Array<String> = arrayOf(
        android.Manifest.permission.SEND_SMS,
        android.Manifest.permission.RECEIVE_SMS
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadData()

        settingsBtn.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        infoBtn.setOnClickListener {
            val intent = Intent(this, InfoActivity::class.java)
            startActivity(intent)
        }

        sendSMSbtn.setOnClickListener {
            setupPermissions()
        }
    }

    private fun loadData() {
        try {
            val file = File(getExternalFilesDir(null).absolutePath, "GovSMSCovid19data.txt")
            if (file.exists()) {
                val fi = FileInputStream(file)
                val br = BufferedReader(InputStreamReader(fi))
                val stringBuilder = StringBuilder()
                var text: String? = null
                while ({ text = br.readLine(); text }() != null) {
                    stringBuilder.append(text)
                }
                val str = stringBuilder.split(":").toTypedArray()
                textToSend = str[0] + " " + str[1]
                println(textToSend)
                fi.close()
            } else {
                println("File doesn't exist")
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }  catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    /*
    * Function to setup permissions for the usage of SMS
    *
    * */
    private fun setupPermissions() {
        var flag = false
        for (permission in permissions) {
            val per = ContextCompat.checkSelfPermission(this, permission)
            if (per != PackageManager.PERMISSION_GRANTED) flag = true
        }
        if (flag) requestPermission(permissions)
        for (permission in permissions) {
            val per = ContextCompat.checkSelfPermission(this, permission)
            if (per != PackageManager.PERMISSION_GRANTED)
            {
                Log.i("DemoPERM", "Permission denied on $permission")
                println(ActivityCompat.shouldShowRequestPermissionRationale(this, permission))
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    val builder = AlertDialog.Builder(this)
                    if (permission == permissions[0]) {
                        builder.setMessage("Xρειάζεται εξουσιοδότηση αποστολής SMS η εφαρμογή για την λειτουργία της.")
                            .setTitle("Εξουσιοδότηση SMS")
                    } else if(permission == permissions[1]) {
                        builder.setMessage("Xρειάζεται εξουσιοδότηση λήψης SMS η εφαρμογή για την λειτουργία της.")
                            .setTitle("Εξουσιοδότηση SMS")
                    }

                    builder.setPositiveButton("Ok") {
                            _, _ ->
                    }
                    val dialog = builder.create()
                    dialog.show()
                }
            } else {
                //Run only if the user wants to send an sms
                if (permission == permissions[0]) {
//                    requestPermission(permission)
                    val id = radioGroup.checkedRadioButtonId
                    if (id != -1) {
                        disableButton()
                        Handler().postDelayed({
                            enableButton()
                        }, interval)
                        val option = findViewById<RadioButton>(id)
                        textToSend = option.text.substring(0, 1).plus(" ").plus(textToSend)
                        SmsManager.getDefault()
                            .sendTextMessage(numberToSend, null, textToSend, null, null)
                    } else {
                        val builder = AlertDialog.Builder(this)
                        builder.setMessage("Πρέπει να επιλέξετε πρώτα μία από τις επιλογές")
                            .setTitle("Επιλογή μετακίνησης")
                        builder.setPositiveButton("Ok") { _, _ -> }
                        val dialog = builder.create()
                        dialog.show()
                    }
                }
            }
        }
    }

    private fun requestPermission(permissions: Array<String>) {
        ActivityCompat.requestPermissions(this, permissions, SMS_REQUEST_CODE)
    }

    private fun disableButton() {
        sendSMSbtn.isClickable = false
        sendSMSbtn.setBackgroundResource(R.drawable.btn_disabled)
    }

    private fun enableButton() {
        sendSMSbtn.isClickable = true
        sendSMSbtn.setBackgroundResource(R.drawable.btn_rounded)
    }
}
