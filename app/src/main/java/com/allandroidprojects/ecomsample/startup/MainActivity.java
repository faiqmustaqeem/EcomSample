package com.allandroidprojects.ecomsample.startup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.allandroidprojects.ecomsample.Models.CategoryModel;
import com.allandroidprojects.ecomsample.Models.ItemModel;
import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.fragments.ImageListFragment;
import com.allandroidprojects.ecomsample.miscellaneous.EmptyActivity;
import com.allandroidprojects.ecomsample.notification.NotificationCountSetClass;
import com.allandroidprojects.ecomsample.options.CartListActivity;
import com.allandroidprojects.ecomsample.options.SearchResultActivity;
import com.allandroidprojects.ecomsample.options.WishlistActivity;
import com.allandroidprojects.ecomsample.utility.MySingleton;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static int notificationCountCart = 0;
  public   static ViewPager viewPager;
   public static TabLayout tabLayout;
    Activity activity;
    public static  List<CategoryModel> categories=new ArrayList<>();
    int MY_SOCKET_TIMEOUT_MS=20*1000;
    public static String selected_category="";
   public static boolean[] loaded=new boolean[100];
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity=this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        for(int i=0 ; i < loaded.length; i++)
        {
            loaded[i]=false;
        }
        sharedPreferences=getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        editor= getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

         viewPager = (ViewPager) findViewById(R.id.viewpager);
         tabLayout = (TabLayout) findViewById(R.id.tabs);

        if (viewPager != null) {
            setupViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
        }


      /*  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        String id= sharedPreferences.getString("id" , "");

        if(!id.equals(""))
        {
            try {
                MainActivity.notificationCountCart = getItemsFromCartSharedPreferences().size();
            }
            catch (Exception e)
            {
                MainActivity.notificationCountCart = 0;
            }
        }


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

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
        NotificationCountSetClass.setAddToCart(MainActivity.this, item,notificationCountCart);
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
            startActivity(new Intent(MainActivity.this, CartListActivity.class));

           /* notificationCount=0;//clear notification count
            invalidateOptionsMenu();*/
            return true;
        }else {
            startActivity(new Intent(MainActivity.this, EmptyActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(final ViewPager viewPager) {
        //http://preloveddesigners.com/wp-json/wp/v2/pages

        final Adapter adapter = new Adapter(getSupportFragmentManager());

        final ProgressDialog dialog=new ProgressDialog(activity);
        dialog.setTitle("Loading");
        dialog.setMessage("Wait...");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.GET, "https://preloveddesigners.com/wc-api/v3/products/categories?consumer_key=ck_12c9e0cb06fbe39f5e0ed7e5a145ab931d13a22f&consumer_secret=cs_1aa04f32ee5b3acf5362b760703d4e187e159d71",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("login" , response);
                        try {

                            JSONObject json=new JSONObject(response);
                            JSONArray jsonArray = json.getJSONArray("product_categories");
                            int tabs=0;
                            for (int i=0 ; i < jsonArray.length() ; i++)
                            {
                                JSONObject jsonObject=jsonArray.getJSONObject(i);
                                String slug=jsonObject.getString("slug");
                                int id=jsonObject.getInt("id");
                                CategoryModel model=new CategoryModel();
                                model.setId(id+"");
                                model.setName(slug);
                                int count =jsonObject.getInt("count");
                                Log.e("count" , slug+" "+id);

                                if(id!=33 && id!=50)
                                {
                                    if(count != 0)
                                    {
                                        categories.add(model);

                                       ImageListFragment fragment = new ImageListFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putInt("type", i+1);
                                        fragment.setArguments(bundle);
                                        adapter.addFragment(fragment, slug);tabs++;

                                    }

                                }

                            }
                            viewPager.setAdapter(adapter);
//                            viewPager.setOffscreenPageLimit(1);


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
                        // Log.e("Volley_error" , error.getMessage() );
                       // parseVolleyError(error);
                        dialog.dismiss();
                    }
                });
        request.setRetryPolicy(new DefaultRetryPolicy(500 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getInstance(activity).getRequestQueue().add(request);



//        fragment = new ImageListFragment();
//        bundle = new Bundle();
//        bundle.putInt("type", 2);
//        fragment.setArguments(bundle);
//        adapter.addFragment(fragment, getString(R.string.item_2));
//        fragment = new ImageListFragment();
//        bundle = new Bundle();
//        bundle.putInt("type", 3);
//        fragment.setArguments(bundle);
//        adapter.addFragment(fragment, getString(R.string.item_3));
//        fragment = new ImageListFragment();
//        bundle = new Bundle();
//        bundle.putInt("type", 4);
//        fragment.setArguments(bundle);
//        adapter.addFragment(fragment, getString(R.string.item_4));
//        fragment = new ImageListFragment();
//        bundle = new Bundle();
//        bundle.putInt("type", 5);
//        fragment.setArguments(bundle);
//        adapter.addFragment(fragment, getString(R.string.item_5));
//        fragment = new ImageListFragment();
//        bundle = new Bundle();
//        bundle.putInt("type", 6);
//        fragment.setArguments(bundle);
//        adapter.addFragment(fragment, getString(R.string.item_6));
//        viewPager.setAdapter(adapter);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_item1) {
//            selected_category=categories.get(0).getName();
//            viewPager.setCurrentItem(0);
//        } else if (id == R.id.nav_item2) {
//            selected_category=categories.get(1).getName();
//            viewPager.setCurrentItem(1);
//        } else if (id == R.id.nav_item3) {
//            selected_category=categories.get(2).getName();
//            viewPager.setCurrentItem(2);
//        } else if (id == R.id.nav_item4) {
//            selected_category=categories.get(3).getName();
//            viewPager.setCurrentItem(3);
//        } else if (id == R.id.nav_item5) {
//            selected_category=categories.get(4).getName();
//            viewPager.setCurrentItem(4);
//        }else if (id == R.id.nav_item6) {
//            selected_category=categories.get(5).getName();
//            viewPager.setCurrentItem(5);
//        }
//        else if (id == R.id.nav_item7) {
//            selected_category=categories.get(6).getName();
//            viewPager.setCurrentItem(6);
//        }
//        else if (id == R.id.nav_item8) {
//            selected_category=categories.get(7).getName();
//            viewPager.setCurrentItem(7);
//        }
//        else if (id == R.id.nav_item9) {
//            selected_category=categories.get(8).getName();
//            viewPager.setCurrentItem(8);
//        }
//        else if (id == R.id.nav_item10) {
//            selected_category=categories.get(9).getName();
//            viewPager.setCurrentItem(9);
//        }
//        else if (id == R.id.nav_item11) {
//            selected_category=categories.get(10).getName();
//            viewPager.setCurrentItem(10);
//        }
//        else if (id == R.id.nav_item12) {
//            selected_category=categories.get(11).getName();
//            viewPager.setCurrentItem(11);
//        }

         if (id == R.id.my_wishlist) {
            startActivity(new Intent(MainActivity.this, WishlistActivity.class));
        }else if (id == R.id.my_cart) {
            startActivity(new Intent(MainActivity.this, CartListActivity.class));
        }
        else if(id== R.id.my_account)
         {
             new MaterialStyledDialog.Builder(activity)
                     .setTitle("Logout")
                     .setDescription("are you sure you want to logout ?")
                     .setPositiveText("yes")
                     .onPositive(new MaterialDialog.SingleButtonCallback() {
                         @Override
                         public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                             SharedPreferences.Editor editor=getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();

                             editor.clear();
                             editor.commit();

                             Intent newIntent = new Intent(activity,SplashActivity.class);
                             newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                             newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                             startActivity(newIntent);
                         }
                     }).setNegativeText("No")
                     .onNegative(new MaterialDialog.SingleButtonCallback() {
                         @Override
                         public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                         }
                     }).show();


         }
         else if (id == R.id.become_vendor) {
             startActivity(new Intent(MainActivity.this, VendorActivity.class));
         }else {
            startActivity(new Intent(MainActivity.this, EmptyActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            Log.e("position" , position+"");
            Fragment fragment= mFragments.get(position);
            Bundle args = new Bundle();
            // Our object is just an integer :-P
            args.putString("cat_id", categories.get(position).getId());
            args.putInt("index"  , position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
