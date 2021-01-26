package com.zejian.myapplication.ui.update;

/**
 * Created by cxr
 * 2018/8/28 0028.
 */

public class DownLoadProgress {

    public static final int STATE_NORMAL = 0;
    public static final int STATE_DOWNLOAD_ERROR = 1;
    public static final int STATE_FINISH = 2;

    private int state;
    private int progress;

    public DownLoadProgress(int state, int progress) {
        this.state = state;
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }

    public int getState() {
        return state;
    }
}
