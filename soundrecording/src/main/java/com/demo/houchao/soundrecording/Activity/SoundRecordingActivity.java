package com.demo.houchao.soundrecording.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.demo.houchao.soundrecording.R;
import com.demo.houchao.soundrecording.Utils.FileUtil;
import com.demo.houchao.soundrecording.view.VolumeView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.demo.houchao.soundrecording.R.id.tv_statu;

public class SoundRecordingActivity extends Activity {

    private static final String tag="RecordActivity";
    private MediaRecorder mRecorder;

    private ImageButton mRecorderBtn;
    private ImageButton mStopBtn;
    private ImageButton mListBtn;
    private TextView mStatusTextView ;
    private TextView mTimeTextView;
    private LinearLayout mVolumeLayout;
    private VolumeView volumeView ;

    private MyHandler mHandler = new MyHandler();

    private FileUtil fileUtil=new FileUtil();
    private static String mFilepath ;
    private List<File> mTmpFile = new ArrayList<File>();
    private int mSeagments =  1;

    private MediaRecorderState mRecordState = MediaRecorderState.STOPPED;

    public SoundRecordingActivity() {
    }

    private enum MediaRecorderState
    {
        STOPPED, RECORDING, PAUSED
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_sound_recording);


        InitViews();
        OnClik();

        mFilepath = fileUtil.getStorePath()+"MySoundRecording";




        mVolumeLayout = (LinearLayout)findViewById(R.id.layout_volume_view);
        int[] location = new int[2];
        mVolumeLayout.getLocationOnScreen(location);
        volumeView = new VolumeView(this,location[1]+400);
        mVolumeLayout.removeAllViews();
        mVolumeLayout.addView(volumeView);
        mVolumeLayout.setBackgroundColor(Color.BLACK);

