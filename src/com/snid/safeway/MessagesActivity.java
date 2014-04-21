package com.snid.safeway;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import com.google.android.gms.common.annotation.KeepName;
import com.snid.safeway.request.RequestAdapter.RequestAdapterListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MessagesActivity extends BaseActivity implements RequestAdapterListener
{
	public static boolean IsActive; 
	private ListView messageList;
	private DBAdapter db;
	private MessageListAdapter adapter;
	private static Vector<MessageItem> updateQueue = null;
	
	private Timer updateTimer;
	private NotificationManager notiManager;
	private String device_id, phone_number;
	private int test_check = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messages);
		
		getKeepAliveInfos();
		
		notiManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		
		db = new DBAdapter(this);
		
		messageList = (ListView)findViewById(R.id.message_list);
//		this.messageList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Globals.sample_strings));
		
		createListAdapter();
		messageList.setAdapter(adapter);

		// scroll to buttom.
		if (0 < adapter.getCount())
			messageList.setSelection(adapter.getCount() - 1);
		
		if (null == updateQueue)
			updateQueue = new Vector<MessageItem>();
		
		updateTimer = new Timer();
		updateTimer.scheduleAtFixedRate(new TimerTask()
		{			
			@Override
			public void run()
			{
				runOnUiThread(new Runnable()
				{					
					@Override
					public void run()
					{
						if (0 < updateQueue.size())
						{
							MessageItem item = updateQueue.get(0);
							adapter.add(item);
							updateQueue.remove(0);
							
							// scroll to buttom.
							if (0 < adapter.getCount())
								messageList.setSelection(adapter.getCount() - 1);
						}
					}
				});
			}
		}, 1000, 1000);
		
		messageList.setDividerHeight(0);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.messages, menu);
		return true;
	}

	/*
	 * 2014-2-15, 현재 푸시 메세지 데이타만 표시하는 구조이므로 메세지창에서 'back' 선택시 종료 처리
	 */
	@Override
	public void onBackPressed()
	{
		// 'back'버튼 선택하면 확인 메세지.
		AlertDialog.Builder exitDlg = new AlertDialog.Builder(this);
		exitDlg.setCancelable(true);
		exitDlg.setTitle(R.string.information);
		exitDlg.setMessage(R.string.msg_exit_confirm);
		exitDlg.setPositiveButton(R.string.exit, new DialogInterface.OnClickListener()
		{			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				setResult(RESULT_OK);
				finish();
//				System.exit(0);
			}
		});
		exitDlg.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
		{			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
			}
		});
		exitDlg.show();
	}
	
	@Override
	protected void onResume()
	{
		// 푸시 수신 리스트 갱신
		updateListAdapter();
		
		// scroll to buttom.
		if (0 < adapter.getCount())
			messageList.setSelection(adapter.getCount() - 1);
		
		GcmIntentService.NOTIFICATION_COUNT = 0;
		notiManager.cancelAll();
		
		IsActive = true;
		
		sendKeepAlive();

		super.onResume();
	}

	@Override
	protected void onPause()
	{
		IsActive = false;
		super.onPause();
	}

	@Override
	protected void onDestroy() 
	{
		updateTimer.cancel();

		super.onDestroy();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		// TODO Auto-generated method stub
		return super.onContextItemSelected(item);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (R.id.action_clear_messages == item.getItemId())
		{
			AlertDialog.Builder dlg = new AlertDialog.Builder(this);
			dlg.setCancelable(false);
			dlg.setTitle(R.string.information);
			dlg.setMessage(R.string.msg_clear_messages_confirm);
			dlg.setPositiveButton(R.string.continue_, new DialogInterface.OnClickListener()
			{	
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					clearMessages();
				}
			});
			
			dlg.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
			{				
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
				}
			});
			dlg.show();

		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void getKeepAliveInfos()
	{
		SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		device_id = prefs.getString(Globals.PROPERTY_REG_ID, "");
		phone_number = prefs.getString(Globals.PROPERTY_PHONE_NUMBER, "");
	}
	

	private void sendKeepAlive()
	{
		if (null != device_id && null != phone_number)
		{
	    	req_type = REQ_KEEP_ALIVE;
			req.SendRegistrationKeepAlive(this, phone_number, device_id);
		}
	}


	private void createListAdapter()
	{
		adapter = new MessageListAdapter(this, new Vector<MessageItem>());
	}
	
	private void updateListAdapter()
	{
		Vector<MessageItem> items = getMessages();
		adapter.clear();
		
		for (MessageItem item : items)
			adapter.add(item);
	}

	private Vector<MessageItem> getMessages()
	{
		Vector<MessageItem> items = new Vector<MessageItem>();
		
		db.open();
		
		Cursor c = db.getMessages(MainApplication.getPhoneNumber());
		if (null == c) return null;
		
		if (c.moveToFirst())
		{
			do
			{
				MessageItem msg = new MessageItem();
				Date recv_time = new Date(c.getLong(0));
				msg.setReceiveTime(recv_time);
				msg.setMessage(c.getString(1));
				
				items.add(msg);
				
			} while (c.moveToNext());
		}
		
		db.close();
		
		return items;
	}

	private void clearMessages()
	{
		db.open();
		db.clearMessages(MainApplication.getPhoneNumber());
		db.close();
		
		adapter.clear();
	}
	
	public static void AddNewMessage(MessageItem newItem)
	{
		if (IsActive)
			updateQueue.add(newItem);
	}
	
	
	
	
	private class MessageItemContainer
	{
		public TextView date_label, message_label;
	}
	
	private class MessageListAdapter extends ArrayAdapter<MessageItem>
	{
		private Vector<MessageItem> m_items;
		private Activity m_context;
		
		public MessageListAdapter(Activity context, Vector<MessageItem> items)
		{
			super(context, R.layout.listitem_message, items);

			this.m_context = context;
			this.m_items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			MessageItemContainer container;
			View rowView = convertView;
			
			if (null == rowView)
			{
				try {
					LayoutInflater inflater = m_context.getLayoutInflater();
					rowView = inflater.inflate(R.layout.listitem_message, null, true);					
				} catch (Exception e) {
					Log.d("SW", e.getLocalizedMessage());
					return null;
				}

				container = new MessageItemContainer();
				container.date_label = (TextView)rowView.findViewById(R.id.date_label);
				container.message_label = (TextView)rowView.findViewById(R.id.message_label);
				
				rowView.setTag(container);
			}
			else
			{
				container = (MessageItemContainer)rowView.getTag();
			}

			SimpleDateFormat formatter = new SimpleDateFormat(Globals.DATETIME_FORMAT_FOR_MESSAGE, Locale.US);
			
			MessageItem item = m_items.get(position);
			
			container.date_label.setText(formatter.format(item.getReceiveTime()));
			container.message_label.setText(item.getMessage());
			
			return rowView;
		}		
	}

	@Override
	public void onFinishRequest(int code, String message, String reason, int user_type)
	{
		if (prog.isShowing()) prog.dismiss();
		
		if (REQ_KEEP_ALIVE == req_type)
		{
			if (test_check >= 3)
			{
//				code = 1;
				test_check = 0;
			}
			
			// do not anything.
			if (0 == code)
			{
				test_check++;
				System.out.println("keep alive ok.");
			}
			else if (1 == code)
			{
				// 서버에서 device-id가 정리된 경우.
				System.out.println("keep alive fail.");
				
				// MainActivity를 통해 인증을 다시 받음.
				setResult(RESULT_CANCELED);
				finish();
			}
		}		
	}	
	

}
