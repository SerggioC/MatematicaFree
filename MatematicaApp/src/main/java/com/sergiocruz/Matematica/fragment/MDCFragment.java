package com.sergiocruz.Matematica.fragment;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
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
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
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
import com.sergiocruz.Matematica.helper.CreateCardView;
import com.sergiocruz.Matematica.helper.GetProLayout;
import com.sergiocruz.Matematica.helper.SwipeToDismissTouchListener;

import java.math.BigInteger;
import java.util.ArrayList;

import static android.animation.LayoutTransition.CHANGE_APPEARING;
import static android.animation.LayoutTransition.CHANGE_DISAPPEARING;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import static android.widget.LinearLayout.HORIZONTAL;
import static android.widget.Toast.makeText;
import static com.sergiocruz.Matematica.fragment.MMCFragment.CARD_TEXT_SIZE;
import static com.sergiocruz.Matematica.helper.MenuHelperKt.removeHistory;
import static com.sergiocruz.Matematica.helper.UtilsKt.collapseIt;
import static com.sergiocruz.Matematica.helper.UtilsKt.expandIt;
import static java.lang.Long.parseLong;

public class MDCFragment extends Fragment {

    EditText mdc_num_1, mdc_num_2, mdc_num_3, mdc_num_4, mdc_num_5, mdc_num_6, mdc_num_7, mdc_num_8;
    float scale;
    int height_dip, cv_width;

    SharedPreferences sharedPrefs;
    private ViewGroup historyLayout;


    public MDCFragment() {
        // Required empty public constructor
    }

    private static BigInteger mdc(ArrayList<BigInteger> input) {
        BigInteger result = input.get(0);
        for (int i = 1; i < input.size(); i++)
            result = result.gcd(input.get(i));
        return result;
    }

    public void showToast() {
        Toast thetoast = makeText(getActivity(), R.string.numero_alto, Toast.LENGTH_SHORT);
        thetoast.setGravity(Gravity.CENTER, 0, 0);
        thetoast.show();
    }

    private void showToastNum(String field) {
        Toast thetoast = makeText(getActivity(), R.string.number_in_field + " " + field + " " + R.string.too_high, Toast.LENGTH_SHORT);
        thetoast.setGravity(Gravity.CENTER, 0, 0);
        thetoast.show();
    }

    private void showToastMoreThanZero() {
        Toast thetoast = makeText(getActivity(), R.string.maiores_qzero, Toast.LENGTH_LONG);
        thetoast.setGravity(Gravity.CENTER, 0, 0);
        thetoast.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        hideKeyboard();
    }

    public void hideKeyboard() {
        //Hide the keyboard
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.

        inflater.inflate(R.menu.menu_main, menu);
        inflater.inflate(R.menu.menu_sub_main, menu);
        inflater.inflate(R.menu.menu_help_mdc, menu);
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
            mdc_num_1.setText("");
            mdc_num_2.setText("");
            mdc_num_3.setText("");
            mdc_num_4.setText("");
            mdc_num_5.setText("");
            mdc_num_6.setText("");
            mdc_num_7.setText("");
            mdc_num_8.setText("");
        }

        if (id == R.id.action_ajuda) {
            String help_divisores = getString(R.string.help_text_mdc);
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_mdc, container, false);
        mdc_num_1 = view.findViewById(R.id.mdc_num_1);
        mdc_num_2 = view.findViewById(R.id.mdc_num_2);
        mdc_num_3 = view.findViewById(R.id.mdc_num_3);
        mdc_num_4 = view.findViewById(R.id.mdc_num_4);
        mdc_num_5 = view.findViewById(R.id.mdc_num_5);
        mdc_num_6 = view.findViewById(R.id.mdc_num_6);
        mdc_num_7 = view.findViewById(R.id.mdc_num_7);
        mdc_num_8 = view.findViewById(R.id.mdc_num_8);

        Button button = view.findViewById(R.id.button_calc_mdc);
        button.setOnClickListener(v -> calc_mdc(view));

        historyLayout = view.findViewById(R.id.history);

        view.findViewById(R.id.btn_clear_1).setOnClickListener(v -> mdc_num_1.setText(""));
        view.findViewById(R.id.btn_clear_2).setOnClickListener(v -> mdc_num_2.setText(""));
        view.findViewById(R.id.btn_clear_3).setOnClickListener(v -> mdc_num_3.setText(""));
        view.findViewById(R.id.btn_clear_4).setOnClickListener(v -> mdc_num_4.setText(""));
        view.findViewById(R.id.btn_clear_5).setOnClickListener(v -> mdc_num_5.setText(""));
        view.findViewById(R.id.btn_clear_6).setOnClickListener(v -> mdc_num_6.setText(""));
        view.findViewById(R.id.btn_clear_7).setOnClickListener(v -> mdc_num_7.setText(""));
        view.findViewById(R.id.btn_clear_8).setOnClickListener(v -> mdc_num_8.setText(""));

