package com.example.placearapp.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.example.placearapp.R;
import com.example.placearapp.activity.HomeActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.ar.sceneform.ux.ArFragment;

public class ShopWebFragment extends BottomSheetDialogFragment {

    WebView mWebView;
    Button mButton;
    private BottomSheetBehavior<View> mBottomSheetBehavior;
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_shop_web, container, false);
        mButton = view.findViewById(R.id.adder_button);
        mButton.setOnClickListener(view1 -> {
            HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.productSelected("lavan");
        });

        mWebView = (WebView) view.findViewById(R.id.shop_webview);

        mWebView.setOnKeyListener((v, keyCode, event) -> {
            //This is the filter
            if (event.getAction()!=KeyEvent.ACTION_DOWN)
                return true;
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    ((HomeActivity)getActivity()).onBackPressed();
                }
                return true;
            }
            return false;
        });
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
            HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.productSelected(pro_cat_id);
        }
    }
}
