package com.sergiocruz.Matematica.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.UnderlineSpan;
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
import com.sergiocruz.Matematica.helper.SwipeToDismissTouchListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import static android.widget.LinearLayout.HORIZONTAL;
import static com.sergiocruz.Matematica.R.array.f_colors_xml;
import static com.sergiocruz.Matematica.R.string.fatorizar_btn;
import static com.sergiocruz.Matematica.fragment.MMCFragment.CARD_TEXT_SIZE;
import static com.sergiocruz.Matematica.helper.MenuHelperKt.removeHistory;
import static com.sergiocruz.Matematica.helper.MenuHelperKt.shareHistory;
import static com.sergiocruz.Matematica.helper.UtilsKt.collapseIt;
import static com.sergiocruz.Matematica.helper.UtilsKt.expandIt;
import static java.lang.Long.parseLong;

public class FatorizarFragment extends Fragment {
    ArrayList<Integer> fColors;
    /**
     *   AsyncTask params <Input datatype, progress update datatype, return datatype>
     **/
    AsyncTask<Long, Float, ArrayList<ArrayList<Long>>> BG_Operation = new BackGroundOperation();
    Button button;
    float scale;
    Fragment thisFragment = this;
    ImageView cancelButton;
    int cv_width, height_dip;
    LinearLayout historyLayout;
    Long num1, startTime;
    SharedPreferences sharedPrefs;
    View progressBar;
    private ViewGroup cardView1;
    private EditText wditTextNum1;

    public FatorizarFragment() {
        // Required empty public constructor
    }

    @SuppressWarnings("unused")
    public static ArrayList<Long> getFatoresPrimos(long number) {
        ArrayList<Long> factoresPrimos = new ArrayList<>();
        for (long i = 2; i <= number / i; i++) {
            while (number % i == 0) {
                factoresPrimos.add(i);
                number /= i;
            }
        }
        if (number > 1) {
            factoresPrimos.add(number);
        }
        return factoresPrimos;
    }

    @SuppressWarnings("unused")
    private ArrayList<ArrayList<Long>> getTabelaFatoresPrimos(long number) {

        ArrayList<ArrayList<Long>> factoresPrimos = new ArrayList<>();
        ArrayList<Long> results = new ArrayList<>();
        ArrayList<Long> divisores = new ArrayList<>();

        results.add(number);
        for (long i = 2; i <= number / i; i++) {
            while (number % i == 0) {
                divisores.add(i);
                number /= i;
                results.add(number);
            }
        }
        if (number > 1) {
            divisores.add(number);
        }

        if (number != 1) {
            results.add(1L);
        }

        factoresPrimos.add(results);
        factoresPrimos.add(divisores);

        return factoresPrimos;
    }

