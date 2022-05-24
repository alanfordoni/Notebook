package com.brzimetrokliziretro.notebook.models;

import android.util.Log;

public class ModelMain {
    private static final String TAG = "bel.modelmain";
    public static final int TYPE_NOTE = 1;
    public static final int TYPE_SHOPPING_LIST = 2;

    private int viewType;
    private long id;
    private String note;
    private long time;
    private int noteType;
    private int status;
    private long createdAt;
    private int alarm;
    private long modifiedAt;
    private String repeatOrder;

    private String shoppingTitle;
    private String shopList;
    private String boxState;
    private String title;

    public ModelMain(){}

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public void setId(long id) {
        if(id > 0) {
            this.id = id;
        }else{
            Log.e(TAG, "wrong id number value");
        }
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setNoteType(int noteType) {
        this.noteType = noteType;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setShoppingTitle(String shoppingTitle) {
        this.shoppingTitle = shoppingTitle;
    }

    public void setShopList(String shopList) {
        this.shopList = shopList;
    }

    public void setBoxState(String boxState) {
        this.boxState = boxState;
    }

    public void setAlarm(int alarm) {
        if(alarm < 0 || alarm > 2){
            Log.e(TAG, "Wrong value for alarm type");
        }else{
            this.alarm = alarm;
        }
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setModifiedAt(long modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public void setTitle(String title) {
        this.title =title;
    }

    public void setRepeatOrder(String repeatOrder){
        this.repeatOrder = repeatOrder;
    }

    public long getId() {
        return this.id;
    }

    public int getViewType(){
        return this.viewType;
    }

    public String getNote() {
        return this.note;
    }

    public int getNoteType() {
        return this.noteType;
    }

    public int getStatus() {
        return this.status;
    }

    public long getCreatedAt() {
        return this.createdAt;
    }

    public String getShoppingTitle(){
        return this.shoppingTitle;
    }

    public String getShopList(){
        return this.shopList;
    }

    public String getBoxState(){
        return this.boxState;
    }

    public long getTime(){
        return this.time;
    }

    public int getAlarm(){
        return this.alarm;
    }

    public long getModifiedAt(){
        return this.modifiedAt;
    }

    public String getTitle(){
        return this.title;
    }

    public String getRepeatOrder() {
        return repeatOrder;
    }
}
