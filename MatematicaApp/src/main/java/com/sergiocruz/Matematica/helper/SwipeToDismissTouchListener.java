package com.sergiocruz.Matematica.helper;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.SpannableString;
import android.text.style.SuperscriptSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.sergiocruz.Matematica.R;

import java.util.ArrayList;

import static android.widget.Toast.makeText;

/*****
 * Project Matematica
 * Package com.sergiocruz.Matematica.helper
 * Created by Sergio on 25/10/2016 23:10
 ******/

public class SwipeToDismissTouchListener implements View.OnTouchListener {

    private static final int POPUP_WIDTH = 188;
    private static final int POPUP_HEIGHT = 190;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private final Handler handler = new Handler();
    boolean mBooleanIsPressed = false;
    // Cached ViewConfiguration and system-wide constant values
    private int mSlop;
    private int mMinFlingVelocity;
    private int mMaxFlingVelocity;
    private long mAnimationTime;
    // Fixed properties
    private View mView;
    private DismissCallbacks mCallbacks;
    private int mViewWidth = 1; // 1 and not 0 to prevent dividing by zero
    // Transient properties
    private float mDownX;
    private float mDownY;
    private boolean mSwiping;
    private int mSwipingSlop;
    private Activity mActivity;
    private final Runnable runnable = new Runnable() {
        public void run() {
            if (mBooleanIsPressed) {
                showCustomPopup();
                //show_popup_options();
                mBooleanIsPressed = false;
            }
        }
    };
    private VelocityTracker mVelocityTracker;
    private float mTranslationX;


    /**
     * Constructs a new swipe-to-dismiss touch listener for the given view.
     *
     * @param view      The view to make dismissable.
     * @param activity  An optional token/cookie object to be passed through to the callback.
     * @param callbacks The callback to trigger when the user has indicated that he would like to
     *                  dismiss this view.
     */
    public SwipeToDismissTouchListener(View view, Activity activity, DismissCallbacks callbacks) {
        ViewConfiguration vc = ViewConfiguration.get(view.getContext());
        mSlop = vc.getScaledTouchSlop();
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity() * 16;
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        mAnimationTime = 200;
        mView = view;
        mActivity = activity;
        mCallbacks = callbacks;
    }

