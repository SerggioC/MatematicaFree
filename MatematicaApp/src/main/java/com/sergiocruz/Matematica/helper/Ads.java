package com.sergiocruz.Matematica.helper;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.sergiocruz.Matematica.R;

import java.net.MalformedURLException;
import java.net.URL;

public class Ads {

    private static AdRequest.Builder adRequestBuilder;
    private static ConsentForm form;

    @Nullable
    private static URL getPrivacyPolicyUrl(Context context) {
        URL privacyUrl = null;
        try {
            privacyUrl = new URL(context.getString(R.string.privacy_policy_url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return privacyUrl;
    }

    public static void showIn(Context context, AdView adView) {
        adRequestBuilder = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // Emulator
                .addTestDevice("6543A9731F19E7E829543EE20A1E4E7C"); // P8 lite
        checkAdsConsent(context, adView);
    }

    public static ConsentStatus getStatus(Context context) {
        ConsentInformation consentInformation = ConsentInformation.getInstance(context);
        return consentInformation.getConsentStatus();
    }

    public static void setStatus(Context context, ConsentStatus status) {
        ConsentInformation consentInformation = ConsentInformation.getInstance(context);
        consentInformation.setConsentStatus(status);
    }

    private static void checkAdsConsent(Context context, AdView adView) {
        ConsentInformation consentInformation = ConsentInformation.getInstance(context);
        String[] publisherIds = {context.getString(R.string.ads_publisher_id)};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                // User's consent status successfully updated.
                switch (consentStatus) {
                    case PERSONALIZED:
                        Log.d("Sergio>", "Showing Personalized ads");
                        showPersonalizedAds(adView);
                        break;
                    case NON_PERSONALIZED:
                        Log.d("Sergio>", "Showing Non-Personalized ads");
                        showNonPersonalizedAds(adView);
                        break;
                    case UNKNOWN:
                        Log.d("Sergio>", "Requesting Consent");
                        // Check if we are in Europe or Unknown location to request consent
                        if (ConsentInformation.getInstance(context).isRequestLocationInEeaOrUnknown()) {
                            requestConsent(context, adView);
                        } else {
                            showPersonalizedAds(adView);
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
            }
        });
    }

    public static void requestConsent(Context context, AdView adView) {
        URL privacyUrl = getPrivacyPolicyUrl(context);

        form = new ConsentForm.Builder(context, privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        // Consent form loaded successfully.
                        showForm(adView);
                    }

                    @Override
                    public void onConsentFormOpened() {
                        // Consent form was displayed.
                    }

                    @Override
                    public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        if (userPrefersAdFree) {
                            // Buy or Subscribe
                            // TODO buy me for some dollars!

                        } else {
                            Log.d("Sergio>", "Requesting Consent: Requesting consent again");
                            switch (consentStatus) {
                                case PERSONALIZED:
                                    showPersonalizedAds(adView);
                                    break;
                                case NON_PERSONALIZED:
                                    showNonPersonalizedAds(adView);
                                    break;
                                case UNKNOWN:
                                    showNonPersonalizedAds(adView);
                                    break;
                            }
                        }
                        // Consent form was closed.
                    }

                    @Override
                    public void onConsentFormError(String errorDescription) {
                        Log.d("Sergio>", "Requesting Consent: onConsentFormError. Error - " + errorDescription);
                        // Consent form error.
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .withAdFreeOption()
                .build();
        form.load();
    }

    private static void showForm(AdView adView) {
        if (form != null) {
            form.show();
        } else {
            showNonPersonalizedAds(adView);
            Log.i("Sergio>", "showForm(): form is null, can't show the consent form to select tailored ads or not or to buy the paid version!");
        }

    }

    private static void showPersonalizedAds(AdView adView) {
        AdRequest request = adRequestBuilder.build();
        adView.loadAd(request);
    }

    private static void showNonPersonalizedAds(AdView adView) {
        Bundle extras = new Bundle();
        extras.putString("npa", "1");
        AdRequest request = adRequestBuilder
                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                .build();
        adView.loadAd(request);
    }


}
