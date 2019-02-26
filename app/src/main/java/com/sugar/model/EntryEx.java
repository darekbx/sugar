package com.sugar.model;

import com.sugar.Entry;

import java.text.SimpleDateFormat;

/**
 * Created by daba on 2016-04-08.
 */
public class EntryEx extends Entry {

    private static final String DATE_FORMAT = "dd-MM-yyyy";

    private static SimpleDateFormat formatter;

    private static SimpleDateFormat getFormatterInstance() {
        if (formatter == null) {
            formatter = new SimpleDateFormat(DATE_FORMAT);
        }
        return formatter;
    }

    public String getDateFormatted() {
        return getFormatterInstance().format(getDate());
    }

    public static EntryEx fromEntry(Entry entry) {
        EntryEx ex = new EntryEx();
        ex.setId(entry.getId());
        ex.setDate(entry.getDate());
        ex.setDescription(entry.getDescription());
        ex.setSugar_amount(entry.getSugar_amount());
        return ex;
    }
}