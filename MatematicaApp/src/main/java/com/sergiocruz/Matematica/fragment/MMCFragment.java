package com.sergiocruz.Matematica.fragment;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sergiocruz.Matematica.R;
import com.sergiocruz.Matematica.activity.AboutActivity;
import com.sergiocruz.Matematica.activity.SettingsActivity;
import com.sergiocruz.Matematica.helper.GetProLayout;
import com.sergiocruz.Matematica.helper.SwipeToDismissTouchListener;

import java.math.BigInteger;
import java.util.ArrayList;

import static android.animation.LayoutTransition.CHANGE_APPEARING;
import static android.animation.LayoutTransition.CHANGE_DISAPPEARING;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import static android.widget.LinearLayout.HORIZONTAL;
import static com.sergiocruz.Matematica.helper.CreateCardView.create;
import static com.sergiocruz.Matematica.helper.MenuHelperKt.removeHistory;
import static com.sergiocruz.Matematica.helper.UtilsKt.collapseIt;
import static com.sergiocruz.Matematica.helper.UtilsKt.expandIt;
import static java.lang.Long.parseLong;

public class MMCFragment extends Fragment {
    public static final int CARD_TEXT_SIZE = 15;

    EditText mmc_num_1, mmc_num_2, mmc_num_3, mmc_num_4, mmc_num_5, mmc_num_6, mmc_num_7, mmc_num_8;
    float scale;
    int height_dip, cv_width;
    SharedPreferences sharedPrefs;
    private LinearLayout historyLayout;

    public MMCFragment() {
        // Required empty public constructor
    }


    /*****************************************************************
     * MMC: Mínimo múltiplo Comum (LCM: Least Common Multiplier)
     *****************************************************************/
    private static BigInteger mmc(BigInteger a, BigInteger b) {
        return b.divide(a.gcd(b)).multiply(a);
    }

    private static BigInteger mmc(ArrayList<BigInteger> input) {
        BigInteger result = input.get(0);
        for (int i = 1; i < input.size(); i++)
            result = mmc(result, input.get(i));
        return result;
    }

    private void showToast() {
        Toast thetoast = Toast.makeText(getContext(), R.string.numero_alto, Toast.LENGTH_SHORT);
        thetoast.setGravity(Gravity.CENTER, 0, 0);
        thetoast.show();
    }

    private void showToastNum(String field) {
        Toast thetoast = Toast.makeText(getContext(), getString(R.string.number_in_field) + " " + field + " " + getString(R.string.too_high), Toast.LENGTH_SHORT);
        thetoast.setGravity(Gravity.CENTER, 0, 0);
        thetoast.show();
    }

    private void showToastMoreThanZero() {
        Toast thetoast = Toast.makeText(getContext(), R.string.maiores_qzero, Toast.LENGTH_SHORT);
        thetoast.setGravity(Gravity.CENTER, 0, 0);
        thetoast.show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // have a menu in this fragment
        setHasOptionsMenu(true);
        
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        scale = getResources().getDisplayMetrics().density;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;  //int height = size.y;
        int lr_dip = (int) (4 * scale + 0.5f) * 2;
        cv_width = width - lr_dip;

        hideKeyboard();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.

        inflater.inflate(R.menu.menu_main, menu);
        inflater.inflate(R.menu.menu_sub_main, menu);
        inflater.inflate(R.menu.menu_help_mmc, menu);
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
            mmc_num_1.setText("");
            mmc_num_2.setText("");
            mmc_num_3.setText("");
            mmc_num_4.setText("");
            mmc_num_5.setText("");
            mmc_num_6.setText("");
            mmc_num_7.setText("");
            mmc_num_8.setText("");
        }
        if (id == R.id.action_ajuda) {
            String help_divisores = getString(R.string.help_text_mmc);
            SpannableStringBuilder ssb = new SpannableStringBuilder(help_divisores);
            create(historyLayout, ssb, getActivity());
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_mmc, container, false);

        mmc_num_1 = view.findViewById(R.id.mmc_num_1);
        mmc_num_2 = view.findViewById(R.id.mmc_num_2);
        mmc_num_3 = view.findViewById(R.id.mmc_num_3);
        mmc_num_4 = view.findViewById(R.id.mmc_num_4);
        mmc_num_5 = view.findViewById(R.id.mmc_num_5);
        mmc_num_6 = view.findViewById(R.id.mmc_num_6);
        mmc_num_7 = view.findViewById(R.id.mmc_num_7);
        mmc_num_8 = view.findViewById(R.id.mmc_num_8);

