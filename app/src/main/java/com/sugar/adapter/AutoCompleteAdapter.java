package com.sugar.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.sugar.Entry;
import com.sugar.databinding.AdapterInputDialogBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daba on 2016-04-11.
 */
public class AutoCompleteAdapter extends ArrayAdapter<Entry> {

    private LayoutInflater inflater;
    private int resourceId;

    private List<Entry> mItems, mSuggestions, mTempItems;

    public AutoCompleteAdapter(Context context, int resource, List<Entry> items) {
        super(context, resource, items);
        inflater = LayoutInflater.from(context);
        resourceId = resource;

        mItems = items;
        mTempItems = new ArrayList<>(items);
        mSuggestions = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AdapterInputDialogBinding binding = (convertView == null)
                ? (AdapterInputDialogBinding) DataBindingUtil.inflate(inflater, resourceId, parent, false)
                : (AdapterInputDialogBinding) DataBindingUtil.bind(convertView);
        binding.setEntry(mItems.get(position));
        return binding.getRoot();
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((Entry) resultValue).getDescription();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                mSuggestions.clear();
                for (Entry entry : mTempItems) {
                    if (entry.getDescription().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        mSuggestions.add(entry);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mSuggestions;
                filterResults.count = mSuggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<Entry> filterList = (ArrayList<Entry>) results.values;
            if (results != null && results.count > 0) {
                clear();
                addAll(filterList);
                notifyDataSetChanged();
            }
        }
    };
}