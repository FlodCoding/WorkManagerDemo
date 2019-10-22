package com.coassets.android.workmanagertest.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import androidx.annotation.Nullable;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Set;

/**
 * SimpleDes:
 * Creator: Flood
 * Date: 2019-03-20
 * UseDes:
 * TODO 存在问题，同一个文件夹如果存放的字段过多就会，速度变慢，应该存放在不同目录下
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class PrefsUtil {

    private static int spMode = Context.MODE_PRIVATE;
    private static SharedPreferences mSharedPreferences;

    public static void init(Application application) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);
    }

    public static void init(Application application, String name, int mode) {
        mSharedPreferences = application.getSharedPreferences(name, mode);
    }

    public static void putString(String key, @Nullable String value) {
        mSharedPreferences.edit().putString(key, value).apply();
    }

    public static boolean putStringCommit(String key, @Nullable String value) {
        return mSharedPreferences.edit().putString(key, value).commit();
    }

    public static String getString(String key, String defValue) {
        return mSharedPreferences.getString(key, defValue);
    }

    public static void putStringSet(String key, @Nullable Set<String> values) {
        mSharedPreferences.edit().putStringSet(key, values).apply();
    }

    public static boolean putStringSetCommit(String key, @Nullable Set<String> values) {
        return mSharedPreferences.edit().putStringSet(key, values).commit();
    }

    public static Set<String> StringSet(String key, Set<String> defValues) {
        return mSharedPreferences.getStringSet(key, defValues);
    }

    public static void putInt(String key, int value) {
        mSharedPreferences.edit().putInt(key, value).apply();
    }

    public static boolean putIntCommit(String key, int value) {
        return mSharedPreferences.edit().putInt(key, value).commit();
    }

    public static int getInt(String key, int defValue) {
        return mSharedPreferences.getInt(key, -1);
    }


    public static void putLong(String key, long value) {
        mSharedPreferences.edit().putLong(key, value).apply();
    }


    public static boolean putLongCommit(String key, long value) {
        return mSharedPreferences.edit().putLong(key, value).commit();
    }

    public static long getLong(String key, long defValue) {
        return mSharedPreferences.getLong(key, defValue);
    }

    public static void putFloat(String key, float value) {
        mSharedPreferences.edit().putFloat(key, value).apply();
    }

    public static boolean putFloatCommit(String key, float value) {
        return mSharedPreferences.edit().putFloat(key, value).commit();
    }

    public static float getFloat(String key, float defValue) {
        return mSharedPreferences.getFloat(key, defValue);
    }

    public static void putBoolean(String key, boolean value) {
        mSharedPreferences.edit().putBoolean(key, value).apply();
    }

    public static boolean putBooleanCommit(String key, boolean value) {
        return mSharedPreferences.edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }

    /**
     * 存放一个实体类，这个实体类需要实现Serializable
     */
    public static boolean putSerializable(String key, Serializable serializable) {
        if (serializable == null) return false;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(serializable);
            String objBase64 = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            mSharedPreferences.edit().putString(key, objBase64).apply();
            baos.close();
            oos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static Serializable getSerializable(String key) {
        String objBase64 = mSharedPreferences.getString(key, null);
        if (objBase64 == null) return null;
        byte[] bytes = Base64.decode(objBase64, Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream bis = new ObjectInputStream(bais);
            Serializable serializable = (Serializable) bis.readObject();
            bais.close();
            bis.close();
            return serializable;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    public static void clear() {
        mSharedPreferences.edit().clear().apply();
    }

    public static boolean clearCommit() {
        return mSharedPreferences.edit().clear().commit();
    }

    public static void remove(String key) {
        mSharedPreferences.edit().remove(key).apply();
    }

    public static boolean removeCommit(String key) {
        return mSharedPreferences.edit().remove(key).commit();
    }

}
