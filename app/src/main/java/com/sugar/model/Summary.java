package com.sugar.model;

import com.sugar.Entry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daba on 2016-04-07.
 */
public class Summary {

    public int color;
    public String date;
    public int count;
    public double sugar_amount;
    public boolean isExpanded;

    public List<Entry> entries;

    public Summary() {
        entries = new ArrayList<>();
    }
}
