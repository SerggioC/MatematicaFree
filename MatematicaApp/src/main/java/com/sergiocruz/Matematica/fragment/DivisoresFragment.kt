package com.sergiocruz.Matematica.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Point
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutCompat
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.TextUtils
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.LinearLayout.VERTICAL
import com.sergiocruz.Matematica.R
import com.sergiocruz.Matematica.activity.AboutActivity
import com.sergiocruz.Matematica.activity.SettingsActivity
import com.sergiocruz.Matematica.helper.CreateCardView
import com.sergiocruz.Matematica.helper.GetProLayout
import com.sergiocruz.Matematica.helper.SwipeToDismissTouchListener
import com.sergiocruz.Matematica.helper.removeHistory
import java.lang.Long.parseLong
import java.util.*


class DivisoresFragment : Fragment() {

    private var bgOperation: AsyncTask<Long, Double, ArrayList<Long>> = BackGroundOperation()
    internal var cvWidth: Int = 0
    internal var heightDip: Int = 0
    internal lateinit var progressBar: View
    internal var thisFragment: Fragment? = this
    internal lateinit var button: Button
    internal lateinit var cancelButton: ImageView
    internal var num: Long = 0
    private lateinit var sharedPrefs: SharedPreferences
    private var startTime: Long = 0
    internal var scale: Float = 0.toFloat()
    private lateinit var historyLayout: LinearLayout
    private var cardView1: CardView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        scale = resources.displayMetrics.density
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_divisores, container, false)
        val number = view.findViewById<EditText>(R.id.editNum)

        historyLayout = view.findViewById(R.id.history)

        cardView1 = view.findViewById(R.id.card_view_1)
        progressBar = view.findViewById(R.id.progress)

        cancelButton = view.findViewById(R.id.cancelTask)
        cancelButton.setOnClickListener { displayCancelDialogBox() }

        button = view.findViewById(R.id.button_calc_divisores)
        button.setOnClickListener { calcDivisores(view) }

        val clearTextBtn = view.findViewById<Button>(R.id.btn_clear)
        clearTextBtn.setOnClickListener { number.setText("") }

        number.addTextChangedListener(object : TextWatcher {
            var num1: Long? = null
            var oldnum1: String = ""

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                oldnum1 = s.toString()
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!TextUtils.isEmpty(s)) {
                    try {
                        // Tentar converter o string para Long
                        num1 = parseLong(s.toString())
                    } catch (e: Exception) {
                        number.setText(oldnum1)
                        number.setSelection(number.text.length) //Colocar o cursor no final do texto
                        val thetoast = Toast.makeText(context, R.string.numero_alto, Toast.LENGTH_SHORT)
                        thetoast.setGravity(Gravity.CENTER, 0, 0)
                        thetoast.show()
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        // Inflate the menu; this adds items to the action bar if it is present.

        inflater?.inflate(R.menu.menu_main, menu)
        inflater?.inflate(R.menu.menu_sub_main, menu)
        inflater?.inflate(R.menu.menu_help_divisores, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item!!.itemId
        if (id == R.id.action_share_history) GetProLayout.getItPopup(context)
        if (id == R.id.action_clear_all_history) removeHistory(historyLayout)
        if (id == R.id.action_help_divisores) {
            val helpText = getString(R.string.help_text_divisores)
            val ssb = SpannableStringBuilder(helpText)
            CreateCardView.create(historyLayout, ssb, activity)
        }
        if (id == R.id.action_about) startActivity(Intent(context, AboutActivity::class.java))
        if (id == R.id.action_settings) startActivity(Intent(context, SettingsActivity::class.java))
        if (id == R.id.action_buy_pro) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(getString(R.string.playstore_url))
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        val display = activity!!.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        //int height = size.y;
        val lrDip = (4 * scale + 0.5f).toInt() * 2
        cvWidth = width - lrDip
        hideKeyboard()
    }

    fun hideKeyboard() {
        try {
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity!!.currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            Log.e("Sergio>>>", "hideKeyboard error: ", e)
        }

    }

    private fun displayCancelDialogBox() {
        val alertDialogBuilder = AlertDialog.Builder(context!!)

        // set title
        alertDialogBuilder.setTitle(getString(R.string.calculate_divisors_title))

        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.cancel_it)
                .setCancelable(true)
                .setPositiveButton(R.string.sim) { dialog, _ ->
                    cancelAsyncTask()
                    dialog.cancel()
                }
                .setNegativeButton(R.string.nao) { dialog, _ -> dialog.cancel() }
        val alertDialog = alertDialogBuilder.create()        // create alert dialog
        alertDialog.show()                                           // show it
    }


    override fun onDestroy() {
        super.onDestroy()
        if (bgOperation.status == AsyncTask.Status.RUNNING) {
            bgOperation.cancel(true)
            val thetoast = Toast.makeText(context, R.string.canceled_op, Toast.LENGTH_SHORT)
            thetoast.setGravity(Gravity.CENTER, 0, 0)
            thetoast.show()
        }
    }

    private fun cancelAsyncTask() {
        if (bgOperation.status == AsyncTask.Status.RUNNING) {
            bgOperation.cancel(true)
            val thetoast = Toast.makeText(context, R.string.canceled_op, Toast.LENGTH_SHORT)
            thetoast.setGravity(Gravity.CENTER, 0, 0)
            thetoast.show()
            cancelButton.visibility = View.GONE
            button.text = getString(R.string.calculate)
            button.isClickable = true
            progressBar.visibility = View.GONE
        }
    }

    private fun calcDivisores(view: View) {
        startTime = System.nanoTime()
        hideKeyboard()
        val edittext = view.findViewById<EditText>(R.id.editNum)
        val editnumText = edittext.text.toString()
        if (TextUtils.isEmpty(editnumText)) {
            val thetoast = Toast.makeText(context, R.string.add_num_inteiro, Toast.LENGTH_LONG)
            thetoast.setGravity(Gravity.CENTER, 0, 0)
            thetoast.show()
            return
        }

        try {
            // Tentar converter o string para long
            num = java.lang.Long.parseLong(editnumText)
        } catch (e: Exception) {
            val thetoast = Toast.makeText(context, R.string.numero_alto, Toast.LENGTH_LONG)
            thetoast.setGravity(Gravity.CENTER, 0, 0)
            thetoast.show()
            return
        }

        if (editnumText == "0" || num == 0L) {
            val ssb = SpannableStringBuilder(getString(R.string.zero_no_divisores))
            CreateCardView.create(historyLayout, ssb, activity)
            return
        }
        bgOperation = BackGroundOperation().execute(num)
    }

    private fun resetButtons() {
        progressBar.visibility = View.GONE
        button.setText(R.string.calculate)
        button.isClickable = true
        cancelButton.visibility = View.GONE
    }

    fun createCardView(ssb: SpannableStringBuilder) {
        //criar novo cardview
        val cardview = CardView(activity!!)
        cardview.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, // width
                ViewGroup.LayoutParams.WRAP_CONTENT) // height
        cardview.preventCornerOverlap = true

        //int pixels = (int) (dips * scale + 0.5f);
        val lrDip = (6 * scale + 0.5f).toInt()
        val tbDip = (8 * scale + 0.5f).toInt()
        cardview.radius = (2 * scale + 0.5f).toInt().toFloat()
        cardview.cardElevation = (2 * scale + 0.5f).toInt().toFloat()
        cardview.setContentPadding(lrDip, tbDip, lrDip, tbDip)
        cardview.useCompatPadding = true

        val cvColor = ContextCompat.getColor(context!!, R.color.cardsColor)
        cardview.setCardBackgroundColor(cvColor)

        // Add cardview to history layout at the top (index 0)
        historyLayout.addView(cardview, 0)

        // criar novo Textview
        val textView = TextView(activity)
        textView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, //largura
                ViewGroup.LayoutParams.WRAP_CONTENT) //altura

        //Adicionar o texto com o resultado
        textView.text = ssb
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        textView.setTag(R.id.texto, "texto")

        val llVerticalRoot = LinearLayout(activity)
        llVerticalRoot.layoutParams = LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT)
        llVerticalRoot.orientation = VERTICAL

        // Create a generic swipe-to-dismiss touch listener.
        cardview.setOnTouchListener(SwipeToDismissTouchListener(
                cardview,
                activity,
                object: SwipeToDismissTouchListener.DismissCallbacks {
                    override fun canDismiss(token: Boolean?): Boolean {
                        return true
                    }

                    override fun onDismiss(view: View) {
                        historyLayout.removeView(cardview)
                    }
                }))

        llVerticalRoot.addView(textView)

        // add the textview to the cardview
        cardview.addView(llVerticalRoot)
    }

    inner class BackGroundOperation : AsyncTask<Long, Double, ArrayList<Long>>() {

        public override fun onPreExecute() {
            button.isClickable = false
            button.setText(R.string.working)
            cancelButton.visibility = VISIBLE
            hideKeyboard()
            cvWidth = cardView1!!.width
            heightDip = (4 * scale + 0.5f).toInt()
            progressBar.layoutParams = LinearLayout.LayoutParams(10, heightDip)
            progressBar.visibility = VISIBLE
        }

        override fun doInBackground(vararg num: Long?): ArrayList<Long> {

            /**
             * Performance update
             * Primeiro obtém os fatores primos depois multiplica-os
             *
             * */
            val divisores = ArrayList<Long>()
            var number: Long? = num[0]
            var progress: Double
            var oldProgress = 0.0

            while (number!! % 2L == 0L) {
                divisores.add(2L)
                number /= 2
            }

            run {
                var i: Long = 3
                while (i <= number / i) {
                    while (number % i == 0L) {
                        divisores.add(i)
                        number /= i
                    }
                    progress = i.toDouble() / (number / i.toDouble())
                    if (progress - oldProgress > 0.1) {
                        publishProgress(progress, i.toDouble())
                        oldProgress = progress
                    }
                    if (isCancelled) break
                    i += 2
                }
            }
            if (number > 1) divisores.add(number)

            val allDivisores = ArrayList<Long>()
            var size: Int
            allDivisores.add(1L)
            for (i in divisores.indices) {
                size = allDivisores.size
                for (j in 0 until size) {
                    val `val` = allDivisores[j] * divisores[i]
                    if (!allDivisores.contains(`val`)) {
                        allDivisores.add(`val`)
                    }
                }
            }
            allDivisores.sort()

            return allDivisores
        }

        override fun onProgressUpdate(vararg values: Double?) {
            if (thisFragment != null && thisFragment!!.isVisible)
                progressBar.layoutParams = LinearLayout.LayoutParams(Math.round(values[0]!! * cvWidth).toInt(), heightDip)
        }

        override fun onPostExecute(result: ArrayList<Long>) {
            if (thisFragment != null && thisFragment!!.isVisible) {
                var str = ""
                for (i in result) {
                    str = "$str, $i"
                    if (i == 1L) {
                        str = num.toString() + " " + getString(R.string.has) + " " + result.size + " " + getString(R.string.divisores_) + "\n{" + i
                    }
                }
                val strDivisores = "$str}"
                val ssb = SpannableStringBuilder(strDivisores)
                if (result.size == 2) {
                    val primeNumber = "\n" + getString(R.string._numero_primo)
                    ssb.append(primeNumber)
                    ssb.setSpan(ForegroundColorSpan(Color.parseColor("#29712d")), ssb.length - primeNumber.length, ssb.length, SPAN_EXCLUSIVE_EXCLUSIVE)
                    ssb.setSpan(RelativeSizeSpan(0.9f), ssb.length - primeNumber.length, ssb.length, SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                createCardView(ssb)
                resetButtons()
            }
        }

        override fun onCancelled(parcial: ArrayList<Long>) {
            super.onCancelled(parcial)
            if (thisFragment != null && thisFragment!!.isVisible) {
                var str = ""
                for (i in parcial) {
                    str = "$str, $i"
                    if (i == 1L) {
                        str = getString(R.string.divisors_of) + " " + num + ":\n" + "{" + i
                    }
                }
                val strDivisores = "$str}"
                val ssb = SpannableStringBuilder(strDivisores)
                val incompleteCalc = "\n" + getString(R.string._incomplete_calc)
                ssb.append(incompleteCalc)
                ssb.setSpan(ForegroundColorSpan(Color.RED), ssb.length - incompleteCalc.length, ssb.length, SPAN_EXCLUSIVE_EXCLUSIVE)
                ssb.setSpan(RelativeSizeSpan(0.8f), ssb.length - incompleteCalc.length, ssb.length, SPAN_EXCLUSIVE_EXCLUSIVE)
                createCardView(ssb)
                resetButtons()
            }
        }
    }
}