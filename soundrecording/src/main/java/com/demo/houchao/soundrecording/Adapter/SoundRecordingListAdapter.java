package com.demo.houchao.soundrecording.Adapter;

import android.content.Context;

import com.demo.houchao.soundrecording.Bean.SoundRecordingListBean;
import com.demo.houchao.soundrecording.R;
import com.demo.houchao.soundrecording.Utils.CommonAdapter;
import com.demo.houchao.soundrecording.Utils.ViewHolder;

import java.util.List;

/**
 * Created by HouChao on 2016/8/8.
 */

public class SoundRecordingListAdapter extends CommonAdapter<SoundRecordingListBean> {
    public SoundRecordingListAdapter(Context context, List<SoundRecordingListBean> data) {
        super(context, data, R.layout.item_list_soundrecordding);
    }

    @Override
    public void convert(ViewHolder holder, SoundRecordingListBean bean) {
        holder.setText(R.id.tv_sound_recording_listitem_name,bean.getName());
        holder.setText(R.id.tv_sound_recording_listitem_time,bean.getTime());

    }
}
