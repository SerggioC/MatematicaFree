package com.sergiocruz.Matematica.helper

import android.content.Intent
import android.text.SpannableString
import android.text.style.SuperscriptSpan
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.sergiocruz.Matematica.R
import java.util.*

/*****
 * Project Matematica
 * Package com.sergiocruz.Matematica.helper
 * Created by Sergio on 09/11/2016 21:11
 */


private fun getViewsByTag(root: ViewGroup, tag: String?): ArrayList<View> {
    val views = ArrayList<View>()
    val childCount = root.childCount
    for (i in 0 until childCount) {
        val child = root.getChildAt(i)
        if (child is ViewGroup) views.addAll(getViewsByTag(child, tag))
        val tagObj = child.getTag(R.id.texto)
        if (tagObj != null && tagObj == tag) views.add(child)
    }
    return views
}

fun removeHistory(historyLayout: ViewGroup) {
    if (historyLayout.childCount > 0)
        historyLayout.removeAllViews()
    val theToast = Toast.makeText(historyLayout.context, R.string.history_deleted, Toast.LENGTH_SHORT)
    theToast.setGravity(Gravity.CENTER, 0, 0)
    theToast.show()
}

fun shareHistory(historyLayout: ViewGroup) {
    val textViewsWithTAGTexto = getViewsByTag(historyLayout, "texto")
    if (textViewsWithTAGTexto.size > 0) {
        var textFinal = ""
        for (i in textViewsWithTAGTexto.indices) {
            if (textViewsWithTAGTexto[i] is TextView) {
                var text = (textViewsWithTAGTexto[i] as TextView).text.toString() + "\n"
                val ss = SpannableString((textViewsWithTAGTexto[i] as TextView).text)
                val spans = ss.getSpans(0, (textViewsWithTAGTexto[i] as TextView).text.length, SuperscriptSpan::class.java)
                for ((corr, span) in spans.withIndex()) {
                    val start = ss.getSpanStart(span) + corr
                    text = text.substring(0, start) + "^" + text.substring(start)
                }
                textFinal += text
            }
        }

        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, historyLayout.context.resources.getString(R.string.app_long_description) +
                historyLayout.context.resources.getString(R.string.app_version_name) + "\n" + textFinal)
        sendIntent.type = "text/plain"
        historyLayout.context.startActivity(Intent.createChooser(sendIntent, historyLayout.context.resources.getString(R.string.app_name)))

    } else {
        val thetoast = Toast.makeText(historyLayout.context, R.string.nothing_toshare, Toast.LENGTH_SHORT)
        thetoast.setGravity(Gravity.CENTER, 0, 0)
        thetoast.show()
    }
}

fun biggerOrEqual(a: Int?, b: Int) = a?: b

fun nulableString() {
    val a: String? = null
    a?.plus("cenas")
}

fun areYouNullOrSomething(a: String?, b: String?) = a ?: b
@JvmOverloads
fun reformat(str: String,
             normalizeCase: Boolean? = true,
             upperCaseFirstLetter: Boolean = true,
             divideByCamelHumps: Boolean = false,
             wordSeparator: Char = ' ') {
}