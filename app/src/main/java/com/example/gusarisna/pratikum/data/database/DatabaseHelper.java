package com.example.gusarisna.pratikum.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.gusarisna.pratikum.data.model.Follower;
import com.example.gusarisna.pratikum.data.model.Postingan;
import com.example.gusarisna.pratikum.data.model.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {


    // Logcat tag
    private static final String LOG = "DEVELOP";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "opiniKu";

    // Table Names
    private static final String TABLE_USER = "user";
    private static final String TABLE_POSTINGAN = "postingan";
    private static final String TABLE_FOLLOWER = "follower";

    //Column Name (Global)
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_UPDATED_AT = "updated_at";

    //Column Name Table User
    private static final String KEY_NAMA = "nama";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_FOTO_PROFIL = "foto_profil";

    //Column Name Table Postingan
    private static final String KEY_KONTEN = "konten";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_LIKE_COUNT = "like_count";
    private static final String KEY_KOMENTAR_COUNT = "komentar_count";
    private static final String KEY_LIKED = "liked";

    //Column Name Table Follower
    private static final String KEY_FOLLOWER_ID = "follower_id";

    //Statement
    //Create table
    private static final String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER +
            "(" + KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_NAMA + " TEXT," +
            KEY_EMAIL + " TEXT," +
            KEY_FOTO_PROFIL + " TEXT," +
            KEY_CREATED_AT + " DATETIME," +
            KEY_UPDATED_AT + " DATETIME)";

    private static final String CREATE_TABLE_POSTINGAN = "CREATE TABLE " + TABLE_POSTINGAN +
            "(" + KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_KONTEN + " TEXT," +
            KEY_USER_ID + " INT," +
            KEY_LIKE_COUNT + " INT," +
            KEY_KOMENTAR_COUNT + " INT," +
            KEY_LIKED + " INT," +
            KEY_CREATED_AT + " DATETIME," +
            KEY_UPDATED_AT + " DATETIME)";

    private static final String CREATE_TABLE_FOLLOWER = "CREATE TABLE " + TABLE_FOLLOWER +
            "(" + KEY_ID + " INTEGER PRIMARY KEY," +
            KEY_USER_ID + " INT," +
            KEY_FOLLOWER_ID + " INT," +
            KEY_CREATED_AT + " DATETIME," +
            KEY_UPDATED_AT + " DATETIME)";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_POSTINGAN);
        db.execSQL(CREATE_TABLE_FOLLOWER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTINGAN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOLLOWER);
        onCreate(db);
    }

    //User
    public void addMultiUser(List<User> listUser){
        SQLiteDatabase db  = getWritableDatabase();

        for(User user : listUser){
            ContentValues values = new ContentValues();
            values.put(KEY_ID, user.getId());
            values.put(KEY_NAMA, user.getNama());
            values.put(KEY_EMAIL, user.getEmail());
            values.put(KEY_FOTO_PROFIL, user.getFotoProfil());
            values.put(KEY_CREATED_AT, user.getCreatedAt());
            values.put(KEY_UPDATED_AT, user.getUpdatedAt());
            db.insert(TABLE_USER, null, values);
        }
        db.close();
    }

    public void deleteAllUser(){
        Log.d(LOG, "Clear All User");
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_USER);
        db.close();
    }

    public List<User> getAllUser(){
        List<User> listUser = new ArrayList<User>();
        //query
        String query = "SELECT  * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do {
                User user = new User();
                user.setId(Integer.parseInt(cursor.getString(0)));
                user.setNama(cursor.getString(1));
                user.setEmail(cursor.getString(2));
                user.setFotoProfil(cursor.getString(3));
                user.setCreatedAt(cursor.getString(4));
                user.setUpdatedAt(cursor.getString(5));
                listUser.add(user);
            } while (cursor.moveToNext());
        }
        Log.d(LOG, "Select All User ("+listUser.size()+")");
        return listUser;
    }

    public User getUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT  * FROM " + TABLE_USER + " WHERE "
                + KEY_ID + " = " + userId;

        Cursor cursor = db.rawQuery(query, null);

        User user = new User();
        if (cursor != null) {
            cursor.moveToFirst();
            user.setId(cursor.getInt(0));
            user.setNama(cursor.getString(1));
            user.setEmail(cursor.getString(2));
            user.setFotoProfil(cursor.getString(3));
            user.setCreatedAt(cursor.getString(4));
            user.setUpdatedAt(cursor.getString(5));
        } else {
            user.setId(userId);
            user.setNama("Tidak Diketahui");
        }

        return user;
    }

    //Postingan
    public void addMultiPostingaan(List<Postingan> listPostingan){
        SQLiteDatabase db  = getWritableDatabase();

        for(Postingan postingan : listPostingan){
            ContentValues values = new ContentValues();
            values.put(KEY_ID, postingan.getId());
            values.put(KEY_KONTEN, postingan.getKonten());
            values.put(KEY_USER_ID, postingan.getUserId());
            values.put(KEY_LIKE_COUNT, postingan.getLikeCount());
            values.put(KEY_KOMENTAR_COUNT, postingan.getKomentarCount());
            int liked = (postingan.isLiked())? 1 : 0;
            values.put(KEY_LIKED, liked);
            values.put(KEY_CREATED_AT, postingan.getCreatedAt());
            values.put(KEY_UPDATED_AT, postingan.getUpdatedAt());
            db.insert(TABLE_POSTINGAN, null, values);
        }
        Log.d(LOG, "Add Postingan ("+listPostingan.size()+")");
        db.close();
    }

    public void deleteAllPostingan(){
        Log.d(LOG, "Clear All Postingan");
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_POSTINGAN);
        db.close();
    }

    public void deletePostingan(int postinganId){
        Log.d(LOG, "Delete Postingan");
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_POSTINGAN+" WHERE "+KEY_ID + " = "+postinganId);
        db.close();
    }

    public void updateLike(Postingan postingan){
        Log.d(LOG, "Update Postingan ID " + postingan.getId() + " : Like Count => " + postingan.getLikeCount());
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        int likedInt = (postingan.isLiked())?  1 : 0;
        values.put(KEY_LIKED, likedInt);
        values.put(KEY_LIKE_COUNT, postingan.getLikeCount());

        db.update(TABLE_POSTINGAN, values, KEY_ID + " = ?",
                new String[] { String.valueOf(postingan.getId()) });
        db.close();
    }

    public List<Postingan> getAllPostingan(){
        List<Postingan> listPostingan = new ArrayList<Postingan>();
        //query
        String query = "SELECT  * FROM " + TABLE_POSTINGAN + " ORDER BY " + KEY_CREATED_AT + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do {
                Postingan postingan = new Postingan();
                postingan.setId(Integer.parseInt(cursor.getString(0)));
                postingan.setKonten(cursor.getString(1));
                postingan.setUserId(cursor.getInt(2));
                User user = getUser(postingan.getUserId());
                postingan.setUser(user);
                postingan.setLikeCount(cursor.getInt(3));
                postingan.setKomentarCount(cursor.getInt(4));
                boolean liked = (cursor.getInt(5) == 1)? true : false;
                postingan.setLiked(liked);
                postingan.setCreatedAt(cursor.getString(6));
                postingan.setUpdatedAt(cursor.getString(7));
                listPostingan.add(postingan);
            } while (cursor.moveToNext());
        }
        Log.d(LOG, "Select All Postingan ("+listPostingan.size()+")");
        return listPostingan;
    }

    public List<Postingan> getUserPostingan(int userId){
        List<Postingan> listPostingan = new ArrayList<Postingan>();
        //query
        String query = "SELECT  * FROM " + TABLE_POSTINGAN + " WHERE " + KEY_USER_ID + " = "+ userId +" ORDER BY " + KEY_CREATED_AT + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        User user = getUser(userId);
        if(cursor.moveToFirst()){
            do {
                Postingan postingan = new Postingan();
                postingan.setId(Integer.parseInt(cursor.getString(0)));
                postingan.setKonten(cursor.getString(1));
                postingan.setUserId(cursor.getInt(2));
                postingan.setUser(user);
                postingan.setLikeCount(cursor.getInt(3));
                postingan.setKomentarCount(cursor.getInt(4));
                boolean liked = (cursor.getInt(5) == 1)? true : false;
                postingan.setLiked(liked);
                postingan.setCreatedAt(cursor.getString(6));
                postingan.setUpdatedAt(cursor.getString(7));
                listPostingan.add(postingan);
            } while (cursor.moveToNext());
        }
        return listPostingan;
    }

    public Postingan getPostingan(int postinganId){
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT  * FROM " + TABLE_POSTINGAN + " WHERE "
                + KEY_ID + " = " + postinganId;

        Cursor cursor = db.rawQuery(query, null);

        Postingan postingan = new Postingan();
        if (cursor != null) {
            cursor.moveToFirst();
            postingan.setId(cursor.getInt(0));
            postingan.setKonten(cursor.getString(1));
            postingan.setUserId(cursor.getInt(2));
            User user = getUser(postingan.getUserId());
            postingan.setUser(user);
            postingan.setLikeCount(cursor.getInt(3));
            postingan.setKomentarCount(cursor.getInt(4));
            boolean liked = (cursor.getInt(5) == 1)? true : false;
            postingan.setLiked(liked);
            postingan.setCreatedAt(cursor.getString(6));
            postingan.setUpdatedAt(cursor.getString(7));
        }

        return postingan;
    }

    public void updateKontenPostingan(Postingan postingan){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_KONTEN, postingan.getKonten());

        db.update(TABLE_POSTINGAN, values, KEY_ID + " = ?",
                new String[] { String.valueOf(postingan.getId()) });
        db.close();
    }

    public List<User> getFollowerUser(){
        List<User> listUser = new ArrayList<User>();
        //query
        String query = "SELECT  * FROM " + TABLE_FOLLOWER + " ORDER BY " + KEY_CREATED_AT + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do {
                User follower = getUser(cursor.getInt(2));
                listUser.add(follower);
            } while (cursor.moveToNext());
        }
        Log.d(LOG, "Select All Follower ("+listUser.size()+")");
        return listUser;
    }

    public void addMultiFollower(List<Follower> listFollower){
        SQLiteDatabase db  = getWritableDatabase();

        for(Follower follower : listFollower){
            ContentValues values = new ContentValues();
            values.put(KEY_ID, follower.getId());
            values.put(KEY_USER_ID, follower.getUserId());
            values.put(KEY_FOLLOWER_ID, follower.getFollowerId());
            values.put(KEY_CREATED_AT, follower.getCreatedAt());
            values.put(KEY_UPDATED_AT, follower.getUpdatedAt());
            db.insert(TABLE_FOLLOWER, null, values);
        }
        db.close();
    }

    public void addFollower(Follower follower){
        SQLiteDatabase db  = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, follower.getUserId());
        values.put(KEY_FOLLOWER_ID, follower.getFollowerId());
        values.put(KEY_CREATED_AT, follower.getCreatedAt());
        values.put(KEY_UPDATED_AT, follower.getUpdatedAt());
        db.insert(TABLE_FOLLOWER, null, values);
        db.close();
    }

    public void deleteFollower(int userId){
        Log.d(LOG, "Unfollow");
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_FOLLOWER+" WHERE "+KEY_USER_ID + " = "+userId);
        db.close();
    }

    public void clearFollower(){
        Log.d(LOG, "Clear Follower");
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_FOLLOWER);
        db.close();
    }
}