    private static ArrayList<View> getViewsByTag(ViewGroup root, String tag) {
        ArrayList<View> views = new ArrayList<View>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((ViewGroup) child, tag));
            }
            final Object tagObj = child.getTag(R.id.texto);
            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }
        }
        return views;
    }


    private void showCustomPopup() {

        // Inflate the popup_menu_layout.xml
        LayoutInflater layoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popup_layout = layoutInflater.inflate(R.layout.popup_menu_layout, null);

        final float scale = mActivity.getResources().getDisplayMetrics().density;

        final int offset_x = (int) ((POPUP_WIDTH) * scale + 0.5f);
        final int offset_y = (int) ((POPUP_HEIGHT) * scale + 0.5f);

        // Creating the PopupWindow
        final PopupWindow customPopUp = new PopupWindow(mActivity);
        customPopUp.setContentView(popup_layout);
        customPopUp.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        customPopUp.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            customPopUp.setElevation(scale * 10.0f);
//        }
        customPopUp.setFocusable(true);
        customPopUp.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //Clear the default translucent background
        customPopUp.setAnimationStyle(R.style.popup_animation);
        customPopUp.showAtLocation(popup_layout, Gravity.NO_GRAVITY, (int) mDownX - offset_x, (int) mDownY - offset_y);

        final CardView theCardView = (CardView) this.mView;
        int selected_cv_color = ContextCompat.getColor(mActivity, R.color.selected_color);
        theCardView.setCardBackgroundColor(selected_cv_color);

        customPopUp.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                int cv_color = ContextCompat.getColor(mActivity, R.color.cardsColor);
                theCardView.setCardBackgroundColor(cv_color);
            }
        });
        popup_layout.findViewById(R.id.action_clipboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text_fromTextViews_final = getFormatedTextFromTextView();
                // aceder ao clipboard manager
                ClipboardManager clipboard = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                boolean hasEqualItem = false;
                if (clipboard.hasPrimaryClip()) {
                    int clipItems = clipboard.getPrimaryClip().getItemCount();
                    for (int i = 0; i < clipItems; i++) {
                        if (clipboard.getPrimaryClip().getItemAt(i).getText().toString().equals(text_fromTextViews_final)) {
                            hasEqualItem = true;
                        }
                    }
                }
                if (hasEqualItem) {
                    Toast thetoast = makeText(mView.getContext(), R.string.already_inclipboard, Toast.LENGTH_SHORT);
                    thetoast.setGravity(Gravity.CENTER, 0, 0);
                    thetoast.show();
                } else {
                    ClipData clip = ClipData.newPlainText("Clipboard", text_fromTextViews_final);
                    clipboard.setPrimaryClip(clip);
                    Toast thetoast = Toast.makeText(mView.getContext(), R.string.copied_toclipboard, Toast.LENGTH_SHORT);
                    thetoast.setGravity(Gravity.CENTER, 0, 0);
                    thetoast.show();
                }
                customPopUp.dismiss();
            }
        });

        popup_layout.findViewById(R.id.action_clear_result).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ViewGroup history = (ViewGroup) theCardView.getParent();
                animateRemoving(theCardView, history);
                customPopUp.dismiss();
            }
        });

        popup_layout.findViewById(R.id.action_share_result).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text_fromTextViews_final = getFormatedTextFromTextView();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, mActivity.getResources().getString(R.string.app_long_description) +
                        mActivity.getResources().getString(R.string.app_version_name) + "\n" + text_fromTextViews_final);
                sendIntent.setType("text/plain");
                mActivity.startActivity(Intent.createChooser(sendIntent, mActivity.getResources().getString(R.string.app_name)));
                customPopUp.dismiss();
            }
        });

        popup_layout.findViewById(R.id.action_save_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                theCardView.setCardBackgroundColor(ContextCompat.getColor(mActivity, R.color.cardsColor));

                GetProLayout.getItPopup(mActivity);

                customPopUp.dismiss();
            }
        });
    }

    @NonNull
    public String getFormatedTextFromTextView() {
        ArrayList<View> textViews_withTAG_texto = getViewsByTag((ViewGroup) mView, "texto");
        String text_fromTextViews_final = "";
        for (int i = 0; i < textViews_withTAG_texto.size(); i++) {
            if (textViews_withTAG_texto.get(i) instanceof TextView) {
                String text_fromTextView = (((TextView) textViews_withTAG_texto.get(i)).getText().toString()) + "\n";
                SpannableString ss = new SpannableString(((TextView) textViews_withTAG_texto.get(i)).getText());
                SuperscriptSpan[] spans = ss.getSpans(0, ((TextView) textViews_withTAG_texto.get(i)).getText().length(), SuperscriptSpan.class);
                int corr = 0;
                for (SuperscriptSpan span : spans) {
                    int start = ss.getSpanStart(span) + corr;
                    text_fromTextView = text_fromTextView.substring(0, start) + "^" + text_fromTextView.substring(start);
                    corr++;
                }
                text_fromTextViews_final += text_fromTextView;
            }
        }
        return text_fromTextViews_final;
    }

    void animateRemoving(final CardView cardview, final ViewGroup history) {
        cardview.animate().translationX(cardview.getWidth()).alpha(0).setDuration(200).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                try {
                    history.removeView(cardview);
                    mCallbacks.onDismiss(mView);
                } catch (Exception e) {
                }

                //performDismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        // offset because the view is translated during swipe
        motionEvent.offsetLocation(mTranslationX, 0);

        if (mViewWidth < 2) {
            mViewWidth = mView.getWidth();
        }

        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                // TODO: ensure this is a finger, and set a flag
                mDownX = motionEvent.getRawX();
                mDownY = motionEvent.getRawY();

                mVelocityTracker = VelocityTracker.obtain();
                mVelocityTracker.addMovement(motionEvent);

                // Execute your Runnable after 600 milliseconds = 0.5 second
                handler.postDelayed(runnable, 600);
                mBooleanIsPressed = true;

                return true;
            }

            case MotionEvent.ACTION_UP: {

                if (mBooleanIsPressed) {
                    mBooleanIsPressed = false;
                    handler.removeCallbacks(runnable);
                }

                if (mVelocityTracker == null) {
                    break;
                }
                float deltaX = motionEvent.getRawX() - mDownX;
                mVelocityTracker.addMovement(motionEvent);
                mVelocityTracker.computeCurrentVelocity(1000);
                float velocityX = mVelocityTracker.getXVelocity();
                float absVelocityX = Math.abs(velocityX);
                float absVelocityY = Math.abs(mVelocityTracker.getYVelocity());
                boolean dismiss = false;
                boolean dismissRight = false;
                if (Math.abs(deltaX) > mViewWidth / 2 && mSwiping) {
                    dismiss = true;
                    dismissRight = deltaX > 0;
                } else if (mMinFlingVelocity <= absVelocityX && absVelocityX <= mMaxFlingVelocity
                        && absVelocityY < absVelocityX
                        && absVelocityY < absVelocityX && mSwiping) {
                    // dismiss only if flinging in the same direction as dragging
                    dismiss = (velocityX < 0) == (deltaX < 0);
                    dismissRight = mVelocityTracker.getXVelocity() > 0;
                }
                if (dismiss) {

                    // dismiss
                    mView.animate()
                            .translationX(dismissRight ? mViewWidth : -mViewWidth)
                            .alpha(0)
                            .setDuration(mAnimationTime)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    if (mView != null) {
                                        final ViewGroup history = (ViewGroup) mView.getParent();
                                        history.removeView(mView);
                                        mCallbacks.onDismiss(mView);
                                    }
                                    //performDismiss();
                                }
                            });
                } else if (mSwiping) {
                    // cancel
                    mView.animate()
                            .translationX(0)
                            .alpha(1)
                            .setDuration(mAnimationTime)
                            .setListener(null);
                }
                try {
                    mVelocityTracker.recycle();
                } catch (Exception e) {
                }
                mVelocityTracker = null;
                mTranslationX = 0;
                mDownX = 0;
                mDownY = 0;
                mSwiping = false;
                break;
            }
            case MotionEvent.ACTION_SCROLL: {
                if (mBooleanIsPressed) {
                    mBooleanIsPressed = false;
                    handler.removeCallbacks(runnable);
                }
            }

            case MotionEvent.ACTION_CANCEL: {

                if (mBooleanIsPressed) {
                    mBooleanIsPressed = false;
                    handler.removeCallbacks(runnable);
                }

                if (mVelocityTracker == null) {
                    break;
                }
                mView.animate()
                        .translationX(0)
                        .alpha(1)
                        .setDuration(mAnimationTime)
                        .setListener(null);
                try {
                    mVelocityTracker.recycle();
                } catch (Exception e) {
                }
                mVelocityTracker = null;
                mTranslationX = 0;
                mDownX = 0;
                mDownY = 0;
                mSwiping = false;
                break;
            }

            case MotionEvent.ACTION_MOVE: {

                if (mVelocityTracker == null) {
                    break;
                }


                mVelocityTracker.addMovement(motionEvent);
                float deltaX = motionEvent.getRawX() - mDownX;
                float deltaY = motionEvent.getRawY() - mDownY;

                if (deltaY > 10 && mBooleanIsPressed) {

                    //a mover verticalmente não disparar longpress
                    if (mBooleanIsPressed) {
                        mBooleanIsPressed = false;
                        handler.removeCallbacks(runnable);
                    }
                }


                if (Math.abs(deltaX) > mSlop && Math.abs(deltaY) < Math.abs(deltaX) / 2) {
                    mSwiping = true;
                    mSwipingSlop = (deltaX > 0 ? mSlop : -mSlop);
                    mView.getParent().requestDisallowInterceptTouchEvent(true);

                    // Cancel listview's touch
                    MotionEvent cancelEvent = MotionEvent.obtain(motionEvent);
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL |
                            (motionEvent.getActionIndex() << MotionEvent.ACTION_POINTER_INDEX_SHIFT));
                    mView.onTouchEvent(cancelEvent);
                    try {
                        cancelEvent.recycle();
                    } catch (Exception e) {
                    }
                }

                if (mSwiping) {

                    //a mover não disparar longpress
                    if (mBooleanIsPressed) {
                        mBooleanIsPressed = false;
                        handler.removeCallbacks(runnable);
                    }
                    mTranslationX = deltaX;
                    mView.setTranslationX(deltaX - mSwipingSlop);
                    // TODO: use an ease-out interpolator or such
                    mView.setAlpha(Math.max(0f, Math.min(1f, 1f - 2f * Math.abs(deltaX) / mViewWidth)));
                    return true;
                }
                break;
            }
        }
        return false;
    }

    private void performDismiss() {
        // Animate the dismissed view to zero-height and then fire the dismiss callback.
        // This triggers layout on each animation frame; in the future we may want to do something
        // smarter and more performant.
        // set animateLayoutChanges="true" in xml layout
        final ViewGroup.LayoutParams lp = mView.getLayoutParams();
        final int originalHeight = mView.getHeight();

        ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 1).setDuration(mAnimationTime);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation2) {
                mCallbacks.onDismiss(mView);
                // Reset view presentation
                mView.setAlpha(1f);
                mView.setTranslationX(0);
                lp.height = originalHeight;
                mView.setLayoutParams(lp);

            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                lp.height = (Integer) valueAnimator.getAnimatedValue();
                mView.setLayoutParams(lp);
            }
        });

        animator.start();
    }

    /**
     * The callback interface used by {@link SwipeToDismissTouchListener} to inform its client
     * about a successful dismissal of the view for which it was created.
     */
    public interface DismissCallbacks {
        /**
         * Called to determine whether the view can be dismissed.
         */
        boolean canDismiss(Boolean token);

        /**
         * Called when the user has indicated they she would like to dismiss the view.
         *
         * @param view The originating {@link View} to be dismissed.
         */
        void onDismiss(View view);
    }
}
