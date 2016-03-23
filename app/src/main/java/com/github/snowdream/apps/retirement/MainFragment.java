package com.github.snowdream.apps.retirement;

import android.annotation.TargetApi;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;
import android.widget.*;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.*;

/**
 * Created by yanghui.yangh on 2016/3/4.
 */
public class MainFragment extends Fragment {
    private WebView mWebView = null;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mWebView = (WebView) view.findViewById(R.id.webview);

        WebSettings webSettings = mWebView.getSettings();
        // 禁止本地文件访问 (除非是在应用 assets/ 和 res/ 目录下)
        webSettings.setAllowFileAccess(false);

        // android默认关闭了javascript，如果不是必要，请不要打开 (可选)
        webSettings.setJavaScriptEnabled(true);

        // 展示应用assets目录下文件内容
//        mWebView.loadUrl("file:///android_asset/book/index.html");

        String htmlFilename = "book/index.html";
        AssetManager mgr = getResources().getAssets();
        try {
            InputStream in = mgr.open(htmlFilename, AssetManager.ACCESS_BUFFER);
            String htmlContentInStringFormat = StreamToString(in);
            in.close();
            mWebView.loadDataWithBaseURL("file:///android_asset/book/", htmlContentInStringFormat, "text/html", "utf-8", null);

        } catch (IOException e) {
            e.printStackTrace();
        }

        mWebView.setWebViewClient(new WebViewClient() {

                                     @Override
                                     public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                                         WebResourceResponse response = null;
                                         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
                                             response = super.shouldInterceptRequest(view,url);
                                             if (url.contains("icon.png")){
                                                 try {
                                                     response = new WebResourceResponse("image/png","UTF-8",getResources().getAssets().open("icon.png"));
                                                 } catch (IOException e) {
                                                     e.printStackTrace();
                                                 }
                                             }
                                         }
//                return super.shouldInterceptRequest(view, url);
                                         return  response;
                                     }

                                     @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                                     @Override
                                     public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                                         WebResourceResponse response = null;
                                         response =  super.shouldInterceptRequest(view, request);

                                         String url = request.getUrl().toString();

                                         if (url.contains("icon.png")){
                                             try {
                                                 response = new WebResourceResponse("image/png","UTF-8",getResources().getAssets().open("icon.png"));
                                             } catch (IOException e) {
                                                 e.printStackTrace();
                                             }
                                         }
                                         return response;
                                     }
                                 });

        AdView mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public static String StreamToString(InputStream in) throws IOException {
        if(in == null) {
            return "";
        }
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
        }
        return writer.toString();
    }
}
