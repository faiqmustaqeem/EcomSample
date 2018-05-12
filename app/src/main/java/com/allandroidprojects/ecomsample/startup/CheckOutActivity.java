package com.allandroidprojects.ecomsample.startup;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.allandroidprojects.ecomsample.Models.ItemModel;
import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.options.CartListActivity;
import com.allandroidprojects.ecomsample.utility.ImageUrlUtils;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CheckOutActivity extends AppCompatActivity {

    Activity activity;
    Button btn_login , btn_place_odrer;
    EditText etFirstname , etLastname , etCompanyname , etCountryname , etAddress , etHousenumber , etCity , etState , etZipcode , etPhone , etEmail;
   int customerId=0;
     ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        activity=this;
        btn_login=(Button)findViewById(R.id.btn_login);
        btn_place_odrer=(Button)findViewById(R.id.btn_place_orde);
        etFirstname=(EditText)findViewById(R.id.etFirstname);
        etLastname=(EditText)findViewById(R.id.etLastname);
        etCompanyname=(EditText)findViewById(R.id.etCompanyname);
        etCountryname=(EditText)findViewById(R.id.etCountry);
        etAddress=(EditText)findViewById(R.id.etAddress);
        etHousenumber=(EditText)findViewById(R.id.etHouseNumber);
        etCity=(EditText)findViewById(R.id.etCity);
        etState=(EditText)findViewById(R.id.etZipcode);
        etZipcode=(EditText)findViewById(R.id.etZipcode);
        etPhone=(EditText)findViewById(R.id.etPhone);
        etEmail=(EditText)findViewById(R.id.etEmail);

        SharedPreferences sharedPreferences=getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        String customer_id=sharedPreferences.getString("id" , "");
        if(!customer_id.equals(""))
        {
            try {
                dialog=new ProgressDialog(activity);
                dialog.setTitle("Wait");
                dialog.setMessage("Loading...");
                dialog.show();
                sendOrderOfLoggedInCustomer();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CheckOutActivity.this , LoginActivity.class);
                startActivity(intent);
            }
        });
        btn_place_odrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(checkFields())
               {
                   try {
                       createCustomer();
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }
            }
        });

    }

    private void createCustomer() throws JSONException {
        JSONObject jsonObject=new JSONObject();



        JSONObject customer=new JSONObject();
        customer.put("email" , etEmail.getText().toString());
        customer.put("first_name" , etFirstname.getText().toString());
        customer.put("last_name" , etLastname.getText().toString());
        customer.put("username" , etFirstname.getText().toString()+" "+ etLastname.getText().toString());

        JSONObject billing_address=new JSONObject();

        billing_address.put("first_name" , etFirstname.getText().toString());
        billing_address.put("last_name" , etLastname.getText().toString());
        billing_address.put("address_1" , etAddress.getText().toString());
        billing_address.put("city" , etCity.getText().toString());
        billing_address.put("state" , etState.getText().toString());
        billing_address.put("postcode" , etZipcode.getText().toString());
        billing_address.put("country" , etCountryname.getText().toString());
        billing_address.put("email" , etEmail.getText().toString());
        billing_address.put("phone" , etPhone.getText().toString());
        customer.put("billing_address" , billing_address);

        JSONObject shipping_address=new JSONObject();
        shipping_address.put("first_name" , etFirstname.getText().toString());
        shipping_address.put("last_name" , etLastname.getText().toString());
        shipping_address.put("address_1" , etAddress.getText().toString());
        shipping_address.put("city" , etCity.getText().toString());
        shipping_address.put("state" , etState.getText().toString());
        shipping_address.put("postcode" , etZipcode.getText().toString());
        shipping_address.put("country" , etCountryname.getText().toString());
        shipping_address.put("city" , etEmail.getText().toString());
        customer.put("shipping_address" , shipping_address);

        jsonObject.put("customer" , customer);

         dialog=new ProgressDialog(activity);
        dialog.setTitle("Wait");
        dialog.setMessage("Loading...");
        dialog.show();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://preloveddesigners.com/wc-api/v2/customers", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject j) {

                        try {

                            JSONObject customerObj=j.getJSONObject("customer");
                            customerId=customerObj.getInt("id");
                            Log.e("customer_id"  , customerId+"");

                            sendOrder();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;
                        if (response != null && response.data != null) {
                            Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(activity, "You application is not connected to internet", Toast.LENGTH_SHORT).show();
                        }


                    }
                }){

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //add params <key,value>
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String credentials = "ck_12c9e0cb06fbe39f5e0ed7e5a145ab931d13a22f:cs_1aa04f32ee5b3acf5362b760703d4e187e159d71";
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                HashMap<String, String> headers = new HashMap<>();
                Log.e("auth" , "Basic " + base64EncodedCredentials);
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        Volley.newRequestQueue(this).add(jsonObjectRequest);



    }
    private void sendOrder() throws JSONException {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("set_paid",false);

        JSONObject billing=new JSONObject();
        billing.put("first_name" , etFirstname.getText().toString());
        billing.put("last_name" , etLastname.getText().toString());
        billing.put("address_1" , etAddress.getText().toString());
        billing.put("city" , etCity.getText().toString());
        billing.put("state" , etState.getText().toString());
        billing.put("postcode" , etZipcode.getText().toString());
        billing.put("country" , etCountryname.getText().toString());
        billing.put("email" , etEmail.getText().toString());
//        billing.put("password" , etPassword.getText().toString());
        billing.put("phone" , etPhone.getText().toString());
        jsonObject.put("billing" , billing);

        JSONObject shipping=new JSONObject();
        shipping.put("first_name" , etFirstname.getText().toString());
        shipping.put("last_name" , etLastname.getText().toString());
        shipping.put("address_1" , etAddress.getText().toString());
        shipping.put("city" , etCity.getText().toString());
        shipping.put("state" , etState.getText().toString());
        shipping.put("postcode" , etZipcode.getText().toString());
        shipping.put("country" , etCountryname.getText().toString());
        shipping.put("city" , etEmail.getText().toString());
        jsonObject.put("shipping" , shipping);

        jsonObject.put("customer_id" , customerId);

        JSONArray cartArray=new JSONArray();

        ImageUrlUtils imageUrlUtils = new ImageUrlUtils();
        ArrayList<ItemModel> cartlist =imageUrlUtils.getCartListImageUri();

        for (int i=0 ; i < cartlist.size() ; i++)
        {
            JSONObject item=new JSONObject();
            ItemModel model=cartlist.get(i);
            item.put("product_id" , model.getId());
            item.put("quantity", 1);

            cartArray.put(item);
        }
        jsonObject.put("line_items" , cartArray);
        Log.e("params" , jsonObject.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://preloveddesigners.com/wp-json/wc/v2/orders", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject j) {

                       Log.e("order_response" , j.toString());
                        dialog.dismiss();

                        Intent intent=new Intent(CheckOutActivity.this , ThankYouActivity.class);
                        startActivity(intent);
                        activity.finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        NetworkResponse response = error.networkResponse;
                        if (response != null && response.data != null) {
                            Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(activity, "You application is not connected to internet", Toast.LENGTH_SHORT).show();
                        }


                    }
                }){

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
@Override
protected Map<String, String> getParams() throws AuthFailureError {
    Map<String, String> params = new HashMap<String, String>();
    //add params <key,value>
    return params;
}
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String credentials = "ck_12c9e0cb06fbe39f5e0ed7e5a145ab931d13a22f:cs_1aa04f32ee5b3acf5362b760703d4e187e159d71";
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                HashMap<String, String> headers = new HashMap<>();
                Log.e("auth" , "Basic " + base64EncodedCredentials);
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void sendOrderOfLoggedInCustomer() throws JSONException {


        SharedPreferences sharedPreferences=getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("set_paid",false);

        JSONObject billing=new JSONObject();
        billing.put("first_name" , sharedPreferences.getString("first_name" , ""));
        billing.put("last_name" , sharedPreferences.getString("last_name" , ""));
        billing.put("address_1" , sharedPreferences.getString("address_1" , ""));
        billing.put("city" , sharedPreferences.getString("city" , ""));
        billing.put("state" , sharedPreferences.getString("state" , ""));
        billing.put("postcode" , sharedPreferences.getString("postcode" , ""));
        billing.put("country" ,sharedPreferences.getString("country" , "") );
        billing.put("email" , sharedPreferences.getString("email" , "") );
//        billing.put("password" , etPassword.getText().toString());
        billing.put("phone" , sharedPreferences.getString("phone" , "") );
        jsonObject.put("billing" , billing);

        JSONObject shipping=new JSONObject();
        shipping.put("first_name" , sharedPreferences.getString("first_name" , ""));
        shipping.put("last_name" , sharedPreferences.getString("last_name" , ""));
        shipping.put("address_1" , sharedPreferences.getString("address_1" , ""));
        shipping.put("city" , sharedPreferences.getString("city" , ""));
        shipping.put("state" , sharedPreferences.getString("state" , ""));
        shipping.put("postcode" , sharedPreferences.getString("postcode" , ""));
        shipping.put("country" , sharedPreferences.getString("country" , "") );
        shipping.put("city" , sharedPreferences.getString("email" , "") );
        jsonObject.put("shipping" , shipping);

        jsonObject.put("customer_id" , sharedPreferences.getString("id" , "") );

        JSONArray cartArray=new JSONArray();

        ImageUrlUtils imageUrlUtils = new ImageUrlUtils();
        ArrayList<ItemModel> cartlist =imageUrlUtils.getCartListImageUri();

        for (int i=0 ; i < cartlist.size() ; i++)
        {
            JSONObject item=new JSONObject();
            ItemModel model=cartlist.get(i);
            item.put("product_id" , model.getId());
            item.put("quantity", 1);

            cartArray.put(item);
        }
        jsonObject.put("line_items" , cartArray);
        Log.e("params" , jsonObject.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://preloveddesigners.com/wp-json/wc/v2/orders", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject j) {

                        Log.e("order_response" , j.toString());
                        dialog.dismiss();

                        Intent intent=new Intent(activity , ThankYouActivity.class);
                        startActivity(intent);
                        activity.finish();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        NetworkResponse response = error.networkResponse;
                        if (response != null && response.data != null) {
                            Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(activity, "You application is not connected to internet", Toast.LENGTH_SHORT).show();
                        }


                    }
                }){

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //add params <key,value>
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String credentials = "ck_12c9e0cb06fbe39f5e0ed7e5a145ab931d13a22f:cs_1aa04f32ee5b3acf5362b760703d4e187e159d71";
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                HashMap<String, String> headers = new HashMap<>();
                Log.e("auth" , "Basic " + base64EncodedCredentials);
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
//                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
    private boolean checkFields()
    {
        if(etFirstname.getText().toString().equals(""))
        {
          printMsg("Enter first name");
            return false;
        }
        if(etLastname.getText().toString().equals(""))
        {
            printMsg("Enter last name");
            return false;
        }
        if(etCompanyname.getText().toString().equals(""))
        {
            printMsg("Enter company name");
            return false;
        }
        if(etCountryname.getText().toString().equals(""))
        {
            printMsg("Enter country name");
            return false;
        }
        if(etAddress.getText().toString().equals(""))
        {
            printMsg("Enter address");
            return false;
        }
        if(etHousenumber.getText().toString().equals(""))
        {
            printMsg("Enter House number");
            return false;
        }
        if(etCity.getText().toString().equals(""))
        {
            printMsg("Enter City");
            return false;
        }
        if(etState.getText().toString().equals(""))
        {
            printMsg("Enter state");
            return false;
        }
        if(etZipcode.getText().toString().equals(""))
        {
            printMsg("Enter zip code");
            return false;
        }
        if(etPhone.getText().toString().equals(""))
        {
            printMsg("Enter Phone number");
            return false;
        }
        if(etEmail.getText().toString().equals(""))
        {
            printMsg("Enter Email address");
            return false;
        }

        return true;
    }
    void printMsg(String msg)
    {
        Toast.makeText(activity , msg , Toast.LENGTH_SHORT).show();
    }
}
