package com.namelox.instagram.downloader.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.namelox.instagram.downloader.R;
import com.namelox.instagram.downloader.utils.DirectoryUtils;
import com.namelox.instagram.downloader.utils.Utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InstagramWebFragment extends Fragment {
    private static ValueCallback<Uri[]> valueCallback;
    private WebView webView;
    private ProgressBar progressBar;
    StringBuilder blockList;
    String loadNormalList = "0";
    View mainView;

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.mainView = layoutInflater.inflate(R.layout.fragment_web, viewGroup, false);
        setRetainInstance(true);
        load();
        webView = this.mainView.findViewById(R.id.webTikTok);
        progressBar = this.mainView.findViewById(R.id.progressBar);

        onstart();

        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(true);
        webView.setLongClickable(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setSavePassword(true);
        webView.getSettings().setSaveFormData(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        webView.getSettings().setEnableSmoothTransition(true);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setMediaPlaybackRequiresUserGesture(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        this.webView.clearFormData();
        this.webView.getSettings().setSaveFormData(true);
        this.webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0");
        this.webView.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
        this.webView.setWebViewClient(new MyBrowser());
        this.webView.getSettings().setAllowFileAccess(true);
        this.webView.getSettings().setAppCacheEnabled(true);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setDefaultTextEncodingName("UTF-8");
        this.webView.getSettings().setCacheMode(1);
        this.webView.getSettings().setDatabaseEnabled(true);
        this.webView.getSettings().setBuiltInZoomControls(false);
        this.webView.getSettings().setSupportZoom(false);
        this.webView.getSettings().setUseWideViewPort(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        this.webView.getSettings().setAllowFileAccess(true);
        this.webView.getSettings().setLoadWithOverviewMode(true);
        this.webView.getSettings().setLoadsImagesAutomatically(true);
        this.webView.getSettings().setBlockNetworkImage(false);
        this.webView.getSettings().setBlockNetworkLoads(false);
        this.webView.getSettings().setLoadWithOverviewMode(true);

        webView.setWebViewClient(new WebViewClient() {


            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                ByteArrayInputStream EMPTY3 = new ByteArrayInputStream("".getBytes());
                String kk53 = String.valueOf(blockList);
                if (kk53.contains(":::::" + request.getUrl().getHost())) {
                    return new WebResourceResponse("text/plain", "utf-8", EMPTY3);
                }
                return super.shouldInterceptRequest(view, request);
            }
        });


        this.webView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String str, String str2, String str3, String str4, long j) {
                String str5 = DirectoryUtils.FOLDER;
                Utils.startDownload(str, str5, getActivity(), "Instagram_" + System.currentTimeMillis());
            }
        });
        this.webView.setWebChromeClient(new WebChromeClient() {

            public void onProgressChanged(WebView webView, int i) {
                if (i < 100 && progressBar.getVisibility() == View.GONE) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                progressBar.setProgress(i);
                if (i == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        this.webView.loadUrl(Utils.InstagramWebUrl);
        return this.mainView;
    }

    public void onstart() {
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_PHONE_STATE", "android.permission.ACCESS_COARSE_LOCATION"}, 123);
        }
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 1001 && Build.VERSION.SDK_INT >= 21) {
            valueCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(i2, intent));
            valueCallback = null;
        }
    }

    private class MyBrowser extends WebViewClient {
        private MyBrowser() {
        }

        public void onPageStarted(WebView webView, String str, Bitmap bitmap) {
            progressBar.setVisibility(View.VISIBLE);
            super.onPageStarted(webView, str, bitmap);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String str) {
            webView.loadUrl(str);
            return true;
        }

        public void onPageFinished(WebView webView, String str) {
            super.onPageFinished(webView, str);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void load(){
        String strLine2="";
        blockList = new StringBuilder();

        InputStream fis2 = this.getResources().openRawResource(R.raw.adblockserverlist);
        BufferedReader br2 = new BufferedReader(new InputStreamReader(fis2));
        if(fis2 != null) {
            try {
                while ((strLine2 = br2.readLine()) != null) {
                    if(loadNormalList.equals("0")){
                        blockList.append(strLine2);
                        blockList.append("\n");
                    }
                    if(loadNormalList.equals("1")){
                        blockList.append(":::::"+strLine2);
                        blockList.append("\n");
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
