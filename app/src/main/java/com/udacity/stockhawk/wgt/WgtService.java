package com.udacity.stockhawk.wgt;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.ui.MainActivity;

/**
 * Created by gaspa on 25.3.2017.
 */

public class WgtService extends IntentService {
    public WgtService() {
        super("WgtService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {


        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,Provider.class));
        Cursor data = getContentResolver().query(Contract.Quote.URI,null,null,null,null);
        if(data == null)
            return;
        if(!data.moveToFirst())
        {
            data.close();
            return;
        }

        for(int appWidgetId:appWidgetIds)
        {
            RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.widget);

            Intent actIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,actIntent,0);

            remoteViews.setOnClickPendingIntent(R.id.widget_list_item,pendingIntent);
            remoteViews.setRemoteAdapter(R.id.widget_list,new Intent(this,WgtRemoteViews.class));

            Intent clickIntentTemplate = new Intent(this, MainActivity.class);
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(this)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);
            remoteViews.setOnClickFillInIntent(R.id.widget_list_item,clickIntentTemplate);
            remoteViews.setEmptyView(R.id.widget_list, R.id.widget_empty_view);

            Intent refreshIntent = new Intent(this,WgtService.class);
            PendingIntent refreshPendingIntent = PendingIntent.getService(this,0,refreshIntent,0);
            remoteViews.setOnClickPendingIntent(R.id.widget_refresh,refreshPendingIntent);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.widget_list);
            appWidgetManager.updateAppWidget(appWidgetId,remoteViews);
        }
    }
}
