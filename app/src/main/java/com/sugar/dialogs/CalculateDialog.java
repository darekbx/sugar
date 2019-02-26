package com.sugar.dialogs;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.sugar.R;
import com.sugar.logic.CalculateWeight;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CalculateDialog extends DialogFragment implements View.OnFocusChangeListener {

    public static final int REQUEST_CODE = 1;
    public static final int RESULT_CODE = 2;
    public static final String RESULT_KEY = "calculate_result";

    @Bind(R.id.weight)
    EditText weightInput;

    @Bind(R.id.sugar)
    EditText sugarInput;

    @Bind(R.id.portion)
    EditText portionInput;

    @Bind(R.id.result)
    EditText resultOutput;

    private float result;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_calculate, container, false);
        ButterKnife.bind(this, root);
        resultOutput.setOnFocusChangeListener(this);
        return root;
    }

    @OnClick(R.id.button_cancel)
    public void cancel() {
        dismiss();
    }

    @OnClick(R.id.button_insert)
    public void insert() {
        forwardResult();
        dismiss();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            updateResult();
        }
    }

    private void forwardResult() {
        Intent intent = new Intent();
        intent.putExtra(RESULT_KEY, result);
        getTargetFragment().onActivityResult(getTargetRequestCode(), RESULT_CODE, intent);
    }

    private void updateResult() {
        result = calculate();
        resultOutput.setText("" + result);
    }

    private float calculate() {
        float weight = Float.parseFloat(weightInput.getText().toString());
        float sugar = Float.parseFloat(sugarInput.getText().toString());
        float portion = Float.parseFloat(portionInput.getText().toString());
        return new CalculateWeight().calculate(weight, sugar, portion);
    }
}
