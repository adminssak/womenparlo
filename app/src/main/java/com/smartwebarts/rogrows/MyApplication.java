package com.smartwebarts.rogrows;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smartwebarts.rogrows.models.NewsModel;
import com.smartwebarts.rogrows.retrofit.UtilMethods;
import com.smartwebarts.rogrows.retrofit.mCallBackResponse;
import com.smartwebarts.rogrows.utils.ApplicationConstants;

import java.lang.reflect.Type;
import java.util.List;

public class MyApplication extends Application {

    private AppEventsLogger logger;

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.setAutoLogAppEventsEnabled(true);
        FacebookSdk.setAdvertiserIDCollectionEnabled(true);
        FacebookSdk.fullyInitialize();

        logger = AppEventsLogger.newLogger(getApplicationContext());

       /* if (UtilMethods.INSTANCE.isNetworkAvialable(getApplicationContext())) {
            UtilMethods.INSTANCE.news(getApplicationContext(), new mCallBackResponse() {
                @Override
                public void success(String from, String message) {

                    Type type = new TypeToken<List<NewsModel>>(){}.getType();
                    List<NewsModel> newsModels = new Gson().fromJson(message, type);

                    StringBuilder messageBuilder = new StringBuilder();
                    for (NewsModel newsModel: newsModels) {
                        messageBuilder.append(newsModel.getNote());
                    }
                    message = messageBuilder.toString();

                    Log.e("TAG", "success: " +message );
                    SharedPreferences sharedpreferences = getSharedPreferences(ApplicationConstants.INSTANCE.MyPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(ApplicationConstants.INSTANCE.NEWS, message);
                    editor.apply();
                }

                @Override
                public void fail(String from) {
                    Toast.makeText(getApplicationContext(), from, Toast.LENGTH_SHORT).show();

                }
            });
        }*/
    }

    public void logLeonEvent (String eventName, String leon, double valToSum) {
        Bundle params = new Bundle();
        params.putString(eventName, leon);
        logger.logEvent(eventName, valToSum, params);
    }

    public static String getNews(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(ApplicationConstants.INSTANCE.MyPREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences.getString(ApplicationConstants.INSTANCE.NEWS, "");
    }

    public void logBattleTheMonsterEvent () {
        logger.logEvent("BattleTheMonster");
    }
}
