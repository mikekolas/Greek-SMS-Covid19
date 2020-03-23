package com.example.govsmscovid19

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.settings_activity.*
import android.content.Intent
import android.net.Uri
import android.widget.ImageButton
import android.widget.TextView
import java.io.*
import java.lang.Exception
import java.lang.StringBuilder


class SettingsActivity : AppCompatActivity() {

    private var fileName: String =  "GovSMSCovid19data.txt"
    private var formaUri: String = "https://forma.gov.gr/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        loadData()

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

        val returnButton = findViewById<ImageButton>(R.id.returnBtn)
        returnButton.setOnClickListener {
            returnBack()
        }

        val redirect = findViewById<TextView>(R.id.formaRedirect)
        redirect.setOnClickListener {
            val openURL = Intent(android.content.Intent.ACTION_VIEW)
            openURL.data = Uri.parse(formaUri)
            startActivity(openURL)
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
}