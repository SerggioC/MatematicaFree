package com.sergiocruz.Matematica.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sergiocruz.Matematica.R;

import static android.graphics.Typeface.BOLD;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import static android.widget.LinearLayout.HORIZONTAL;

/*****
 * Project MatematicaFree
 * Package com.sergiocruz.Matematica.helper
 * Created by Sergio on 07/01/2017 20:01
 ******/

public class GetProLayout {

    public static void getItPopup(Context context) {
        Toast thetoast = Toast.makeText(context, R.string.getProFeature, Toast.LENGTH_LONG);
        thetoast.setGravity(Gravity.CENTER, 0, 0);
        thetoast.show();
    }


    public static LinearLayout get_ll_horizontal_pro_ad(final Activity mActivity) {

        //Linearlayout horizontal para monstrar versão pro
        LinearLayout ll_horizontal_Pro_ad = new LinearLayout(mActivity);
        ll_horizontal_Pro_ad.setOrientation(HORIZONTAL);
        ll_horizontal_Pro_ad.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        ImageView pro_icone = new ImageView(mActivity);
        pro_icone.setImageResource(R.mipmap.ic_launcher_pro);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.setMargins(0,0,10,0);
        pro_icone.setLayoutParams(layoutParams);

        TextView pro_ad_text = new TextView(mActivity);
        pro_ad_text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        pro_ad_text.setGravity(Gravity.LEFT);

        SpannableStringBuilder adtexttitle = new SpannableStringBuilder(mActivity.getString(R.string.app_long_description_PRO) + "\n");
        adtexttitle.setSpan(new AbsoluteSizeSpan(15, true), 0, adtexttitle.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        adtexttitle.setSpan(new StyleSpan(BOLD), 0, adtexttitle.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        String adsubtitlestring = mActivity.getString(R.string.getPro) + "\n" + mActivity.getString(R.string.adSubtitle);
        adtexttitle.append(adsubtitlestring);
        adtexttitle.setSpan(new AbsoluteSizeSpan(14, true), adtexttitle.length() - adsubtitlestring.length(), adtexttitle.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        pro_ad_text.setText(adtexttitle);

        ll_horizontal_Pro_ad.addView(pro_icone);
        ll_horizontal_Pro_ad.addView(pro_ad_text);
        ll_horizontal_Pro_ad.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(mActivity.getString(R.string.playstore_url)));
            mActivity.startActivity(intent);
        });
        ll_horizontal_Pro_ad.setTag(R.id.texto, "texto");
        return ll_horizontal_Pro_ad;
    }

}
