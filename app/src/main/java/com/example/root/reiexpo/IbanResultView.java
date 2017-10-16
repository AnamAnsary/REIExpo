package com.example.root.reiexpo;

/**
 * Created by root on 9/10/17.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import at.nineyards.anyline.util.DimensUtil;

public class IbanResultView extends RelativeLayout {

    private static final String TAG = "IbanResultView";
    private static TextView resultText;
    static String result;
    String res;

    public IbanResultView(Context context) {
        super(context);
        init();
    }

    public IbanResultView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IbanResultView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {

        setPadding(DimensUtil.getPixFromDp(getContext(), 4), DimensUtil.getPixFromDp(getContext(), 16),
                DimensUtil.getPixFromDp(getContext(), 4), DimensUtil.getPixFromDp(getContext(), 16));

        setBackgroundResource(R.drawable.rounded_corners);

        inflate(getContext(), R.layout.iban_result, this);

        resultText = (TextView) findViewById(R.id.text_result);

        result = resultText.getText().toString();
        Log.w(TAG, "init: result is " +result );

    }

    public void setResult(String result) {
        resultText.setText(result.trim());
    }

    public static String getScanResult() {
        Log.w(TAG, "getScanResult: " + resultText.getText().toString());
       // result = res;
        return resultText.getText().toString();
    }
}
