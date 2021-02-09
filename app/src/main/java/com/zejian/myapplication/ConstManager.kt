package com.zejian.myapplication

object ConstManager {

    init {
        System.loadLibrary("native-lib")
    }


    external fun getDeviceId(): String

    external fun getAliToken(): String



}