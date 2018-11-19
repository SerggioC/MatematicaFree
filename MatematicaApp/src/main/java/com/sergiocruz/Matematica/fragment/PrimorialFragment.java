package com.sergiocruz.Matematica.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sergiocruz.Matematica.R;
import com.sergiocruz.Matematica.activity.AboutActivity;
import com.sergiocruz.Matematica.activity.SettingsActivity;
import com.sergiocruz.Matematica.helper.Ads;
import com.sergiocruz.Matematica.helper.CreateCardView;
import com.sergiocruz.Matematica.helper.GetPro;
import com.sergiocruz.Matematica.helper.MenuHelper;
import com.sergiocruz.Matematica.helper.SwipeToDismissTouchListener;

import java.math.BigInteger;
import java.util.ArrayList;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import static java.lang.Long.parseLong;

/*****
 * Project MatematicaFree
 * Package com.sergiocruz.Matematica.fragment
 * Created by Sergio on 05/02/2017 12:47
 ******/

public class PrimorialFragment extends Fragment {

    final static int sieveLimit = 1_300_000;
    static boolean[] notPrime = sieve(sieveLimit);
    public AsyncTask<Long, Double, BigInteger> BG_Operation = new BackGroundOperation();
    int cv_width, height_dip;
    View progressBar;
    Fragment thisFragment = this;
    Button button;
    ImageView cancelButton;
    long num;
    Activity mActivity;
    SharedPreferences sharedPrefs;
    long startTime;
    float scale;

    public PrimorialFragment() {
        // Required empty public constructor
    }

    static BigInteger primorial(int n) {
        if (n == 0)
            return BigInteger.ONE;

        BigInteger result = BigInteger.ONE;
        for (int i = 0; i < sieveLimit && n > 0; i++) {
            if (notPrime[i])
                continue;
            result = result.multiply(BigInteger.valueOf(i));
            n--;
        }
        return result;
    }

