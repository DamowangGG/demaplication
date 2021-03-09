package com.wheel;


import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wheel.interfaces.IPickerViewData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class JsonBean {


    public static class ProvinceBean implements IPickerViewData{
        private int code;
        private String name;
        private List<CityBean> cityList;

        public List<CityBean> getCityList() {
            return cityList;
        }

        public void setCityList(List<CityBean> cityList) {
            this.cityList = cityList;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        @Override
        public String getPickerViewText() {
            return name;
        }
    }

    public static class CityBean implements IPickerViewData{

        private int code;
        private String name;
        private List<AreaBean> areaList;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public List<AreaBean> getAreaList() {
            return areaList;
        }

        public void setAreaList(List<AreaBean> areaList) {
            this.areaList = areaList;
        }

        @Override
        public String getPickerViewText() {
            return name;
        }
    }


    public static class AreaBean implements IPickerViewData{

        private int code;
        private String name;

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        @Override
        public String getPickerViewText() {
            return name;
        }
    }


    public static List<ProvinceBean> parseData(Context context){
        String json = getJson(context, "2020_8.json");
        List<ProvinceBean> provinceBeanList = new Gson().fromJson(json, new TypeToken<List<ProvinceBean>>(){}.getType());
        return provinceBeanList;
    }


    public static String getJson(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


}