        historyLayout = view.findViewById(R.id.history);


        Button button = view.findViewById(R.id.button_calc_mmc);
        button.setOnClickListener(v -> calc_mmc());

        Button clearTextBtn_1 = view.findViewById(R.id.btn_clear_1);
        clearTextBtn_1.setOnClickListener(v -> mmc_num_1.setText(""));
        Button clearTextBtn_2 = view.findViewById(R.id.btn_clear_2);
        clearTextBtn_2.setOnClickListener(v -> mmc_num_2.setText(""));
        Button clearTextBtn_3 = view.findViewById(R.id.btn_clear_3);
        clearTextBtn_3.setOnClickListener(v -> mmc_num_3.setText(""));
        Button clearTextBtn_4 = view.findViewById(R.id.btn_clear_4);
        clearTextBtn_4.setOnClickListener(v -> mmc_num_4.setText(""));
        Button clearTextBtn_5 = view.findViewById(R.id.btn_clear_5);
        clearTextBtn_5.setOnClickListener(v -> mmc_num_5.setText(""));
        Button clearTextBtn_6 = view.findViewById(R.id.btn_clear_6);
        clearTextBtn_6.setOnClickListener(v -> mmc_num_6.setText(""));
        Button clearTextBtn_7 = view.findViewById(R.id.btn_clear_7);
        clearTextBtn_7.setOnClickListener(v -> mmc_num_7.setText(""));
        Button clearTextBtn_8 = view.findViewById(R.id.btn_clear_8);
        clearTextBtn_8.setOnClickListener(v -> mmc_num_8.setText(""));

        ImageButton add_mmc = view.findViewById(R.id.button_add_mmc);
        add_mmc.setOnClickListener(v -> add_mmc(view));

        ImageButton remove_mmc = view.findViewById(R.id.button_remove_mmc);
        remove_mmc.setOnClickListener(v -> remove_mmc(view));

