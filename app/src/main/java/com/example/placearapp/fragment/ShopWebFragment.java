package com.example.placearapp.fragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.placearapp.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ShopWebFragment extends BottomSheetDialogFragment {

    WebView mWebView;
    private BottomSheetBehavior<View> mBottomSheetBehavior;
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_shop_web, container, false);
        mWebView = (WebView) view.findViewById(R.id.shop_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.addJavascriptInterface(new WebAppInterface(getActivity()), "Android");
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl("http://geereh.com/");
        return view;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.activity_shop_web, null);
        dialog.setContentView(contentView);

        Log.i("dialog", "called");
        mBottomSheetBehavior = BottomSheetBehavior.from(((View) contentView.getParent()));
        mBottomSheetBehavior.setPeekHeight(10);
        if (mBottomSheetBehavior != null) {
            mBottomSheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);
            mBottomSheetBehavior.setPeekHeight(10);
            contentView.requestLayout();
        }
    }


    public class WebAppInterface {

        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /**
         * Show a toast from the web page
         */
        @JavascriptInterface
        public void addToAR(String pro_cat_id) {

        }
    }
}
