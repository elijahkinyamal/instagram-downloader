package com.namelox.instagram.downloader.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.namelox.instagram.downloader.BuildConfig;
import com.namelox.instagram.downloader.R;
import com.namelox.instagram.downloader.adapters.StoryAdapter;
import com.namelox.instagram.downloader.adapters.ProfileAdapter;
import com.namelox.instagram.downloader.api.CommonAPI;
import com.namelox.instagram.downloader.api.data.EdgeModel;
import com.namelox.instagram.downloader.api.data.EdgeSidecarModel;
import com.namelox.instagram.downloader.api.data.ResponseModel;
import com.namelox.instagram.downloader.api.model.FullDetailModel;
import com.namelox.instagram.downloader.api.model.NodeModel;
import com.namelox.instagram.downloader.api.model.StoryModel;
import com.namelox.instagram.downloader.api.model.TrayModel;
import com.namelox.instagram.downloader.constants.Constants;
import com.namelox.instagram.downloader.dialogs.RateDialog;
import com.namelox.instagram.downloader.fragments.DownloadFragment;
import com.namelox.instagram.downloader.fragments.InstagramWebFragment;
import com.namelox.instagram.downloader.listener.UserListInterface;
import com.namelox.instagram.downloader.preference.SharePrefs;
import com.namelox.instagram.downloader.utils.DirectoryUtils;
import com.namelox.instagram.downloader.utils.Utils;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.observers.DisposableObserver;

import static com.namelox.instagram.downloader.utils.DirectoryUtils.createFile;


public class MainActivity extends AppCompatActivity implements UserListInterface {
    MainActivity mainActivity;
    CommonAPI commonAPI;
    private static final String PRODUCT_ID = "remove_ads_pro";
    private static final String MERCHANT_ID = "15903383679150127845";
    private static final String LICENSE_KEY = "CIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIACgKCAQEAjx0knBnJuTmi5Afe1Kw6heP4HvpAxPR8PQOmf51C8GA3b1BvljdmU0kXI/ekWOEbBqGwey+X2LGsmfKWwEtPS1emJy64p5Q/bL3QhLiONS0kbUchxRbW2HBgtwBQCfWOv94EvYa+Mc4x7LglovIf80/YqcHYSmrbyE+JJaFGAvHOHJb/m/s51hetH6ujhNM/8nPYx8Kg+7UWnNpmQH7VlFaE91Zm0sCKA2qUqszBOdJtTPA7u1euKV2U2BHZM9XRQ11tdH/l3I+bn8u5dvU+mFCW6Yxd0ZhavJPyJB5mpfFIE/2M56WfqY6rV6yBms2gT3TFCJU0Mx36cnYwxF6DGwIDAQAB";
    private BillingProcessor bp;
    private boolean readyToPurchase = false;
    public static boolean isPro = false;
    public SharedPreferences sharedPreferencess;
    public SharedPreferences.Editor editor;
    SharedPreferences.Editor edito;
    public static String PROAPP = "PROAPP";
    private FirebaseAnalytics mFirebaseAnalytics;
    private ClipboardManager clipboardManager;
    public TextView textViewPaste;
    public TextView textViewDownload;
    public ImageView imageViewOpenFacebook;
    public EditText editTextPasteUrl;
    private InterstitialAd interstitial;
    private UnifiedNativeAd nativeAd;
    public AdView adViewAdMob;
    boolean isDark;
    private ImageView imageViewPurchase;
    private CardView cardViewAD;
    FragmentTransaction fragmentTransaction;
    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    LinearLayout linearLayoutAccount;
    ScrollView contraintLayoutLink;
    LinearLayout linearLayoutGallery;
    LinearLayout linearLayoutRate;
    LinearLayout linearLayoutShare;
    LinearLayout linearLayoutFeedBack;
    LinearLayout linearLayoutPrivacy;
    LinearLayout linearLayoutApps;
    ScrollView scrollViewHelp;
    BubbleNavigationConstraintView bubbleNavigationLinearView;
    StoryAdapter storyAdapter;
    ProfileAdapter profileAdapter;
    private String photoUrl;
    private String videoUrl;
    LinearLayout linearLayoutStories;
    LinearLayout linearLayoutContainer;
    LinearLayout linearLayoutPlaceHolder;
    RecyclerView recyclerViewUser;
    RecyclerView recyclerViewStories;
    private TextView textViewLogin;
    ProgressBar progressLoadingBar;
    private Switch switchDarkTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("push", MODE_PRIVATE);
        isDark = sharedPreferences.getBoolean("dark", false);
        if (isDark) {
            setTheme(R.style.facebook_theme_dark);
        } else {
            setTheme(R.style.facebook_theme_light);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        sharedPreferencess = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        isPro = sharedPreferencess.getBoolean(PROAPP, false);
        edito = sharedPreferencess.edit();
        if (!BillingProcessor.isIabServiceAvailable(this)) {
            showToast("In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
        }

        bp = new BillingProcessor(this, LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
                edito.putBoolean(PROAPP, true);
                imageViewPurchase.setVisibility(View.GONE);
                edito.commit();
                showToast("Purchase Success ");
                isPro = sharedPreferencess.getBoolean(PROAPP,false);
            }

            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {
                showToast("onBillingError: " + Integer.toString(errorCode));
            }

            @Override
            public void onBillingInitialized() {

                readyToPurchase = true;

            }

            @Override
            public void onPurchaseHistoryRestored() {

                for (String sku : bp.listOwnedProducts())
                    Log.d("LOG_TAG", "Owned Managed Product: " + sku);
                for (String sku : bp.listOwnedSubscriptions())
                    Log.d("LOG_TAG", "Owned Subscription: " + sku);

            }
        });

