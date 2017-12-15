package com.hayk.totem.activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.hayk.totem.R;
import com.hayk.totem.adapters.AdapterForVideos;
import com.hayk.totem.fragments.HomeFragment;
import com.hayk.totem.fragments.VideoFragment;
import com.hayk.totem.fragments.WebFragment;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnHomeFragmentListener {
    public static final String URL_ARGUMENT = "UrlArgument";
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 7;
    private static final String HOME_FRAGMENT = "HomeFragment";
    private static final String WEB_FRAGMENT = "WebFragment";
    private static final String VIDEO_FRAGMENT = "VideoFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
           ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},READ_STORAGE_PERMISSION_REQUEST_CODE);
        }else {
            getFragmentManager().beginTransaction().add(R.id.main_conatiner, new HomeFragment(),HOME_FRAGMENT).commit();
        }
    }

//    public void createDir() {
//        File mediaFolder = new File(Utils.getInstance(this).getMediaFolderPath());
//        if (!mediaFolder.exists()) {
//            mediaFolder.mkdir();
//        }
//    }

    @Override
    public void onHomeFragmentEvent(String action, String path) {
        switch (action){
            case AdapterForVideos.VIDEO_ACTION:
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.main_conatiner,VideoFragment.newInstance(path),VIDEO_FRAGMENT).commit();
                break;
            case AdapterForVideos.WEB_ACTION:
                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.main_conatiner,WebFragment.newInstance(path),WEB_FRAGMENT).commit();
                break;
            case AdapterForVideos.GAME_ACTION:
                PackageManager packageManager = getPackageManager();
                Intent intent = packageManager.getLaunchIntentForPackage("com.byril.seabattle2");
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().findFragmentById(R.id.main_conatiner).getTag().equals(WEB_FRAGMENT)) {
            ((WebFragment) getFragmentManager().findFragmentById(R.id.main_conatiner)).goBack();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == READ_STORAGE_PERMISSION_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getFragmentManager().beginTransaction().add(R.id.main_conatiner, new HomeFragment()).commit();
            }
        }
    }
}
