package com.udacity.stockhawk.wgt;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

/**
 * Created by gaspa on 25.3.2017.
 */

public class WgtRemoteViews extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent innerIntent) {
        return new RemoteViewsFactory() {
            Context context;
            Cursor mCursor;
            Intent outerIntent;
            @Override
            public void onCreate() {
                context = WgtRemoteViews.this;
                outerIntent = innerIntent;
            }

            @Override
            public void onDataSetChanged() {
                if(mCursor !=null)
                    if(!mCursor.isClosed())
                        mCursor.close();

                final long identityToken = Binder.clearCallingIdentity();
                mCursor=getContentResolver().query(Contract.Quote.URI,null,null,null,null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if(mCursor!=null)
                    mCursor.close();
                mCursor = null;
            }

            @Override
            public int getCount() {
                if(mCursor == null)
                    return 0;
                return mCursor.getCount();
            }

            @Override
            public RemoteViews getViewAt(int i) {
                if(i == AdapterView.INVALID_POSITION || mCursor == null || !mCursor.moveToPosition(i))
                {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),R.layout.widget_list_item);
                views.setTextViewText(R.id.symbol,mCursor.getString(mCursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL)));
                views.setTextViewText(R.id.price,"$" + mCursor.getString(mCursor.getColumnIndex(Contract.Quote.COLUMN_PRICE)));
                float change = mCursor.getFloat(
                        mCursor.getColumnIndex(
                                Contract.Quote.COLUMN_PERCENTAGE_CHANGE
                        )
                );
                views.setTextViewText(
                        R.id.change,
                        change>0?"+"+Float.toString(change)+"%":Float.toString(change)+"%"
                );
                if(change>0)
                    views.setInt(R.id.change,"setBackgroundResource",R.drawable.percent_change_pill_green);
                else
                    views.setInt(R.id.change,"setBackgroundResource",R.drawable.percent_change_pill_red);
                Intent intent = new Intent();
                intent.putExtra(getString(R.string.intent_extra_stock_id),mCursor.getString(mCursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL)));
                intent.putExtra(getString(R.string.intent_extra_stock_history),mCursor.getString(mCursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY)));
                views.setOnClickFillInIntent(R.id.widget_list_item,intent);

                Intent refresh = new Intent();
                views.setOnClickFillInIntent(R.id.widget_refresh,refresh);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int i) {
                if(mCursor!=null) {
                    if (mCursor.moveToPosition(i)) {
                        return mCursor.getColumnIndex(Contract.Quote._ID);
                    }
                    else return i;
                }
                else
                return i;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
