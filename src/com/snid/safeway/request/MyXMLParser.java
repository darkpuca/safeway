package com.snid.safeway.request;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;


public class MyXMLParser
{
	public class snidResponse
	{
		public String Message, Reason;
		public int Code;
		
		public snidResponse()
		{
			this.Code = -1;
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
						response = new snidResponse();
					else if (tagName.equalsIgnoreCase("code"))
						response.Code = Integer.parseInt(parser.nextText());
					else if (tagName.equalsIgnoreCase("message"))
						response.Message = parser.nextText();
					else if (tagName.equalsIgnoreCase("reason"))
						response.Reason = parser.nextText();
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

}
