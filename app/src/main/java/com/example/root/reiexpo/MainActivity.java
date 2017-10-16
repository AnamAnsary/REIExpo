package com.example.root.reiexpo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import at.nineyards.anyline.camera.CameraPermissionHelper;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private CameraPermissionHelper cameraPermissionHelper;
    private static final int PERMISSIONS_REQUEST_CAMERA = 1;
    Intent intent;
    //private Intent targetIntent;
    EditText etOrgName, etCName, etEmail, etTel, etCountry, etWeb;
    Button btnscan,btnCompanyname,btSave,btClear;
    private String comp_name;
    private String details;
    private String Name;
    private String Tel;
    private String Email;
    private String website;
    private String Country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // CameraPermissionHelper simplifies the request for the camera permission
        cameraPermissionHelper = new CameraPermissionHelper(this);

        etOrgName = (EditText) findViewById(R.id.etOrgName);
        etCName = (EditText) findViewById(R.id.etCName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etTel = (EditText) findViewById(R.id.etTel);
        etCountry = (EditText) findViewById(R.id.etCountry);
        etWeb = (EditText) findViewById(R.id.etWeb);

        btnscan = (Button) findViewById(R.id.btnscan);
        btnCompanyname = (Button) findViewById(R.id.btnCname);
        btSave = (Button) findViewById(R.id.btSave);
        btClear = (Button) findViewById(R.id.btClear);

        btnCompanyname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this,ScanCompanyNameActivity.class);

                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    // No explanation needed, it should be obvious that the camera is needed to scan something
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);

                    return;
                }

                startActivityForResult(intent,1);
            }
        });


        btnscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intent = new Intent(MainActivity.this,ScanActivity.class);

                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    // No explanation needed, it should be obvious that the camera is needed to scan something
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);

                    return;
                }

                startActivityForResult(intent,2);
                /*// ask if permissions were already granted
                if (cameraPermissionHelper.hasPermissions()) {
                    startActivity(intent);
                } else {
                    // otherwise request the permissions
                    cameraPermissionHelper.requestPermissions();
                    //this.targetIntent = intent;
                }*/

            }
        });

        btClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etOrgName.setText("");
                etCName.setText("");
                etEmail.setText("");
                etTel.setText("");
                etWeb.setText("");
                etCountry.setText("");
            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                comp_name = etOrgName.getText().toString();
                Name = etCName.getText().toString();
                Email = etEmail.getText().toString();
                Tel = etTel.getText().toString();
                website = etWeb.getText().toString();
                Country = etCountry.getText().toString();

                if (Name != null && Name.length() !=0 && Email != null && Email.length() !=0)
                {
                    if (checkInternetConnection()) {

                        @SuppressWarnings("deprecation")
                        final ProgressDialog pd = ProgressDialog.show(MainActivity.this, "", "Sending Mail...", true);
                        RequestQueue queue1 = Volley.newRequestQueue(MainActivity.this);
                        String urlMail = "http://vmechatronics.com/app/sendMail.php";

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlMail,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.w(TAG, "onResponse: " + response.toString());
                                        try {
                                            JSONArray array = new JSONArray(response);
                                            JSONObject jsonResponse = array.getJSONObject(0);
                                            boolean success = jsonResponse.getBoolean("success");
                                            Log.w(TAG, "onResponse: jsonresponse" + success);
                                            if (success) {
                                                Toast.makeText(MainActivity.this, "Mail Sent!", Toast.LENGTH_LONG).show();

                                                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                                                String url = "http://vmechatronics.com/app/REIShowDir.php";
                                                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                                        new Response.Listener<String>() {

                                                            @Override
                                                            public void onResponse(String response) {
                                                                Log.w(TAG, "onResponse: " + response.toString());
                                                                try {
                                                                    JSONArray array = new JSONArray(response);
                                                                    JSONObject jsonResponse = array.getJSONObject(0);
                                                                    boolean success = jsonResponse.getBoolean("success");
                                                                    Log.w(TAG, "onResponse: jsonresponse" + success);
                                                                    if (success) {
                                                                        pd.dismiss();
                                                                        Toast.makeText(MainActivity.this, "Details saved!", Toast.LENGTH_LONG).show();
                                                                    } else {
                                                                        pd.dismiss();
                                                                        Toast.makeText(MainActivity.this, "Please try again. Couldn't save details.", Toast.LENGTH_LONG).show();
                                                                    }
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        // TODO Auto-generated method stub
                                                    }
                                                }) {
                                                    @Override
                                                    protected Map<String, String> getParams() throws AuthFailureError {
                                                        Map<String, String> params = new HashMap<String, String>();
                                                        Log.w(TAG, "getParams: " + comp_name + " " + Name + " " + Email + " " + Tel + " " + Country + " " + website);
                                                        params.put("comp_name", comp_name);
                                                        params.put("Name", Name);
                                                        params.put("Email", Email);
                                                        params.put("Tel", String.valueOf(Tel));
                                                        params.put("Country", Country);
                                                        params.put("website", website);
                                                        return params;
                                                    }
                                                };
                                                // Add the request to the RequestQueue.
                                                queue.add(stringRequest);


                                            } else {
                                                pd.dismiss();
                                                Toast.makeText(MainActivity.this, "Couldn't sent Mail.Please check Email-ID!", Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO Auto-generated method stub
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                Log.w(TAG, "getParams: " + Name + " " + Email + " "+ comp_name );
                                params.put("comp_name", comp_name);
                                params.put("Name", Name);
                                params.put("Email", Email);

                                return params;
                            }
                        };
                        // Add the request to the RequestQueue.
                        queue1.add(stringRequest);
                    }
            }
            else {
                    Toast.makeText(MainActivity.this, "No Email Id and name found!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

   /* @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        // CameraPermissionHelper will return true if the permission for the camera was granted (and was made via the
        // CameraPermissionHelper class)
        if (cameraPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            startActivity(intent);
        } else {
            // Displays a message to the user, asking to grant the permissions for the camera in order for Anyline to
            // work
            cameraPermissionHelper.showPermissionMessage(null);
        }
        // Other app permission checks go here
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startActivity(intent);

                } else {

                    new AlertDialog.Builder(this)
                            .setTitle(R.string.camera_permission_required)
                            .setMessage(R.string.camera_permission_required_details)
                            .show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 1
        if(requestCode==1)
        {
            if(null!=data) {
                comp_name = data.getStringExtra("CompanyName");
                etOrgName.setText(comp_name);
            }
            Log.w(TAG, "onActivityResult: company result " + comp_name );
        }
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==2)
        {
            if(null!=data){
                details = data.getStringExtra("Details");
                String[] tokens = details.split("\n");

                for (int i = 0; i < tokens.length; i++) {
                    Log.w(TAG, "onActivityResult: Token " + tokens[i] );

                    if (tokens[i].startsWith("Contact Person:")) {
                        Name = tokens[i].substring(16);
                        Log.w(TAG, "name is" + Name);
                        etCName.setText(Name);
                    } else if (tokens[i].startsWith("Tel:")) {
                        Tel = tokens[i].substring(5);
                        Log.w(TAG, "tel is" + Tel);
                        etTel.setText(Tel);
                    } else if (tokens[i].startsWith("Email:")) {
                        Email = tokens[i].substring(7);
                        Log.w(TAG, "Email is" + Email);
                        etEmail.setText(Email);
                    } else if (tokens[i].startsWith("Website:")) {
                        website = tokens[i].substring(9);
                        Log.w(TAG, "website is" + website);
                        etWeb.setText(website);
                    } else if (tokens[i].startsWith("Country:")) {
                        Country = tokens[i].substring(9);
                        Log.w(TAG, "Country is" + Country);
                        etCountry.setText(Country);
                    }
                }
            }
            Log.w(TAG, "onActivityResult: Other Details " + details );
        }

    }


    private boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            return true;
        }
        // not connected to the internet
        Toast.makeText(MainActivity.this, "Please check your Internet Connection!", Toast.LENGTH_LONG).show();
        return false;
    }
}
