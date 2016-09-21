package com.demo.houchao.soundrecording.Activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.demo.houchao.soundrecording.R;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by HouChao on 2016/8/9.
 */

public class SoundPlayerActivity extends Activity {

    private TextView tv_back;

    private TextView tv_state;
    private TextView tv_name;

    private ImageButton play_pause, reset;
    private SeekBar seekbar;
    private boolean isplay = false;//是否播放
    private MediaPlayer player = null;
    private boolean iffirst = false;//是否第一次播放
    private Timer mTimer;
    private TimerTask mTimerTask;
    private boolean isChanging=false;//互斥变量，防止定时器与SeekBar拖动时进度冲突

    private String filePath,fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_sound_player);

        Intent intent=getIntent();
        filePath=intent.getStringExtra("filePath");
        fileName=intent.getStringExtra("fileName");

        initView();
        OnClik();

    }

    /**
     * 初始化控件
     */
    private void initView() {
        tv_back=(TextView)findViewById(R.id.tv_sound_play_back);
        tv_state=(TextView)findViewById(R.id.tv_sound_player_state);
        tv_name=(TextView)findViewById(R.id.tv_sound_player_name);
        play_pause = (ImageButton) findViewById(R.id.imgb_play_pause);
        reset = (ImageButton) findViewById(R.id.imgb_stop);
        seekbar = (SeekBar) findViewById(R.id.seekbar);

        player = new MediaPlayer();

        tv_name.setText(fileName);


    }


    /**
     * 按钮监听
     */
    private void OnClik() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //播放、暂停按钮监听
        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player != null && !isplay) {
                    play_pause.setImageResource(R.drawable.pause_play);
                    if (!iffirst) {
                        player.reset();
                        try {
                            player.setDataSource(filePath);
                            player.prepare();// 准备

                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        seekbar.setMax(player.getDuration());//设置进度条
                        //----------定时器记录播放进度---------//
                        mTimer = new Timer();
                        mTimerTask = new TimerTask() {
                            @Override
                            public void run() {
                                if(isChanging==true) {
                                    return;
                                }
                                seekbar.setProgress(player.getCurrentPosition());
                            }
                        };
                        mTimer.schedule(mTimerTask, 0, 10);
                        iffirst=true;
                    }
                    player.start();// 开始
                    isplay = true;
                } else if (isplay) {
                    play_pause.setImageResource(R.drawable.start_play);
                    player.pause();
                    isplay = false;
                }
            }
        });


        //停止按钮监听
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play_pause.setImageResource(R.drawable.start_play);
                if (isplay) {
//
                    player.stop();
                    player.reset();
                    player=new MediaPlayer();
                    try {
                        player.setDataSource(filePath);
                        player.prepare();// 准备

                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    seekbar.setMax(player.getDuration());//设置进度条
                    //----------定时器记录播放进度---------//
                    mTimer = new Timer();
                    mTimerTask = new TimerTask() {
                        @Override
                        public void run() {
                            if(isChanging==true) {
                                return;
                            }
                            seekbar.setProgress(player.getCurrentPosition());
                        }
                    };
                    mTimer.schedule(mTimerTask, 0, 10);
                    iffirst=true;

                    isplay=false;
                } else {
                    player.reset();
                    try {
                        player.setDataSource(filePath);
                        player.prepare();// 准备

                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });



        //进度条处理
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.seekTo(seekbar.getProgress());
                isChanging=false;
            }
        });

    }
}
