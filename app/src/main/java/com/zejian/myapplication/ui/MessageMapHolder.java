package com.zejian.myapplication.ui;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.lxj.xpopup.XPopup;
import com.tencent.imsdk.TIMLocationElem;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.component.map.LocationInfo;
import com.tencent.qcloud.tim.uikit.component.map.MapLocateActivity;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;
import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;

import me.yinmai.yidui.R;

public class MessageMapHolder extends MessageContentHolder {

    private FrameLayout llLocation;
    private TextureMapView mapView;
    private TextView locationStr;
    private TextView locationAddress;
    private Marker mMarker;
    private LocationInfo locationInfo;

    public MessageMapHolder(View itemView) {
        super(itemView);
    }

    @Override
    public int getVariableLayout() {
        return R.layout.message_adapter_content_map;
    }

    @Override
    public void initVariableViews() {
        llLocation = rootView.findViewById(R.id.ll_location);
        mapView = rootView.findViewById(R.id.item_map);
        locationStr = rootView.findViewById(R.id.tv_location);
        locationAddress = rootView.findViewById(R.id.tv_location_address);
        mapView.onCreate(null);
    }

    @Override
    public void layoutVariableViews(MessageInfo msg, int position) {
        msgContentFrame.setBackground(null);
        switch (msg.getMsgType()) {
            case MessageInfo.MSG_TYPE_LOCATION:
            case MessageInfo.MSG_TYPE_LOCATION + 1:
                performLocation(msg, position);
                break;
        }
    }

    private void resetParentLayout() {
        int padding = ScreenUtil.getPxByDp(4.5f);
        ((FrameLayout) llLocation.getParent()).setPadding(padding, padding, padding, 0);
    }


    private void performLocation(final MessageInfo msg, final int position) {
        resetParentLayout();
        final TIMLocationElem locationElem = (TIMLocationElem) msg.getTIMMessage().getElement(0);
        String str = locationElem.getDesc();
        locationInfo = new Gson().fromJson(str, LocationInfo.class);
        locationStr.setVisibility(View.VISIBLE);
        locationStr.setText(locationInfo.getPlace());
        locationAddress.setText(locationInfo.getAddress());

        addMarker(mapView.getMap(),new LatLng(locationInfo.getLatitude(),locationInfo.getLongitude()));
        mapView.getMap().setOnMapLongClickListener(new AMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (onItemClickListener != null) {
                    final XPopup.Builder builder = new XPopup.Builder(onItemClickListener.getContext())
                            .hasShadowBg(false)
                            .watchView(llLocation);
                    llLocation.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            onItemClickListener.onMessageLongClick(v, position, msg,builder);
                            return true;
                        }
                    });
                }
            }
        });
        llLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(locationInfo != null) {
                    MapLocateActivity.startPreview(TUIKit.getAppContext(),locationInfo);
                }
            }
        });
        //// 聊天气泡的点击事件处理

    }

    public void addMarker(AMap aMap, LatLng D) {
        if(mMarker != null){
            mMarker.remove();
        }
        aMap.getUiSettings().setZoomControlsEnabled(true);
        aMap.getUiSettings().setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
        aMap.getUiSettings().setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
        aMap.getUiSettings().setAllGesturesEnabled(false);
        mMarker = aMap.addMarker(new MarkerOptions().position(new LatLng(D.latitude + 0.00015, D.longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location)));
        mMarker.setVisible(true);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(D, 17));
    }


}
