package com.hayk.totem.fragments;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hayk.totem.R;
import com.hayk.totem.adapters.AdapterForVideos;


public class HomeFragment extends Fragment {
    private OnHomeFragmentListener homeFragmentListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        homeFragmentListener = (OnHomeFragmentListener)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        homeFragmentListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        RecyclerView videoContainer = view.findViewById(R.id.video_list);
        videoContainer.setLayoutManager(new GridLayoutManager(getActivity(),4));
        AdapterForVideos adapterForVideos = new AdapterForVideos(getActivity(),getActivity().getWindowManager().getDefaultDisplay().getWidth());
        videoContainer.setAdapter(adapterForVideos);
        adapterForVideos.setOnAdapterItemClicked(new AdapterForVideos.OnVideoAdapterItemClicked() {
            @Override
            public void itemClicked(String action, String path) {
                homeFragmentListener.onHomeFragmentEvent(action,path);
            }
        });
    }

    public interface OnHomeFragmentListener{
        void onHomeFragmentEvent(String action,String path);
    }

    public void setOnHomeFragmentListener(OnHomeFragmentListener homeFragmentListener){
        this.homeFragmentListener = homeFragmentListener;
    }
}
