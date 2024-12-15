package com.example.pawpalclinic.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.pawpalclinic.model.Produit;

import java.util.ArrayList;
import java.util.List;

public class CartService {

    private static final String TAG = "CartService";
    private final CartDatabaseHelper dbHelper;
    private final Object dbLock = new Object();
    private final int userId;

    public CartService(Context context, int userId) {
        dbHelper = new CartDatabaseHelper(context);
        this.userId = userId;
    }

    public void addToCart(Produit product) {
        synchronized (dbLock) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            try {
                ContentValues values = new ContentValues();
                values.put(CartDatabaseHelper.COLUMN_USER_ID, userId);
                values.put(CartDatabaseHelper.COLUMN_ID, product.getId());
                values.put(CartDatabaseHelper.COLUMN_NAME, product.getNomProduit());
                values.put(CartDatabaseHelper.COLUMN_DESCRIPTION, product.getDescription());
                values.put(CartDatabaseHelper.COLUMN_PRICE, product.getPrix());
                values.put(CartDatabaseHelper.COLUMN_IMAGE, product.getImage());
                values.put(CartDatabaseHelper.COLUMN_QUANTITY, product.getQuantity());

                Produit existingProduct = getProductById(product.getId());
                if (existingProduct != null) {
                    db.update(CartDatabaseHelper.TABLE_CART, values, CartDatabaseHelper.COLUMN_ID + " = ? AND " + CartDatabaseHelper.COLUMN_USER_ID + " = ?", new String[]{String.valueOf(product.getId()), String.valueOf(userId)});
                } else {
                    db.insert(CartDatabaseHelper.TABLE_CART, null, values);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error adding product to cart", e);
            }
        }
    }

    public List<Produit> getCart() {
        synchronized (dbLock) {
            List<Produit> cart = new ArrayList<>();
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query(CartDatabaseHelper.TABLE_CART, null, CartDatabaseHelper.COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)}, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    Produit product = new Produit(
                            cursor.getInt(cursor.getColumnIndexOrThrow(CartDatabaseHelper.COLUMN_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(CartDatabaseHelper.COLUMN_NAME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(CartDatabaseHelper.COLUMN_DESCRIPTION)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(CartDatabaseHelper.COLUMN_PRICE)),
                            0, // quantiteStock is not used in cart
                            null, // creeLe is not used in cart
                            cursor.getString(cursor.getColumnIndexOrThrow(CartDatabaseHelper.COLUMN_IMAGE)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(CartDatabaseHelper.COLUMN_QUANTITY))
                    );
                    cart.add(product);
                } while (cursor.moveToNext());
            }
            cursor.close();
            return cart;
        }
    }

    public void removeFromCart(int productId) {
        synchronized (dbLock) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            try {
                db.delete(CartDatabaseHelper.TABLE_CART, CartDatabaseHelper.COLUMN_ID + " = ? AND " + CartDatabaseHelper.COLUMN_USER_ID + " = ?", new String[]{String.valueOf(productId), String.valueOf(userId)});
                Log.d(TAG, "Product with ID " + productId + " removed from cart");
            } catch (Exception e) {
                Log.e(TAG, "Error removing product from cart", e);
            }
        }
    }

    public void clearCart() {
        synchronized (dbLock) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            try {
                db.delete(CartDatabaseHelper.TABLE_CART, CartDatabaseHelper.COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
            } catch (Exception e) {
                Log.e(TAG, "Error clearing cart", e);
            }
        }
    }



    public Produit getProductById(int productId) {
        synchronized (dbLock) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query(CartDatabaseHelper.TABLE_CART, null, CartDatabaseHelper.COLUMN_ID + " = ? AND " + CartDatabaseHelper.COLUMN_USER_ID + " = ?", new String[]{String.valueOf(productId), String.valueOf(userId)}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                Produit product = new Produit(
                        cursor.getInt(cursor.getColumnIndexOrThrow(CartDatabaseHelper.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(CartDatabaseHelper.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(CartDatabaseHelper.COLUMN_DESCRIPTION)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(CartDatabaseHelper.COLUMN_PRICE)),
                        0, // quantiteStock is not used in cart
                        null, // creeLe is not used in cart
                        cursor.getString(cursor.getColumnIndexOrThrow(CartDatabaseHelper.COLUMN_IMAGE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(CartDatabaseHelper.COLUMN_QUANTITY))
                );
                cursor.close();
                return product;
            } else {
                return null;
            }
        }
    }

    private void logCartContents() {
        List<Produit> cart = getCart();
        for (Produit product : cart) {
            Log.d(TAG, "Product in cart: " + product.toString());
        }
    }
}