package com.qr.scanner.preference

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {

    private  var preferences: SharedPreferences?=null
     init {
        preferences= context.getSharedPreferences(MY_PREFS_NAME, 0)
     }
    fun edit(): SharedPreferences.Editor? {
       return preferences?.edit()
    }


    var sound by preferences?.booleanPreference(key_sound,true)!!
    var autoFocus by preferences?.booleanPreference(key_auto_focus,true)!!
    var autoCopy by preferences?.booleanPreference(key_auto_copy,false)!!
    var vibrate by preferences?.booleanPreference(key_vibrate,true)!!
    var flash by preferences?.booleanPreference(key_flash,false)!!
    var backCamera by preferences?.booleanPreference(key_back_camera,true)!!

}



const val MY_PREFS_NAME="pref_qr_scanner"

const val key_sound= "key_sound"
const val key_auto_focus= "key_auto_focus"
const val key_auto_copy= "key_auto_copy"
const val key_vibrate= "key_vibrate"
const val key_flash= "key_flash"
const val key_back_camera= "key_back_camera"

