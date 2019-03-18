package com.sugar.dialogs;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.sugar.Entry;
import com.sugar.R;
import com.sugar.adapter.AutoCompleteAdapter;
import com.sugar.data.DataManager;

import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by daba on 2016-04-08.
 */
public class AddDialog extends DialogFragment {

    public interface Listener {
        void onAdd();
    }

    @Bind(R.id.description)
    AutoCompleteTextView description;

    @Bind(R.id.sugar)
    EditText sugar;

    private Listener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_add, container, false);
        ButterKnife.bind(this, root);

        description.setThreshold(1);
        description.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Entry entry = ((AutoCompleteAdapter) parent.getAdapter()).getItem(position);
                sugar.setText(String.valueOf(entry.getSugar_amount()));
            }
        });

        new DataManager()
                .getEntries(getActivity())
                .subscribe(new Action1<List<Entry>>() {
                    @Override
                    public void call(List<Entry> entries) {
                        AutoCompleteAdapter adapter = new AutoCompleteAdapter(getActivity(),
                                R.layout.adapter_input_dialog, entries);
                        description.setAdapter(adapter);
                    }
                });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        description.setOnItemClickListener(null);
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.button_cancel)
    public void cancel() {
        dismiss();
    }

    @OnClick(R.id.button_save)
    public void save() {

        if (TextUtils.isEmpty(description.getText().toString().trim())) {
            description.setError(getString(R.string.empty_error));
            return;
        }
        if (TextUtils.isEmpty(sugar.getText().toString().trim())) {
            sugar.setError(getString(R.string.empty_error));
            return;
        }

        new DataManager().saveEntry(getActivity(), createEntry());
        if (listener != null) {
            listener.onAdd();
        }
        dismiss();
    }

    @OnClick(R.id.button_calculate)
    public void calculate() {
        CalculateDialog calculateDialog = new CalculateDialog();
        calculateDialog.setTargetFragment(this, CalculateDialog.REQUEST_CODE);
        calculateDialog.show(getFragmentManager(), "calculate");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CalculateDialog.REQUEST_CODE && resultCode == CalculateDialog.RESULT_CODE) {
            sugar.setText("" + data.getFloatExtra(CalculateDialog.RESULT_KEY, 0));
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private Entry createEntry() {
        return new Entry(
                null,
                description.getText().toString(),
                Double.parseDouble(sugar.getText().toString()),
                Calendar.getInstance().getTimeInMillis());
    }
}