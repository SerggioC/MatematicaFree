package com.sergiocruz.Matematica.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.widget.Toast

import com.sergiocruz.Matematica.R

/**
 * Created by sergi on 21/10/2016.
 */
class SendMailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_mail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val webView = findViewById<WebView>(R.id.web_view)
        webView.loadUrl("file:///android_asset/mail_smiley.gif")

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun sendMeMail(view: View) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.type = "text/plain"
        intent.data = Uri.parse(getString(R.string.app_email)) // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, application.resources.getString(R.string.app_long_description) +
                application.resources.getString(R.string.app_version_name) + "e-mail")
        intent.putExtra(Intent.EXTRA_TEXT, (this.findViewById<View>(R.id.mail_text) as android.support.design.widget.TextInputEditText).text!!.toString())
        if (intent.resolveActivity(packageManager) != null) {
            displayDialogBox(intent)
        } else {
            Toast.makeText(this, R.string.has_email, Toast.LENGTH_LONG).show()
        }
    }


    private fun displayDialogBox(intent: Intent) {
        val alertDialogBuilder = AlertDialog.Builder(this)

        // set title
        //alertDialogBuilder.setTitle("Enviar email?");

        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.send_it)
                .setCancelable(true)
                .setPositiveButton(R.string.sim) { dialog, _ ->
                    startActivity(intent)
                    dialog.cancel()
                }
                .setNegativeButton(R.string.nao) { dialog, _ -> dialog.cancel() }
        val alertDialog = alertDialogBuilder.create()        // create alert dialog
        alertDialog.show()                                           // show it
    }

}