        view.findViewById(R.id.button_add_mdc).setOnClickListener(v -> add_mdc(view));
        view.findViewById(R.id.button_remove_mdc).setOnClickListener(v -> remove_mdc(view));

        mdc_num_1.addTextChangedListener(new TextWatcher() {
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
                        // Tentar converter o string para long
                        num1 = parseLong(s.toString());

                    } catch (Exception e) {
                        mdc_num_1.setText(oldnum1);
                        mdc_num_1.setSelection(mdc_num_1.getText().length());
                        showToast();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mdc_num_2.addTextChangedListener(new TextWatcher() {
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
                        // Tentar converter o string para long
                        num2 = parseLong(s.toString());
                    } catch (Exception e) {
                        mdc_num_2.setText(oldnum2);
                        mdc_num_2.setSelection(mdc_num_2.getText().length());
                        showToast();

                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mdc_num_3.addTextChangedListener(new TextWatcher() {
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
                        // Tentar converter o string para long
                        num3 = parseLong(s.toString());

                    } catch (Exception e) {
                        mdc_num_3.setText(oldnum3);
                        mdc_num_3.setSelection(mdc_num_3.getText().length());
                        showToast();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mdc_num_4.addTextChangedListener(new TextWatcher() {
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
                        // Tentar converter o string para long
                        num4 = parseLong(s.toString());
                    } catch (Exception e) {
                        mdc_num_4.setText(oldnum4);
                        mdc_num_4.setSelection(mdc_num_4.getText().length());
                        showToast();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mdc_num_5.addTextChangedListener(new TextWatcher() {
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
                        // Tentar converter o string para long
                        num5 = parseLong(s.toString());
                    } catch (Exception e) {
                        mdc_num_5.setText(oldnum5);
                        mdc_num_5.setSelection(mdc_num_5.getText().length());
                        showToast();
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mdc_num_6.addTextChangedListener(new TextWatcher() {
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
                        // Tentar converter o string para long
                        num6 = parseLong(s.toString());
                    } catch (Exception e) {
                        mdc_num_6.setText(oldnum6);
                        mdc_num_6.setSelection(mdc_num_6.getText().length());
                        showToast();

                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mdc_num_7.addTextChangedListener(new TextWatcher() {
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
                        // Tentar converter o string para long
                        num7 = parseLong(s.toString());
                    } catch (Exception e) {
                        mdc_num_7.setText(oldnum7);
                        mdc_num_7.setSelection(mdc_num_7.getText().length());
                        showToast();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mdc_num_8.addTextChangedListener(new TextWatcher() {
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
                        // Tentar converter o string para long
                        num8 = parseLong(s.toString());
                    } catch (Exception e) {
                        mdc_num_8.setText(oldnum8);
                        mdc_num_8.setSelection(mdc_num_8.getText().length());
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

    public void add_mdc(View view) {

        LinearLayout ll_34 = view.findViewById(R.id.linear_layout_34);
        LinearLayout ll_56 = view.findViewById(R.id.linear_layout_56);
        LinearLayout ll_78 = view.findViewById(R.id.linear_layout_78);
        FrameLayout f_3 = view.findViewById(R.id.frame_3);
        FrameLayout f_4 = view.findViewById(R.id.frame_4);
        FrameLayout f_5 = view.findViewById(R.id.frame_5);
        FrameLayout f_6 = view.findViewById(R.id.frame_6);
        FrameLayout f_7 = view.findViewById(R.id.frame_7);
        FrameLayout f_8 = view.findViewById(R.id.frame_8);
        ImageButton add_one = view.findViewById(R.id.button_add_mdc);
        ImageButton less_one = view.findViewById(R.id.button_remove_mdc);

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

    public void remove_mdc(View view) {

        LinearLayout ll_34 = view.findViewById(R.id.linear_layout_34);
        LinearLayout ll_56 = view.findViewById(R.id.linear_layout_56);
        LinearLayout ll_78 = view.findViewById(R.id.linear_layout_78);

        FrameLayout f_3 = view.findViewById(R.id.frame_3);
        FrameLayout f_4 = view.findViewById(R.id.frame_4);
        FrameLayout f_5 = view.findViewById(R.id.frame_5);
        FrameLayout f_6 = view.findViewById(R.id.frame_6);
        FrameLayout f_7 = view.findViewById(R.id.frame_7);
        FrameLayout f_8 = view.findViewById(R.id.frame_8);

        ImageButton add_one = view.findViewById(R.id.button_add_mdc);
        ImageButton less_one = view.findViewById(R.id.button_remove_mdc);

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
                mdc_num_8.setText("");
                f_8.setVisibility(View.GONE);
                add_one.setVisibility(View.VISIBLE);
                return;
            }
            if (f7_visible) {
                mdc_num_7.setText("");
                f_7.setVisibility(View.GONE);
                ll_78.setVisibility(View.GONE);
                return;
            }
        }

        if (ll_56_visibe) {
            if (f6_visible) {
                mdc_num_6.setText("");
                f_6.setVisibility(View.GONE);
                return;
            }
            if (f5_visible) {
                mdc_num_5.setText("");
                f_5.setVisibility(View.GONE);
                ll_56.setVisibility(View.GONE);
                return;
            }
        }

        if (ll_34_visibe) {
            if (f4_visible) {
                mdc_num_4.setText("");
                f_4.setVisibility(View.GONE);
                return;
            }
            if (f3_visible) {
                mdc_num_3.setText("");
                f_3.setVisibility(View.GONE);
                ll_34.setVisibility(View.GONE);
                less_one.setVisibility(View.INVISIBLE);
                return;
            }
        }

    }

    private void calc_mdc(View view) {
        hideKeyboard();

        String str_num1 = mdc_num_1.getText().toString();
        String str_num2 = mdc_num_2.getText().toString();
        String str_num3 = mdc_num_3.getText().toString();
        String str_num4 = mdc_num_4.getText().toString();
        String str_num5 = mdc_num_5.getText().toString();
        String str_num6 = mdc_num_6.getText().toString();
        String str_num7 = mdc_num_7.getText().toString();
        String str_num8 = mdc_num_8.getText().toString();

        long num1, num2, num3, num4, num5, num6, num7, num8;

        ArrayList<BigInteger> numbers = new ArrayList<>();
        ArrayList<Long> long_numbers = new ArrayList<>();

        if (!TextUtils.isEmpty(str_num1)) {
            try {
                // Tentar converter o string para long
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
                // Tentar converter o string para long
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
                // Tentar converter o string para long
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
                // Tentar converter o string para long
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
                // Tentar converter o string para long
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
                // Tentar converter o string para long
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
                // Tentar converter o string para long
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
                // Tentar converter o string para long
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
            Toast thetoast = Toast.makeText(getActivity(), R.string.add_number_pair, Toast.LENGTH_SHORT);
            thetoast.setGravity(Gravity.CENTER, 0, 0);
            thetoast.show();
            return;
        }

        String mdc_string = getString(R.string.mdc_result_prefix);
        BigInteger result_mdc = null;

        if (numbers.size() > 1) {
            for (int i = 0; i < numbers.size() - 1; i++) {
                mdc_string += numbers.get(i) + ", ";
            }
            mdc_string += numbers.get(numbers.size() - 1) + ")= ";
            result_mdc = mdc(numbers);
        }

        mdc_string += result_mdc;
        SpannableStringBuilder ssb = new SpannableStringBuilder(mdc_string);
        if (result_mdc.toString().equals("1")) {
            ssb.append("\n" + getString(R.string.primos_si));
            ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#29712d")), ssb.length() - 24, ssb.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(new RelativeSizeSpan(0.9f), ssb.length() - 24, ssb.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        }

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
        cardview.setLayoutTransition(new LayoutTransition());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            LayoutTransition lt = new LayoutTransition();
            lt.enableTransitionType(CHANGE_APPEARING);
            lt.enableTransitionType(CHANGE_DISAPPEARING);
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

                    }
                }));


        LinearLayout history = view.findViewById(R.id.history);
        // Add cardview to history layout at the top (index 0)
        history.addView(cardview, 0);

        LinearLayout ll_vertical_root = new LinearLayout(getContext());
        ll_vertical_root.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        ll_vertical_root.setOrientation(LinearLayout.VERTICAL);

        // criar novo Textview
        final TextView textView = new TextView(getContext());
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,   //largura
                ViewGroup.LayoutParams.WRAP_CONTENT)); //altura

        //Adicionar o texto com o resultado ao TextView
        textView.setText(ssb);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, CARD_TEXT_SIZE);
        textView.setTag(R.id.texto, "texto");

        // add the textview to the cardview
        ll_vertical_root.addView(textView);

        String shouldShowExplanation = sharedPrefs.getString(getString(R.string.show_explanation), "0");
        // -1 = sempre  0 = quando pedidas   1 = nunca
        if (shouldShowExplanation.equals("-1") || shouldShowExplanation.equals("0")) {
            createExplanations(cardview, ll_vertical_root, shouldShowExplanation);
        } else {
            cardview.addView(ll_vertical_root); //Só o resultado sem explicações
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

        //Linearlayout
        LinearLayout ll_horizontal = new LinearLayout(getContext());
        ll_horizontal.setOrientation(HORIZONTAL);
        ll_horizontal.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        final TextView explainLink = new TextView(getContext());
        explainLink.setTag("explainLink");
        explainLink.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,   //largura
                ViewGroup.LayoutParams.WRAP_CONTENT)); //altura
        explainLink.setTextSize(TypedValue.COMPLEX_UNIT_SP, CARD_TEXT_SIZE);
        explainLink.setTextColor(ContextCompat.getColor(getContext(), R.color.linkBlue));

        //View separator with gradient
        TextView gradient_separator = getGradientSeparator();

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

        LinearLayout ll_horizontal_Pro_ad = GetProLayout.getLlHorizontalProAd(getContext());

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

}