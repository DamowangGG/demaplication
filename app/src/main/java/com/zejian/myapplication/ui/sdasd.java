package com.zejian.myapplication.ui;

import com.google.gson.Gson;

public class sdasd {


    public static MessageInfo buildMapMessage(double lat,double lng,String title,String address) {
        TIMMessage TIMMsg = new TIMMessage();
        MessageInfo info = new MessageInfo();
        //位置描述信息
        LocationInfo locationInfo = new LocationInfo(lng,lat,title,address);
        TIMLocationElem elem = new TIMLocationElem();
        elem.setLongitude(lng);
        elem.setLatitude(lat);
        elem.setDesc(new Gson().toJson(locationInfo));

        TIMMsg.addElement(elem);

        info.setExtra(title);
        info.setMsgTime(System.currentTimeMillis()/1000);
        info.setSelf(true);
        info.setTIMMessage(TIMMsg);
        info.setExtra("[位置]");
        info.setFromUser(TIMManager.getInstance().getLoginUser());
        info.setMsgType(MessageInfo.MSG_TYPE_LOCATION);

        return info;
    }

}
