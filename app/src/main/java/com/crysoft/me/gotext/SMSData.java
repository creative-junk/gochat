package com.crysoft.me.gotext;

import java.io.Serializable;
import java.sql.Date;

public class SMSData implements Serializable {
    private long id;
    private long threadId;
    private String number;
    private String body;
    private String name;
    private int type; // 1 for receive sms; 2 for sent sms
    private long date;
    private Date dateObj;
    private String ReadStatus;
    private long contactID;
    private String messageCenter;
    private String status;
    private int seen;

    private int locked;

    public SMSData clone() {
        SMSData sms = new SMSData();

        sms.setId(this.id);
        sms.setThreadId(this.threadId);
        sms.setNumber(this.number);
        sms.setBody(this.body);
        sms.setName(this.name);
        sms.setType(this.type);
        sms.setDate(this.date);
        sms.setReadStatus(this.ReadStatus);
        sms.setContactID(this.contactID);
        sms.setMessageCenter(this.messageCenter);
        sms.setStatus(this.status);
        sms.setSeen(this.seen);

        return sms;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public boolean isSeen() {
        if (seen == 0) {
            return false;
        }

        return true;
    }

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }

    public boolean isLocked() {
        if (locked == 0) {
            return false;
        }

        return true;
    }

    public int getLocked() {
        return locked;
    }

    public void setLock(int lock) {
        this.locked = lock;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessageCenter() {
        return messageCenter;
    }

    public void setMessageCenter(String messageCenter) {
        this.messageCenter = messageCenter;
    }

    public long getContactID() {
        return contactID;
    }

    public void setContactID(long contactID) {
        this.contactID = contactID;
    }


    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
        this.dateObj = new Date(date);
    }

    public Date getDateObj() {
        return dateObj;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getReadStatus() {
        return ReadStatus;
    }

    public void setReadStatus(String readStatus) {
        ReadStatus = readStatus;
    }

}
