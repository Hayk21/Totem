package com.hayk.totem.adapters;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hayk.totem.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 13.12.2017.
 */

public class AdapterForVideos extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int IS_VIDEO = 0;
    private static final int NO_VIDEO = 1;
    public static final String VIDEO_ACTION = "VideoAction";
    public static final String WEB_ACTION = "WebAction";
    public static final String GAME_ACTION = "GameAction";
    private List<MainItem> list;
    private Context context;
    private OnVideoAdapterItemClicked adapterItemClicked;
    private int screenWidth;

    public AdapterForVideos(Context context,int screenWidth){
        this.context = context;
        this.screenWidth = screenWidth;
        list = new ArrayList<>();
        File mediaFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        int i;
        for (i=0;i<3;i++){
            if(i >1){
                list.add(list.get(0));
            }else
            list.add(new MainItem(true, mediaFolder.listFiles()[i].getPath(),"Example"));
        }
        list.add(new MainItem(false,"https://play.google.com/store/apps/details?id=com.byril.seabattle2","Website"));
        for (i++;i<7;i++){
            if(i >0) {
                list.add(list.get(1));
            }else {
                list.add(new MainItem(true, mediaFolder.listFiles()[i].getPath(), "Example"));
            }
        }
        list.add(new MainItem(false,null,"Battleships"));
    }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position).isVideo()){
            return IS_VIDEO;
        }else return NO_VIDEO;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == IS_VIDEO){
            return new VideoHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.video_card,parent,false));
        }else {
            return new ButtonHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.button_card,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        params.width = screenWidth/5;
        holder.itemView.setLayoutParams(params);
        if(holder.getItemViewType() == IS_VIDEO){
            ((VideoHolder)holder).title.setText(list.get(position).getTitle());
            ((VideoHolder)holder).desc.setText(list.get(position).getDesc());
            ((VideoHolder)holder).videoImg.setImageBitmap(list.get(position).getImage());
            ((VideoHolder)holder).setOnVideoHolderItemClicked(new OnVideoHolderItemClicked() {
                @Override
                public void itemClicked() {
                    if(adapterItemClicked != null){
                        adapterItemClicked.itemClicked(VIDEO_ACTION,list.get(position).getPath());
                    }
                }
            });
        }else {
            ((ButtonHolder)holder).button.setText(list.get(position).getDesc());
            ((ButtonHolder)holder).setOnVideoHolderItemClicked(new OnVideoHolderItemClicked() {
                @Override
                public void itemClicked() {
                    if (adapterItemClicked != null) {
                        if (list.get(position).getPath() != null) {
                            adapterItemClicked.itemClicked(WEB_ACTION,list.get(position).getPath());
                        }else {
                            adapterItemClicked.itemClicked(GAME_ACTION,null);
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnVideoAdapterItemClicked{
        void itemClicked(String action,String path);
    }

    public void setOnAdapterItemClicked(OnVideoAdapterItemClicked adapterItemClicked){
        this.adapterItemClicked = adapterItemClicked;
    }

    public interface OnVideoHolderItemClicked{
        void itemClicked();
    }

    class ButtonHolder extends RecyclerView.ViewHolder{
        TextView button;
        OnVideoHolderItemClicked videoHolderItemClicked;

        public ButtonHolder(View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.main_button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(videoHolderItemClicked != null){
                        videoHolderItemClicked.itemClicked();
                    }
                }
            });
        }

        public void setOnVideoHolderItemClicked(OnVideoHolderItemClicked videoHolderItemClicked){
            this.videoHolderItemClicked = videoHolderItemClicked;
        }
    }

    class VideoHolder extends RecyclerView.ViewHolder{
        TextView title,desc;
        ImageView videoImg;
        OnVideoHolderItemClicked videoHolderItemClicked;

        public VideoHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.video_title);
            desc = itemView.findViewById(R.id.video_desc);
            videoImg = itemView.findViewById(R.id.video_img);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(videoHolderItemClicked != null){
                        videoHolderItemClicked.itemClicked();
                    }
                }
            });
        }

        public void setOnVideoHolderItemClicked(OnVideoHolderItemClicked videoHolderItemClicked){
            this.videoHolderItemClicked = videoHolderItemClicked;
        }
    }
}
