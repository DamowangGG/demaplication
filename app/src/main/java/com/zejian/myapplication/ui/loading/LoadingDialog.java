package com.zejian.myapplication.ui.loading;

import android.app.Dialog;
import android.content.Context;

import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.zejian.myapplication.R;


public class LoadingDialog extends Dialog{
    private static final String TAG = "LoadingDialog";
    private TextView txtInfo;
    private int seconds;
    private CountDownTimer timer;

    public LoadingDialog(@NonNull Context context,String message) {
        super(context, R.style.LoadingDialog);
        View layout = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        txtInfo = layout.findViewById(R.id.tvContent);
        if(TextUtils.isEmpty(message)) {
            txtInfo.setVisibility(View.GONE);
        }else {
            txtInfo.setText(message);
        }
        setCanceledOnTouchOutside(false);
        addContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setContentView(layout);
    }

    @Override
    public void show() {
        super.show();
        if(timer != null){
            timer.cancel();
        }
        timer = new CountDownTimer(seconds*1000,5000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                timer = null;
                dismiss();
            }
        };
        timer.start();
    }

    @Override
    public void onDetachedFromWindow() {
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    public void dismiss() {
        if(timer != null){
            timer.cancel();
        }
        super.dismiss();
    }

    public void setMessage(String message){
        if(txtInfo != null){
            if(TextUtils.isEmpty(message)) {
                txtInfo.setVisibility(View.GONE);
            }else {
                txtInfo.setText(message);
            }
        }
    }

    public void setSeconds(int seconds){
        this.seconds = seconds;
        if(seconds <= 0 && timer != null){
            timer.cancel();
        }
    }




    public static LoadingDialog create(Context context, String message) {
        return new LoadingDialog(context,message);
    }


}
