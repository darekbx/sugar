package com.sugar.adapter;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.OnRebindCallback;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sugar.Entry;
import com.sugar.R;
import com.sugar.databinding.AdapterEntryBinding;
import com.sugar.databinding.AdapterExpandableBinding;
import com.sugar.model.Summary;

import java.util.List;

/**
 * Created by daba on 2016-04-27.
 */
public class ExpandableRecyclerAdapter extends RecyclerView.Adapter<ExpandableRecyclerAdapter.BindingViewHolder> {

    private static Context context;
    private List<Summary> items;

    public ExpandableRecyclerAdapter(Context context, List<Summary> items) {
        ExpandableRecyclerAdapter.context = context;
        this.items = items;
    }

    @Override
    public BindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AdapterExpandableBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.adapter_expandable,
                parent,
                false);
        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(BindingViewHolder holder, int position) {
        AdapterExpandableBinding viewDataBinding = holder.getViewDataBinding();
        viewDataBinding.setHandlers(holder);
        viewDataBinding.setSummary(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public void expand(int groupIndex) {
        if (items.size() == 0) return;
        items.get(groupIndex).isExpanded = true;
    }

    public static class BindingViewHolder extends RecyclerView.ViewHolder {

        private AdapterExpandableBinding mViewDataBinding;

        public BindingViewHolder(AdapterExpandableBinding viewDataBinding) {
            super(viewDataBinding.getRoot());
            mViewDataBinding = viewDataBinding;
            mViewDataBinding.executePendingBindings();
            mViewDataBinding.addOnRebindCallback(new OnRebindCallback() {
                @Override
                public void onBound(ViewDataBinding binding) {
                    super.onBound(binding);
                    expandCollapseChild();
                }
            });
        }

        public AdapterExpandableBinding getViewDataBinding() {
            return mViewDataBinding;
        }

        @BindingAdapter({"bind:textAmountColor"})
        public static void setTextAmountColor(View view, int color) {
            if (context != null && color != 0) {
                ((TextView) view).setTextColor(color);
            }
        }

        public void onClickGroup(View view) {
            boolean isExpanded = mViewDataBinding.getSummary().isExpanded;
            mViewDataBinding.getSummary().isExpanded = !isExpanded;
            expandCollapseChild();
        }

        private void expandCollapseChild() {
            if (mViewDataBinding.getSummary().isExpanded) {
                expandChild();
            } else {
                collapseChild();
            }
        }

        private void expandChild() {
            mViewDataBinding.childrenContainer.setVisibility(View.VISIBLE);
            mViewDataBinding.childrenContainer.removeAllViews();

            LayoutInflater inflater = LayoutInflater.from(context);

            for (Entry entry : mViewDataBinding.getSummary().entries) {
                AdapterEntryBinding binding = DataBindingUtil.inflate(
                        inflater, R.layout.adapter_entry, mViewDataBinding.childrenContainer, false);
                binding.setEntry(entry);
                mViewDataBinding.childrenContainer.addView(binding.getRoot());
            }

            mViewDataBinding.getRoot().requestLayout();
            mViewDataBinding.childrenContainer.setTag(mViewDataBinding.getSummary().date);
        }

        private void collapseChild() {
            mViewDataBinding.childrenContainer.removeAllViews();
            mViewDataBinding.childrenContainer.setVisibility(View.GONE);
        }
    }
}
