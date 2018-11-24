package com.sergiocruz.Matematica.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
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
import com.sergiocruz.Matematica.helper.CreateCardView;
import com.sergiocruz.Matematica.helper.GetProLayout;
import com.sergiocruz.Matematica.helper.SwipeToDismissTouchListener;

import java.util.ArrayList;
import java.util.Collections;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import static com.sergiocruz.Matematica.helper.MenuHelperKt.removeHistory;
import static java.lang.Long.parseLong;


public class DivisoresFragment extends Fragment {

    public AsyncTask<Long, Double, ArrayList<Long>> BG_Operation = new BackGroundOperation();
    int cv_width, height_dip;
    View progressBar;
    Fragment thisFragment = this;
    Button button;
    ImageView cancelButton;
    long num;
    SharedPreferences sharedPrefs;
    long startTime;
    float scale;
    private LinearLayout historyLayout;
    private CardView cardView1;

    public DivisoresFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        scale = getResources().getDisplayMetrics().density;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_divisores, container, false);
        final EditText num_1 = view.findViewById(R.id.editNum);

        historyLayout = view.findViewById(R.id.history);

        cardView1 = view.findViewById(R.id.card_view_1);
        progressBar = view.findViewById(R.id.progress);

        cancelButton = view.findViewById(R.id.cancelTask);
        cancelButton.setOnClickListener(v -> displayCancelDialogBox());

        button = view.findViewById(R.id.button_calc_divisores);
        button.setOnClickListener(v -> calcDivisores(view));

        Button clearTextBtn = view.findViewById(R.id.btn_clear);
        clearTextBtn.setOnClickListener(v -> num_1.setText(""));

        num_1.addTextChangedListener(new TextWatcher() {
            Long num1;
            String oldnum1;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldnum1 = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    try {
                        // Tentar converter o string para Long
                        num1 = parseLong(s.toString());
                    } catch (Exception e) {
                        num_1.setText(oldnum1);
                        num_1.setSelection(num_1.getText().length()); //Colocar o cursor no final do texto
                        Toast thetoast = Toast.makeText(getContext(), R.string.numero_alto, Toast.LENGTH_SHORT);
                        thetoast.setGravity(Gravity.CENTER, 0, 0);
                        thetoast.show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.

        inflater.inflate(R.menu.menu_main, menu);
        inflater.inflate(R.menu.menu_sub_main, menu);
        inflater.inflate(R.menu.menu_help_divisores, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_share_history) {
            GetProLayout.getItPopup(getContext());
        }
        if (id == R.id.action_clear_all_history) {
            removeHistory(historyLayout);
        }
        if (id == R.id.action_help_divisores) {
            String help_divisores = getString(R.string.help_text_divisores);
            SpannableStringBuilder ssb = new SpannableStringBuilder(help_divisores);
            CreateCardView.create(historyLayout, ssb, getActivity());
        }
        if (id == R.id.action_about) {
            startActivity(new Intent(getContext(), AboutActivity.class));
        }
        if (id == R.id.action_settings) {
            startActivity(new Intent(getContext(), SettingsActivity.class));
        }
        if (id == R.id.action_buy_pro) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(getString(R.string.playstore_url)));
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
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
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            Log.e("Sergio>>>", "hideKeyboard error: ", e);
        }
    }

    public void displayCancelDialogBox() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        // set title
        alertDialogBuilder.setTitle(getString(R.string.calculate_divisors_title));

        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.cancel_it)
                .setCancelable(true)
                .setPositiveButton(R.string.sim, (dialog, id) -> {
                    cancel_AsyncTask();
                    dialog.cancel();
                })
                .setNegativeButton(R.string.nao, (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();        // create alert dialog
        alertDialog.show();                                           // show it
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (BG_Operation.getStatus() == AsyncTask.Status.RUNNING) {
            BG_Operation.cancel(true);
            Toast thetoast = Toast.makeText(getContext(), R.string.canceled_op, Toast.LENGTH_SHORT);
            thetoast.setGravity(Gravity.CENTER, 0, 0);
            thetoast.show();
        }
    }


    public void cancel_AsyncTask() {
        if (BG_Operation.getStatus() == AsyncTask.Status.RUNNING) {
            BG_Operation.cancel(true);
            Toast thetoast = Toast.makeText(getContext(), R.string.canceled_op, Toast.LENGTH_SHORT);
            thetoast.setGravity(Gravity.CENTER, 0, 0);
            thetoast.show();
            cancelButton.setVisibility(View.GONE);
            button.setText(getString(R.string.calculate));
            button.setClickable(true);
            progressBar.setVisibility(View.GONE);
        }
    }

    public void calcDivisores(View view) {
        startTime = System.nanoTime();
        hideKeyboard();
        EditText edittext = view.findViewById(R.id.editNum);
        String editnumText = edittext.getText().toString();
        if (TextUtils.isEmpty(editnumText)) {
            Toast thetoast = Toast.makeText(getContext(), R.string.add_num_inteiro, Toast.LENGTH_LONG);
            thetoast.setGravity(Gravity.CENTER, 0, 0);
            thetoast.show();
            return;
        }

        try {
            // Tentar converter o string para long
            num = Long.parseLong(editnumText);
        } catch (Exception e) {
            Toast thetoast = Toast.makeText(getContext(), R.string.numero_alto, Toast.LENGTH_LONG);
            thetoast.setGravity(Gravity.CENTER, 0, 0);
            thetoast.show();
            return;
        }

        if (editnumText.equals("0") || num == 0L) {
            SpannableStringBuilder ssb = new SpannableStringBuilder(getString(R.string.zero_no_divisores));
            CreateCardView.create(historyLayout, ssb, getActivity());
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
        final CardView cardview = new CardView(getActivity());
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

        int cv_color = ContextCompat.getColor(getContext(), R.color.cardsColor);
        cardview.setCardBackgroundColor(cv_color);

        // Add cardview to history layout at the top (index 0)
        historyLayout.addView(cardview, 0);

        // criar novo Textview
        final TextView textView = new TextView(getActivity());
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,   //largura
                ViewGroup.LayoutParams.WRAP_CONTENT)); //altura

        //Adicionar o texto com o resultado
        textView.setText(ssb);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setTag(R.id.texto, "texto");

        LinearLayout ll_vertical_root = new LinearLayout(getActivity());
        ll_vertical_root.setLayoutParams(new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_vertical_root.setOrientation(LinearLayout.VERTICAL);

        // Create a generic swipe-to-dismiss touch listener.
        cardview.setOnTouchListener(new SwipeToDismissTouchListener(
                cardview,
                getActivity(),
                new SwipeToDismissTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(Boolean token) {
                        return true;
                    }

                    @Override
                    public void onDismiss(View view) {
                        historyLayout.removeView(cardview);
                    }
                }));


        ll_vertical_root.addView(textView);

        // add the textview to the cardview
        cardview.addView(ll_vertical_root);
    }

    public class BackGroundOperation extends AsyncTask<Long, Double, ArrayList<Long>> {

        @Override
        public void onPreExecute() {
            button.setClickable(false);
            button.setText(R.string.working);
            cancelButton.setVisibility(View.VISIBLE);
            hideKeyboard();
            cv_width = cardView1.getWidth();
            height_dip = (int) (4 * scale + 0.5f);
            progressBar.setLayoutParams(new LinearLayout.LayoutParams(10, height_dip));
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Long> doInBackground(Long... num) {

            /*
             *
             * Performance update
             * Primeiro obtem os fatores primos depois multiplica-os
             *
             * */
            ArrayList<Long> divisores = new ArrayList<>();
            Long number = num[0];
            double progress;
            double oldProgress = 0f;

            while (number % 2L == 0) {
                divisores.add(2L);
                number /= 2;
            }

            for (long i = 3; i <= number / i; i += 2) {
                while (number % i == 0) {
                    divisores.add(i);
                    number /= i;
                }
                progress = (double) i / (((double) number / (double) i));
                if (progress - oldProgress > 0.1d) {
                    publishProgress(progress, (double) i);
                    oldProgress = progress;
                }
                if (isCancelled()) break;
            }
            if (number > 1) {
                divisores.add(number);
            }

            ArrayList<Long> AllDivisores = new ArrayList<Long>();
            int size;
            AllDivisores.add(1L);
            for (int i = 0; i < divisores.size(); i++) {
                size = AllDivisores.size();
                for (int j = 0; j < size; j++) {
                    long val = AllDivisores.get(j) * divisores.get(i);
                    if (!AllDivisores.contains(val)) {
                        AllDivisores.add(val);
                    }
                }
            }
            Collections.sort(AllDivisores);

            return AllDivisores;

        }

        @Override
        public void onProgressUpdate(Double... values) {
            if (thisFragment != null && thisFragment.isVisible()) {
                progressBar.setLayoutParams(new LinearLayout.LayoutParams((int) Math.round(values[0] * cv_width), height_dip));
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Long> result) {
            if (thisFragment != null && thisFragment.isVisible()) {
                ArrayList<Long> nums = result;
                String str = "";
                for (long i : nums) {
                    str = str + ", " + i;
                    if (i == 1L) {
                        str = num + " " + getString(R.string.has) + " " + nums.size() + " " + getString(R.string.divisores_) + "\n{" + i;
                    }
                }
                String str_divisores = str + "}";
                SpannableStringBuilder ssb = new SpannableStringBuilder(str_divisores);
                if (nums.size() == 2) {
                    String prime_number = "\n" + getString(R.string._numero_primo);
                    ssb.append(prime_number);
                    ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#29712d")), ssb.length() - prime_number.length(), ssb.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb.setSpan(new RelativeSizeSpan(0.9f), ssb.length() - prime_number.length(), ssb.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                createCardView(ssb);
                resetButtons();
            }
        }

        @Override
        protected void onCancelled(ArrayList<Long> parcial) {
            super.onCancelled(parcial);
            if (thisFragment != null && thisFragment.isVisible()) {
                ArrayList<Long> nums = parcial;
                String str = "";
                for (long i : nums) {
                    str = str + ", " + i;
                    if (i == 1L) {
                        str = getString(R.string.divisors_of) + " " + num + ":\n" + "{" + i;
                    }
                }
                String str_divisores = str + "}";
                SpannableStringBuilder ssb = new SpannableStringBuilder(str_divisores);
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
