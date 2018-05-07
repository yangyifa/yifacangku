package com.example.contectstest;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class MyProvider extends ContentProvider{
	
	  public static final int TABLE1_DIR=0;
	  public static final int TABLE1_ITEM=1;
	  public static final int TABLE2_DIR=2;
	  public static final int TABLE2_ITEM=3;
	  private static UriMatcher uriMatcher;
	  
       public boolean onCreate(){
    	   return false;
       }
       
       public Cursor query(Uri uri,String[] projection,String selection,String[] selectionArgs,String sortorder){
    	    return null;
       }
       
       public Uri insert(Uri uri,ContentValues values){
    	   return null;
       }
       
       public int update(Uri uri,ContentValues values,String selection,String[] selectionArgs){
    	   return 0;
       }
       
       public int delete(Uri uri,String selection,String[] selectionArgs){
    	   return 0;
       }
       
       public String getType(Uri uri){
    	   return null;
       }
}
