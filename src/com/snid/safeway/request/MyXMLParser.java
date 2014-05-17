package com.snid.safeway.request;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.snid.safeway.Globals;
import com.snid.safeway.MessageItem;


public class MyXMLParser
{
	public class snidResponse
	{
		public String Message, Reason;
		public int Code, UserType;
		
		public snidResponse()
		{
			this.Code = -1;
			this.UserType = 0;
		}
	}
	
	private String xmlString;
	
	public MyXMLParser(String xml)
	{
		this.xmlString = xml;
	}
	
	public MyXMLParser(File xmlFile)
	{
		StringBuilder text = new StringBuilder();

		try
		{
		    BufferedReader br = new BufferedReader(new FileReader(xmlFile));
		    String line;

		    while (null != (line = br.readLine()))
		    {
		        text.append(line);
		        text.append('\n');
		    }
		    br.close();
		    
		    this.xmlString = text.toString();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	

	public snidResponse GetResponse()
	{
		if (null == xmlString || 0 == xmlString.length()) return null;
		
		try
		{
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(new StringReader(xmlString));
			
			snidResponse response = null;
			
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT)
			{
				String tagName = null;
				
				switch (eventType)
				{
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.END_DOCUMENT:						
					break;
				case XmlPullParser.START_TAG:
					tagName = parser.getName();
					if (tagName.equalsIgnoreCase("result"))
					{
						response = new snidResponse();
					}
					else if (tagName.equalsIgnoreCase("code"))
					{
						response.Code = Integer.parseInt(parser.nextText());
					}
					else if (tagName.equalsIgnoreCase("message"))
					{
						response.Message = parser.nextText();
					}
					else if (tagName.equalsIgnoreCase("reason"))
					{
						response.Reason = parser.nextText();
					}
					else if (tagName.equalsIgnoreCase("mtype"))
					{
						String retType = parser.nextText();
						if (retType.equalsIgnoreCase("p"))
							response.UserType = Globals.USERTYPE_PARENTS;
						else if (retType.equalsIgnoreCase("s"))
							response.UserType = Globals.USERTYPE_STUDENT;
						else if (retType.equalsIgnoreCase("t"))
							response.UserType = Globals.USERTYPE_TEACHER;
						else if (retType.equalsIgnoreCase("at"))
							response.UserType = Globals.USERTYPE_AFTER_TEACHER;
					}
					break;
				case XmlPullParser.END_TAG:
					tagName = parser.getName();
					break;
				}					
				eventType = parser.next();
			}
			
			return response;			
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public Vector<MessageItem> GetMessages()
	{
		if (null == xmlString || 0 == xmlString.length()) return null;
		
		try
		{
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(new StringReader(xmlString));
			
			Vector<MessageItem> messages = null;
			MessageItem item = null;
			
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT)
			{
				String tagName = null;
				
				switch (eventType)
				{
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.END_DOCUMENT:						
					break;
				case XmlPullParser.START_TAG:
					tagName = parser.getName();
					if (tagName.equalsIgnoreCase("result"))
					{
						messages = new Vector<MessageItem>();
					}
					else if (tagName.equalsIgnoreCase("item"))
					{
						item = new MessageItem();
					}
					else if (tagName.equalsIgnoreCase("sender"))
					{
						item.setSenderID(parser.nextText());
					}
					else if (tagName.equalsIgnoreCase("msg"))
					{
						item.setMessage(parser.nextText());
					}
					else if (tagName.equalsIgnoreCase("logidx"))
					{
						item.setId(Integer.parseInt(parser.nextText()));
					}
					else if (tagName.equalsIgnoreCase("sendtime"))
					{
						item.setReceiveTime(dateFromString(parser.nextText(), Globals.DATETIME_FORMAT_FOR_SERVER));
					}
					break;
				case XmlPullParser.END_TAG:
					tagName = parser.getName();
					if (tagName.equalsIgnoreCase("item"))
						messages.add(0, item);
					else if (tagName.equalsIgnoreCase("result"))
						return messages;
					break;
				}					
				eventType = parser.next();
			}
			
			return messages;			
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private Date dateFromString(String xmlText, String strFormat)
	{
		if (null == xmlText || 0 == xmlText.length()) return null;
		
		if (xmlText.equalsIgnoreCase("0000-00-00 00:00:00")) return null;
		
		SimpleDateFormat  format = new SimpleDateFormat(strFormat, Locale.US);  
		try
		{
		    Date parseDate = format.parse(xmlText);
		    return parseDate;
		}
		catch (Exception e)
		{
		    e.printStackTrace();
		    return null;
		}
	}
}