        if(!isPro) {
            MobileAds.initialize(this,  getResources().getString(R.string.admob_app_id));
            adsViews();
            interstitialAdMobAds();
            loadNativeAd();
        }

        Fragment mFragment = null;
        mFragment = new InstagramWebFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayoutAccount, mFragment).commit();
        mainActivity = this;
        commonAPI = CommonAPI.getInstance(mainActivity);

        switchDarkTheme = findViewById(R.id.theme_switch);


        if (isDark) {
            switchDarkTheme.setChecked(true);
        }else {
            switchDarkTheme.setChecked(false);
        }

        switchDarkTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    SharedPreferences.Editor editor = getSharedPreferences("push", MODE_PRIVATE).edit();
                    editor.putBoolean("dark",true);
                    editor.apply();

                } else {
                    SharedPreferences.Editor editor = getSharedPreferences("push", MODE_PRIVATE).edit();
                    editor.putBoolean("dark",false);
                    editor.apply();
                }
                startActivity(new Intent(MainActivity.this, SplashActivity.class));
                finish();
            }
        });

        createFile();
        initViews();

        if (Build.VERSION.SDK_INT >= 23) {
            checkPermissions(0);
        }

        bubbleNavigationLinearView = findViewById(R.id.top_navigation_constraint);
        linearLayoutAccount = findViewById(R.id.linearLayoutAccount);
        contraintLayoutLink = findViewById(R.id.contraintLayoutLink);
        linearLayoutGallery = findViewById(R.id.linearLayoutGallery);
        scrollViewHelp = findViewById(R.id.scrollViewHelp);
        this.linearLayoutShare = findViewById(R.id.linearLayoutShare);
        this.linearLayoutContainer = findViewById(R.id.linearLayoutContainer);
        this.linearLayoutRate = findViewById(R.id.linearLayoutRate);
        this.linearLayoutFeedBack = findViewById(R.id.linearLayoutFeedBack);
        this.linearLayoutPrivacy = findViewById(R.id.linearLayoutPrivacy);
        this.linearLayoutApps = findViewById(R.id.linearLayoutApps);

        this.linearLayoutShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("img_text/plain");
                intent.putExtra("android.intent.extra.SUBJECT", MainActivity.this.getString(R.string.app_name));
                intent.putExtra("android.intent.extra.TEXT", "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                MainActivity.this.startActivity(Intent.createChooser(intent, "Choose"));
            }
        });

        this.linearLayoutRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RateDialog(MainActivity.this, false).show();
            }
        });

        this.linearLayoutFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.SEND");
                intent.putExtra("android.intent.extra.EMAIL", new String[]{getResources().getString(R.string.email_feedback)});
                intent.putExtra("android.intent.extra.SUBJECT", getResources().getString(R.string.app_name) + " feedback : ");
                intent.putExtra("android.intent.extra.TEXT", "");
                intent.setType("message/rfc822");
                MainActivity.this.startActivity(Intent.createChooser(intent, MainActivity.this.getString(R.string.choose_email) + " :"));
            }
        });

        this.linearLayoutPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(getString(R.string.policy_url))));
                } catch (Exception ignored) {
                }
            }
        });

        this.linearLayoutApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.isConnected(MainActivity.this)) {
                    String url = getResources().getString(R.string.developer_account_link);
                    Intent apps = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    if (url != null && Constants.isAvailable(apps, MainActivity.this)) {
                        startActivity(apps);
                    } else {
                        Toast.makeText(MainActivity.this, "There is no app availalbe for this task", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bubbleNavigationLinearView.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int i) {
                switch (i){
                    case 0:
                        contraintLayoutLink.setVisibility(View.VISIBLE);
                        linearLayoutAccount.setVisibility(View.GONE);
                        linearLayoutGallery.setVisibility(View.GONE);
                        linearLayoutContainer.setVisibility(View.GONE);
                        scrollViewHelp.setVisibility(View.GONE);
                        break;
                    case 1:
                        contraintLayoutLink.setVisibility(View.GONE);
                        linearLayoutAccount.setVisibility(View.GONE);
                        linearLayoutGallery.setVisibility(View.GONE);
                        linearLayoutContainer.setVisibility(View.VISIBLE);
                        scrollViewHelp.setVisibility(View.GONE);
                        break;
                    case 2:
                        contraintLayoutLink.setVisibility(View.GONE);
                        linearLayoutAccount.setVisibility(View.VISIBLE);
                        linearLayoutGallery.setVisibility(View.GONE);
                        linearLayoutContainer.setVisibility(View.GONE);
                        scrollViewHelp.setVisibility(View.GONE);
                        break;
                    case 3:
                        if (interstitial != null && interstitial.isLoaded()) {
                            interstitial.show();
                        }
                        contraintLayoutLink.setVisibility(View.GONE);
                        linearLayoutAccount.setVisibility(View.GONE);
                        linearLayoutGallery.setVisibility(View.VISIBLE);
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frameLayoutGallery, new DownloadFragment());
                        fragmentTransaction.commit();
                        scrollViewHelp.setVisibility(View.GONE);
                        linearLayoutContainer.setVisibility(View.GONE);
                        break;
                    case 4:
                        linearLayoutContainer.setVisibility(View.GONE);
                        contraintLayoutLink.setVisibility(View.GONE);
                        linearLayoutAccount.setVisibility(View.GONE);
                        linearLayoutGallery.setVisibility(View.GONE);
                        scrollViewHelp.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }

    private boolean checkPermissions(int type) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(MainActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) (MainActivity.this),
                    listPermissionsNeeded.toArray(new
                            String[listPermissionsNeeded.size()]), type);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startGalleryActivity();
            } else {
            }
            return;
        }

    }

    private void adsViews() {
        adViewAdMob = new AdView(this);
        adViewAdMob.setAdUnitId(getString(R.string.admob_banner_id));
        adViewAdMob.setAdSize(getAdSize(this));
        ((FrameLayout) this.findViewById(R.id.fb_banner_Container)).addView(adViewAdMob);
        AdRequest build = new AdRequest.Builder().build();
        adViewAdMob.loadAd(build);
    }

    private static com.google.android.gms.ads.AdSize getAdSize(Activity activity) {
        Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        return com.google.android.gms.ads.AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, (int) (((float) displayMetrics.widthPixels) / displayMetrics.density));
    }

    public void interstitialAdMobAds(){
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(getResources().getString(R.string.admob_inter_id));
        interstitial.loadAd(new AdRequest.Builder().build());

        interstitial.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {

                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
            }
        });

    }

    private void loadNativeAd() {
        MobileAds.initialize(this, getString(R.string.admob_app_id));
        refreshAd();
    }

    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {

        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));

        ((TextView)adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }
        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }
        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }
        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }
        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());

        }
        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }
        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);

        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }
        adView.setNativeAd(nativeAd);
    }

    private void refreshAd() {
        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.admob_native_id));
        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                if (nativeAd != null) {
                    nativeAd.destroy();
                }
                nativeAd = unifiedNativeAd;
                FrameLayout frameLayout = findViewById(R.id.id_native_ad);

                UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater().inflate(R.layout.native_ads, null);

                populateUnifiedNativeAdView(unifiedNativeAd, adView);
                frameLayout.removeAllViews();
                frameLayout.addView(adView);

            }
        });

        NativeAdOptions adOptions = new NativeAdOptions.Builder().build();
        builder.withNativeAdOptions(adOptions);
        AdLoader adLoader = builder.withAdListener (new AdListener(){

            @Override
            public void onAdFailedToLoad(int i) {

            }
        }).build();
        adLoader.loadAd(new AdRequest.Builder().build());

    }


    public void startGalleryActivity() {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mainActivity = this;
        assert mainActivity != null;
        clipboardManager = (ClipboardManager) mainActivity.getSystemService(CLIPBOARD_SERVICE);
        pasteText();
    }

    public void initPurchaseDialog() {
        final Dialog dialog = new Dialog(this,  R.style.UploadDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_purchase);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.show();
        TextView textViewPurchase = dialog.findViewById(R.id.textViewPurchase);
        textViewPurchase.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                bp.purchase(MainActivity.this, PRODUCT_ID);
                dialog.dismiss();
            }
        });
        ImageView imageViewClose = dialog.findViewById(R.id.imageViewClose);
        imageViewClose.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void initViews() {
        this.imageViewOpenFacebook = findViewById(R.id.imageViewOpenFacebook);
        this.textViewPaste = findViewById(R.id.textViewPaste);
        this.textViewDownload = findViewById(R.id.textViewDownload);
        this.editTextPasteUrl = findViewById(R.id.editTextPasteUrl);
        this.linearLayoutStories = findViewById(R.id.linearLayoutStories);
        this.linearLayoutPlaceHolder = findViewById(R.id.linearLayoutPlaceHold);
        this.textViewLogin = findViewById(R.id.textViewLogin);
        this.recyclerViewUser = findViewById(R.id.recyclerViewUser);
        this.recyclerViewStories = findViewById(R.id.recyclerViewStories);
        this.progressLoadingBar = findViewById(R.id.progressLoadingBar);
        this.imageViewPurchase = findViewById(R.id.imageViewPurchase);
        this.cardViewAD = findViewById(R.id.cardViewAD);
        this.imageViewPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPurchaseDialog();
            }
        });

        if(!isPro) {
            this.cardViewAD.setVisibility(View.VISIBLE);
            this.imageViewPurchase.setVisibility(View.VISIBLE);
        } else {
            this.imageViewPurchase.setVisibility(View.GONE);
            this.cardViewAD.setVisibility(View.GONE);
        }

        clipboardManager = (ClipboardManager) mainActivity.getSystemService(CLIPBOARD_SERVICE);

        this.textViewDownload.setOnClickListener(v -> {
            String obj = this.editTextPasteUrl.getText().toString();
            if (obj.equals("")) {
                Utils.setToast(this.mainActivity, getResources().getString(R.string.enter_url));
            } else if (!Patterns.WEB_URL.matcher(obj).matches()) {
                Utils.setToast(this.mainActivity, getResources().getString(R.string.enter_valid_url));
            } else {
                if (interstitial != null && interstitial.isLoaded()) {
                    interstitial.show();
                }
                getInstagramData();
            }
        });

        this.textViewPaste.setOnClickListener(v -> {
            pasteText();
        });
        this.imageViewOpenFacebook.setOnClickListener(v -> {
            try {
                startActivity(getPackageManager().getLaunchIntentForPackage(Constants.INSTAGRAM_PACKAGE));
            } catch (Exception unused) {
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + Constants.INSTAGRAM_PACKAGE)));
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        this.recyclerViewUser.setLayoutManager(gridLayoutManager);
        this.recyclerViewUser.setNestedScrollingEnabled(false);
        gridLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        this.linearLayoutStories.setVisibility(View.VISIBLE);
        if (SharePrefs.getInstance(this.mainActivity).getBoolean(SharePrefs.IS_INSTAGRAM_LOGIN).booleanValue()) {
            getStoriesApi();
            this.textViewLogin.setText(getResources().getString(R.string.logout));
            this.linearLayoutStories.setVisibility(View.VISIBLE);
            this.linearLayoutPlaceHolder.setVisibility(View.GONE);
        } else {
            this.textViewLogin.setText(getResources().getString(R.string.login));
            this.linearLayoutStories.setVisibility(View.GONE);
            this.linearLayoutPlaceHolder.setVisibility(View.VISIBLE);
        }
        this.textViewLogin.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                if (!SharePrefs.getInstance(mainActivity).getBoolean(SharePrefs.IS_INSTAGRAM_LOGIN).booleanValue()) {
                    startActivityForResult(new Intent(mainActivity, LoginActivity.class), 100);
                    return;
                }
                initLogoutDialog();
            }
        });

        this.recyclerViewStories.setLayoutManager(new GridLayoutManager(MainActivity.this.getApplicationContext(), 3));
        this.recyclerViewStories.setAdapter(this.storyAdapter);
        this.recyclerViewStories.setHasFixedSize(true);
    }

    public void initLogoutDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this, R.style.SheetDialog);
        bottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bottomSheetDialog.setContentView(R.layout.dialog_login);
        bottomSheetDialog.show();
        TextView textViewYes = bottomSheetDialog.findViewById(R.id.textViewYes);
        textViewYes.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                SharePrefs.getInstance(mainActivity).putBoolean(SharePrefs.IS_INSTAGRAM_LOGIN, false);
                SharePrefs.getInstance(mainActivity).putString(SharePrefs.COOKIES, "");
                SharePrefs.getInstance(mainActivity).putString(SharePrefs.CSRF, "");
                SharePrefs.getInstance(mainActivity).putString(SharePrefs.SESSIONID, "");
                SharePrefs.getInstance(mainActivity).putString(SharePrefs.USERID, "");
                if (SharePrefs.getInstance(mainActivity).getBoolean(SharePrefs.IS_INSTAGRAM_LOGIN).booleanValue()) {
                    textViewLogin.setText(getResources().getString(R.string.logout));
                    linearLayoutStories.setVisibility(View.VISIBLE);
                    linearLayoutPlaceHolder.setVisibility(View.GONE);
                } else {
                    textViewLogin.setText(getResources().getString(R.string.login));
                    linearLayoutStories.setVisibility(View.GONE);
                    linearLayoutPlaceHolder.setVisibility(View.VISIBLE);
                }
                bottomSheetDialog.dismiss();
            }
        });

        TextView textViewCancel = bottomSheetDialog.findViewById(R.id.textViewCancel);
        textViewCancel.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void getStoriesApi() {
        try {
            if (!new Utils(this.mainActivity).isNetworkAvailable()) {
                Utils.setToast(this.mainActivity, this.mainActivity.getResources().getString(R.string.no_net_conn));
            } else if (this.commonAPI != null) {
                this.progressLoadingBar.setVisibility(View.VISIBLE);
                CommonAPI commonAPI1 = this.commonAPI;
                DisposableObserver<StoryModel> disposableObserver = this.storyObserver;
                commonAPI1.getStories(disposableObserver, "ds_user_id=" + SharePrefs.getInstance(this.mainActivity).getString(SharePrefs.USERID) + "; sessionid=" + SharePrefs.getInstance(this.mainActivity).getString(SharePrefs.SESSIONID));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void FacebookUserListClick(int i, TrayModel trayModel) {
        getStories(String.valueOf(trayModel.getUser().getPk()));
    }

    private void getStories(String str) {
        try {
            if (!new Utils(this.mainActivity).isNetworkAvailable()) {
                Utils.setToast(this.mainActivity, this.mainActivity.getResources().getString(R.string.no_net_conn));
            } else if (this.commonAPI != null) {
                this.progressLoadingBar.setVisibility(View.VISIBLE);
                CommonAPI commonAPI1 = this.commonAPI;
                DisposableObserver<FullDetailModel> disposableObserver = this.storyDetailObserver;
                commonAPI1.getFullFeed(disposableObserver, str, "ds_user_id=" + SharePrefs.getInstance(this.mainActivity).getString(SharePrefs.USERID) + "; sessionid=" + SharePrefs.getInstance(this.mainActivity).getString(SharePrefs.SESSIONID));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getInstagramData() {
        try {
            createFile();
            URL url = new URL(editTextPasteUrl.getText().toString());
            String host = url.getHost();
            Log.e("initViews: ", host);
            if (host.equals("www.instagram.com")) {
                setDownloadStory(editTextPasteUrl.getText().toString());
            } else {
                Utils.setToast(mainActivity, getResources().getString(R.string.enter_valid_url));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pasteText() {
        try {
            this.editTextPasteUrl.setText("");
            String stringExtra = getIntent().getStringExtra("CopyIntent");
            if (stringExtra != null) {
                if (!stringExtra.equals("")) {
                    if (stringExtra.contains(Constants.INSTAGRAM_SITE)) {
                        this.editTextPasteUrl.setText(stringExtra);
                        return;
                    }
                    return;
                }
            }
            if (!this.clipboardManager.hasPrimaryClip()) {
                Log.d("ContentValues", "PasteText");
            } else if (this.clipboardManager.getPrimaryClipDescription().hasMimeType("text/plain")) {
                ClipData.Item itemAt = this.clipboardManager.getPrimaryClip().getItemAt(0);
                if (itemAt.getText().toString().contains(Constants.INSTAGRAM_SITE)) {
                    this.editTextPasteUrl.setText(itemAt.getText().toString());
                }
            } else if (this.clipboardManager.getPrimaryClip().getItemAt(0).getText().toString().contains(Constants.INSTAGRAM_SITE)) {
                this.editTextPasteUrl.setText(this.clipboardManager.getPrimaryClip().getItemAt(0).getText().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getUrlWithoutParameters(String url) {
        try {
            URI uri = new URI(url);
            return new URI(uri.getScheme(),
                    uri.getAuthority(),
                    uri.getPath(),
                    null,
                    uri.getFragment()).toString();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.setToast(mainActivity, getResources().getString(R.string.enter_valid_url));
            return "";
        }
    }

    private void setDownloadStory(String Url) {
        String UrlWithoutQP = getUrlWithoutParameters(Url);
        UrlWithoutQP = UrlWithoutQP + "?__a=1";
        try {
            Utils utils = new Utils(mainActivity);
            if (utils.isNetworkAvailable()) {
                if (commonAPI != null) {
                    Utils.showProgress(mainActivity);
                    commonAPI.Result(disposableObserver, UrlWithoutQP,
                            "ds_user_id="+SharePrefs.getInstance(mainActivity).getString(SharePrefs.USERID)
                                    +"; sessionid="+SharePrefs.getInstance(mainActivity).getString(SharePrefs.SESSIONID));
                }
            } else {
                Utils.setToast(mainActivity, getResources().getString(R.string.no_net_conn));
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private DisposableObserver<JsonObject> disposableObserver = new DisposableObserver<JsonObject>() {
        public void onNext(JsonObject jsonObject) {
            Utils.hideProgress(mainActivity);
            try {
                Log.e("onNext: ", jsonObject.toString());
                ResponseModel responseModel = (ResponseModel) new Gson().fromJson(jsonObject.toString(), new TypeToken<ResponseModel>() {
                }.getType());
                EdgeSidecarModel edgeSidecarToChildren = responseModel.getGraphql().getShortcodeMedia().getEdgeSidecarToChildren();
                if (edgeSidecarToChildren != null) {
                    List<EdgeModel> edges = edgeSidecarToChildren.getEdges();
                    for (int i = 0; i < edges.size(); i++) {
                        if (edges.get(i).getNode().isVideo()) {
                            MainActivity.this.videoUrl = edges.get(i).getNode().getVideoUrl();
                            String str = MainActivity.this.videoUrl;
                            MainActivity instagramActivity = MainActivity.this.mainActivity;
                            Utils.startDownload(str, DirectoryUtils.FOLDER, instagramActivity, "Instagram_" + System.currentTimeMillis() + ".mp4");
                            MainActivity.this.editTextPasteUrl.setText("");
                            MainActivity.this.videoUrl = "";
                        } else {
                            MainActivity.this.photoUrl = edges.get(i).getNode().getDisplayResources().get(edges.get(i).getNode().getDisplayResources().size() - 1).getSrc();
                            String str2 = MainActivity.this.photoUrl;
                            MainActivity instagramActivity2 = MainActivity.this.mainActivity;
                            Utils.startDownload(str2, DirectoryUtils.FOLDER, instagramActivity2, "Instagram_" + System.currentTimeMillis() + ".png");
                            MainActivity.this.photoUrl = "";
                            MainActivity.this.editTextPasteUrl.setText("");
                        }
                    }
                } else if (responseModel.getGraphql().getShortcodeMedia().isVideo()) {
                    MainActivity.this.videoUrl = responseModel.getGraphql().getShortcodeMedia().getVideoUrl();
                    String str3 = MainActivity.this.videoUrl;
                    MainActivity instagramActivity3 = MainActivity.this.mainActivity;
                    Utils.startDownload(str3, DirectoryUtils.FOLDER, instagramActivity3, "Instagram_" + System.currentTimeMillis() + ".mp4");
                    MainActivity.this.videoUrl = "";
                    MainActivity.this.editTextPasteUrl.setText("");
                } else {
                    MainActivity.this.photoUrl = responseModel.getGraphql().getShortcodeMedia().getDisplayResources().get(responseModel.getGraphql().getShortcodeMedia().getDisplayResources().size() - 1).getSrc();
                    String str4 = MainActivity.this.photoUrl;
                    MainActivity instagramActivity4 = MainActivity.this.mainActivity;
                    Utils.startDownload(str4, DirectoryUtils.FOLDER, instagramActivity4, "Instagram_" + System.currentTimeMillis() + ".png");
                    MainActivity.this.photoUrl = "";
                    MainActivity.this.editTextPasteUrl.setText("");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Throwable th) {
            Utils.hideProgress(mainActivity);
            th.printStackTrace();
        }

        @Override
        public void onComplete() {
            Utils.hideProgress(mainActivity);
        }
    };

    private DisposableObserver<FullDetailModel> storyDetailObserver = new DisposableObserver<FullDetailModel>() {
        public void onNext(FullDetailModel fullDetailModel) {
            MainActivity.this.recyclerViewUser.setVisibility(View.VISIBLE);
            MainActivity.this.progressLoadingBar.setVisibility(View.GONE);
            try {
                MainActivity.this.storyAdapter = new StoryAdapter(MainActivity.this.mainActivity, fullDetailModel.getReelFeed().getItems());
                MainActivity.this.recyclerViewStories.setAdapter(MainActivity.this.storyAdapter);
                MainActivity.this.storyAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Throwable th) {
            MainActivity.this.progressLoadingBar.setVisibility(View.GONE);
            th.printStackTrace();
        }

        @Override
        public void onComplete() {
            MainActivity.this.progressLoadingBar.setVisibility(View.GONE);
        }
    };

    private DisposableObserver<StoryModel> storyObserver = new DisposableObserver<StoryModel>() {

        public void onNext(StoryModel storyModel) {
            MainActivity.this.recyclerViewUser.setVisibility(View.VISIBLE);
            MainActivity.this.progressLoadingBar.setVisibility(View.GONE);
            try {
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < storyModel.getTray().size(); i++) {
                    try {
                        if (storyModel.getTray().get(i).getUser().getFullname() != null) {
                            arrayList.add(storyModel.getTray().get(i));
                        }
                    } catch (Exception unused) {
                    }
                }
                MainActivity.this.profileAdapter = new ProfileAdapter(MainActivity.this.mainActivity, arrayList, MainActivity.this.mainActivity);
                MainActivity.this.recyclerViewUser.setAdapter(MainActivity.this.profileAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Throwable th) {
            MainActivity.this.progressLoadingBar.setVisibility(View.GONE);
            th.printStackTrace();
        }

        @Override
        public void onComplete() {
            MainActivity.this.progressLoadingBar.setVisibility(View.GONE);
        }
    };

    @Override
    public void FacebookUserListClick(int i, NodeModel nodeModel) {
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        try {
            super.onActivityResult(i, i2, intent);
            if (i != 100 || i2 != -1) {
                return;
            }
            if (SharePrefs.getInstance(this.mainActivity).getBoolean(SharePrefs.IS_INSTAGRAM_LOGIN).booleanValue()) {
                this.textViewLogin.setText(getResources().getString(R.string.logout));
                this.linearLayoutStories.setVisibility(View.VISIBLE);
                this.linearLayoutPlaceHolder.setVisibility(View.GONE);
                getStoriesApi();
                return;
            }
            this.textViewLogin.setText(getResources().getString(R.string.login));
            this.linearLayoutStories.setVisibility(View.GONE);
            this.linearLayoutPlaceHolder.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}