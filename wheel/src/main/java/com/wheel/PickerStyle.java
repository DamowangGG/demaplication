package com.wheel;

import android.content.Context;
import android.graphics.Color;

import androidx.core.content.ContextCompat;
import com.wheel.builder.OptionsPickerBuilder;
import com.wheel.builder.TimePickerBuilder;


public class PickerStyle {

    public static OptionsPickerBuilder setCommonStyle(Context context, OptionsPickerBuilder builder){
        builder.setContentTextSize(16)//设置滚轮文字大小
                .setDividerColor(ContextCompat.getColor(context, R.color.divider))//设置分割线的颜色
                .setBgColor(Color.WHITE)
                .setTitleBgColor(Color.WHITE)
                .setTitleColor(ContextCompat.getColor(context,R.color.text))
                .setCancelColor(ContextCompat.getColor(context,R.color.design_default_color_primary_variant))
                .setTitleSize(16)
                .setSubCalSize(15)
                .setLineSpacingMultiplier(2.0f)
                .setSubmitColor(ContextCompat.getColor(context,R.color.color_primary))
                .setTextColorCenter(ContextCompat.getColor(context,R.color.text888));
        return builder;
    }

    public static TimePickerBuilder setTimeStyle(Context context, TimePickerBuilder builder){
        builder.setContentTextSize(16)//设置滚轮文字大小
                .setDividerColor(ContextCompat.getColor(context, R.color.divider))//设置分割线的颜色
                .setBgColor(Color.WHITE)
                .setTitleBgColor(Color.WHITE)
                .setTitleColor(ContextCompat.getColor(context,R.color.text))
                .setCancelColor(ContextCompat.getColor(context,R.color.color_primary))
                .setTitleSize(16)
                .setSubCalSize(15)
                .setLineSpacingMultiplier(2.0f)
                .setSubmitColor(ContextCompat.getColor(context,R.color.color_primary))
                .setTextColorCenter(ContextCompat.getColor(context,R.color.text888));
        return builder;
    }

}
