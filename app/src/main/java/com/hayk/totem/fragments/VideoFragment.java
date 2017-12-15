package com.hayk.totem.fragments;


import android.app.Fragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hayk.totem.R;
import com.hayk.totem.activitys.MainActivity;

import java.io.IOException;

import static android.content.Context.AUDIO_SERVICE;


public class VideoFragment extends Fragment implements SurfaceHolder.Callback {

//    VideoView videoView;
//
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        videoView = view.findViewById(R.id.video_view);
//        videoView.setVideoPath(getArguments().getString(MainActivity.URL_ARGUMENT));
//        videoView.setMediaController(new MediaController(getActivity()));
//        videoView.start();
//    }

    private static final String SAVED_CONFIG = "SavedConfig";
    private static final String IS_PLAYED = "IsPlayed";
    private SurfaceView videoContainer;
    private LinearLayout mediaIndicators;
    private RelativeLayout container;
    private MediaPlayer mediaPlayer;
    private SeekBar progressBar;
    private ImageView playOrPause;
    private TextView playedTime,totalTime;
    private Runnable progresRun, indicatorsRun;
    private Handler handlerForProgress, handlerForIndicators;
    private int savedPosition = 0;
    private boolean isPlayed = true;
    private Animation indicatorOpenAnimation, indicatorCloseAnimation;
    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener;
    private AudioManager audioManager;

