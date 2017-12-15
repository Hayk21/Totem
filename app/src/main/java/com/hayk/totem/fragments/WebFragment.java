package com.hayk.totem.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hayk.totem.R;
import com.hayk.totem.activitys.MainActivity;

public class WebFragment extends Fragment {
    WebView webView;

    public static WebFragment newInstance(String path) {

        Bundle args = new Bundle();
        args.putString(MainActivity.URL_ARGUMENT,path);

        WebFragment fragment = new WebFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        webView = view.findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new CustomWebClient());
        String url = getArguments().getString(MainActivity.URL_ARGUMENT);
        webView.loadUrl(url);
    }

    public void goBack(){
        if(webView.canGoBack()) {
            webView.goBack();
        }else {
            getActivity().getFragmentManager().popBackStack();
        }
    }

    private class CustomWebClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
