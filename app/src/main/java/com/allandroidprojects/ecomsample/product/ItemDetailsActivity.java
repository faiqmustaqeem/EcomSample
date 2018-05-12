package com.allandroidprojects.ecomsample.product;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.allandroidprojects.ecomsample.Models.ItemModel;
import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.fragments.ImageListFragment;
import com.allandroidprojects.ecomsample.fragments.ViewPagerActivity;
import com.allandroidprojects.ecomsample.miscellaneous.EmptyActivity;
import com.allandroidprojects.ecomsample.notification.NotificationCountSetClass;
import com.allandroidprojects.ecomsample.options.CartListActivity;
import com.allandroidprojects.ecomsample.startup.GlobalClass;
import com.allandroidprojects.ecomsample.startup.MainActivity;
import com.allandroidprojects.ecomsample.utility.ImageUrlUtils;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ItemDetailsActivity extends AppCompatActivity {
    int imagePosition;
    String stringImageUri;
    TextView tvItemName , tvItemPrice;
    String itemName , itemPrice;
    ItemModel selected_item;
    private SliderLayout mDemoSlider;
//    TextView item_title;
    ImageView notification;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        sharedPreferences=getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        editor= getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();

        mDemoSlider = (SliderLayout) findViewById(R.id.slider);
//        SimpleDraweeView mImageView = (SimpleDraweeView)findViewById(R.id.image1);
        TextView textViewAddToCart = (TextView)findViewById(R.id.text_action_bottom1);
        TextView textViewBuyNow = (TextView)findViewById(R.id.text_action_bottom2);
        tvItemName=(TextView)findViewById(R.id.item_name);
        tvItemPrice=(TextView)findViewById(R.id.item_price);

//        item_title=(TextView)findViewById(R.id.item_title);

     //   notification=(ImageView)findViewById(R.id.notifications);

//        MenuItem item = (MenuItem) notification;
      //  NotificationCountSetClass.setAddToCart(ItemDetailsActivity.this, notification,MainActivity.notificationCountCart);


        selected_item= GlobalClass.selectedItem;



        stringImageUri=selected_item.getImageLink().get(0);
        itemName=selected_item.getName();
//        item_title.setText(itemName);
        getSupportActionBar().setTitle(itemName);
        itemPrice=selected_item.getPrice();
        Uri uri = Uri.parse(stringImageUri);
//        mImageView.setImageURI(uri);
        tvItemName.setText(itemName);
        tvItemPrice.setText(itemPrice+"$");



        HashMap<String, String> file_maps = new HashMap<String, String>();

        int index=0;
        for (String image_src : selected_item.getImageLink())
        {
            Log.e("image_src" , image_src);
            file_maps.put(itemName+" Pic"+index++,image_src );
        }


        for (String name : file_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);

            mDemoSlider.addSlider(textSliderView);
        }

            mDemoSlider.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ItemDetailsActivity.this, ViewPagerActivity.class);
                    intent.putExtra("position", imagePosition);
                    startActivity(intent);

                }

        });

        textViewAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String id=sharedPreferences.getString("id","");
                if(!id.equals("")) {
                   addItemInCartSharedPrefernce(selected_item);
                }
                else {
                    ImageUrlUtils imageUrlUtils = new ImageUrlUtils();
                    imageUrlUtils.addCartListImageUri(selected_item);
                }
                Toast.makeText(ItemDetailsActivity.this,"Item added to cart.",Toast.LENGTH_SHORT).show();
                MainActivity.notificationCountCart++;
                invalidateOptionsMenu();
                NotificationCountSetClass.setNotifyCount(MainActivity.notificationCountCart);


            }
        });

        textViewBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 String id=sharedPreferences.getString("id","");
                if(!id.equals("")) {
                    addItemInCartSharedPrefernce(selected_item);
                }
                else {
                    ImageUrlUtils imageUrlUtils = new ImageUrlUtils();
                    imageUrlUtils.addCartListImageUri(selected_item);
                }
                MainActivity.notificationCountCart++;
                invalidateOptionsMenu();
                NotificationCountSetClass.setNotifyCount(MainActivity.notificationCountCart);
                startActivity(new Intent(ItemDetailsActivity.this, CartListActivity.class));

            }
        });

    }
    private void addItemInCartSharedPrefernce(ItemModel itemToAdd){

        Gson gson = new Gson();


        String jsonSaved = sharedPreferences.getString("MyCart", "");
        String jsonNewproductToAdd = gson.toJson(itemToAdd);

        JSONArray jsonArrayProduct= new JSONArray();

        try {
            if(jsonSaved.length()!=0){
                jsonArrayProduct = new JSONArray(jsonSaved);
            }
            jsonArrayProduct.put(new JSONObject(jsonNewproductToAdd));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //SAVE NEW ARRAY
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("MyCart", jsonArrayProduct.toString());
        Log.e("cart_after_added" , jsonArrayProduct.toString());
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Get the notifications MenuItem and
        // its LayerDrawable (layer-list)
        MenuItem item = menu.findItem(R.id.action_cart);
        NotificationCountSetClass.setAddToCart(ItemDetailsActivity.this, item,MainActivity.notificationCountCart);
        // force the ActionBar to relayout its MenuItems.
        // onCreateOptionsMenu(Menu) will be called again.
        invalidateOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cart) {

           /* NotificationCountSetClass.setAddToCart(MainActivity.this, item, notificationCount);
            invalidateOptionsMenu();*/
            startActivity(new Intent(ItemDetailsActivity.this, CartListActivity.class));

           /* notificationCount=0;//clear notification count
            invalidateOptionsMenu();*/
            return true;
        }else {
            startActivity(new Intent(ItemDetailsActivity.this, EmptyActivity.class));

        }
        return super.onOptionsItemSelected(item);
    }
}