    public static VideoFragment newInstance(String path) {

        Bundle args = new Bundle();
        args.putString(MainActivity.URL_ARGUMENT, path);

        VideoFragment fragment = new VideoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            savedPosition = savedInstanceState.getInt(SAVED_CONFIG);
            isPlayed = savedInstanceState.getBoolean(IS_PLAYED);
        }
        init(view);
        setListeners();
    }

    @Override
    public void onStart() {
        super.onStart();
        createMediaPlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        removeMediaPlayer();
    }

    private void init(View view) {
        indicatorOpenAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.media_indicator_open);
        indicatorCloseAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.media_indicator_close);
        container = view.findViewById(R.id.media_container);
        mediaIndicators = view.findViewById(R.id.media_indicators);
        videoContainer = view.findViewById(R.id.video_container);
        progressBar = view.findViewById(R.id.progressBar);
        playedTime = view.findViewById(R.id.played_time);
        totalTime = view.findViewById(R.id.total_time);
        playOrPause = view.findViewById(R.id.play_video);
        audioManager = (AudioManager) getActivity().getSystemService(AUDIO_SERVICE);
        onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int i) {
                switch (i) {
                    case AudioManager.AUDIOFOCUS_LOSS:
                        mediaPlayer.pause();
                        playOrPause.setImageDrawable(getResources().getDrawable(R.drawable.play_file_icon));
                        playOrPause.setColorFilter(getResources().getColor(R.color.white));
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        mediaPlayer.pause();
                        playOrPause.setImageDrawable(getResources().getDrawable(R.drawable.play_file_icon));
                        playOrPause.setColorFilter(getResources().getColor(R.color.white));
                        break;
                    case AudioManager.AUDIOFOCUS_GAIN:
                        if (!mediaPlayer.isPlaying()) {
                            mediaPlayer.start();
                            playOrPause.setImageDrawable(getResources().getDrawable(R.drawable.pause_file_icon));
                        } else {
                            mediaPlayer.setVolume(1f, 1f);
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        mediaPlayer.setVolume(0.5f, 0.5f);
                        break;
                }
            }
        };

        handlerForProgress = new Handler();
        playOrPause.setColorFilter(getResources().getColor(R.color.white));
        videoContainer.getHolder().addCallback(VideoFragment.this);

        progresRun = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    progressBar.setProgress(mediaPlayer.getCurrentPosition() / 1000);
                    playedTime.setText(milliSecondsToTimer(mediaPlayer.getCurrentPosition()));
                    handlerForProgress.postDelayed(this, 1000);
                }
            }
        };

        indicatorsRun = new Runnable() {
            @Override
            public void run() {
                mediaIndicators.startAnimation(indicatorCloseAnimation);
            }
        };
    }

    private void setListeners() {
        indicatorCloseAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mediaIndicators.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setIndicatorsVisibility();
            }
        });

        playOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlerForIndicators.removeCallbacks(indicatorsRun);
                handlerForIndicators.postDelayed(indicatorsRun, 4000);
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    audioManager.abandonAudioFocus(onAudioFocusChangeListener);
                    playOrPause.setImageDrawable(getResources().getDrawable(R.drawable.play_file_icon));
                    playOrPause.setColorFilter(getResources().getColor(R.color.white));
                } else {
                    if (requestAudioFocus()) {
                        mediaPlayer.start();
                    }
                    getActivity().runOnUiThread(progresRun);
                    playOrPause.setImageDrawable(getResources().getDrawable(R.drawable.pause_file_icon));
                    playOrPause.setColorFilter(getResources().getColor(R.color.white));
                }
            }
        });

        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                playedTime.setText(milliSecondsToTimer(i * 1000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handlerForIndicators.removeCallbacks(indicatorsRun);
                handlerForProgress.removeCallbacks(progresRun);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        mediaPlayer.seekTo(seekBar.getProgress() * 1000);
                        mediaPlayer.start();
                        getActivity().runOnUiThread(progresRun);
                        handlerForIndicators.postDelayed(indicatorsRun, 4000);
                    } else {
                        mediaPlayer.seekTo(seekBar.getProgress() * 1000);
                    }
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mediaPlayer.isPlaying()) {
            outState.putBoolean(IS_PLAYED, true);
        }
        outState.putInt(SAVED_CONFIG, mediaPlayer.getCurrentPosition());
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setDisplay(surfaceHolder);
        setIndicatorsVisibility();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        setVideoSize();
        progressBar.setMax(mediaPlayer.getDuration());
        totalTime.setText(milliSecondsToTimer(mediaPlayer.getDuration()));
        if (savedPosition != 0) {
            mediaPlayer.seekTo(savedPosition);
        }
        if (isPlayed) {
            if (requestAudioFocus()) {
                mediaPlayer.start();
            }
        }
        isPlayed = true;
        progressBar.setMax(mediaPlayer.getDuration() / 1000);
        getActivity().runOnUiThread(progresRun);
    }

    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";
        String mp3Minutes = "";

        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        if (minutes < 10) {
            mp3Minutes = "0" + minutes;
        } else {
            mp3Minutes = "" + minutes;
        }
        finalTimerString = finalTimerString + mp3Minutes + ":" + secondsString;
        return finalTimerString;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    private boolean requestAudioFocus() {
        int request = audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        return request == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    private void setIndicatorsVisibility() {
        if (mediaIndicators.getVisibility() == View.INVISIBLE) {
            mediaIndicators.setVisibility(View.VISIBLE);
            mediaIndicators.startAnimation(indicatorOpenAnimation);
            handlerForIndicators = new Handler();
            handlerForIndicators.postDelayed(indicatorsRun, 4000);
        } else {
            mediaIndicators.startAnimation(indicatorCloseAnimation);
            handlerForIndicators.removeCallbacks(indicatorsRun);
        }
    }

    private void setVideoSize() {
        int videoWidth = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;

        int screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;

        android.view.ViewGroup.LayoutParams lp = videoContainer.getLayoutParams();
        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        videoContainer.setLayoutParams(lp);
    }

    private void createMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(getArguments().getString(MainActivity.URL_ARGUMENT));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                playOrPause.setImageDrawable(getResources().getDrawable(R.drawable.play_file_icon));
                playOrPause.setColorFilter(getResources().getColor(R.color.white));
                isPlayed = false;
                handlerForProgress.removeCallbacks(progresRun);
                playedTime.setText(milliSecondsToTimer(mediaPlayer.getDuration()));
            }
        });
    }

    private void removeMediaPlayer() {
        isPlayed = mediaPlayer.isPlaying();
        mediaPlayer.pause();
        savedPosition = mediaPlayer.getCurrentPosition();
        mediaPlayer.stop();
        audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        mediaPlayer.release();
        mediaPlayer = null;
    }
}
