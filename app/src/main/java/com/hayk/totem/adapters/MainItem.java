package com.hayk.totem.adapters;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

/**
 * Created by User on 13.12.2017.
 */

public class MainItem {
    private boolean isVideo;
    private String title,desc,path;
    private Bitmap image;

    public MainItem(boolean isVideo, String path, String desc) {
        this.isVideo = isVideo;
        this.path = path;
        this.desc = desc;
        if(isVideo){
            image = createThumbnailAtTime(path,20);
            title = getFileName(path);
        }else {
            image = null;
            title = null;
        }
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    private Bitmap createThumbnailAtTime(String filePath, int timeInSeconds){
        MediaMetadataRetriever mMMR = new MediaMetadataRetriever();
        mMMR.setDataSource(filePath);
        //api time unit is microseconds
        return mMMR.getFrameAtTime(timeInSeconds*1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
    }

    private String getFileName(String path) {
        int firstPeriodPos = path.lastIndexOf('/');
        int lastPeriodPos = path.lastIndexOf('.');
        String name = path.substring(firstPeriodPos + 1, lastPeriodPos);
        return name;
    }
}