    @SuppressWarnings("unused")
    private ArrayList<ArrayList<Long>> getTabelaFatoresPrimos2(long number) {

        ArrayList<ArrayList<Long>> factoresPrimos = new ArrayList<>();
        ArrayList<Long> results = new ArrayList<>();
        ArrayList<Long> divisores = new ArrayList<>();

        results.add(number);

        while (number % 2L == 0) {
            divisores.add(2L);
            number /= 2;
            results.add(number);
        }

        for (long i = 3; i <= number / i; i += 2) {
            while (number % i == 0) {
                divisores.add(i);
                number /= i;
                results.add(number);
            }
        }
        if (number > 1) {
            divisores.add(number);
        }

        if (number != 1) {
            results.add(1L);
        }

        factoresPrimos.add(results);
        factoresPrimos.add(divisores);

        return factoresPrimos;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        scale = getResources().getDisplayMetrics().density;
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        int[] f_colors = getResources().getIntArray(f_colors_xml);
        fColors = new ArrayList<>();
        for (int f_color : f_colors) fColors.add(f_color);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.

        inflater.inflate(R.menu.menu_main, menu);
        inflater.inflate(R.menu.menu_sub_main, menu);
        inflater.inflate(R.menu.menu_help_fatorizar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_share_history) {
            shareHistory(historyLayout);
        }

        if (id == R.id.action_clear_all_history) {
            removeHistory(historyLayout);
        }

        if (id == R.id.action_ajuda) {

            String help_divisores = getString(R.string.help_text_fatores);
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
//         Checks the orientation of the screen
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Toast.makeText(getContext(), "landscape", Toast.LENGTH_SHORT).show();
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//            Toast.makeText(getContext(), "portrait", Toast.LENGTH_SHORT).show();
//        }

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
        //Hide the keyboard
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (BG_Operation.getStatus() == AsyncTask.Status.RUNNING) {
            BG_Operation.cancel(true);
            Toast thetoast = Toast.makeText(getContext(), getString(R.string.canceled_op), Toast.LENGTH_SHORT);
            thetoast.setGravity(Gravity.CENTER, 0, 0);
            thetoast.show();
        }
    }

    public void cancel_AsyncTask() {
        if (BG_Operation.getStatus() == AsyncTask.Status.RUNNING) {
            BG_Operation.cancel(true);
            Toast thetoast = Toast.makeText(getContext(), getString(R.string.canceled_op), Toast.LENGTH_SHORT);
            thetoast.setGravity(Gravity.CENTER, 0, 0);
            thetoast.show();
            cancelButton.setVisibility(View.GONE);
            button.setText(getString(R.string.calculate));
            button.setClickable(true);
            progressBar.setVisibility(View.GONE);
        }
    }

    public void displayCancelDialogBox() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        // set title
        alertDialogBuilder.setTitle(getString(R.string.fatorize_title));

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_fatorizar, container, false);
        wditTextNum1 = view.findViewById(R.id.editNumFact);

        historyLayout = view.findViewById(R.id.history);
        cardView1 = view.findViewById(R.id.card_view_1);
        progressBar = view.findViewById(R.id.progress);


        cancelButton = view.findViewById(R.id.cancelTask);
        cancelButton.setOnClickListener(v -> displayCancelDialogBox());
        button = view.findViewById(R.id.button_calc_fatores);
        button.setOnClickListener(v -> calcfatoresPrimos());

        Button clearTextBtn = view.findViewById(R.id.btn_clear);
        clearTextBtn.setOnClickListener(v -> wditTextNum1.setText(""));

        wditTextNum1.addTextChangedListener(new TextWatcher() {
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
                        wditTextNum1.setText(oldnum1);
                        wditTextNum1.setSelection(wditTextNum1.getText().length()); //Colocar o cursor no final do texto
                        Toast thetoast = Toast.makeText(getContext(), getString(R.string.numero_alto), Toast.LENGTH_SHORT);
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

    private void calcfatoresPrimos() {
        startTime = System.nanoTime();
        String editnumText = wditTextNum1.getText().toString();
        long num;

        if (TextUtils.isEmpty(editnumText)) {
            Toast thetoast = Toast.makeText(getContext(), R.string.insert_integer, Toast.LENGTH_LONG);
            thetoast.setGravity(Gravity.CENTER, 0, 0);
            thetoast.show();
            return;
        }
        try {
            // Tentar converter o string para long
            num = Long.parseLong(editnumText);
        } catch (Exception e) {
            Toast thetoast = Toast.makeText(getContext(), getString(R.string.numero_alto), Toast.LENGTH_LONG);
            thetoast.setGravity(Gravity.CENTER, 0, 0);
            thetoast.show();
            return;
        }
        if (num == 0L || num == 1L) {
            Toast thetoast = Toast.makeText(getContext(), getString(R.string.the_number) + " " + num + " " + getString(R.string.has_no_factors), Toast.LENGTH_LONG);
            thetoast.setGravity(Gravity.CENTER, 0, 0);
            thetoast.show();
            return;
        }

        BG_Operation = new BackGroundOperation().execute(num);

    }

    void createCardViewLayout(Long number, final ViewGroup history, String str_results, SpannableStringBuilder ssb_str_divisores, SpannableStringBuilder ssbFatores, SpannableStringBuilder str_fact_exp, Boolean hasExpoentes) {

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
        cardview.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.cardsColor));

        // Add cardview to history layout at the top (index 0)
        history.addView(cardview, 0);

        LinearLayout ll_vertical_root = new LinearLayout(getContext());
        ll_vertical_root.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ll_vertical_root.setOrientation(LinearLayout.VERTICAL);

        // Criar novo Textview para o resultado da fatorização
        TextView textView = new TextView(getContext());
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setPadding(0, 0, 0, 0);

        SpannableStringBuilder ssb_fatores_top = new SpannableStringBuilder(ssbFatores);
        ForegroundColorSpan[] spans = ssb_fatores_top.getSpans(0, ssb_fatores_top.length(), ForegroundColorSpan.class);
        for (ForegroundColorSpan span : spans) ssb_fatores_top.removeSpan(span);

        // Adicionar o texto com o resultado da fatorização com expoentes
        String str_num = getString(R.string.factorization_of) + " " + number + " = \n";
        SpannableStringBuilder ssb_num = new SpannableStringBuilder(str_num);
        ssb_num.append(ssb_fatores_top);
        textView.setText(ssb_num);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, CARD_TEXT_SIZE);
        textView.setTag(R.id.texto, "texto");

