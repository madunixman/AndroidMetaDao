package net.lulli.android.metadao;

import android.util.Log;

public class ALog
{
    public static final String APP_TAG = "METADAO";

    public String logTag = APP_TAG;

    public ALog(String logTag)
    {
        this.logTag = APP_TAG + ":" + logTag;
    }

    public void debug(String s)
    {
        Log.d(s, logTag);
    }

    public void error(String s)
    {
        Log.e(s, logTag);
    }

    public void error(Exception e)
    {
        Log.e(e.getMessage(), logTag);
    }

    //TODO:
    public void trace(String s)
    {
        Log.i(s, logTag);
    }

    public void info(String s)
    {
        Log.i(s, logTag);
    }

}
