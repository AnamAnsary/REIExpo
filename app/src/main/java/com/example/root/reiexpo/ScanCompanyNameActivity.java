package com.example.root.reiexpo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import at.nineyards.anyline.camera.AnylineViewConfig;
import at.nineyards.anyline.camera.CameraConfig;
import at.nineyards.anyline.camera.CameraFeatures;
import at.nineyards.anyline.modules.ocr.AnylineOcrConfig;
import at.nineyards.anyline.modules.ocr.AnylineOcrResult;
import at.nineyards.anyline.modules.ocr.AnylineOcrResultListener;
import at.nineyards.anyline.modules.ocr.AnylineOcrScanView;

/**
 * Created by root on 11/10/17.
 */

public class ScanCompanyNameActivity  extends AppCompatActivity {

    private static final String TAG = ScanActivity.class.getSimpleName();
    private AnylineOcrScanView scanView;
    private IbanResultView ibanResultView;
    private String scannedCName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the flag to keep the screen on (otherwise the screen may go dark during scanning)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_anyline_ocr);

        addIbanResultView();

        String license = getString(R.string.anyline_license_key);
        // Get the view from the layout
        scanView = (AnylineOcrScanView) findViewById(R.id.scan_view);
        // Configure the view (cutout, the camera resolution, etc.) via json (can also be done in xml in the layout)
        scanView.setConfig(new AnylineViewConfig(this, "companyName_view_config.json"));

        // Copies given traineddata-file to a place where the core can access it.
        // This MUST be called for every traineddata file that is used (before startScanning() is called).
        // The file must be located directly in the assets directory (or in tessdata/ but no other folders are allowed)
        scanView.copyTrainedData("tessdata/eng_no_dict.traineddata", "d142032d86da1be4dbe22dce2eec18d7");
        //scanView.copyTrainedData("tessdata/deu.traineddata", "2d5190b9b62e28fa6d17b728ca195776");

        //Configure the OCR for IBANs
        AnylineOcrConfig anylineOcrConfig = new AnylineOcrConfig();
        // AUTO ScanMode automatically detects the correct text without any further parameters to be set
        anylineOcrConfig.setScanMode(AnylineOcrConfig.ScanMode.LINE);
        // set the languages used for OCR
        anylineOcrConfig.setTesseractLanguages("eng_no_dict");

        //anylineOcrConfig.setMinCharHeight(16);
        //anylineOcrConfig.setMaxCharHeight(23);
        anylineOcrConfig.setRemoveSmallContours(true);
        anylineOcrConfig.setMinSharpness(70);
        // allow only capital letters and numbers
        anylineOcrConfig.setCharWhitelist("ABCDEFGHIJKLMNOPQRSTUVWXYZ.,");
        // The minimum confidence required to return a result, a value between 0 and 100.
        // (higher confidence means less likely to get a wrong result, but may be slower to get a result)
        anylineOcrConfig.setMinConfidence(70);
        // a simple regex for a basic validation of the IBAN, results that don't match this, will not be returned
        // (full validation is more complex, as different countries have different formats)
        //anylineOcrConfig.setValidationRegex("^[A-Z]{2}([0-9A-Z]\\s*){13,32}$");
        scanView.setAnylineOcrConfig(anylineOcrConfig);

        // set individual camera settings for this example by getting the current preferred settings and adapting them
        CameraConfig camConfig = scanView.getPreferredCameraConfig();
        // change default focus mode to auto (works better if cutout is not in the center)
        camConfig.setFocusMode(CameraFeatures.FocusMode.AUTO);
        // autofocus is called in this interval (8000 is default)
        camConfig.setAutoFocusInterval(8000);
        // call autofocus if view is touched (true is default)
        camConfig.setFocusOnTouchEnabled(true);
        // focus where the cutout is (true is default)
        camConfig.setFocusRegionEnabled(true);
        // automatic exposure calculation based on where the cutout is (true is default)
        camConfig.setAutoExposureRegionEnabled(true);

        // initialize with the license and a listener
        scanView.initAnyline(license, new AnylineOcrResultListener() {

            @Override
            public void onResult(AnylineOcrResult anylineOcrResult) {
                // Called when a valid result is found (minimum confidence is exceeded and validation with regex was ok)
                Log.d(TAG, "onResult() called with: " + "result = [" + anylineOcrResult + "]");
                ibanResultView.setResult(anylineOcrResult.getResult());
                ibanResultView.setVisibility(View.VISIBLE);
            }
        });

        //scanView.setReportingEnabled(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SettingsFragment.KEY_PREF_REPORTING_ON, true));

        ibanResultView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ibanResultView.setVisibility(View.INVISIBLE);
                Intent intent=new Intent();
                scannedCName = IbanResultView.getScanResult();
                Log.d(TAG, "onResult() returned: " +scannedCName);
                intent.putExtra("CompanyName",scannedCName);
                setResult(1,intent);
                if (!scanView.isRunning()) {
                    scanView.startScanning();
                }
            }
        });
    }

    private void addIbanResultView() {
        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.main_layout);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

        ibanResultView = new IbanResultView(this);
        ibanResultView.setVisibility(View.INVISIBLE);

        mainLayout.addView(ibanResultView, params);
    }

    @Override
    protected void onResume() {
        super.onResume();

        scanView.startScanning();
    }

    @Override
    protected void onPause() {
        super.onPause();

        scanView.cancelScanning();
        scanView.releaseCameraInBackground();
    }

    @Override
    public void onBackPressed() {
        if (ibanResultView.getVisibility() == View.VISIBLE) {
            Intent intent=new Intent();
            scannedCName = IbanResultView.getScanResult();
            intent.putExtra("CompanyName",scannedCName);
            setResult(1,intent);
            ibanResultView.setVisibility(View.INVISIBLE);
            if (!scanView.isRunning()) {
                scanView.startScanning();
            }
        } else {
            super.onBackPressed();
        }

    }
}
