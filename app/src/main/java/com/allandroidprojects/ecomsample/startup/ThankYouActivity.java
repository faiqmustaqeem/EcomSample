package com.allandroidprojects.ecomsample.startup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.allandroidprojects.ecomsample.Models.ItemModel;
import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.utility.ImageUrlUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ThankYouActivity extends AppCompatActivity {

    Button btn_ok;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);
        btn_ok=(Button)findViewById(R.id.btn_ok);
        sharedPreferences=getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        editor= getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();
        String id= sharedPreferences.getString("id" , "");

        if(!id.equals(""))
        {
            try {
                deleteAllItemsFromCartSharedPreferences();
                MainActivity.notificationCountCart=0;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            ImageUrlUtils imageUrlUtils=new ImageUrlUtils();
            imageUrlUtils.removeAllItems();
            MainActivity.notificationCountCart=0;

        }



        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(ThankYouActivity.this,SplashActivity.class);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(newIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent newIntent = new Intent(ThankYouActivity.this,SplashActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(newIntent);

    }
    private void deleteAllItemsFromCartSharedPreferences() throws JSONException {

        ArrayList<ItemModel> productFromShared = new ArrayList<>();
        SharedPreferences sharedPref = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        String jsonPreferences = sharedPref.getString("MyCart", "");

        SharedPreferences.Editor editor=sharedPref.edit();
        editor.putString("MyCart", "");
        editor.commit();


    }
}
