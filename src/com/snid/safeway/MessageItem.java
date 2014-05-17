package com.snid.safeway;

import java.util.Date;

public class MessageItem
{
	private int _id;
	private String phone_number, sender_id, sender_number, message;
	private Date receive_time;
	public String getPhoneNumber() {
		return phone_number;
	}
	public void setPhoneNumber(String phone_number) {
		this.phone_number = phone_number;
	}
	public String getSenderID() {
		return sender_id;
	}
	public void setSenderID(String sender_id) {
		this.sender_id = sender_id;
	}
	public String getSenderNumber() {
		return sender_number;
	}
	public void setSenderNumber(String sender_number) {
		this.sender_number = sender_number;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getReceiveTime() {
		return receive_time;
	}
	public void setReceiveTime(Date receive_time) {
		this.receive_time = receive_time;
	}
	public int getId() {
		return _id;
	}
	public void setId(int id) {
		this._id = id;
	}
	
	
	
}
