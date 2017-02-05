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
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sergiocruz.Matematica.R;
import com.sergiocruz.Matematica.activity.AboutActivity;
import com.sergiocruz.Matematica.activity.MainActivity;
import com.sergiocruz.Matematica.activity.SettingsActivity;
import com.sergiocruz.Matematica.helper.CreateCardView;
import com.sergiocruz.Matematica.helper.MenuHelper;
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
import static java.lang.Long.parseLong;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FatorizarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FatorizarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FatorizarFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Activity mActivity;
    ArrayList<Integer> fColors;
    /*
    *   AsyncTask params <Input datatype, progress update datatype, return datatype>
    * */
    AsyncTask<Long, Float, ArrayList<ArrayList<Long>>> BG_Operation = new BackGroundOperation();
    Button button;
    float scale;
    Fragment thisFragment = this;
    ImageView cancelButton;
    int cv_width, height_dip;
    LinearLayout history;
    Long num1, startTime;
    SharedPreferences sharedPrefs;
    View progressBar;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public FatorizarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FatorizarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FatorizarFragment newInstance(String param1, String param2) {
        FatorizarFragment fragment = new FatorizarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static ArrayList<Long> getFatoresPrimos(long number) {
        ArrayList<Long> factoresPrimos = new ArrayList<Long>();
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

    public static void expandIt(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();
        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapseIt(final View v) {
        final int initialHeight = v.getMeasuredHeight();
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    private ArrayList<ArrayList<Long>> getTabelaFatoresPrimos(long number) {

        ArrayList<ArrayList<Long>> factoresPrimos = new ArrayList<ArrayList<Long>>();
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

    private ArrayList<ArrayList<Long>> getTabelaFatoresPrimos2(long number) {

        ArrayList<ArrayList<Long>> factoresPrimos = new ArrayList<ArrayList<Long>>();
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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mActivity = getActivity();
        scale = mActivity.getResources().getDisplayMetrics().density;
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        int[] f_colors = mActivity.getResources().getIntArray(f_colors_xml);
        fColors = new ArrayList<>();
        for (int f_color : f_colors) fColors.add(f_color);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.getAds();
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
            MenuHelper.share_history(mActivity);
        }

        if (id == R.id.action_clear_all_history) {
            MenuHelper.remove_history(mActivity);
        }

        if (id == R.id.action_ajuda) {
            ViewGroup history = (ViewGroup) mActivity.findViewById(R.id.history);
            String help_divisores = getString(R.string.help_text_fatores);
            SpannableStringBuilder ssb = new SpannableStringBuilder(help_divisores);
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
//         Checks the orientation of the screen
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Toast.makeText(mActivity, "landscape", Toast.LENGTH_SHORT).show();
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//            Toast.makeText(mActivity, "portrait", Toast.LENGTH_SHORT).show();
//        }

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
        //Hide the keyboard
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (BG_Operation.getStatus() == AsyncTask.Status.RUNNING) {
            BG_Operation.cancel(true);
            Toast thetoast = Toast.makeText(mActivity, getString(R.string.canceled_op), Toast.LENGTH_SHORT);
            thetoast.setGravity(Gravity.CENTER, 0, 0);
            thetoast.show();
        }
    }

    public void cancel_AsyncTask() {
        if (BG_Operation.getStatus() == AsyncTask.Status.RUNNING) {
            BG_Operation.cancel(true);
            Toast thetoast = Toast.makeText(mActivity, getString(R.string.canceled_op), Toast.LENGTH_SHORT);
            thetoast.setGravity(Gravity.CENTER, 0, 0);
            thetoast.show();
            cancelButton.setVisibility(View.GONE);
            button.setText(getString(R.string.calculate));
            button.setClickable(true);
            progressBar.setVisibility(View.GONE);
        }
    }

    public void displayCancelDialogBox() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);

        // set title
        alertDialogBuilder.setTitle(getString(R.string.fatorize_title));

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
        final View view = inflater.inflate(R.layout.fragment_fatorizar, container, false);
        final EditText num_1 = (EditText) view.findViewById(R.id.editNumFact);

        cancelButton = (ImageView) view.findViewById(R.id.cancelTask);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCancelDialogBox();
            }
        });
        button = (Button) view.findViewById(R.id.button_calc_fatores);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcfatoresPrimos();
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
                    Toast thetoast = Toast.makeText(mActivity, getString(R.string.numero_alto), Toast.LENGTH_SHORT);
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

    private void calcfatoresPrimos() {
        startTime = System.nanoTime();
        EditText edittext = (EditText) mActivity.findViewById(R.id.editNumFact);
        String editnumText = edittext.getText().toString();
        long num;

        if (editnumText.equals(null) || editnumText.equals("") || editnumText == null) {
            Toast thetoast = Toast.makeText(mActivity, R.string.insert_integer, Toast.LENGTH_LONG);
            thetoast.setGravity(Gravity.CENTER, 0, 0);
            thetoast.show();
            return;
        }
        try {
            // Tentar converter o string para long
            num = Long.parseLong(editnumText);
        } catch (Exception e) {
            Toast thetoast = Toast.makeText(mActivity, getString(R.string.numero_alto), Toast.LENGTH_LONG);
            thetoast.setGravity(Gravity.CENTER, 0, 0);
            thetoast.show();
            return;
        }
        if (num == 0L || num == 1L) {
            Toast thetoast = Toast.makeText(mActivity, getString(R.string.the_number) + " " + num + " " + getString(R.string.has_no_factors), Toast.LENGTH_LONG);
            thetoast.setGravity(Gravity.CENTER, 0, 0);
            thetoast.show();
            return;
        }

        BG_Operation = new BackGroundOperation().execute(num);

    }

    void createCardViewLayout(Long number, final ViewGroup history, String str_results, SpannableStringBuilder str_divisores, SpannableStringBuilder ssb_fatores, SpannableStringBuilder str_fact_exp, Boolean hasExpoentes) {

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
        history.addView(cardview, 0);

        LinearLayout ll_vertical_root = new LinearLayout(mActivity);
        ll_vertical_root.setLayoutParams(new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_vertical_root.setOrientation(LinearLayout.VERTICAL);

        // criar novo Textview para o resultado da fatorização
        final TextView textView = new TextView(mActivity);
        textView.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        textView.setPadding(0, 0, 0, 0);

        //Adicionar o texto com o resultado da fatorizaçãoo com expoentes
        String str_num = getString(R.string.factorization_of) + " " + number + " = \n";
        SpannableStringBuilder ssb_num = new SpannableStringBuilder(str_num);
        ssb_num.append(ssb_fatores);
        textView.setText(ssb_num);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, CARD_TEXT_SIZE);
        textView.setTag(R.id.texto, "texto");

        // add the textview com os fatores multiplicados to the Linear layout vertical root
        ll_vertical_root.addView(textView);

        String shouldShowExplanation = sharedPrefs.getString("pref_show_explanation", "0");
        // -1 = sempre  0 = quando pedidas   1 = nunca
        if (shouldShowExplanation.equals("-1") || shouldShowExplanation.equals("0")) {

            LinearLayout ll_vertical_expl = new LinearLayout(mActivity);
            ll_vertical_expl.setLayoutParams(new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            ll_vertical_expl.setOrientation(LinearLayout.VERTICAL);
            ll_vertical_expl.setTag("ll_vertical_expl");

            TextView textView_expl1 = new TextView(mActivity);
            textView_expl1.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            textView_expl1.setTextSize(TypedValue.COMPLEX_UNIT_SP, CARD_TEXT_SIZE);
            String explain_text_1 = getString(R.string.expl_text_divisores_1);
            SpannableStringBuilder ssb_explain_1 = new SpannableStringBuilder(explain_text_1);
            ForegroundColorSpan boldColorSpan = new ForegroundColorSpan(ContextCompat.getColor(mActivity, R.color.boldColor));
            ssb_explain_1.setSpan(boldColorSpan, 0, ssb_explain_1.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
            textView_expl1.setText(ssb_explain_1);
            textView_expl1.setTag(R.id.texto, "texto");
            ll_vertical_expl.addView(textView_expl1);

            LinearLayout ll_horizontal = new LinearLayout(mActivity);
            ll_horizontal.setLayoutParams(new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            ll_horizontal.setOrientation(LinearLayout.HORIZONTAL);
            ll_horizontal.setTag("ll_horizontal_expl");

            LinearLayout ll_vertical_results = new LinearLayout(mActivity);
            ll_vertical_results.setLayoutParams(new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            ll_vertical_results.setOrientation(LinearLayout.VERTICAL);
            ll_vertical_results.setPadding(0, 0, (int) (4 * scale + 0.5f), 0);

            LinearLayout ll_vertical_separador = new LinearLayout(mActivity);
            ll_vertical_separador.setLayoutParams(new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
            ll_vertical_separador.setOrientation(LinearLayout.VERTICAL);
            ll_vertical_separador.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.separatorLineColor));
            int um_dip = (int) (1.2 * scale + 0.5f);
            ll_vertical_separador.setPadding(um_dip, 4, 0, um_dip);

            LinearLayout ll_vertical_divisores = new LinearLayout(mActivity);
            ll_vertical_divisores.setLayoutParams(new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            ll_vertical_divisores.setOrientation(LinearLayout.VERTICAL);
            ll_vertical_divisores.setPadding((int) (4 * scale + 0.5f), 0, (int) (8 * scale + 0.5f), 0);

            TextView textView_results = new TextView(mActivity);
            textView_results.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            textView_results.setTextSize(TypedValue.COMPLEX_UNIT_SP, CARD_TEXT_SIZE);
            textView_results.setGravity(Gravity.RIGHT);
            SpannableStringBuilder ssb_str_results = new SpannableStringBuilder(str_results);
            ssb_str_results.setSpan(new RelativeSizeSpan(0.9f), ssb_str_results.length() - str_results.length(), ssb_str_results.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
            textView_results.setText(ssb_str_results);
            textView_results.setTag(R.id.texto, "texto");

            ll_vertical_results.addView(textView_results);

            TextView textView_divisores = new TextView(mActivity);
            textView_divisores.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            textView_divisores.setTextSize(TypedValue.COMPLEX_UNIT_SP, CARD_TEXT_SIZE);
            textView_divisores.setGravity(Gravity.LEFT);
            SpannableStringBuilder ssb_str_divisores = new SpannableStringBuilder(str_divisores);
            ssb_str_divisores.setSpan(new RelativeSizeSpan(0.9f), ssb_str_divisores.length() - str_divisores.length(), ssb_str_divisores.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
            textView_divisores.setText(ssb_str_divisores);


            //textView_divisores.setText(str_divisores);



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

            final TextView explainLink = new TextView(mActivity);
            explainLink.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,   //largura
                    LinearLayout.LayoutParams.WRAP_CONTENT)); //altura
            explainLink.setTextSize(TypedValue.COMPLEX_UNIT_SP, CARD_TEXT_SIZE);
            explainLink.setTextColor(ContextCompat.getColor(mActivity, R.color.linkBlue));

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

            explainLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View explView = ((CardView) view.getParent().getParent().getParent()).findViewWithTag("ll_vertical_expl");
                    if (!isExpanded[0]) {
                        ((TextView) view).setText(ssb_hide_expl);
                        //explView.setVisibility(View.VISIBLE);
                        expandIt(explView);
                        isExpanded[0] = true;

                    } else if (isExpanded[0]) {
                        ((TextView) view).setText(ssb_show_expl);
                        //explView.setVisibility(View.GONE);
                        collapseIt(explView);
                        isExpanded[0] = false;
                    }
                }
            });
            TextView gradient_separator = getGradientSeparator();

            gradient_separator.setText("");

            //Linearlayout horizontal com o explainlink e gradiente
            LinearLayout ll_horizontal_link = new LinearLayout(mActivity);
            ll_horizontal_link.setOrientation(HORIZONTAL);
            ll_horizontal_link.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            ll_horizontal_link.addView(explainLink);
            ll_horizontal_link.addView(gradient_separator);

            ll_vertical_root.addView(ll_horizontal_link);

            ll_vertical_expl.addView(ll_horizontal);

            TextView textView_fact_expanded = new TextView(mActivity);
            textView_fact_expanded.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            textView_fact_expanded.setTextSize(TypedValue.COMPLEX_UNIT_SP, CARD_TEXT_SIZE);
            textView_fact_expanded.setGravity(Gravity.LEFT);
            String explain_text_2 = getString(R.string.explain_divisores2) + "\n";
            SpannableStringBuilder ssb_explain_2 = new SpannableStringBuilder(explain_text_2);
            ssb_explain_2.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mActivity, R.color.boldColor)), 0, ssb_explain_2.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb_explain_2.append(str_fact_exp);
            ssb_explain_2.setSpan(new RelativeSizeSpan(0.9f), ssb_explain_2.length() - str_fact_exp.length(), ssb_explain_2.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
            if (hasExpoentes) {
                String text_fact_repetidos = "\n" + getString(R.string.explain_divisores3) + "\n";
                ssb_explain_2.append(text_fact_repetidos);
                ssb_explain_2.setSpan(boldColorSpan, ssb_explain_2.length() - text_fact_repetidos.length(), ssb_explain_2.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb_explain_2.append(ssb_fatores);
                ssb_explain_2.setSpan(new RelativeSizeSpan(0.9f), ssb_explain_2.length() - ssb_fatores.length(), ssb_explain_2.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
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
    }

    @NonNull
    private TextView getGradientSeparator() {
        //View separator with gradient
        TextView gradient_separator = new TextView(mActivity);
        gradient_separator.setTag("gradient_separator");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            gradient_separator.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.bottom_border2));
        } else {
            gradient_separator.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.drawable.bottom_border2));
        }
        gradient_separator.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,   //largura
                LinearLayout.LayoutParams.WRAP_CONTENT)); //altura
        gradient_separator.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
        gradient_separator.setTextColor(ContextCompat.getColor(mActivity, R.color.lightBlue));
        gradient_separator.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        return gradient_separator;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class BackGroundOperation extends AsyncTask<Long, Float, ArrayList<ArrayList<Long>>> {

        @Override
        public void onPreExecute() {
            button.setClickable(false);
            button.setText(getString(R.string.working));
            cancelButton.setVisibility(View.VISIBLE);
            hideKeyboard();
            history = (LinearLayout) mActivity.findViewById(R.id.history);
            ViewGroup cardView1 = (ViewGroup) mActivity.findViewById(R.id.card_view_1);
            cv_width = cardView1.getWidth();
            progressBar = (View) mActivity.findViewById(R.id.progress);
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
                String str_divisores = "";
                SpannableStringBuilder ssb_fatores;


                if (sizeList == 1) {
                    str_fatores = resultadosDivisao.get(0) + " " + getString(R.string.its_a_prime);
                    ssb_fatores = new SpannableStringBuilder(str_fatores);
                    ssb_fatores.setSpan(new ForegroundColorSpan(Color.parseColor("#29712d")), 0, ssb_fatores.length(), SPAN_EXCLUSIVE_EXCLUSIVE); //verde
                    CreateCardView.create(history, ssb_fatores, mActivity);

                } else {
                    str_fatores = "";
                    Boolean hasExpoentes = false;
                    Integer counter = 1;
                    Long lastItem = fatoresPrimos.get(0);
                    String str_fact_expanded = "";

                    Collections.shuffle(fColors);
                    SpannableStringBuilder ssb_fact_expanded = new SpannableStringBuilder();
                    int colorIndex = 0;

                    //TreeMap
                    LinkedHashMap<String, Integer> dataset = new LinkedHashMap<>();

                    //Contar os expoentes
                    for (int i = 0; i < fatoresPrimos.size(); i++) {
                        Long fatori = fatoresPrimos.get(i);
                        if (lastItem != fatori) {
                            colorIndex++;
                        }

                        String fi = fatori.toString();
                        ssb_fact_expanded.append(fi);
                        ssb_fact_expanded.setSpan(new ForegroundColorSpan(fColors.get(colorIndex)),
                                ssb_fact_expanded.length() - fi.length(), ssb_fact_expanded.length(),
                                SPAN_EXCLUSIVE_EXCLUSIVE);
                        ssb_fact_expanded.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                                ssb_fact_expanded.length() - fi.length(), ssb_fact_expanded.length(),
                                SPAN_EXCLUSIVE_EXCLUSIVE);
                        ssb_fact_expanded.append("×");
                        str_fact_expanded += fatori + "×";

                        if (i == 0) {
                            dataset.put(String.valueOf(fatoresPrimos.get(0)), 1);
                        } else if (fatori.equals(lastItem) && i > 0) {
                            hasExpoentes = true;
                            counter++;
                            dataset.put(String.valueOf(fatori), counter);
                        } else if (!fatori.equals(lastItem) && i > 0) {
                            counter = 1;
                            dataset.put(String.valueOf(fatori), counter);
                        }
                        lastItem = fatori;
                    }
                    ssb_fact_expanded.delete(ssb_fact_expanded.length()-1,ssb_fact_expanded.length());

                    str_fact_expanded = str_fact_expanded.substring(0, str_fact_expanded.length() - 1);

                    ssb_fatores = new SpannableStringBuilder(str_fatores);

                    int value_length;
                    colorIndex = 0;

                    final Set<Map.Entry<String, Integer>> mapValues = dataset.entrySet();       // Confusão para sacar o último elemento
                    final Map.Entry<String, Integer>[] test = new Map.Entry[mapValues.size()];  //(fator primo)
                    mapValues.toArray(test);                                                    //
                    int lastkey = Integer.parseInt(test[0].getKey());                           //

                    Iterator iterator = dataset.entrySet().iterator();

                    //Criar os expoentes
                    while (iterator.hasNext()) {
                        Map.Entry pair = (Map.Entry) iterator.next();

                        if (lastkey != Integer.parseInt(pair.getKey().toString())) {
                            colorIndex++;
                        }

                        if (Integer.parseInt(pair.getValue().toString()) == 1) {
                            //Expoente 1
                            ssb_fatores.append(pair.getKey().toString());
                            ssb_fatores.setSpan(new ForegroundColorSpan(fColors.get(colorIndex)),
                                    ssb_fatores.length() - pair.getKey().toString().length(), ssb_fatores.length(),
                                    SPAN_EXCLUSIVE_EXCLUSIVE);


                        } else if (Integer.parseInt(pair.getValue().toString()) > 1) {
                            //Expoente superior a 1 // pair.getkey = fator; pair.getvalue = expoente

                            ssb_fatores.append(pair.getKey().toString());
                            ssb_fatores.setSpan(new ForegroundColorSpan(fColors.get(colorIndex)),
                                    ssb_fatores.length() - pair.getKey().toString().length(), ssb_fatores.length(),
                                    SPAN_EXCLUSIVE_EXCLUSIVE);

                            value_length = pair.getValue().toString().length();
                            ssb_fatores.append(pair.getValue().toString());
                            ssb_fatores.setSpan(new SuperscriptSpan(), ssb_fatores.length() - value_length, ssb_fatores.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                            ssb_fatores.setSpan(new RelativeSizeSpan(0.8f), ssb_fatores.length() - value_length, ssb_fatores.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                        }

                        if (iterator.hasNext()) {
                            ssb_fatores.append("×");
                        }
                        lastkey = Integer.parseInt(pair.getKey().toString());

                        iterator.remove(); // avoids a ConcurrentModificationException
                    }

                    SpannableStringBuilder ssb_divisores = new SpannableStringBuilder();
                    colorIndex = 0;
                    Long currentLong = fatoresPrimos.get(0);
                    for (int i = 0; i < sizeList - 1; i++) {
                        Long fator_i = fatoresPrimos.get(i);
                        if (currentLong != fator_i) {
                            colorIndex++;
                        }
                        currentLong = fator_i;

                        String fa = fator_i.toString() + "\n";
                        ssb_divisores.append(fa);
                        ssb_divisores.setSpan(new ForegroundColorSpan(fColors.get(colorIndex)),
                                ssb_divisores.length() - fa.length(), ssb_divisores.length(),
                                SPAN_EXCLUSIVE_EXCLUSIVE);

                        str_divisores += String.valueOf(fator_i) + "\n";

                    }
                    str_divisores += String.valueOf(fatoresPrimos.get(sizeList - 1)); //estava com strings simples aqui

                    Long fator_i = fatoresPrimos.get(sizeList - 1);
                    if (currentLong != fator_i) {
                        colorIndex++;
                    }
                    ssb_divisores.append(String.valueOf(fator_i));
                    ssb_divisores.setSpan(new ForegroundColorSpan(fColors.get(colorIndex)),
                            ssb_divisores.length() - fator_i.toString().length(), ssb_divisores.length(),
                            SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb_divisores.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, ssb_divisores.length(), SPAN_EXCLUSIVE_EXCLUSIVE);


                    for (int i = 0; i < resultadosDivisao.size() - 1; i++) {
                        str_results += String.valueOf(resultadosDivisao.get(i)) + "\n";
                    }
                    str_results += String.valueOf(resultadosDivisao.get(resultadosDivisao.size() - 1));

                    createCardViewLayout(resultadosDivisao.get(0), history, str_results, ssb_divisores, ssb_fatores, ssb_fact_expanded, hasExpoentes);
                }

                progressBar.setVisibility(View.GONE);
                button.setText(getString(fatorizar_btn));
                button.setClickable(true);
                cancelButton.setVisibility(View.GONE);
            }
        }

        @Override
        protected void onCancelled(ArrayList<ArrayList<Long>> parcial) {
            super.onCancelled(parcial);


            if (thisFragment != null && thisFragment.isVisible()) {

                /* resultadosDivisao|fatoresPrimos
                *                100|2
                *                 50|2
                *                 25|5
                *                  5|5
                *                  1|
                *
                * */

                ArrayList<Long> resultadosDivisao = parcial.get(0);
                ArrayList<Long> fatoresPrimos = parcial.get(1);

                // Tamanho da lista de números primos
                int sizeList = fatoresPrimos.size();

                String str_fatores = "";
                String str_results = "";
                String str_divisores = "";
                SpannableStringBuilder ssb_fatores;

                if (sizeList == 1) {
                    str_fatores = resultadosDivisao.get(0) + " " + getString(R.string.its_a_prime);
                    ssb_fatores = new SpannableStringBuilder(str_fatores);
                    CreateCardView.create(history, ssb_fatores, mActivity);

                } else {
                    str_fatores = "";
                    Boolean hasExpoentes = false;
                    Integer counter = 1;
                    Long lastItem = fatoresPrimos.get(0);
                    String str_fact_expanded = "";

                    //TreeMap
                    LinkedHashMap<String, Integer> dataset = new LinkedHashMap<>();

                    //Contar os expoentes
                    for (int i = 0; i < fatoresPrimos.size(); i++) {
                        str_fact_expanded += fatoresPrimos.get(i) + "×";
                        if (i == 0) {
                            dataset.put(String.valueOf(fatoresPrimos.get(0)), 1);
                        } else if (fatoresPrimos.get(i).equals(lastItem) && i > 0) {
                            hasExpoentes = true;
                            counter++;
                            dataset.put(String.valueOf(fatoresPrimos.get(i)), counter);
                        } else if (!fatoresPrimos.get(i).equals(lastItem) && i > 0) {
                            counter = 1;
                            dataset.put(String.valueOf(fatoresPrimos.get(i)), counter);
                        }
                        lastItem = fatoresPrimos.get(i);
                    }

                    str_fact_expanded = str_fact_expanded.substring(0, str_fact_expanded.length() - 1);

                    ssb_fatores = new SpannableStringBuilder(str_fatores);

                    int value_length;

                    Iterator iterator = dataset.entrySet().iterator();

                    //Criar os expoentes
                    while (iterator.hasNext()) {
                        Map.Entry pair = (Map.Entry) iterator.next();

                        if (Integer.parseInt(pair.getValue().toString()) == 1) {
                            //Expoente 1
                            ssb_fatores.append(pair.getKey().toString());

                        } else if (Integer.parseInt(pair.getValue().toString()) > 1) {
                            //Expoente superior a 1
                            value_length = pair.getValue().toString().length();
                            ssb_fatores.append(pair.getKey().toString() + pair.getValue().toString());
                            ssb_fatores.setSpan(new SuperscriptSpan(), ssb_fatores.length() - value_length, ssb_fatores.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                            ssb_fatores.setSpan(new RelativeSizeSpan(0.8f), ssb_fatores.length() - value_length, ssb_fatores.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                        }

                        if (iterator.hasNext()) {
                            ssb_fatores.append("×");
                        }

                        iterator.remove(); // avoids a ConcurrentModificationException
                    }

                    String incomplete_calc = "\n" + getString(R.string._incomplete_calc);
                    ssb_fatores.append(incomplete_calc);
                    ssb_fatores.setSpan(new ForegroundColorSpan(Color.RED), ssb_fatores.length() - incomplete_calc.length(), ssb_fatores.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                    ssb_fatores.setSpan(new RelativeSizeSpan(0.8f), ssb_fatores.length() - incomplete_calc.length(), ssb_fatores.length(), SPAN_EXCLUSIVE_EXCLUSIVE);

                    for (int i = 0; i < sizeList - 1; i++) {
                        str_divisores += String.valueOf(fatoresPrimos.get(i)) + "\n";
                    }
                    str_divisores += String.valueOf(fatoresPrimos.get(sizeList - 1));

                    for (int i = 0; i < resultadosDivisao.size() - 1; i++) {
                        str_results += String.valueOf(resultadosDivisao.get(i)) + "\n";
                    }
                    str_results += String.valueOf(resultadosDivisao.get(resultadosDivisao.size() - 1));
//TODO err
                    //createCardViewLayout(resultadosDivisao.get(0), history, str_results, str_divisores, ssb_fatores, str_fact_expanded, hasExpoentes);
                }

                progressBar.setVisibility(View.GONE);
                button.setText(getString(R.string.fatorizar_btn));
                button.setClickable(true);
                cancelButton.setVisibility(View.GONE);
            }


        }


    }
}