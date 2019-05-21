package ru.buylist.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.buylist.data.BuyListBaseHelper;
import ru.buylist.data.BuyListCursorWrapper;

import static ru.buylist.data.BuyListDbSchema.*;

public class ProductLab {
    private static ProductLab productLab;
    private Context context;
    private SQLiteDatabase database;

    private ProductLab(Context c) {
        context = c.getApplicationContext();
        database = new BuyListBaseHelper(context).getWritableDatabase();
    }

    public static ProductLab get(Context context) {
        if (productLab == null) {
            productLab = new ProductLab(context);
        }
        return productLab;
    }

    private static ContentValues getBuyListContentValues(BuyList buyList) {
        ContentValues values = new ContentValues();
        values.put(BuyTable.Cols.UUID, buyList.getId().toString());
        values.put(BuyTable.Cols.TITLE, buyList.getTitle());
        return values;
    }

    private static ContentValues getProductsContentValues(Product product) {
        ContentValues values = new ContentValues();
        values.put(ProductTable.Cols.BUYLIST_ID, product.getBuylistId());
        values.put(ProductTable.Cols.PRODUCT_NAME, product.getName());
        values.put(ProductTable.Cols.IS_PURCHASED, product.isPurchased() ? 1 : 0);
        values.put(ProductTable.Cols.CATEGORY, product.getCategory());
        return values;
    }

    public void addBuyList(BuyList buyList) {
        ContentValues values = getBuyListContentValues(buyList);
        database.insert(BuyTable.NAME, null, values);
    }

    public void addProducts(Product product) {
        ContentValues values = getProductsContentValues(product);
        database.insert(ProductTable.NAME, null, values);
    }

    public void deleteFromDb(UUID id, String tableName) {
        database.delete(tableName, BuyTable.Cols.UUID + "=?", new String[]{id.toString()});
    }

    public List<BuyList> getBuyLists() {
        List<BuyList> lists = new ArrayList<>();
        BuyListCursorWrapper cursor = queryList(null, null, BuyTable.NAME);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                lists.add(cursor.getBuyList());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return lists;
    }

    public BuyList getBuyList(UUID id) {
        BuyListCursorWrapper cursor = queryList(
                BuyTable.Cols.UUID + " = ?", new String[]{id.toString()}, BuyTable.NAME);

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getBuyList();

        } finally {
            cursor.close();
        }
    }

    public List<Product> getProductList(String id) {
        List<Product> products = new ArrayList<>();
        BuyListCursorWrapper cursor = queryList(
                ProductTable.Cols.BUYLIST_ID + " = ?", new String[]{id}, ProductTable.NAME);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                products.add(cursor.getProductList());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return products;
    }

    public void updateBuyList(BuyList buyList) {
        String uuidString = buyList.getId().toString();
        ContentValues values = getBuyListContentValues(buyList);

        database.update(BuyTable.NAME, values, BuyTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    public void updateBuyTable(List<BuyList> lists) {
        database.delete(BuyTable.NAME, null, null);

        for (BuyList buyList : lists) {
            addBuyList(buyList);
        }
    }

    private BuyListCursorWrapper queryList(String whereClause, String[] whereArgs, String tableName) {
        Cursor cursor = database.query(
                tableName,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new BuyListCursorWrapper(cursor);
    }
}
