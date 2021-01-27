package com.zejian.myapplication.ui.update;

public class UpdateBean {
    private String serverVersionName;
    private String serverVersionCode;
    private String apkUrl;
    private String updateMsg;
    private boolean force;

    public UpdateBean(){

    }

    public String getServerVersionCode() {
        return serverVersionCode;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public String getServerVersionName() {
        return serverVersionName;
    }

    public String getUpdateMsg() {
        return updateMsg;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public void setServerVersionCode(String serverVersionCode) {
        this.serverVersionCode = serverVersionCode;
    }

    public void setUpdateMsg(String updateMsg) {
        this.updateMsg = updateMsg;
    }

    public void setServerVersionName(String serverVersionName) {
        this.serverVersionName = serverVersionName;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }
}
