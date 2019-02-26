package com.sugar.data;

import android.content.Context;
import android.support.annotation.NonNull;

import com.sugar.Entry;
import com.sugar.model.Summary;

import java.util.List;

import rx.Observable;

/**
 * Created by daba on 2016-05-10.
 */
public interface EntryDataSource {
    Observable<List<Summary>> getSummaries(@NonNull final Context context);
    Observable<List<Entry>> getEntries(@NonNull final Context context);
    void saveEntry(@NonNull final Context context, @NonNull Entry... entry);
}