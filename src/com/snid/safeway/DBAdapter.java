package com.snid.safeway;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter
{
	private static final String KEY_PHONE_NUMBER = "phone_number";
	private static final String KEY_RECEIVE_TIME = "receive_time";
	private static final String KEY_SENDER_ID = "sender_id";
	private static final String KEY_SENDER_NUMBER = "sender_number";
	private static final String KEY_MESSAGE = "message";
	private static final String TAG = "DBAdapter";
	
	private static final String DATABASE_NAME = "safewayDB";
	private static final String DATABASE_TABLE = "messages";
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE =
			"CREATE TABLE messages (phone_number TEXT NOT NULL , "
			+ "receive_time INTEGER NOT NULL , "
			+ "sender_id TEXT NOT NULL , "
			+ "sender_number TEXT NOT NULL , "
			+ "message TEXT NOT NULL )";

	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	
	
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			try
			{
				db.execSQL(DATABASE_CREATE);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}
	}
	
	
	

	public DBAdapter open() throws SQLException
	{
		db = DBHelper.getWritableDatabase();
		return this;
	}
	
	public void close()
	{
		DBHelper.close();
	}
	

	public long insertMessage(MessageItem message)
	{
		if (null == message) return 0;
		
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_PHONE_NUMBER, message.getPhoneNumber());
		initialValues.put(KEY_RECEIVE_TIME, message.getReceiveTime().getTime());
		initialValues.put(KEY_SENDER_ID, message.getSenderID());
		initialValues.put(KEY_SENDER_NUMBER, message.getSenderNumber());
		initialValues.put(KEY_MESSAGE, message.getMessage());
		
		try
		{
			long ret = db.insert(DATABASE_TABLE, null,  initialValues);
			return ret;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public boolean deleteMessage(MessageItem message)
	{
		String sqlWhere = KEY_PHONE_NUMBER + "='" + message.getPhoneNumber() + "' AND "
				+ KEY_RECEIVE_TIME + "='" + Long.toString(message.getReceiveTime().getTime()) + "'";
		
		return db.delete(DATABASE_TABLE, sqlWhere, null) > 0;
	}
	
	public void clearMessages(String phone_number)
	{
		String sql = "DELETE FROM " + DATABASE_TABLE + " WHERE " + KEY_PHONE_NUMBER + "='" + phone_number + "'";
		db.execSQL(sql);
	}
	
	public Cursor getMessages(String phone_number)
	{
		String[] columns = new String[] { KEY_PHONE_NUMBER, KEY_RECEIVE_TIME, KEY_SENDER_ID, KEY_SENDER_NUMBER, KEY_MESSAGE };
		String sqlWhere = KEY_PHONE_NUMBER + "='" + phone_number + "'";
		
		Cursor c = db.query(DATABASE_TABLE, columns, sqlWhere, null, null, null, KEY_RECEIVE_TIME + " ASC");
		
		return c;
	}
}