        Thread t = new Thread(new HandlerInvocation());
        t.start();
    }


    /**
     * 初始化控件
     */

    private void InitViews() {
        mStatusTextView = (TextView)findViewById(tv_statu);
        mTimeTextView = (TextView)findViewById(R.id.tv_time);
        mRecorderBtn = (ImageButton)findViewById(R.id.imb_start);
        mStopBtn = (ImageButton)findViewById(R.id.imb_stop);
        mListBtn=(ImageButton)findViewById(R.id.imb_list);

        mStatusTextView.setText("点击录音按钮开始录音.");
        mStatusTextView.setTextColor(Color.WHITE);
        mStopBtn.setEnabled(false);

    }

    /**
     * 按钮监听
     */

    private void OnClik() {
        mRecorderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStopBtn.setEnabled(true);
                if(mRecordState == MediaRecorderState.STOPPED || mRecordState == MediaRecorderState.PAUSED){
                    startRecording();
                    mRecorderBtn.setImageResource(R.drawable.pause_recording);
                    mStatusTextView.setText("正在进行录音...");
                    mStatusTextView.setTextColor(Color.GREEN);

                }else if(mRecordState == MediaRecorderState.RECORDING){
                    pauseRecording();
                    mRecorderBtn.setImageResource(R.drawable.start_recording);
                    mStatusTextView.setText("录音已暂停");
                    mStatusTextView.setTextColor(Color.GRAY);
                }
            }
        });


        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mStopBtn.setEnabled(false);
                mRecorderBtn.setImageResource(R.drawable.start_recording);
                mStatusTextView.setText("录音已停止");
                mStatusTextView.setTextColor(Color.RED);
                saveDialog();
            }
        });

        mListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SoundRecordingActivity.this,SoundRecordingListActivity.class));
            }
        });

    }

    /**
     * 停止并保存录音对话框
     */
    private void saveDialog() {

        mRecordState = MediaRecorderState.STOPPED;
        timeCount = 0;
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }

        LayoutInflater factory=LayoutInflater.from(SoundRecordingActivity.this);
        final View myview=factory.inflate(R.layout.dialog_savesound_name_edit,null);
        EditText edit=(EditText)myview.findViewById(R.id.edit_dialog_save_sound_name);
        edit.setText("录音"+"("+GetDateTime()+")");

        AlertDialog.Builder dialog=new AlertDialog.Builder(SoundRecordingActivity.this);
        dialog.setTitle("保存此录音");
        dialog.setView(myview);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText edit=(EditText)myview.findViewById(R.id.edit_dialog_save_sound_name);
                if(edit.getText().toString().length()!=0)
                    stopRecording(edit.getText().toString());
                else
                    stopRecording("录音"+"("+GetDateTime()+")");

                for (File f : mTmpFile)
                    f.delete();
                mTmpFile.clear();
                mSeagments = 1;

            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for (File f : mTmpFile)
                    f.delete();
                mTmpFile.clear();
                mSeagments = 1;
            }
        });
        dialog.show();
    }

    /**
     * 获取当前时间
     */
    private String GetDateTime() {
        int month,day,hour,minute;
        Calendar c=Calendar.getInstance();
        month=c.get(Calendar.MONTH)+1;
        day=c.get(Calendar.DAY_OF_MONTH);
        hour=c.get(Calendar.HOUR_OF_DAY);
        minute=c.get(Calendar.MINUTE);

        String initDateTime=month+"月"+day+"日"+"\t"+hour+"点"+minute+"分";
        return initDateTime;
    }







    @Override
    protected void onStop() {
        if (mRecorder != null && mRecordState != MediaRecorderState.STOPPED) {
            stopRecording("录音"+"("+GetDateTime()+")");
        }
        super.onStop();
    }


    /**
     * 开始录音
     */
    private void startRecording(){
        mRecordState = MediaRecorderState.RECORDING;
        File file = new File(mFilepath+"("+GetDateTime()+")_"+mSeagments+".amr");
        mTmpFile.add(file);
        mSeagments++;
        if(file.exists()){
            if(file.delete())
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }else{
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        mRecorder.setOutputFile(file.getAbsolutePath());
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener(){
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                mRecorder.reset();
            }
        });
        try {
            mRecorder.prepare();
            mRecorder.start();
            Thread t = new Thread(new DbThread());
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
            mRecorder.release();
        }finally{

        }
    }


    /**
     * 暂停录音
     */
    private void pauseRecording(){
        mRecordState = MediaRecorderState.PAUSED;
        if(mRecorder!=null){
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }


    /**
     * 保存录音
     */
    private void stopRecording(String name){


        File folder=null;
        folder=fileUtil.creatSDDir(mFilepath);
        File finalFile = new File(mFilepath+"/"+name+".mp3");
        if (!finalFile.exists()) {
            try {
                finalFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(finalFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < mTmpFile.size(); i++) {
            File tmpFile = mTmpFile.get(i);
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(tmpFile);
                byte[] tmpBytes = new byte[fis.available()];
                int lenght = tmpBytes.length;
                if (i == 0) {
                    while (fis.read(tmpBytes) != -1) {
                        fileOutputStream.write(tmpBytes, 0, lenght);
                    }
                } else {
                    while (fis.read(tmpBytes) != -1) {
                        fileOutputStream.write(tmpBytes, 6, lenght - 6);
                    }
                }
                fileOutputStream.flush();
                fis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                fis = null;
            }
        }
        try {
            if (fileOutputStream != null)
                fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileOutputStream = null;
        }

    }




    private int timeCount = 0;
    class MyHandler extends Handler {
        public MyHandler() {
        }

        public MyHandler(Looper L) {
            super(L);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == -1) {
                int minute = timeCount / 60;
                int second = timeCount % 60;
                String min = minute >= 10 ? minute + "" : "0" + minute;
                String sec = second >= 10 ? second + "" : "0" + second;
                mTimeTextView.setText(min + ":" + sec);
            }else if(volumeView!=null){
                volumeView.changed(msg.what);
            }
        }
    }
    class HandlerInvocation implements Runnable{
        @Override
        public void run() {
            while (true) {
                if (mRecordState == MediaRecorderState.RECORDING) {
                    Message msg = new Message();
                    msg.what = -1;
                    SoundRecordingActivity.this.mHandler.sendMessage(msg);
                    timeCount++;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class DbThread implements Runnable{
        @Override
        public void run() {
            while(true){
                int db = 0;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(mRecorder!=null && mRecordState==MediaRecorderState.RECORDING){
                    try{
                        double f=10*Math.log10(mRecorder.getMaxAmplitude());
                        if(f<0){
                            db = 0;
                        }else{
                            db = (int)(f*2);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                        continue;
                    }
                    Message msg = new Message();
                    msg.what = db;
                    SoundRecordingActivity.this.mHandler.sendMessage(msg);
                }else{
                    Message msg = new Message();
                    msg.what = db;
                    SoundRecordingActivity.this.mHandler.sendMessage(msg);
                    break;
                }
            }
        }

    }
}
