package com.zejian.myapplication.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lxj.xpopup.interfaces.XPopupImageLoader;
import com.zejian.myapplication.widget.pyq.MomentImage;

import java.io.File;

public class XPopImageLoader implements XPopupImageLoader {


    @Override
    public void loadImage(int position, @NonNull Object uri, @NonNull ImageView imageView) {
        if(uri instanceof MomentImage){
            MomentImage photo = (MomentImage) uri;
            Glide.with(imageView).load(photo.getUrl()).into(imageView);
        }else {
            Glide.with(imageView).load(uri).into(imageView);
        }

    }


    public void loadOriImage(int position, @NonNull Object photoUrl, @NonNull ImageView imageView) {
        Glide.with(imageView)
                .load(photoUrl)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        Log.d("loadOriImage","getUrl_onResourceReady");
                        if(imageView == null || !imageView.isAttachedToWindow()){
                            return;
                        }
                        imageView.setImageDrawable(resource);
                        Glide.with(imageView).load(photoUrl).into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable oriResource, @Nullable Transition<? super Drawable> transition) {
                                if(imageView != null && imageView.isAttachedToWindow()) {
                                    Log.d("loadOriImage","getOriUrl_onResourceReady");
                                    imageView.setImageDrawable(oriResource);
                                }
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                Log.d("loadOriImage","getOriUrl_onLoadCleared");

                            }
                        });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    //必须实现这个方法，返回uri对应的缓存文件，可参照下面的实现，内部保存图片会用到。如果你不需要保存图片这个功能，可以返回null。
    @Override
    public File getImageFile(@NonNull Context context, @NonNull Object uri) {
        try {
            return Glide.with(context).downloadOnly().load(uri).submit().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