        mmc_num_1.addTextChangedListener(new TextWatcher() {
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
                        mmc_num_1.setText(oldnum1);
                        mmc_num_1.setSelection(mmc_num_1.getText().length()); //Colocar o cursor no final do texto
                        showToast();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mmc_num_2.addTextChangedListener(new TextWatcher() {
            Long num2;
            String oldnum2;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldnum2 = s.toString();

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    try {
                        // Tentar converter o string para Long
                        num2 = parseLong(s.toString());
                    } catch (Exception e) {
                        mmc_num_2.setText(oldnum2);
                        mmc_num_2.setSelection(mmc_num_2.getText().length());
                        showToast();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mmc_num_3.addTextChangedListener(new TextWatcher() {
            Long num3;
            String oldnum3;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldnum3 = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    try {
                        // Tentar converter o string para Long
                        num3 = parseLong(s.toString());

                    } catch (Exception e) {
                        mmc_num_3.setText(oldnum3);
                        mmc_num_3.setSelection(mmc_num_3.getText().length());
                        showToast();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mmc_num_4.addTextChangedListener(new TextWatcher() {
            Long num4;
            String oldnum4;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldnum4 = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    try {
                        // Tentar converter o string para Long
                        num4 = parseLong(s.toString());

                    } catch (Exception e) {
                        mmc_num_4.setText(oldnum4);
                        mmc_num_4.setSelection(mmc_num_4.getText().length());
                        showToast();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mmc_num_5.addTextChangedListener(new TextWatcher() {
            Long num5;
            String oldnum5;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldnum5 = s.toString();

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    try {
                        // Tentar converter o string para Long
                        num5 = parseLong(s.toString());

                    } catch (Exception e) {
                        mmc_num_5.setText(oldnum5);
                        mmc_num_5.setSelection(mmc_num_5.getText().length());
                        showToast();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mmc_num_6.addTextChangedListener(new TextWatcher() {
            Long num6;
            String oldnum6;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldnum6 = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    try {
                        // Tentar converter o string para Long
                        num6 = parseLong(s.toString());
                    } catch (Exception e) {
                        mmc_num_6.setText(oldnum6);
                        mmc_num_6.setSelection(mmc_num_6.getText().length());
                        showToast();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mmc_num_7.addTextChangedListener(new TextWatcher() {
            Long num7;
            String oldnum7;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldnum7 = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    try {
                        // Tentar converter o string para Long
                        num7 = parseLong(s.toString());
                    } catch (Exception e) {
                        mmc_num_7.setText(oldnum7);
                        mmc_num_7.setSelection(mmc_num_7.getText().length());
                        showToast();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mmc_num_8.addTextChangedListener(new TextWatcher() {
            Long num8;
            String oldnum8;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldnum8 = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    try {
                        // Tentar converter o string para Long
                        num8 = parseLong(s.toString());
                    } catch (Exception e) {
                        mmc_num_8.setText(oldnum8);
                        mmc_num_8.setSelection(mmc_num_8.getText().length());
                        showToast();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    public void add_mmc(View view) {

        LinearLayout ll_34 = view.findViewById(R.id.linear_layout_34);
        LinearLayout ll_56 = view.findViewById(R.id.linear_layout_56);
        LinearLayout ll_78 = view.findViewById(R.id.linear_layout_78);
        FrameLayout f_3 = view.findViewById(R.id.frame_3);
        FrameLayout f_4 = view.findViewById(R.id.frame_4);
        FrameLayout f_5 = view.findViewById(R.id.frame_5);
        FrameLayout f_6 = view.findViewById(R.id.frame_6);
        FrameLayout f_7 = view.findViewById(R.id.frame_7);
        FrameLayout f_8 = view.findViewById(R.id.frame_8);
        ImageButton add_one = view.findViewById(R.id.button_add_mmc);
        ImageButton less_one = view.findViewById(R.id.button_remove_mmc);

        boolean ll_34_visibe = ll_34.getVisibility() == View.VISIBLE;
        boolean f3_visible = f_3.getVisibility() == View.VISIBLE;
        boolean f4_visible = f_4.getVisibility() == View.VISIBLE;
        boolean ll_56_visibe = ll_56.getVisibility() == View.VISIBLE;
        boolean f5_visible = f_5.getVisibility() == View.VISIBLE;
        boolean f6_visible = f_6.getVisibility() == View.VISIBLE;
        boolean ll_78_visibe = ll_78.getVisibility() == View.VISIBLE;
        boolean f7_visible = f_7.getVisibility() == View.VISIBLE;
        boolean f8_visible = f_8.getVisibility() == View.VISIBLE;


        if (!ll_34_visibe || f3_visible || f4_visible) {
            ll_34.setVisibility(View.VISIBLE);

            if (!f3_visible) {
                f_3.setVisibility(View.VISIBLE);
                less_one.setVisibility(View.VISIBLE);
                return;
            }
            if (!f4_visible) {
                f_4.setVisibility(View.VISIBLE);
                return;
            }
        }

        if (!ll_56_visibe || f5_visible || f6_visible) {
            ll_56.setVisibility(View.VISIBLE);

            if (!f5_visible) {
                f_5.setVisibility(View.VISIBLE);
                return;
            }
            if (!f6_visible) {
                f_6.setVisibility(View.VISIBLE);
                return;
            }
        }
        if (!ll_78_visibe || f7_visible || f8_visible) {
            ll_78.setVisibility(View.VISIBLE);

            if (!f7_visible) {
                f_7.setVisibility(View.VISIBLE);
                return;
            }
            if (!f8_visible) {
                f_8.setVisibility(View.VISIBLE);
                add_one.setVisibility(View.INVISIBLE);
                return;
            }
        }

    }

    public void remove_mmc(View view) {

        LinearLayout ll_34 = view.findViewById(R.id.linear_layout_34);
        LinearLayout ll_56 = view.findViewById(R.id.linear_layout_56);
        LinearLayout ll_78 = view.findViewById(R.id.linear_layout_78);

        FrameLayout f_3 = view.findViewById(R.id.frame_3);
        FrameLayout f_4 = view.findViewById(R.id.frame_4);
        FrameLayout f_5 = view.findViewById(R.id.frame_5);
        FrameLayout f_6 = view.findViewById(R.id.frame_6);
        FrameLayout f_7 = view.findViewById(R.id.frame_7);
        FrameLayout f_8 = view.findViewById(R.id.frame_8);

        ImageButton add_one = view.findViewById(R.id.button_add_mmc);
        ImageButton less_one = view.findViewById(R.id.button_remove_mmc);

        boolean ll_34_visibe = ll_34.getVisibility() == View.VISIBLE;
        boolean f3_visible = f_3.getVisibility() == View.VISIBLE;
        boolean f4_visible = f_4.getVisibility() == View.VISIBLE;
        boolean ll_56_visibe = ll_56.getVisibility() == View.VISIBLE;
        boolean f5_visible = f_5.getVisibility() == View.VISIBLE;
        boolean f6_visible = f_6.getVisibility() == View.VISIBLE;
        boolean ll_78_visibe = ll_78.getVisibility() == View.VISIBLE;
        boolean f7_visible = f_7.getVisibility() == View.VISIBLE;
        boolean f8_visible = f_8.getVisibility() == View.VISIBLE;

        if (ll_78_visibe) {
            if (f8_visible) {
                mmc_num_8.setText("");
                f_8.setVisibility(View.GONE);
                add_one.setVisibility(View.VISIBLE);
                return;
            }
            if (f7_visible) {
                mmc_num_7.setText("");
                f_7.setVisibility(View.GONE);
                ll_78.setVisibility(View.GONE);
                return;
            }
        }

        if (ll_56_visibe) {
            if (f6_visible) {
                mmc_num_6.setText("");
                f_6.setVisibility(View.GONE);
                return;
            }
            if (f5_visible) {
                mmc_num_5.setText("");
                f_5.setVisibility(View.GONE);
                ll_56.setVisibility(View.GONE);
                return;
            }
        }

        if (ll_34_visibe) {
            if (f4_visible) {
                mmc_num_4.setText("");
                f_4.setVisibility(View.GONE);
                f_4.setAlpha(0);
                return;
            }
            if (f3_visible) {
                mmc_num_3.setText("");
                f_3.setVisibility(View.GONE);
                f_3.setAlpha(0);
                ll_34.setVisibility(View.GONE);
                less_one.setVisibility(View.INVISIBLE);
                return;
            }
        }

    }

    public void hideKeyboard() {
        //Hide the keyboard
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    private void calc_mmc() {
        hideKeyboard();

        String str_num1 = mmc_num_1.getText().toString().replaceAll("[^\\d]", "");
        String str_num2 = mmc_num_2.getText().toString().replaceAll("[^\\d]", "");
        String str_num3 = mmc_num_3.getText().toString().replaceAll("[^\\d]", "");
        String str_num4 = mmc_num_4.getText().toString().replaceAll("[^\\d]", "");
        String str_num5 = mmc_num_5.getText().toString().replaceAll("[^\\d]", "");
        String str_num6 = mmc_num_6.getText().toString().replaceAll("[^\\d]", "");
        String str_num7 = mmc_num_7.getText().toString().replaceAll("[^\\d]", "");
        String str_num8 = mmc_num_8.getText().toString().replaceAll("[^\\d]", "");

        long num1, num2, num3, num4, num5, num6, num7, num8;

        ArrayList<BigInteger> numbers = new ArrayList<BigInteger>();
        ArrayList<Long> long_numbers = new ArrayList<Long>();

        if (!TextUtils.isEmpty(str_num1)) {
            try {
                // Tentar converter o string para Long
                num1 = parseLong(str_num1);
                if (num1 == 0L) {
                    showToastMoreThanZero();
                    return;
                } else if (num1 > 0L) {
                    BigInteger num1b = new BigInteger(str_num1);
                    numbers.add(num1b);
                    long_numbers.add(num1);
                }
            } catch (Exception e) {
                showToastNum("1");
                return;
            }
        }
        if (!TextUtils.isEmpty(str_num2)) {
            try {
                // Tentar converter o string para Long
                num2 = parseLong(str_num2);
                if (num2 == 0L) {
                    showToastMoreThanZero();
                    return;
                } else if (num2 > 0L) {
                    BigInteger num2b = new BigInteger(str_num2);
                    numbers.add(num2b);
                    long_numbers.add(num2);
                }
            } catch (Exception e) {
                showToastNum("2");
                return;
            }
        }
        if (!TextUtils.isEmpty(str_num3)) {
            try {
                // Tentar converter o string para Long
                num3 = parseLong(str_num3);
                if (num3 == 0L) {
                    showToastMoreThanZero();
                    return;
                } else if (num3 > 0L) {
                    BigInteger num3b = new BigInteger(str_num3);
                    numbers.add(num3b);
                    long_numbers.add(num3);
                }
            } catch (Exception e) {
                showToastNum("3");
                return;
            }
        }
        if (!TextUtils.isEmpty(str_num4)) {
            try {
                // Tentar converter o string para Long
                num4 = parseLong(str_num4);
                if (num4 == 0L) {
                    showToastMoreThanZero();
                    return;
                } else if (num4 > 0L) {
                    BigInteger num4b = new BigInteger(str_num4);
                    numbers.add(num4b);
                    long_numbers.add(num4);
                }
            } catch (Exception e) {
                showToastNum("4");
                return;
            }
        }
        if (!TextUtils.isEmpty(str_num5)) {
            try {
                // Tentar converter o string para Long
                num5 = parseLong(str_num5);
                if (num5 == 0L) {
                    showToastMoreThanZero();
                    return;
                } else if (num5 > 0L) {
                    BigInteger num5b = new BigInteger(str_num5);
                    numbers.add(num5b);
                    long_numbers.add(num5);
                }
            } catch (Exception e) {
                showToastNum("5");
                return;
            }
        }
        if (!TextUtils.isEmpty(str_num6)) {
            try {
                // Tentar converter o string para Long
                num6 = parseLong(str_num6);
                if (num6 == 0L) {
                    showToastMoreThanZero();
                    return;
                } else if (num6 > 0L) {
                    BigInteger num6b = new BigInteger(str_num6);
                    numbers.add(num6b);
                    long_numbers.add(num6);
                }
            } catch (Exception e) {
                showToastNum("6");
                return;
            }
        }

        if (!TextUtils.isEmpty(str_num7)) {
            try {
                // Tentar converter o string para Long
                num7 = parseLong(str_num7);
                if (num7 == 0L) {
                    showToastMoreThanZero();
                    return;
                } else if (num7 > 0L) {
                    BigInteger num7b = new BigInteger(str_num7);
                    numbers.add(num7b);
                    long_numbers.add(num7);
                }
            } catch (Exception e) {
                showToastNum("7");
                return;
            }
        }

        if (!TextUtils.isEmpty(str_num8)) {
            try {
                // Tentar converter o string para Long
                num8 = parseLong(str_num8);
                if (num8 == 0L) {
                    showToastMoreThanZero();
                    return;
                } else if (num8 > 0L) {
                    BigInteger num8b = new BigInteger(str_num8);
                    numbers.add(num8b);
                    long_numbers.add(num8);
                }
            } catch (Exception e) {
                showToastNum("8");
                return;
            }
        }
        if (numbers.size() < 2) {
            Toast thetoast = Toast.makeText(getContext(), R.string.add_number_pair, Toast.LENGTH_SHORT);
            thetoast.setGravity(Gravity.CENTER, 0, 0);
            thetoast.show();
            return;
        }

        String mmc_string = getString(R.string.mmc_result_prefix);
        BigInteger result_mmc = null;

        if (numbers.size() > 1) {
            for (int i = 0; i < numbers.size() - 1; i++) {
                mmc_string += numbers.get(i) + ", ";
            }
            mmc_string += numbers.get(numbers.size() - 1) + ")= ";
            result_mmc = mmc(numbers);
        }

        mmc_string += result_mmc;

        //criar novo cardview
        CardView cardview = new CardView(getContext());
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            LayoutTransition lt = new LayoutTransition();
            lt.enableTransitionType(CHANGE_APPEARING);
            lt.enableTransitionType(CHANGE_DISAPPEARING);
            cardview.setLayoutTransition(lt);
        }

        int cv_color = ContextCompat.getColor(getContext(), R.color.cardsColor);
        cardview.setCardBackgroundColor(cv_color);

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
                        //historyLayout.removeView(cardview);
                    }
                }));


        
        // Add cardview to historyLayout layout at the top (index 0)
        historyLayout.addView(cardview, 0);

        LinearLayout ll_vertical_root = new LinearLayout(getContext());
        ll_vertical_root.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        ll_vertical_root.setOrientation(LinearLayout.VERTICAL);

        // criar novo Textview
        final TextView textView = new TextView(getContext());
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,   //largura
                LinearLayout.LayoutParams.WRAP_CONTENT)); //altura

        //Adicionar o texto com o resultado
        textView.setText(mmc_string);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, CARD_TEXT_SIZE);
        textView.setTag(R.id.texto, "texto");

        // add the textview to the cardview
        ll_vertical_root.addView(textView);

        String shouldShowExplanation = sharedPrefs.getString(getString(R.string.show_explanation), "0");
        // -1 = sempre  0 = quando pedidas   1 = nunca
        if (shouldShowExplanation.equals("-1") || shouldShowExplanation.equals("0")) {
            createExplanations(cardview, ll_vertical_root, shouldShowExplanation);
        } else {
            cardview.addView(ll_vertical_root);
        }
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

    private void createExplanations(CardView cardview, LinearLayout ll_vertical_root, String shouldShowExplanation) {

        final SpannableStringBuilder ssb_hide_expl = new SpannableStringBuilder(getString(R.string.hide_explain));
        ssb_hide_expl.setSpan(new UnderlineSpan(), 0, ssb_hide_expl.length() - 2, SPAN_EXCLUSIVE_EXCLUSIVE);
        final SpannableStringBuilder ssb_show_expl = new SpannableStringBuilder(getString(R.string.explain));
        ssb_show_expl.setSpan(new UnderlineSpan(), 0, ssb_show_expl.length() - 2, SPAN_EXCLUSIVE_EXCLUSIVE);

        //Linearlayout horizontal com o explainlink e gradiente
        LinearLayout ll_horizontal = new LinearLayout(getContext());
        ll_horizontal.setOrientation(HORIZONTAL);
        ll_horizontal.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        final TextView explainLink = new TextView(getContext());
        explainLink.setTag("explainLink");
        explainLink.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,   //largura
                LinearLayout.LayoutParams.WRAP_CONTENT)); //altura
        explainLink.setTextSize(TypedValue.COMPLEX_UNIT_SP, CARD_TEXT_SIZE);
        explainLink.setTextColor(ContextCompat.getColor(getContext(), R.color.linkBlue));
        explainLink.setGravity(Gravity.CENTER_VERTICAL);

        //View separator with gradient
        TextView gradient_separator = getGradientSeparator();

        ll_horizontal.setGravity(Gravity.CENTER_VERTICAL);

        final Boolean[] isExpanded = {false};
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

        ll_horizontal.addView(explainLink);
        ll_horizontal.addView(gradient_separator);

        //LL vertical das explicações
        LinearLayout ll_vertical_expl = new LinearLayout(getContext());
        ll_vertical_expl.setTag("ll_vertical_expl");
        ll_vertical_expl.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        ll_vertical_expl.setOrientation(LinearLayout.VERTICAL);
        ll_vertical_expl.setLayoutTransition(new LayoutTransition());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            LayoutTransition lt = new LayoutTransition();
            lt.enableTransitionType(CHANGE_APPEARING);
            lt.enableTransitionType(CHANGE_DISAPPEARING);
            ll_vertical_expl.setLayoutTransition(lt);
        }

        LinearLayout ll_horizontal_Pro_ad = GetProLayout.getLlHorizontalProAd(getActivity());

        ll_vertical_expl.addView(ll_horizontal_Pro_ad);
        ll_vertical_root.addView(ll_horizontal);
        ll_vertical_root.addView(ll_vertical_expl);

        if (shouldShowExplanation.equals("-1")) {  //Always show Explanation
            ll_vertical_expl.setVisibility(View.VISIBLE);
            explainLink.setText(ssb_hide_expl);
            isExpanded[0] = true;
        } else if (shouldShowExplanation.equals("0")) { // Show Explanation on demand on click
            ll_vertical_expl.setVisibility(View.GONE);
            explainLink.setText(ssb_show_expl);
            isExpanded[0] = false;
        }
        cardview.addView(ll_vertical_root);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
