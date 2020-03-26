package com.example.govsmscovid19

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.settings_activity.*
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.android.synthetic.main.activity_info.*
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder


class SettingsActivity : AppCompatActivity() {

    private var fileName: String =  "GovSMSCovid19data.txt"
    private var formaUri: String = "https://forma.gov.gr/"

    private var themeSwitch: Switch? = null
    internal lateinit var sharedPref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPref = SharedPref(this)
        println(sharedPref.loadNightModeState())
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.DarkTheme)
        } else setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        // Set toolbar
        setSupportActionBar(secondaryToolbar2)
        secondaryToolbar2.setNavigationOnClickListener {
            returnBack()
        }

        loadData()
        // Save button actions
        val saveButton = findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener {
            if (nameInput.text.isEmpty() || addressInput.text.isEmpty()) {
                val builder: AlertDialog.Builder? = this@SettingsActivity.let {
                    AlertDialog.Builder(it)
                }
                builder?.setMessage("Ξεχάσατε να βάλετε τα στοιχεία σας")
                    ?.setNegativeButton("Κλείσιμο") { _, _ ->
                        // User cancelled
                    }
                val dialog: AlertDialog? =builder?.create()
                dialog?.show()
            }
            else {
                println(filesDir.resolve("GovSMSCovid19data.txt"))
                val fileContent = "${nameInput.text}:${addressInput.text}"
                try {
                    val file = File(getExternalFilesDir(null).absolutePath, "GovSMSCovid19data.txt")
                    val fo = FileOutputStream(file)
                    fo.write(fileContent.toByteArray())
                    fo.close()
                    returnBack()
                } catch (ex:Exception) {
                    println(ex.message)
                }
            }
        }
        themeSwitch = findViewById<View>(R.id.themeSwitch) as Switch?
        if (sharedPref.loadNightModeState() == true) {
            themeSwitch!!.isChecked = true
        }
        themeSwitch!!.setOnCheckedChangeListener { buttonView, isChecked ->
            disableSwitch()
            if (isChecked) {
               sharedPref.setNightModeState(true)
                restartApp()
            } else {
                sharedPref.setNightModeState(false)
                restartApp()
            }
            Handler().postDelayed({
                enableSwitch()
            }, 300)
        }
    }

    private fun loadData() {
        try {
            val file = File(getExternalFilesDir(null).absolutePath, "GovSMSCovid19data.txt")
            val fi = FileInputStream(file)
            val br = BufferedReader(InputStreamReader(fi))
            val stringBuilder = StringBuilder()
            var text: String? = null
            while ({ text = br.readLine(); text }() != null) {
                stringBuilder.append(text)
            }
            val str = stringBuilder.split(":").toTypedArray()
            nameInput.setText(str[0])
            addressInput.setText(str[1])
            fi.close()
        }  catch (ex:Exception) {
            ex.printStackTrace()
        }
    }

    private fun returnBack() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun restartApp() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
    private fun disableSwitch() {
        themeSwitch?.isClickable = false
    }

    private fun enableSwitch() {
        themeSwitch?.isClickable = true
    }
}