package com.example.fy071.floatingwidget.component.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.fy071.floatingwidget.R;

/**
 * Created by Administrator on 2018/4/18.
 */

public class RingActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_ring);
        //时间一到跳转Activity,在这个Activity中播放音乐
        //mediaPlayer = MediaPlayer.create(this, R.raw.one);
        mediaPlayer.start();
    }
    public void stop(View view){
        mediaPlayer.stop();
        finish();
    }
}
