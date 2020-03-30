package com.example.govsmscovid19

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.settings_activity.*
import java.io.*


class SettingsActivity : AppCompatActivity() {

    private var themeSwitch: Switch? = null
    private lateinit var sharedPref: SharedPref

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
                builder?.setMessage(getString(R.string.missingSettings))
                       ?.setNegativeButton(getString(R.string.btnClose)) { _, _ ->
                           // User cancelled
                       }
                val dialog: AlertDialog? = builder?.create()
                dialog?.show()
            } else {
                println(filesDir.resolve(FILENAME_COVID19))
                val fileContent = "${nameInput.text}:${addressInput.text}"
                try {
                    val file = File(getExternalFilesDir(null).absolutePath, FILENAME_COVID19)
                    val fo = FileOutputStream(file)
                    fo.write(fileContent.toByteArray())
                    fo.close()
                    returnBack()
                } catch (ex: Exception) {
                    println(ex.message)
                }
            }
        }
        themeSwitch = findViewById<View>(R.id.themeSwitch) as Switch?
        if (sharedPref.loadNightModeState() == true) {
            themeSwitch!!.isChecked = true
        }
        themeSwitch!!.setOnCheckedChangeListener { _, isChecked ->
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
            val file = File(getExternalFilesDir(null).absolutePath, FILENAME_COVID19)
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
        } catch (ex: Exception) {
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