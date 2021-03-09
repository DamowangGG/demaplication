package com.zejian.myapplication.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wheel.JsonBean
import com.wheel.PickerStyle
import com.wheel.builder.OptionsPickerBuilder
import com.wheel.interfaces.IPickerViewData
import com.wheel.listener.OnOptionsSelectListener
import com.zejian.myapplication.R
import kotlinx.android.synthetic.main.activity_main234.*

class Main234Activity : AppCompatActivity() {

    var index1 = 0
    var idnex0 = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main234)

        SHOW_ALL.setOnClickListener {
            val list = mutableListOf<JsonBean.ProvinceBean>()
            list.addAll(JsonBean.parseData(this))
            val cityList = mutableListOf<MutableList<JsonBean.CityBean>>()
            cityList.addAll(list.map { it.cityList })

            val builder = OptionsPickerBuilder(this, OnOptionsSelectListener { options1, options2, options3, _ ->
                //返回的分别是三个级别的选中位置
                idnex0 = options1
                index1 = options2
            })
            val pvOptions = PickerStyle.setCommonStyle(this,builder).setTitleText("城市选择")
                .build<IPickerViewData>()
            pvOptions.setPicker(list as List<IPickerViewData>,
                cityList as List<MutableList<IPickerViewData>>
            )//三级选择器
            pvOptions.setSelectOptions(0,0)
            pvOptions.show()
        }

    }
}