package net.lulli.android.metadao;

import java.io.File;
import java.nio.file.Path;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbConnectionManager extends SQLiteOpenHelper {
	
	public static final String DATABASE_NAME = "__DEFAULT__.db";
	
	ALog log = new ALog(this.getClass().getName());
		
	
	//ANDROID 
	//----------------------------------------------------------------
	public DbConnectionManager(Context context, String DATABASE_NAME){
		//log.debug("BEGIN getConnection()");
		//File dbpath = context.getDatabasePath(DATABASE_NAME);
	    //context.getDatabasePath(DATABASE_NAME);
		super(context, DATABASE_NAME , null, 1);
		//WARN::
		File dbpath = context.getDatabasePath(DATABASE_NAME);
		log.debug("dbpath=["+dbpath+"]");
	 }
	
	public DbConnectionManager(Context context){
		super(context, DATABASE_NAME , null, 1);
		//WARN::
		File dbpath = context.getDatabasePath(DATABASE_NAME);
		log.debug("dbpath=["+dbpath+"]");
	 }
	public void onCreate(SQLiteDatabase db) {
		log.debug("BEGIN onCreate()");
		String createSQL ="create table metadata (key text, value, text);" +
				"insert into metadata ('DATE', now())";
		db.execSQL(createSQL);
		log.debug("createSQL:"+createSQL);
		log.debug("END onCreate()");
	}

	 
	   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		   log.debug("BEGIN onUpgrade()");
		   String updateSQL ="DROP TABLE  metadata ";
		   log.debug("updateSQL:"+updateSQL);
		   log.debug("BEFORE UPDATE");
		   db.execSQL(updateSQL);
		   log.debug("AFTER UPDATE");
		   onCreate(db);
		   log.debug("END onUpgrade()");
	   }
	
	public  SQLiteDatabase getConnection(){
		log.debug("BEGIN getConnection()");
		SQLiteDatabase db = this.getWritableDatabase();
		log.debug("END getConnection()");
		return db;
	}
	
	
	
	public  void releaseConnection(SQLiteDatabase dbConnection){
	    try{
	    	if (null != dbConnection){
	    		dbConnection.close();
	    	} 
	    }
	    catch (Exception e){
	      e.printStackTrace();
	    }
	}
	
	public  void closeConnection(SQLiteDatabase dbConnection){
	    try{
	    	if (null != dbConnection){
	    		dbConnection.close();
	    	} 
	    }
	    catch (Exception e){
	      e.printStackTrace();
	    }
}
}
