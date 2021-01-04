package com.zejian.myapplication;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import androidx.annotation.Nullable;


/**
 * @author pc
 */
public class NotificationService extends IntentService {
    private final static String TAG = "NotificationService";
    public final static String ACTION_CONVERSATION = "CHAT_CONVERSATION";
    public final static String ACTION_CONVERSATION_LIST = "CHAT_CONVERSATION_LIST";
    public final static String ACTION_LIKE = "LIKE";
    public final static String USER_ID = "userID";
    private Context mContext;


    public NotificationService() {
        super("NotificationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //用户点击了私聊消息通知栏
        if(ACTION_CONVERSATION.equals(intent.getAction())) {

        }else if(ACTION_CONVERSATION_LIST.equals(intent.getAction())){

        }
        return START_NOT_STICKY;
    }


    private boolean checkDrawOverlaysPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {

            }else {
                return true;
            }
        }
        return false;
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }


}