        // add the textview com os fatores multiplicados to the Linear layout vertical root
        ll_vertical_root.addView(textView);

        String shouldShowExplanation = sharedPrefs.getString(getString(R.string.show_explanation), "0");
        // -1 = sempre  0 = quando pedidas   1 = nunca
        if (shouldShowExplanation.equals("-1") || shouldShowExplanation.equals("0")) {
            ForegroundColorSpan boldColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.boldColor));

            LinearLayout ll_vertical_expl = new LinearLayout(getContext());
            ll_vertical_expl.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            ll_vertical_expl.setOrientation(LinearLayout.VERTICAL);
            ll_vertical_expl.setTag("ll_vertical_expl");

            TextView textView_expl1 = new TextView(getContext());
            textView_expl1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            textView_expl1.setTextSize(TypedValue.COMPLEX_UNIT_SP, CARD_TEXT_SIZE);
            String explain_text_1 = getString(R.string.expl_text_divisores_1);
            SpannableStringBuilder ssb_explain_1 = new SpannableStringBuilder(explain_text_1);
            ssb_explain_1.setSpan(boldColorSpan, 0, ssb_explain_1.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
            textView_expl1.setText(ssb_explain_1);
            textView_expl1.setTag(R.id.texto, "texto");
            ll_vertical_expl.addView(textView_expl1);

            LinearLayout ll_horizontal = new LinearLayout(getContext());
            ll_horizontal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            ll_horizontal.setOrientation(LinearLayout.HORIZONTAL);
            ll_horizontal.setTag("ll_horizontal_expl");

            LinearLayout ll_vertical_results = new LinearLayout(getContext());
            ll_vertical_results.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            ll_vertical_results.setOrientation(LinearLayout.VERTICAL);
            ll_vertical_results.setPadding(0, 0, (int) (4 * scale + 0.5f), 0);

            LinearLayout ll_vertical_separador = new LinearLayout(getContext());
            ll_vertical_separador.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
            ll_vertical_separador.setOrientation(LinearLayout.VERTICAL);
            ll_vertical_separador.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.separatorLineColor));
            int um_dip = (int) (1.2 * scale + 0.5f);
            ll_vertical_separador.setPadding(um_dip, 4, 0, um_dip);

            LinearLayout ll_vertical_divisores = new LinearLayout(getContext());
            ll_vertical_divisores.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            ll_vertical_divisores.setOrientation(LinearLayout.VERTICAL);
            ll_vertical_divisores.setPadding((int) (4 * scale + 0.5f), 0, (int) (8 * scale + 0.5f), 0);

            TextView textView_results = new TextView(getContext());
            textView_results.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            textView_results.setTextSize(TypedValue.COMPLEX_UNIT_SP, CARD_TEXT_SIZE);
            textView_results.setGravity(Gravity.RIGHT);
            SpannableStringBuilder ssb_str_results = new SpannableStringBuilder(str_results);
            ssb_str_results.setSpan(new RelativeSizeSpan(0.9f), 0, ssb_str_results.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
            textView_results.setText(ssb_str_results);
            textView_results.setTag(R.id.texto, "texto");

            ll_vertical_results.addView(textView_results);

            TextView textView_divisores = new TextView(getContext());
            textView_divisores.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            textView_divisores.setTextSize(TypedValue.COMPLEX_UNIT_SP, CARD_TEXT_SIZE);
            textView_divisores.setGravity(Gravity.LEFT);
            ssb_str_divisores.setSpan(new RelativeSizeSpan(0.9f), 0, ssb_str_divisores.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
            textView_divisores.setText(ssb_str_divisores);
            textView_divisores.setTag(R.id.texto, "texto");

            ll_vertical_divisores.addView(textView_divisores);

            //Adicionar os LL Verticais ao Horizontal
            ll_horizontal.addView(ll_vertical_results);

            ll_horizontal.addView(ll_vertical_separador);

            //LinearLayout divisores
            ll_horizontal.addView(ll_vertical_divisores);

            final SpannableStringBuilder ssb_hide_expl = new SpannableStringBuilder(getString(R.string.hide_explain));
            ssb_hide_expl.setSpan(new UnderlineSpan(), 0, ssb_hide_expl.length() - 2, SPAN_EXCLUSIVE_EXCLUSIVE);
            final SpannableStringBuilder ssb_show_expl = new SpannableStringBuilder(getString(R.string.explain));
            ssb_show_expl.setSpan(new UnderlineSpan(), 0, ssb_show_expl.length() - 2, SPAN_EXCLUSIVE_EXCLUSIVE);

            final TextView explainLink = new TextView(getContext());
            explainLink.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,   //largura
                    LinearLayout.LayoutParams.WRAP_CONTENT)); //altura
            explainLink.setTextSize(TypedValue.COMPLEX_UNIT_SP, CARD_TEXT_SIZE);
            explainLink.setTextColor(ContextCompat.getColor(getContext(), R.color.linkBlue));

            final Boolean[] isExpanded = {false};

            if (shouldShowExplanation.equals("-1")) {  //Always show Explanation
                ll_vertical_expl.setVisibility(View.VISIBLE);
                explainLink.setText(ssb_hide_expl);
                isExpanded[0] = true;
            } else if (shouldShowExplanation.equals("0")) { // Show Explanation on demand on click
                ll_vertical_expl.setVisibility(View.GONE);
                explainLink.setText(ssb_show_expl);
                isExpanded[0] = false;
            }

            explainLink.setOnClickListener(view -> {
                View explView = ((CardView) view.getParent().getParent().getParent()).findViewWithTag("ll_vertical_expl");
                if (!isExpanded[0]) {
                    ((TextView) view).setText(ssb_hide_expl);
                    expandIt(explView);
                    isExpanded[0] = true;

                } else if (isExpanded[0]) {
                    ((TextView) view).setText(ssb_show_expl);
                    collapseIt(explView);
                    isExpanded[0] = false;
                }
            });
            TextView gradient_separator = getGradientSeparator();

            gradient_separator.setText("");

            //Linearlayout horizontal com o explainlink e gradiente
            LinearLayout ll_horizontal_link = new LinearLayout(getContext());
            ll_horizontal_link.setOrientation(HORIZONTAL);
            ll_horizontal_link.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            ll_horizontal_link.addView(explainLink);
            ll_horizontal_link.addView(gradient_separator);

            ll_vertical_root.addView(ll_horizontal_link);

            ll_vertical_expl.addView(ll_horizontal);

            TextView textView_fact_expanded = new TextView(getContext());
            textView_fact_expanded.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            textView_fact_expanded.setTextSize(TypedValue.COMPLEX_UNIT_SP, CARD_TEXT_SIZE);
            textView_fact_expanded.setGravity(Gravity.LEFT);
            String explain_text_2 = getString(R.string.explain_divisores2) + "\n";
            SpannableStringBuilder ssb_explain_2 = new SpannableStringBuilder(explain_text_2);
            ssb_explain_2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.boldColor)), 0, ssb_explain_2.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb_explain_2.append(str_fact_exp);
            ssb_explain_2.setSpan(new RelativeSizeSpan(0.9f), ssb_explain_2.length() - str_fact_exp.length(), ssb_explain_2.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
            if (hasExpoentes) {
                String text_fact_repetidos = "\n" + getString(R.string.explain_divisores3) + "\n";
                ssb_explain_2.append(text_fact_repetidos);
                ssb_explain_2.append(ssbFatores);
                ssb_explain_2.setSpan(new RelativeSizeSpan(0.9f), ssb_explain_2.length() - ssbFatores.length(), ssb_explain_2.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb_explain_2.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), ssb_explain_2.length() - ssbFatores.length(), ssb_explain_2.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            textView_fact_expanded.setText(ssb_explain_2);
            textView_fact_expanded.setTag(R.id.texto, "texto");

            ll_vertical_expl.addView(textView_fact_expanded);

            ll_vertical_root.addView(ll_vertical_expl);


        }

        cardview.addView(ll_vertical_root);

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
                        history.removeView(cardview);
                    }
                }));
    }

    @NonNull
    private TextView getGradientSeparator() {
        //View separator with gradient
        TextView gradient_separator = new TextView(getContext());
        gradient_separator.setTag("gradient_separator");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            gradient_separator.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bottom_border2));
        } else {
            gradient_separator.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.bottom_border2));
        }
        gradient_separator.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,   //largura
                LinearLayout.LayoutParams.WRAP_CONTENT)); //altura
        gradient_separator.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
        gradient_separator.setTextColor(ContextCompat.getColor(getContext(), R.color.lightBlue));
        gradient_separator.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        return gradient_separator;
    }

    private void processData(ArrayList<ArrayList<Long>> result, Boolean wasCanceled) {
        /* resultadosDivisao|fatoresPrimos
         *                100|2
         *                 50|2
         *                 25|5
         *                  5|5
         *                  1|
         *
         * */

        ArrayList<Long> resultadosDivisao = result.get(0);
        ArrayList<Long> fatoresPrimos = result.get(1);

        // Tamanho da lista de números primos
        int sizeList = fatoresPrimos.size();

        String str_fatores = "";
        String str_results = "";
        SpannableStringBuilder ssb_fatores;

        if (sizeList == 1) {
            str_fatores = resultadosDivisao.get(0) + " " + getString(R.string.its_a_prime);
            ssb_fatores = new SpannableStringBuilder(str_fatores);
            ssb_fatores.setSpan(new ForegroundColorSpan(Color.parseColor("#29712d")), 0, ssb_fatores.length(), SPAN_EXCLUSIVE_EXCLUSIVE); //verde
            CreateCardView.create(historyLayout, ssb_fatores, getActivity());

        } else {
            Boolean hasExpoentes = false;
            Integer counter = 1;
            Long lastItem = fatoresPrimos.get(0);

            Collections.shuffle(fColors);
            SpannableStringBuilder ssb_fact_expanded = new SpannableStringBuilder();
            int colorIndex = 0;

            //TreeMap
            LinkedHashMap<String, Integer> dataset = new LinkedHashMap<>();

            //Contar os expoentes
            for (int i = 0; i < fatoresPrimos.size(); i++) {
                Long fatori = fatoresPrimos.get(i);
                if (lastItem != fatori) colorIndex++;

                String fi = fatori.toString();
                ssb_fact_expanded.append(fi);
                ssb_fact_expanded.setSpan(new ForegroundColorSpan(fColors.get(colorIndex)),
                        ssb_fact_expanded.length() - fi.length(), ssb_fact_expanded.length(),
                        SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb_fact_expanded.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                        ssb_fact_expanded.length() - fi.length(), ssb_fact_expanded.length(),
                        SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb_fact_expanded.append("×");

                if (i == 0) {
                    dataset.put(fatoresPrimos.get(0).toString(), 1);
                } else if (fatori.equals(lastItem) && i > 0) {
                    hasExpoentes = true;
                    counter++;
                    dataset.put(fatori.toString(), counter);
                } else if (!fatori.equals(lastItem) && i > 0) {
                    counter = 1;
                    dataset.put(fatori.toString(), counter);
                }
                lastItem = fatori;
            }
            ssb_fact_expanded.delete(ssb_fact_expanded.length() - 1, ssb_fact_expanded.length());

            ssb_fatores = new SpannableStringBuilder(str_fatores);

            int value_length;
            colorIndex = 0;

            final Set<Map.Entry<String, Integer>> mapValues = dataset.entrySet();       // Confusão para sacar o último elemento
            final Map.Entry<String, Integer>[] test = new Map.Entry[mapValues.size()];  // (fator primo)
            mapValues.toArray(test);                                                    //
            String lastkey = test[0].getKey();                            //

            Iterator iterator = dataset.entrySet().iterator();

            //Criar os expoentes
            while (iterator.hasNext()) {
                Map.Entry pair = (Map.Entry) iterator.next();

                String key = pair.getKey().toString();
                String value = pair.getValue().toString();

                //if (lastkey != Integer.parseInt(pair.getKey().toString())) {
                if (!lastkey.equals(key)) {
                    colorIndex++;
                }

                if (Integer.parseInt(value) == 1) {
                    //Expoente 1
                    ssb_fatores.append(key);
                    ssb_fatores.setSpan(new ForegroundColorSpan(fColors.get(colorIndex)),
                            ssb_fatores.length() - key.length(), ssb_fatores.length(),
                            SPAN_EXCLUSIVE_EXCLUSIVE);

                } else if (Integer.parseInt(value) > 1) {
                    //Expoente superior a 1 // pair.getkey = fator; pair.getvalue = expoente

                    ssb_fatores.append(key);
                    ssb_fatores.setSpan(new ForegroundColorSpan(fColors.get(colorIndex)),
                            ssb_fatores.length() - key.length(), ssb_fatores.length(),
                            SPAN_EXCLUSIVE_EXCLUSIVE);
                    value_length = value.length();
                    ssb_fatores.append(value);
                    ssb_fatores.setSpan(new SuperscriptSpan(), ssb_fatores.length() - value_length, ssb_fatores.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb_fatores.setSpan(new RelativeSizeSpan(0.8f), ssb_fatores.length() - value_length, ssb_fatores.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                if (iterator.hasNext()) {
                    ssb_fatores.append("×");
                }
                lastkey = key;

                iterator.remove(); // avoids a ConcurrentModificationException
            }

            if (wasCanceled) {
                String incomplete_calc = "\n" + getString(R.string._incomplete_calc);
                ssb_fatores.append(incomplete_calc);
                ssb_fatores.setSpan(new ForegroundColorSpan(Color.RED), ssb_fatores.length() - incomplete_calc.length(), ssb_fatores.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb_fatores.setSpan(new RelativeSizeSpan(0.8f), ssb_fatores.length() - incomplete_calc.length(), ssb_fatores.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
            }


            // Todos os números primos divisores
            SpannableStringBuilder ssb_divisores = new SpannableStringBuilder();
            colorIndex = 0;
            Long currentLong = fatoresPrimos.get(0);
            for (int i = 0; i < sizeList - 1; i++) {
                Long fator_i = fatoresPrimos.get(i);
                if (currentLong != fator_i) {
                    colorIndex++;
                }
                currentLong = fator_i;

                String fa = fator_i + "\n";
                ssb_divisores.append(fa);
                ssb_divisores.setSpan(new ForegroundColorSpan(fColors.get(colorIndex)),
                        ssb_divisores.length() - fa.length(), ssb_divisores.length(),
                        SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            Long fator_i = fatoresPrimos.get(sizeList - 1);
            if (currentLong != fator_i) {
                colorIndex++;
            }
            ssb_divisores.append(fator_i.toString());
            ssb_divisores.setSpan(new ForegroundColorSpan(fColors.get(colorIndex)),
                    ssb_divisores.length() - fator_i.toString().length(), ssb_divisores.length(),
                    SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb_divisores.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, ssb_divisores.length(), SPAN_EXCLUSIVE_EXCLUSIVE);

            for (int i = 0; i < resultadosDivisao.size() - 1; i++) {
                str_results += String.valueOf(resultadosDivisao.get(i)) + "\n";
            }
            str_results += String.valueOf(resultadosDivisao.get(resultadosDivisao.size() - 1));

            createCardViewLayout(resultadosDivisao.get(0), historyLayout, str_results, ssb_divisores, ssb_fatores, ssb_fact_expanded, hasExpoentes); //ok
        }

        progressBar.setVisibility(View.GONE);
        button.setText(getString(fatorizar_btn));
        button.setClickable(true);
        cancelButton.setVisibility(View.GONE);
    }

    public class BackGroundOperation extends AsyncTask<Long, Float, ArrayList<ArrayList<Long>>> {

        @Override
        public void onPreExecute() {
            button.setClickable(false);
            button.setText(getString(R.string.working));
            cancelButton.setVisibility(View.VISIBLE);
            hideKeyboard();
            cv_width = cardView1.getWidth();
            height_dip = (int) (4 * scale + 0.5f);
            progressBar.setLayoutParams(new LinearLayout.LayoutParams(1, height_dip));
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<ArrayList<Long>> doInBackground(Long... num) {
            ArrayList<ArrayList<Long>> factoresPrimos = new ArrayList<ArrayList<Long>>();
            ArrayList<Long> results = new ArrayList<>();
            ArrayList<Long> divisores = new ArrayList<>();
            Long number = num[0];
            Float progress;
            Float oldProgress = 0f;

            results.add(number);

            while (number % 2L == 0) {
                divisores.add(2L);
                number /= 2;
                results.add(number);
            }

            for (long i = 3; i <= number / i; i += 2) {
                while (number % i == 0) {
                    divisores.add(i);
                    number /= i;
                    results.add(number);
                }
                progress = (float) i / (number / i);
                if (progress - oldProgress > 0.1f) {
                    publishProgress(progress, (float) i);
                    oldProgress = progress;
                }
                if (isCancelled()) break;
            }
            if (number > 1) {
                divisores.add(number);
            }

            if (number != 1) {
                results.add(1L);
            }

            factoresPrimos.add(results);
            factoresPrimos.add(divisores);

            return factoresPrimos;
        }

        @Override
        public void onProgressUpdate(Float... values) {
            if (thisFragment != null && thisFragment.isVisible()) {
                progressBar.setLayoutParams(new LinearLayout.LayoutParams(Math.round(values[0] * cv_width), height_dip));
            }
        }

        @Override
        protected void onPostExecute(ArrayList<ArrayList<Long>> result) {

            if (thisFragment != null && thisFragment.isVisible()) {
                processData(result, false);
            }
        }

        @Override
        protected void onCancelled(ArrayList<ArrayList<Long>> parcial) {
            super.onCancelled(parcial);


            if (thisFragment != null && thisFragment.isVisible()) {
                processData(parcial, true);
            }


        }


    }
}