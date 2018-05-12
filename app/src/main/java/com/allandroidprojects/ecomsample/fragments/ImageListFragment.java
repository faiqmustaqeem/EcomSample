/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.allandroidprojects.ecomsample.fragments;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allandroidprojects.ecomsample.Models.CategoryModel;
import com.allandroidprojects.ecomsample.Models.ItemModel;
import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.product.ItemDetailsActivity;
import com.allandroidprojects.ecomsample.startup.GlobalClass;
import com.allandroidprojects.ecomsample.startup.MainActivity;
import com.allandroidprojects.ecomsample.utility.ImageUrlUtils;
import com.allandroidprojects.ecomsample.utility.MySingleton;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class ImageListFragment extends Fragment {

    public static final String STRING_IMAGE_URI = "ImageUri";
    public static final String STRING_IMAGE_POSITION = "ImagePosition";
    private static MainActivity mActivity;
    int MY_SOCKET_TIMEOUT_MS=20*1000;
    List<ItemModel> items=new ArrayList<>();

    RecyclerView rv;
    RecyclerView recyclerView;
    SimpleStringRecyclerViewAdapter adapter;
    String cat_id="";
    boolean dataLoaded=false;
    int pos=0;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(rv==null)
        {
            rv = (RecyclerView) inflater.inflate(R.layout.layout_recylerview_list, container, false);
            this.recyclerView=rv;
            Log.e("oncreate" , "oncreate");
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            adapter= new SimpleStringRecyclerViewAdapter(recyclerView, items);
            recyclerView.setAdapter(adapter);
            Bundle args = getArguments();
            cat_id=args.getString("cat_id");
            pos=args.getInt("index");
            Log.e("position" , pos+"");
            setupRecyclerView();
            sharedPreferences=mActivity.getSharedPreferences("SharedPreferences", MODE_PRIVATE);
            editor= mActivity.getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();

        }
          return rv;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void setupRecyclerView() {


        if(!MainActivity.loaded[pos]) {
            loadData(cat_id);
        }



/*

        MainActivity.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //do stuff here
               String cat_id =MainActivity.categories.get(tab.getPosition()).getId();
                Log.e("tab" , cat_id);


                loadData( cat_id);
                items.clear();
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        */







    }
    public void loadData(String cat_id)
    {
        final ProgressDialog dialog=new ProgressDialog(mActivity);
        dialog.setTitle("Loading");
        dialog.setMessage("Wait...");
        dialog.show();
        String url="https://preloveddesigners.com/wp-json/wc/v1/products?category="+cat_id+"&filter[posts_per_page]=-1&consumer_key=ck_12c9e0cb06fbe39f5e0ed7e5a145ab931d13a22f&consumer_secret=cs_1aa04f32ee5b3acf5362b760703d4e187e159d71";
        Log.e("sub_category" , url);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("sub_category" , response);
                        try {
                            dataLoaded=true;
                            items.clear();
                            MainActivity.loaded[pos]=true;


                            JSONArray jsonArray = new JSONArray(response);

                            for (int i=0 ; i < jsonArray.length() ; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                ItemModel model = new ItemModel();

                                model.setId(jsonObject.getString("id"));
                                model.setName(jsonObject.getString("name"));
                                model.setPrice(jsonObject.getString("price"));

                                JSONArray imagesArray = jsonObject.getJSONArray("images");
                                List<String> images = new ArrayList<>();
                                for (int j=0 ; j < imagesArray.length() ; j++)
                                {
                                    JSONObject img=imagesArray.getJSONObject(j);
                                    images.add(img.getString("src"));
                                }
                                model.setImageLink(images);
                                items.add(model);

                            }

                            adapter.notifyDataSetChanged();


                            dialog.dismiss();



                        } catch (JSONException e)
                        {
                            Log.e("ErrorMessage", e.getMessage());
                            e.printStackTrace();
                            dialog.dismiss();
                        }

                    }
                }
                ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                         Log.e("Volley_error" , error.getMessage() );
                         parseVolleyError(error);
                        dialog.dismiss();
                    }
                });
        request.setRetryPolicy(new DefaultRetryPolicy(500 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(mActivity).getRequestQueue().add(request);

    }
    public void parseVolleyError(VolleyError error) {
        NetworkResponse response=error.networkResponse;

        if(response!=null && response.data!=null) {
            try {
                String responseBody = new String(error.networkResponse.data, "utf-8");
                JSONObject data = new JSONObject(responseBody);

                Toast.makeText(mActivity, "Error", Toast.LENGTH_LONG).show();
            } catch (JSONException | UnsupportedEncodingException e) {
                printMsg("internet problem...");
            }
        }
        else
        {
            printMsg("Your application is not connected to internet...");
        }
    }
    void printMsg(String msg)
    {
        Toast.makeText(mActivity ,msg,Toast.LENGTH_SHORT).show();
    }



    public  class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {


        private List<ItemModel> mValues=new ArrayList<>();
        private RecyclerView mRecyclerView;

        public  class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final SimpleDraweeView mImageView;
            public final LinearLayout mLayoutItem;
            public final ImageView mImageViewWishlist;
            TextView name , desc , price ;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (SimpleDraweeView) view.findViewById(R.id.image1);
                mLayoutItem = (LinearLayout) view.findViewById(R.id.layout_item);
                mImageViewWishlist = (ImageView) view.findViewById(R.id.ic_wishlist);
                name=(TextView) view.findViewById(R.id.tv_item_name);
                desc=(TextView)view.findViewById(R.id.tv_description);
                price=(TextView)view.findViewById(R.id.tv_price);
            }
        }

        public SimpleStringRecyclerViewAdapter(RecyclerView recyclerView, List<ItemModel> items) {
            mValues = items;
            this.mRecyclerView = recyclerView;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new ViewHolder(view);
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
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
           /* FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) holder.mImageView.getLayoutParams();
            if (mRecyclerView.getLayoutManager() instanceof GridLayoutManager) {
                layoutParams.height = 200;
            } else if (mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                layoutParams.height = 600;
            } else {
                layoutParams.height = 800;
            }*/


            final ItemModel model=mValues.get(position);
            final Uri uri = Uri.parse(model.getImageLink().get(0));
            holder.mImageView.setImageURI(uri);
            holder.name.setText(model.getName());
            holder.desc.setText(model.getDescription());
            holder.price.setText(model.getPrice()+"$");
            holder.mLayoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, ItemDetailsActivity.class);
                    intent.putExtra(STRING_IMAGE_URI, model.getImageLink().get(0));
                    intent.putExtra(STRING_IMAGE_POSITION, position);
                    intent.putExtra("item_name" , model.getName());
                    intent.putExtra("item_price" , model.getPrice());
                    GlobalClass.selectedItem=items.get(position);
                    mActivity.startActivity(intent);

                }
            });

            // check if item is already liked by user ?
            boolean isLiked=sharedPreferences.getBoolean("liked_" + model.getId() , false);

            if(isLiked)
            {
//                ImageUrlUtils imageUrlUtils = new ImageUrlUtils();
//                imageUrlUtils.addWishlistImageUri(model);
                holder.mImageViewWishlist.setImageResource(R.drawable.ic_favorite_black_18dp);
            }

            //Set click action for wishlist
            holder.mImageViewWishlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    addItemInWishlistSharedPrefernce(model);
                    ImageUrlUtils imageUrlUtils = new ImageUrlUtils();
                    imageUrlUtils.addWishlistImageUri(model);
                    holder.mImageViewWishlist.setImageResource(R.drawable.ic_favorite_black_18dp);
                    notifyDataSetChanged();
                    Toast.makeText(mActivity,"Item added to wishlist.",Toast.LENGTH_SHORT).show();

                    String id=sharedPreferences.getString("id","");
                    if(!id.equals("")) {
                        editor.putBoolean("liked_" + model.getId(), true);
                        editor.apply();
                    }



                }
            });

        }

        private void addItemInWishlistSharedPrefernce(ItemModel itemToAdd){

            Gson gson = new Gson();


            String jsonSaved = sharedPreferences.getString("MyWishlist", "");
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
            editor.putString("MyWishlist", jsonArrayProduct.toString());
            editor.commit();
        }


        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }
}
