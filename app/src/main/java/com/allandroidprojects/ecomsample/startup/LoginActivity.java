package com.allandroidprojects.ecomsample.startup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.allandroidprojects.ecomsample.Models.ItemModel;
import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.utility.ImageUrlUtils;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity {

    EditText etUSername;
    EditText etPasword;
    Button btn_login;
    Activity activity;
    String id="";
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        activity=this;
        dialog=new ProgressDialog(activity);
        dialog.setTitle("Loading");
        dialog.setMessage("Wait...");

        etUSername=(EditText)findViewById(R.id.etUsername);
        etPasword=(EditText)findViewById(R.id.etPassword);
        btn_login=(Button)findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkFields())
                {
                    login();
                }
            }
        });
    }
    public boolean checkFields()
    {
        if(etUSername.getText().toString().equals(""))
        {
            printMsg("Please enter username");
            return false;
        }
        if(etPasword.getText().toString().equals(""))
        {
            printMsg("Please enter password");
            return false;
        }
        return true;
    }

    void printMsg(String msg)
    {
        Toast.makeText(activity ,msg,Toast.LENGTH_SHORT).show();
    }

    public boolean isValidEmail(String emailStr) {
        final Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public void login()
    {


        if (checkFields()) {

            dialog.show();
            StringRequest request = new StringRequest(Request.Method.POST, "https://preloveddesigners.com/wp-json/jwt-auth/v1/token",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.e("login" , response);
                            try {

                                JSONObject jsonObject=new JSONObject(response);
                                String token= jsonObject.getString("token");


                              String parsed =  JWTUtils.decoded(token);

                                JSONObject parsedObj=new JSONObject(parsed);
                                JSONObject data=parsedObj.getJSONObject("data");
                                JSONObject user=data.getJSONObject("user");

                                 id=user.getString("id");

                                Log.e("id" , id);




                                getDatafromId();

//                                    SharedPreferences.Editor editor = getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();

//                                    editor.putString("email", email.getText().toString());
//                                    editor.putString("id" ,id );


//                                    editor.apply();
//
//                                    Intent i = new Intent(activity, MainActivity.class);
//                                    startActivity(i);
//                                    finish();


                            } catch (JSONException e)
                            {
                                Log.e("ErrorMessage", e.getMessage());
                                e.printStackTrace();
                                dialog.dismiss();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                    ,
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Log.e("Volley_error" , error.getMessage() );
                            parseVolleyError(error);
                            dialog.dismiss();
                        }
                    })
            {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();


                    params.put("username", etUSername.getText().toString());
                    params.put("password", etPasword.getText().toString());


                    Log.e("params_login" , params.toString());

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

            Volley.newRequestQueue(activity).add(request);

        }


    }
    private void getDatafromId()
    {
        StringRequest request = new StringRequest(Request.Method.GET, "https://preloveddesigners.com/wc-api/v3/customers/"+id+"?consumer_key=ck_12c9e0cb06fbe39f5e0ed7e5a145ab931d13a22f&consumer_secret=cs_1aa04f32ee5b3acf5362b760703d4e187e159d71",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("getdatafromid" , response);
                        try {

                            JSONObject jsonObject=new JSONObject(response);

                            JSONObject customer=jsonObject.getJSONObject("customer");

                            SharedPreferences.Editor editor = getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();

                            editor.putString("id" ,id );
                            editor.putString("email", customer.getString("email"));
                            editor.putString("first_name", customer.getString("first_name"));
                            editor.putString("last_name", customer.getString("last_name"));
                            editor.putString("username", customer.getString("username"));

                            JSONObject billing_address=customer.getJSONObject("billing_address");
                            editor.putString("company", billing_address.getString("company"));
                            editor.putString("address_1", billing_address.getString("address_1"));

                            editor.putString("city", billing_address.getString("city"));
                            editor.putString("state", billing_address.getString("state"));
                            editor.putString("postcode", billing_address.getString("postcode"));
                            editor.putString("country", billing_address.getString("country"));
                            editor.putString("phone", billing_address.getString("phone"));



                            editor.apply();

                            sendOrder();



                        } catch (JSONException e)
                        {
                            Log.e("ErrorMessage", e.getMessage());
                            e.printStackTrace();

                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
                ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log.e("Volley_error" , error.getMessage() );
                        parseVolleyError(error);

                    }
                });

        Volley.newRequestQueue(activity).add(request);
    }

    public void parseVolleyError(VolleyError error) {
        NetworkResponse response=error.networkResponse;

        if(response!=null && response.data!=null) {
            try {
                String responseBody = new String(error.networkResponse.data, "utf-8");
                JSONObject data = new JSONObject(responseBody);
                JSONObject result = data.getJSONObject("result");
                String message = result.getString("response");
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            } catch (JSONException | UnsupportedEncodingException e) {
                printMsg("internet problem...");
            }
        }
        else
        {
            printMsg("Your application is not connected to internet...");
        }
    }
    private void sendOrder() throws JSONException {


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
                        if (response != null && response.data != null)
                        {
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

}
class JWTUtils {

    public static String decoded(String JWTEncoded) throws Exception {
        String parsed="";
        try {
            String[] split = JWTEncoded.split("\\.");
            Log.d("JWT_DECODED", "Header: " + getJson(split[0]));
             parsed =  getJson(split[1]);
        } catch (UnsupportedEncodingException e) {
            //Error
        }
        return parsed;
    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException{
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }
}
