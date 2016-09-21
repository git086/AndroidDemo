package com.demo.houchao.soundrecording.Bean;

/**
 * Created by HouChao on 2016/8/8.
 */

public class SoundRecordingListBean {

    private String name;
    private String time;
    private String localtionPath;

    public SoundRecordingListBean() {
    }

    public SoundRecordingListBean(String name, String time,String localtionPath) {
        this.name = name;
        this.time = time;
        this.localtionPath=localtionPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocaltionPath() {
        return localtionPath;
    }

    public void setLocaltionPath(String localtionPath) {
        this.localtionPath = localtionPath;
    }
}
