package com.turingoal.bts.wps.follow.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.turingoal.bts.wps.follow.R;
import com.turingoal.common.android.base.TgBaseActivity;
import com.turingoal.common.android.util.lang.TgStringUtil;

import butterknife.BindView;

/**
 * 通用单独播放界面
 */
public class VideoPlayerActivity extends TgBaseActivity {
    @BindView(R.id.videoView)
    VideoView videoView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    public static void actionStart(Context context, String path) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.putExtra("path", path);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.app_activity_video_player;
    }

    @Override
    protected void initialized() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  // 防止锁屏
        String mPath = getIntent().getStringExtra("path"); // 播放路径
        if (TgStringUtil.isBlank(mPath)) {
            Toast.makeText(this, "视频路径错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        videoView.setVideoURI(Uri.parse(mPath));
        videoView.setMediaController(new MediaController(this));
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
            }
        });
        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    if (mediaPlayer.isPlaying()) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
                return true;
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                progressBar.setVisibility(View.GONE); // 缓冲完成就隐藏
            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                if (!isFinishing()) { // 播放失败
                    Toast.makeText(VideoPlayerActivity.this, "播放失败", Toast.LENGTH_SHORT).show();
                }
                finish();
                return false;
            }
        });
        videoView.start();
    }
}