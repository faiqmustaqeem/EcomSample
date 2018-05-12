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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allandroidprojects.ecomsample.Models.ItemModel;
import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.product.ItemDetailsActivity;
import com.allandroidprojects.ecomsample.utility.ImageUrlUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.allandroidprojects.ecomsample.fragments.ImageListFragment.STRING_IMAGE_POSITION;
import static com.allandroidprojects.ecomsample.fragments.ImageListFragment.STRING_IMAGE_URI;

public class WishlistActivity extends AppCompatActivity {
    private static Context mContext;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
   static Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_recylerview_list);
        mContext = WishlistActivity.this;
        activity=this;
        sharedPreferences=getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        editor= getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();

        ImageUrlUtils imageUrlUtils = new ImageUrlUtils();
        ArrayList<ItemModel> wishlistImageUri;
        String id= sharedPreferences.getString("id" , "");
        if(!id.equals(""))
        {
            wishlistImageUri=getItemsFromWishlistSharedPreferences();
        }
        else {
            wishlistImageUri =imageUrlUtils.getWishlistImageUri();
        }


        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager recylerViewLayoutManager = new LinearLayoutManager(mContext);

        recyclerView.setLayoutManager(recylerViewLayoutManager);
        recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(recyclerView, wishlistImageUri));
    }
    private ArrayList<ItemModel> getItemsFromWishlistSharedPreferences(){
        Gson gson = new Gson();
        ArrayList<ItemModel> productFromShared = new ArrayList<>();
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        String jsonPreferences = sharedPref.getString("MyWishlist", "");

        Type type = new TypeToken<List<ItemModel>>() {}.getType();
        productFromShared = gson.fromJson(jsonPreferences, type);

        return productFromShared;
    }




    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<WishlistActivity.SimpleStringRecyclerViewAdapter.ViewHolder> {

        private ArrayList<ItemModel> mWishlistImageUri;
        private RecyclerView mRecyclerView;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final SimpleDraweeView mImageView;
            public final LinearLayout mLayoutItem;
            public final ImageView mImageViewWishlist;
            TextView item_name , item_price;
            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (SimpleDraweeView) view.findViewById(R.id.image_wishlist);
                mLayoutItem = (LinearLayout) view.findViewById(R.id.layout_item_desc);
                mImageViewWishlist = (ImageView) view.findViewById(R.id.ic_wishlist);
                item_name=(TextView)view.findViewById(R.id.item_name);
                item_price=(TextView)view.findViewById(R.id.item_price);
            }
        }

        public SimpleStringRecyclerViewAdapter(RecyclerView recyclerView, ArrayList<ItemModel> wishlistImageUri) {
            mWishlistImageUri = wishlistImageUri;
            mRecyclerView = recyclerView;
        }

        @Override
        public WishlistActivity.SimpleStringRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_wishlist_item, parent, false);
            return new WishlistActivity.SimpleStringRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            if (holder.mImageView.getController() != null) {
                holder.mImageView.getController().onDetach();
            }
            if (holder.mImageView.getTopLevelDrawable() != null) {
                holder.mImageView.getTopLevelDrawable().setCallback(null);
//                ((BitmapDrawable) holder.mImageView.getTopLevelDrawable()).getBitmap().recycle();
            }
        }

        @Override
        public void onBindViewHolder(final WishlistActivity.SimpleStringRecyclerViewAdapter.ViewHolder holder, final int position) {

          final ItemModel model=mWishlistImageUri.get(position);
            final SharedPreferences.Editor editor;
            final SharedPreferences sharedPreferences;

            sharedPreferences= activity.getSharedPreferences("SharedPreferences", MODE_PRIVATE);
            editor=activity.getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();

            final Uri uri = Uri.parse(model.getImageLink().get(0));
            holder.mImageView.setImageURI(uri);
            holder.item_name.setText(model.getName());
            holder.item_price.setText(model.getPrice());
            holder.mLayoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ItemDetailsActivity.class);
                    intent.putExtra(STRING_IMAGE_URI,model.getImageLink().get(0));
                    intent.putExtra(STRING_IMAGE_POSITION, position);
                    mContext.startActivity(intent);
                }
            });

            //Set click action for wishlist
            holder.mImageViewWishlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String id= sharedPreferences.getString("id" , "");
                    Log.e("id_id" , id);
                    if(!id.equals(""))
                    {
                        try {
                            deleteItemFromWishlistSharedPreferences(model);
                            mWishlistImageUri=getItemsFromWishlistSharedPreferences();
                            notifyDataSetChanged();
                            editor.remove("liked_"+model.getId());
                            editor.commit();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        ImageUrlUtils imageUrlUtils = new ImageUrlUtils();
                        imageUrlUtils.removeWishlistImageUri(position);
                    }


                }
            });
        }
        private void deleteItemFromWishlistSharedPreferences(ItemModel itemToDelete) throws JSONException {
            Gson gson = new Gson();
            ArrayList<ItemModel> productFromShared = new ArrayList<>();
            SharedPreferences sharedPref = activity.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
            String jsonPreferences = sharedPref.getString("MyWishlist", "");

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
            editor.putString("MyWishlist", itemDeletedList.toString());
            editor.commit();


        }
        private ArrayList<ItemModel> getItemsFromWishlistSharedPreferences(){
            Gson gson = new Gson();
            ArrayList<ItemModel> productFromShared = new ArrayList<>();
            SharedPreferences sharedPref =activity.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
            String jsonPreferences = sharedPref.getString("MyWishlist", "");

            Type type = new TypeToken<List<ItemModel>>() {}.getType();
            productFromShared = gson.fromJson(jsonPreferences, type);

            return productFromShared;
        }
        @Override
        public int getItemCount() {
            return mWishlistImageUri.size();
        }
    }
}
