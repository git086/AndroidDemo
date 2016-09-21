package com.demo.houchao.soundrecording.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.demo.houchao.soundrecording.Adapter.SoundRecordingListAdapter;
import com.demo.houchao.soundrecording.Bean.SoundRecordingListBean;
import com.demo.houchao.soundrecording.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by HouChao on 2016/8/8.
 */

public class SoundRecordingListActivity extends Activity {

    private TextView tv_back;

    private ListView lv_recording;
    private List<SoundRecordingListBean> recordingdata;
    private SoundRecordingListAdapter adapter;

    private static String path="/"+"MySoundRecording";

    private boolean isPlayer=false;
    private int playerPosition;

    private String[] selects = new String[] { "重命名", "删除" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_sound_recording_list);

        initView();
        initData();
        ListOnClik();

        lv_recording.setAdapter(adapter);



    }


    /**
     * 初始化控件
     */
    private void initView() {
        tv_back=(TextView)findViewById(R.id.tv_sound_recording_list_back);
        lv_recording=(ListView)findViewById(R.id.lv_sound_recording_list);
        recordingdata = new ArrayList<SoundRecordingListBean>();
        adapter = new SoundRecordingListAdapter(SoundRecordingListActivity.this,recordingdata);

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    /**
     * 初始化数据
     */
    private void initData() {
        String locationPath= Environment.getExternalStorageDirectory()+path;
        File[] files=new File(locationPath).listFiles();
        for(File file : files){
            String time=LongToTime(file.lastModified());
            SoundRecordingListBean bean=new SoundRecordingListBean();
            bean=new SoundRecordingListBean(file.getName(),time,locationPath+"/"+file.getName());
            recordingdata.add(bean);
        }

    }

    /**
     * ListView点击、长按事件监听
     */
    private void ListOnClik(){
        lv_recording.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                SoundRecordingListBean bean=recordingdata.get(position);
                Intent intent=new Intent(SoundRecordingListActivity.this,SoundPlayerActivity.class);
                intent.putExtra("filePath",bean.getLocaltionPath());
                intent.putExtra("fileName",bean.getName());
                startActivity(intent);
            }
        });

        lv_recording.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(SoundRecordingListActivity.this);
                dialog.setItems(selects, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (selects[which]) {

                            case "删除":
                                ConfirmDelete(position);
                                break;

                            case "重命名":
                                ReName(position);
                                break;
                        }
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.show();
                return true;
            }
        });
    }


    /**
     * 文件重命名
     * @param position
     */
    private void ReName(int position) {
        final SoundRecordingListBean bean = recordingdata.get(position);
        final File file=new File(bean.getLocaltionPath());

        LayoutInflater factory=LayoutInflater.from(SoundRecordingListActivity.this);
        final View myview=factory.inflate(R.layout.dialog_savesound_name_edit,null);

        EditText edit=(EditText)myview.findViewById(R.id.edit_dialog_save_sound_name);
        String fname = bean.getName();
        fname = fname.replace(".mp3", "");
        edit.setText(fname);

        AlertDialog.Builder dialog=new AlertDialog.Builder(SoundRecordingListActivity.this);
        dialog.setTitle("重命名");
        dialog.setView(myview);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText edit=(EditText)myview.findViewById(R.id.edit_dialog_save_sound_name);
                if(edit.getText().toString().length()!=0){
                    String locationpath=Environment.getExternalStorageDirectory()+path;
                    File newfile=new File(locationpath+"/"+edit.getText().toString()+".mp3");
                    file.renameTo(newfile);

                    recordingdata.clear();
                    initData();
                    lv_recording.setAdapter(adapter);
                }
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();

    }


    /**
     * 确认删除文件
     * @param position
     */
    private void ConfirmDelete(final int position) {
        final SoundRecordingListBean bean = recordingdata.get(position);
        final File file=new File(bean.getLocaltionPath());
        AlertDialog.Builder dialog = new AlertDialog.Builder(SoundRecordingListActivity.this);
        dialog.setTitle(bean.getName());
        dialog.setMessage("确认删除该录音？");
        dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                file.delete();
                recordingdata.remove(position);
                lv_recording.setAdapter(adapter);
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.show();
    }


    /**
     * long型时间，转String
     * @param date
     * @return
     */
    private String LongToTime(long time){
        Date date=new Date(time);
        String strs="";
        try {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            strs=sdf.format(date);
            System.out.println(strs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strs;
    }


}
