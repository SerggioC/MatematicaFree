package com.sergiocruz.Matematica.helper;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.SpannableStringBuilder;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sergiocruz.Matematica.R;

/*****
 * Project Matematica
 * Package com.sergiocruz.Matematica.helper
 * Created by Sergio on 03/11/2016 22:27
 ******/

public class CreateCardView {

    public static void create(final ViewGroup history, SpannableStringBuilder ssb_result, final Activity activity) {

        //criar novo cardview
        final CardView cardview = new CardView(history.getContext());
        cardview.setLayoutParams(new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT,   // width
                CardView.LayoutParams.WRAP_CONTENT)); // height
        cardview.setPreventCornerOverlap(true);

        //int pixels = (int) (dips * scale + 0.5f);
        final float scale = history.getContext().getResources().getDisplayMetrics().density;
        int lr_dip = (int) (6 * scale + 0.5f);
        int tb_dip = (int) (8 * scale + 0.5f);
        cardview.setRadius((int) (2 * scale + 0.5f));
        cardview.setCardElevation((int) (2 * scale + 0.5f));
        cardview.setContentPadding(lr_dip, tb_dip, lr_dip, tb_dip);
        cardview.setUseCompatPadding(true);

        int cv_color = ContextCompat.getColor(history.getContext(), R.color.cardsColor);
        cardview.setCardBackgroundColor(cv_color);

        // Add cardview to history layout at the top (index 0)
        history.addView(cardview, 0);

        // criar novo Textview
        final TextView textView = new TextView(history.getContext());
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,   //largura
                ViewGroup.LayoutParams.WRAP_CONTENT)); //altura

        //Adicionar o texto com o resultado
        textView.setText(ssb_result);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setTag(R.id.texto, "texto");

        // add the textview to the cardview
        cardview.addView(textView);

        // Create a generic swipe-to-dismiss touch listener.
        cardview.setOnTouchListener(
                new SwipeToDismissTouchListener(
                        cardview,
                        activity,
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

}