package com.allandroidprojects.ecomsample.options;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allandroidprojects.ecomsample.Models.ItemModel;
import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.product.ItemDetailsActivity;
import com.allandroidprojects.ecomsample.startup.CheckOutActivity;
import com.allandroidprojects.ecomsample.startup.MainActivity;
import com.allandroidprojects.ecomsample.utility.ImageUrlUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.allandroidprojects.ecomsample.fragments.ImageListFragment.STRING_IMAGE_POSITION;
import static com.allandroidprojects.ecomsample.fragments.ImageListFragment.STRING_IMAGE_URI;

public class CartListActivity extends AppCompatActivity {
    private static Context mContext;
    TextView text_action_bottom1;
    TextView text_action_bottom2;
   static Activity activity;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);
        mContext = CartListActivity.this;
        activity=this;
        sharedPreferences=getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        editor= getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();

        text_action_bottom1=(TextView)findViewById(R.id.text_action_bottom1);
        text_action_bottom2=(TextView)findViewById(R.id.text_action_bottom2);


        text_action_bottom2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CartListActivity.this , CheckOutActivity.class);
                startActivity(intent);
            }
        });

        ImageUrlUtils imageUrlUtils = new ImageUrlUtils();

        ArrayList<ItemModel> cartlistImageUri=new ArrayList<>();
        String id= sharedPreferences.getString("id" , "");

        if(!id.equals(""))
        {
   try  {
       cartlistImageUri =getItemsFromCartSharedPreferences();
       MainActivity.notificationCountCart=cartlistImageUri.size();
   }
   catch (Exception e)
   {
       MainActivity.notificationCountCart=0;
       cartlistImageUri=new ArrayList<>();
   }
        }
        else {
            cartlistImageUri =imageUrlUtils.getCartListImageUri();
        }


        //Show cart layout based on items
        setCartLayout();

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager recylerViewLayoutManager = new LinearLayoutManager(mContext);

        recyclerView.setLayoutManager(recylerViewLayoutManager);
        int totalPrice=0;
        for (ItemModel model : cartlistImageUri) {
            totalPrice+= Integer.parseInt(model.getPrice());
        }
        text_action_bottom1.setText(totalPrice+"$");
        recyclerView.setAdapter(new CartListActivity.SimpleStringRecyclerViewAdapter(recyclerView, cartlistImageUri));

    }

    private ArrayList<ItemModel> getItemsFromCartSharedPreferences(){
        Gson gson = new Gson();
        ArrayList<ItemModel> productFromShared = new ArrayList<>();
        SharedPreferences sharedPref =activity.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        String jsonPreferences = sharedPref.getString("MyCart", "");
        Log.e("cart" , jsonPreferences);
        Type type = new TypeToken<List<ItemModel>>() {}.getType();
        productFromShared = gson.fromJson(jsonPreferences, type);

        return productFromShared;
    }



    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<CartListActivity.SimpleStringRecyclerViewAdapter.ViewHolder> {

        private ArrayList<ItemModel> mCartlistImageUri;
        private RecyclerView mRecyclerView;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final SimpleDraweeView mImageView;
            public final LinearLayout mLayoutItem, mLayoutRemove , mLayoutEdit;
            TextView itemName , itemPrice;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (SimpleDraweeView) view.findViewById(R.id.image_cartlist);
                mLayoutItem = (LinearLayout) view.findViewById(R.id.layout_item_desc);
                mLayoutRemove = (LinearLayout) view.findViewById(R.id.layout_action1);
                mLayoutEdit = (LinearLayout) view.findViewById(R.id.layout_action2);
                itemName=(TextView)view.findViewById(R.id.item_name);
                itemPrice=(TextView)view.findViewById(R.id.item_price);
            }
        }

        public SimpleStringRecyclerViewAdapter(RecyclerView recyclerView, ArrayList<ItemModel> wishlistImageUri) {
            mCartlistImageUri = wishlistImageUri;
            mRecyclerView = recyclerView;
        }

        @Override
        public CartListActivity.SimpleStringRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cartlist_item, parent, false);
            return new CartListActivity.SimpleStringRecyclerViewAdapter.ViewHolder(view);
        }


        @Override
        public void onViewRecycled(CartListActivity.SimpleStringRecyclerViewAdapter.ViewHolder holder) {
            if (holder.mImageView.getController() != null) {
                holder.mImageView.getController().onDetach();
            }
            if (holder.mImageView.getTopLevelDrawable() != null) {
                holder.mImageView.getTopLevelDrawable().setCallback(null);
//                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
            }
        }

        @Override
        public void onBindViewHolder(final CartListActivity.SimpleStringRecyclerViewAdapter.ViewHolder holder, final int position) {
            final SharedPreferences.Editor editor;
            final SharedPreferences sharedPreferences;

            sharedPreferences= activity.getSharedPreferences("SharedPreferences", MODE_PRIVATE);
            editor=activity.getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();
            final ItemModel model=mCartlistImageUri.get(position);
            final Uri uri = Uri.parse(model.getImageLink().get(0));
            holder.mImageView.setImageURI(uri);
            holder.itemName.setText(model.getName());
            holder.itemPrice.setText(model.getPrice()+"$");
            holder.mLayoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ItemDetailsActivity.class);
                    intent.putExtra(STRING_IMAGE_URI,mCartlistImageUri.get(position).getImageLink().get(0));
                    intent.putExtra(STRING_IMAGE_POSITION, position);
                    mContext.startActivity(intent);
                }
            });

           //Set click action
            holder.mLayoutRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String id= sharedPreferences.getString("id" , "");
                    Log.e("id_id" , id);
                    if(!id.equals(""))
                    {
                        try {
                            deleteItemFromCartSharedPreferences(model);
                            mCartlistImageUri=getItemsFromCartSharedPreferences();
                            notifyDataSetChanged();
                            MainActivity.notificationCountCart--;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        ImageUrlUtils imageUrlUtils = new ImageUrlUtils();
                        imageUrlUtils.removeCartListImageUri(position);
                        notifyDataSetChanged();
                        //Decrease notification count
                        MainActivity.notificationCountCart--;
                    }


                }
            });

            //Set click action
            holder.mLayoutEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        }
        private void deleteItemFromCartSharedPreferences(ItemModel itemToDelete) throws JSONException {
            Gson gson = new Gson();
            ArrayList<ItemModel> productFromShared = new ArrayList<>();
            SharedPreferences sharedPref = activity.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
            String jsonPreferences = sharedPref.getString("MyCart", "");

            Log.e("jsonPreferences" , jsonPreferences);
            Type type = new TypeToken<List<ItemModel>>() {}.getType();
            productFromShared = gson.fromJson(jsonPreferences, type);



            JSONArray itemDeletedList=new JSONArray();

            for (ItemModel model: productFromShared) {

                if(!model.getId().equals(itemToDelete.getId()))
                {
                    itemDeletedList.put(new JSONObject(gson.toJson(model)));
                }
            }
            SharedPreferences.Editor editor=sharedPref.edit();
            editor.putString("MyCart", itemDeletedList.toString());
            editor.commit();


        }
        private ArrayList<ItemModel> getItemsFromCartSharedPreferences(){
            Gson gson = new Gson();
            ArrayList<ItemModel> productFromShared = new ArrayList<>();
            SharedPreferences sharedPref =activity.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
            String jsonPreferences = sharedPref.getString("MyCart", "");

            Type type = new TypeToken<List<ItemModel>>() {}.getType();
            productFromShared = gson.fromJson(jsonPreferences, type);

            return productFromShared;
        }


        @Override
        public int getItemCount() {
            return mCartlistImageUri.size();
        }
    }

    protected void setCartLayout(){
        LinearLayout layoutCartItems = (LinearLayout) findViewById(R.id.layout_items);
        LinearLayout layoutCartPayments = (LinearLayout) findViewById(R.id.layout_payment);
        LinearLayout layoutCartNoItems = (LinearLayout) findViewById(R.id.layout_cart_empty);

        if(MainActivity.notificationCountCart >0){
            layoutCartNoItems.setVisibility(View.GONE);
            layoutCartItems.setVisibility(View.VISIBLE);
            layoutCartPayments.setVisibility(View.VISIBLE);
        }else {
            layoutCartNoItems.setVisibility(View.VISIBLE);
            layoutCartItems.setVisibility(View.GONE);
            layoutCartPayments.setVisibility(View.GONE);

            Button bStartShopping = (Button) findViewById(R.id.bAddNew);
            bStartShopping.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }
}
