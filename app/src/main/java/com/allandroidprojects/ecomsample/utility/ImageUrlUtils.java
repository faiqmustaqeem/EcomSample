package com.allandroidprojects.ecomsample.utility;

import com.allandroidprojects.ecomsample.Models.ItemModel;

import java.util.ArrayList;

/**
 * Created by 06peng on 2015/6/24.
 */
public class ImageUrlUtils {
    static ArrayList<ItemModel> wishlistImageUri = new ArrayList<>();
    static ArrayList<ItemModel> cartListImageUri = new ArrayList<>();


    // Methods for Wishlist
    public void addWishlistImageUri(ItemModel wishlistImageUri) {
        this.wishlistImageUri.add(0,wishlistImageUri);
    }

    public void removeWishlistImageUri(int position) {
        this.wishlistImageUri.remove(position);
    }

    public ArrayList<ItemModel> getWishlistImageUri(){ return this.wishlistImageUri; }

    // Methods for Cart
    public void addCartListImageUri(ItemModel wishlistImageUri) {
        this.cartListImageUri.add(0,wishlistImageUri);
    }
    public void removeAllItems()
    {
        cartListImageUri.clear();
    }

    public void removeCartListImageUri(int position) {
        this.cartListImageUri.remove(position);
    }

    public ArrayList<ItemModel> getCartListImageUri(){ return this.cartListImageUri; }


}