    public static boolean[] sieve(int limit) {
        boolean[] composite = new boolean[limit];
        composite[0] = composite[1] = true;

        int max = (int) Math.sqrt(limit);
        for (int n = 2; n <= max; n++) {
            if (!composite[n]) {
                for (int k = n * n; k < limit; k += n) {
                    composite[k] = true;
                }
            }
        }
        return composite;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mActivity = getActivity();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        scale = mActivity.getResources().getDisplayMetrics().density;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.

        inflater.inflate(R.menu.menu_main, menu);
        inflater.inflate(R.menu.menu_sub_main, menu);
        inflater.inflate(R.menu.menu_help_primorial, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_share_history) {
            GetPro.getItPopup(mActivity);
        }
        if (id == R.id.action_clear_all_history) {
            MenuHelper.remove_history(mActivity);
        }
        if (id == R.id.action_help_primorial) {
            String help_primorial = getString(R.string.help_text_primorial);
            SpannableStringBuilder ssb = new SpannableStringBuilder(help_primorial);
            LinearLayout history = (LinearLayout) mActivity.findViewById(R.id.history);
            CreateCardView.create(history, ssb, mActivity);
        }
        if (id == R.id.action_about) {
            startActivity(new Intent(mActivity, AboutActivity.class));
        }
        if (id == R.id.action_settings) {
            startActivity(new Intent(mActivity, SettingsActivity.class));
        }
        if (id == R.id.action_buy_pro) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=com.sergiocruz.MatematicaPro"));
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Display display = mActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        //int height = size.y;
        int lr_dip = (int) (4 * scale + 0.5f) * 2;
        cv_width = width - lr_dip;
        hideKeyboard();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            Log.e("Sergio>>>", "hideKeyboard error: ", e);
        }
    }

    public void displayCancelDialogBox() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);

        // set title
        alertDialogBuilder.setTitle(getString(R.string.calculate_primorial_title));

        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.cancel_it)
                .setCancelable(true)
                .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        cancel_AsyncTask();
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();        // create alert dialog
        alertDialog.show();                                           // show it
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_primorial, container, false);
        final EditText num_1 = (EditText) view.findViewById(R.id.editNum);

        cancelButton = (ImageView) view.findViewById(R.id.cancelTask);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCancelDialogBox();
            }
        });

        button = (Button) view.findViewById(R.id.button_calc_primorial);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcPrimorial(view);
            }
        });

        Button clearTextBtn = (Button) view.findViewById(R.id.btn_clear);
        clearTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num_1.setText("");
            }
        });

        num_1.addTextChangedListener(new TextWatcher() {
            Long num1;
            String oldnum1;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldnum1 = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    return;
                }
                try {
                    // Tentar converter o string para Long
                    num1 = parseLong(s.toString());
                } catch (Exception e) {
                    num_1.setText(oldnum1);
                    num_1.setSelection(num_1.getText().length()); //Colocar o cursor no final do texto
                    Toast thetoast = Toast.makeText(mActivity, R.string.numero_alto, Toast.LENGTH_SHORT);
                    thetoast.setGravity(Gravity.CENTER, 0, 0);
                    thetoast.show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Ads.showIn(getContext(), view.findViewById(R.id.adView));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (BG_Operation.getStatus() == AsyncTask.Status.RUNNING) {
            BG_Operation.cancel(true);
            Toast thetoast = Toast.makeText(mActivity, R.string.canceled_op, Toast.LENGTH_SHORT);
            thetoast.setGravity(Gravity.CENTER, 0, 0);
            thetoast.show();
        }
    }

    public void cancel_AsyncTask() {
        if (BG_Operation.getStatus() == AsyncTask.Status.RUNNING) {
            BG_Operation.cancel(true);
            Toast thetoast = Toast.makeText(mActivity, R.string.canceled_op, Toast.LENGTH_SHORT);
            thetoast.setGravity(Gravity.CENTER, 0, 0);
            thetoast.show();
            cancelButton.setVisibility(View.GONE);
            button.setText(getString(R.string.calculate));
            button.setClickable(true);
            progressBar.setVisibility(View.GONE);
        }
    }

    public void calcPrimorial(View view) {
        startTime = System.nanoTime();
        hideKeyboard();
        EditText edittext = (EditText) view.findViewById(R.id.editNum);
        String editnumText = (String) edittext.getText().toString();
        if (editnumText.equals(null) || editnumText.equals("") || editnumText == null) {
            Toast thetoast = Toast.makeText(mActivity, R.string.add_num_inteiro, Toast.LENGTH_LONG);
            thetoast.setGravity(Gravity.CENTER, 0, 0);
            thetoast.show();
            return;
        }

        try {
            // Tentar converter o string para long
            num = Long.parseLong(editnumText);
            if (num == 0L) {
                Toast thetoast = Toast.makeText(mActivity, R.string.zeroPrimorial, Toast.LENGTH_LONG);
                thetoast.setGravity(Gravity.CENTER, 0, 0);
                thetoast.show();
                return;
            }
        } catch (Exception e) {
            Toast thetoast = Toast.makeText(mActivity, R.string.numero_alto, Toast.LENGTH_LONG);
            thetoast.setGravity(Gravity.CENTER, 0, 0);
            thetoast.show();
            return;
        }

        BG_Operation = new BackGroundOperation().execute(num);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void resetButtons() {
        progressBar.setVisibility(View.GONE);
        button.setText(R.string.calculate);
        button.setClickable(true);
        cancelButton.setVisibility(View.GONE);
    }

    public void createCardView(SpannableStringBuilder ssb) {
        //criar novo cardview
        final CardView cardview = new CardView(mActivity);
        cardview.setLayoutParams(new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT,   // width
                CardView.LayoutParams.WRAP_CONTENT)); // height
        cardview.setPreventCornerOverlap(true);

        //int pixels = (int) (dips * scale + 0.5f);
        int lr_dip = (int) (6 * scale + 0.5f);
        int tb_dip = (int) (8 * scale + 0.5f);
        cardview.setRadius((int) (2 * scale + 0.5f));
        cardview.setCardElevation((int) (2 * scale + 0.5f));
        cardview.setContentPadding(lr_dip, tb_dip, lr_dip, tb_dip);
        cardview.setUseCompatPadding(true);

        int cv_color = ContextCompat.getColor(mActivity, R.color.cardsColor);
        cardview.setCardBackgroundColor(cv_color);

        // Add cardview to history layout at the top (index 0)
        final LinearLayout history = (LinearLayout) mActivity.findViewById(R.id.history);
        history.addView(cardview, 0);

        // criar novo Textview
        final TextView textView = new TextView(mActivity);
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,   //largura
                ViewGroup.LayoutParams.WRAP_CONTENT)); //altura

        //Adicionar o texto com o resultado
        textView.setText(ssb);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setTag(R.id.texto, "texto");

        LinearLayout ll_vertical_root = new LinearLayout(mActivity);
        ll_vertical_root.setLayoutParams(new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_vertical_root.setOrientation(LinearLayout.VERTICAL);

        // Create a generic swipe-to-dismiss touch listener.
        cardview.setOnTouchListener(new SwipeToDismissTouchListener(
                cardview,
                mActivity,
                new SwipeToDismissTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(Boolean token) {
                        return true;
                    }

                    @Override
                    public void onDismiss(View view) {
                        history.removeView(cardview);
                    }
                }));

        ll_vertical_root.addView(textView);

        // add the root layout to the cardview
        cardview.addView(ll_vertical_root);
    }

    public class BackGroundOperation extends AsyncTask<Long, Double, BigInteger> {
        Long number;
        ArrayList<Long> primes = new ArrayList<>();

        @Override
        public void onPreExecute() {
            button.setClickable(false);
            button.setText(R.string.working);
            cancelButton.setVisibility(View.VISIBLE);
            hideKeyboard();
            CardView cardView1 = (CardView) mActivity.findViewById(R.id.card_view_1);
            cv_width = cardView1.getWidth();
            height_dip = (int) (4 * scale + 0.5f);
            progressBar = (View) mActivity.findViewById(R.id.progress);
            progressBar.setLayoutParams(new LinearLayout.LayoutParams(10, height_dip));
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected BigInteger doInBackground(Long... num) {
            number = num[0];
            if (number == 1L) return BigInteger.ONE;
            if (number == 2L) return BigInteger.valueOf(2L);

            primes.add(1L);
            primes.add(2L);

            double progress;
            double oldProgress = 0d;

            for (long i = 3L; i <= number; i++) {
                boolean isPrime = true;
                if (i % 2 == 0) isPrime = false;
                if (isPrime) {
                    for (long j = 3; j < i; j = j + 2) {
                        if (i % j == 0) {
                            isPrime = false;
                            break;
                        }
                    }
                }
                if (isPrime) {
                    primes.add(i);
                }
                progress = (double) i / (double) number;
                if (progress - oldProgress > 0.05d) {
                    publishProgress(progress);
                    oldProgress = progress;
                }
                if (isCancelled()) break;
            }

            BigInteger primorial = BigInteger.ONE;
            for (int j = 0; j < primes.size(); j++) {
                primorial = primorial.multiply(BigInteger.valueOf(primes.get(j)));
            }

            return primorial;

        }

        @Override
        public void onProgressUpdate(Double... values) {
            if (thisFragment != null && thisFragment.isVisible()) {
                progressBar.setLayoutParams(new LinearLayout.LayoutParams((int) Math.round(values[0] * cv_width), height_dip));
            }
        }

        @Override
        protected void onPostExecute(BigInteger result) {
            if (thisFragment != null && thisFragment.isVisible()) {
                BigInteger primorial = result;
                String text = number + "#=\n" + primorial;
                SpannableStringBuilder ssb = new SpannableStringBuilder(text);
                createCardView(ssb);
                resetButtons();
            }
        }

        @Override
        protected void onCancelled(BigInteger parcial) {
            super.onCancelled(parcial);
            if (thisFragment != null && thisFragment.isVisible()) {
                String ssb_string = primes.get(primes.size() - 1) + "#=\n" + parcial;
                SpannableStringBuilder ssb = new SpannableStringBuilder(ssb_string);
                String incomplete_calc = "\n" + getString(R.string._incomplete_calc);
                ssb.append(incomplete_calc);
                ssb.setSpan(new ForegroundColorSpan(Color.RED), ssb.length() - incomplete_calc.length(), ssb.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.setSpan(new RelativeSizeSpan(0.8f), ssb.length() - incomplete_calc.length(), ssb.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                createCardView(ssb);
                resetButtons();
            }
        }
    }


}
