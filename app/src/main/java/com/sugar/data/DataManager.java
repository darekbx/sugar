package com.sugar.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.sugar.DaoMaster;
import com.sugar.DaoSession;
import com.sugar.Entry;
import com.sugar.EntryDao;
import com.sugar.R;
import com.sugar.model.EntryEx;
import com.sugar.model.Summary;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import de.greenrobot.dao.query.DeleteQuery;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.GroupedObservable;

/**
 * Created by daba on 2016-04-08.
 */
public class DataManager implements EntryDataSource {

    private static final String DB = "items-db";

    protected void deleteOlderThanAYear(Context context) {
        Calendar yearAgoCalendar = Calendar.getInstance();
        yearAgoCalendar.add(Calendar.YEAR, -1);

        DaoSession session = newDaoSession(context);
        DeleteQuery<Entry> deleteQuery = session.getEntryDao()
                .queryBuilder()
                .where(EntryDao.Properties.Date.le(yearAgoCalendar.getTimeInMillis()))
                .buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();
    }

    public Observable<List<Summary>> getSummaries(final Context context) {
        return Observable
                .create(new Observable.OnSubscribe<List<Entry>>() {
                    @Override
                    public void call(Subscriber<? super List<Entry>> subscriber) {
                        DaoSession session = newDaoSession(context);
                        deleteOlderThanAYear(context);
                        List<Entry> entries = session.getEntryDao().loadAll();
                        session.getDatabase().close();

                        subscriber.onNext(entries);
                        subscriber.onCompleted();
                    }
                })
                .flatMap(new Func1<List<Entry>, Observable<Entry>>() {
                    @Override
                    public Observable<Entry> call(List<Entry> entries) {
                        return Observable.from(entries);
                    }
                })
                .groupBy(new Func1<Entry, String>() {
                    @Override
                    public String call(Entry entry) {
                        return EntryEx.fromEntry(entry).getDateFormatted();
                    }
                })
                .flatMap(new Func1<GroupedObservable<String, Entry>, Observable<Summary>>() {
                    @Override
                    public Observable<Summary> call(GroupedObservable<String, Entry> stringEntryGroupedObservable) {

                        final Summary summary = new Summary();
                        summary.date = stringEntryGroupedObservable.getKey();
                        summary.sugar_amount = 0d;

                        stringEntryGroupedObservable.forEach(new Action1<Entry>() {
                            @Override
                            public void call(Entry entry) {
                                summary.entries.add(entry);
                                summary.sugar_amount += entry.getSugar_amount();
                                summary.color = getColorByAmount(summary.sugar_amount);
                                summary.count++;
                            }
                        });

                        return Observable.just(summary);
                    }
                })
                .toList()
                .flatMap(new Func1<List<Summary>, Observable<List<Summary>>>() {
                    @Override
                    public Observable<List<Summary>> call(List<Summary> summaries) {
                        Collections.reverse(summaries);
                        return Observable.just(summaries);
                    }
                });
    }

    public Observable<List<Entry>> getEntries(final Context context) {
        return Observable
                .create(new Observable.OnSubscribe<List<Entry>>() {
                    @Override
                    public void call(Subscriber<? super List<Entry>> subscriber) {
                        List<Entry> entries = doGetEntries(context);
                        subscriber.onNext(entries);
                        subscriber.onCompleted();
                    }
                })
                .flatMap(new Func1<List<Entry>, Observable<Entry>>() {
                    @Override
                    public Observable<Entry> call(List<Entry> entries) {
                        return Observable.from(entries);
                    }
                })
                .distinct(new Func1<Entry, String>() {
                    @Override
                    public String call(Entry entry) {
                        return entry.getDescription();
                    }
                })
                .toList();
    }

    protected List<Entry> doGetEntries(Context context) {
        DaoSession session = newDaoSession(context);
        List<com.sugar.Entry> entries = session.getEntryDao().loadAll();
        session.getDatabase().close();
        return entries;
    }

    public void deleteEntry(Context context, Entry entry) {
        DaoSession session = newDaoSession(context);
        session.getEntryDao().delete(entry);
        session.getDatabase().close();
    }

    public void saveEntry(Context context, Entry... entries) {
        DaoSession session = newDaoSession(context);
        session.getEntryDao().insertInTx(entries);
        session.getDatabase().close();
    }

    private int getColorByAmount(double amount) {
        if (amount <= 36) {
            return R.color.green;
        } else if (amount > 36 && amount <= 46) {
            return R.color.lightgreen;
        } else if (amount > 46 && amount <= 56) {
            return R.color.lime;
        } else if (amount > 56 && amount <= 66) {
            return R.color.yellow;
        } else if (amount > 66 && amount <= 76) {
            return R.color.amber;
        } else if (amount > 76 && amount <= 86) {
            return R.color.orange;
        } else if (amount > 86 && amount <= 96) {
            return R.color.deeporange;
        } else if (amount > 96 && amount <= 106) {
            return R.color.red;
        } else if (amount > 106 && amount <= 116) {
            return R.color.pink;
        } else if (amount > 116 && amount <= 126) {
            return R.color.violet;
        } else if (amount > 126 && amount <= 136) {
            return R.color.darkviolet;
        } else {
            return R.color.grey;
        }
    }

    private DaoSession newDaoSession(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster master = new DaoMaster(db);
        return master.newSession();
    }
}