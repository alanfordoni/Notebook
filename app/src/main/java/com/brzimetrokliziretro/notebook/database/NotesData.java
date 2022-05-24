package com.brzimetrokliziretro.notebook.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.brzimetrokliziretro.notebook.models.CalendarDays;
import com.brzimetrokliziretro.notebook.models.ModelMain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NotesData extends SQLiteOpenHelper {
    private static final String TAG = "BEL.notesdata";
    private static final int    DATABASE_VERSION = 1;
    private static final String LOG = "ToDoNotesHelper";
    private static final String DATABASE_NAME = "contactsManager";
    private static final String TABLE_TODO = "todos";
    private static final String TABLE_LISTA = "lista";

    private static final String KEY_ID = "id";
    private static final String KEY_NOTE_TYPE = "notetype";
    private static final String KEY_CREATED_AT = "createdat";
    private static final String KEY_TODO = "todo";
    private static final String KEY_STATUS = "status";
    private static final String KEY_VIEW_TYPE = "viewtype";
    private static final String KEY_SHOP_LIST = "shoppinglist";
    private static final String KEY_SHOP_TITLE = "shoptitle";
    private static final String KEY_BOX_STATE = "boxstate";
    private static final String KEY_ALARM_STATE = "alarm_state";
    private static final String KEY_TIME = "time";
    private static final String KEY_MODIFIED_AT = "modifiedat";
    private static final String KEY_TITLE = "title";
    private static final String KEY_REPEAT_ORDER = "repeat";

    private Context context;

    private static final String CREATE_TABLE_TODO = "CREATE TABLE "
            + TABLE_TODO + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOTE_TYPE
            + " INTEGER," + KEY_CREATED_AT + " LONG," + KEY_TODO + " TEXT,"
            + KEY_STATUS + " INTEGER," + KEY_VIEW_TYPE + " INTEGER," + KEY_ALARM_STATE
            + " INTEGER," + KEY_TIME + " LONG," + KEY_MODIFIED_AT + " long," + KEY_TITLE
            + " TEXT" + KEY_REPEAT_ORDER + "TEXT" + ")";

    public NotesData(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      //  db.execSQL(CREATE_TABLE_TODO);
        db.execSQL("create table todos "
                + "(id integer primary key, notetype int, createdat long," +
                " todo text, status int, viewtype int, time long," +
                " alarm_state integer, modifiedat long, title text, repeat text)");
        db.execSQL("create table lista " + "(id integer primary key, shoptitle text," +
                " shoppinglist text, boxstate text, status int, viewtype int, createdat long)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_TODO + "'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_LISTA + "'");
        onCreate(db);
    }
    // Methods for table "todos"

    public void addToDo(ModelMain model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_VIEW_TYPE, model.getViewType());
        values.put(KEY_TODO, model.getNote());
        values.put(KEY_TIME, model.getTime());
        values.put(KEY_NOTE_TYPE, model.getNoteType());
        values.put(KEY_ALARM_STATE, model.getAlarm());
        values.put(KEY_STATUS, 0);
        values.put(KEY_TITLE, model.getTitle());
        values.put(KEY_CREATED_AT, currentTime());
        values.put(KEY_REPEAT_ORDER, model.getRepeatOrder());

        db.insert("todos", null, values);
        db.close();
    }

    public ModelMain getToDo(long model_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_TODO + " WHERE "
                + KEY_ID + " = " + model_id;

        ModelMain td = new ModelMain();

        Cursor c = db.rawQuery(selectQuery, null);
        try {
            if (c != null) {
                if (c.moveToFirst()) {
                    td.setViewType(c.getInt(c.getColumnIndex(KEY_VIEW_TYPE)));
                    td.setId(c.getLong(c.getColumnIndex(KEY_ID)));
                    td.setNote(c.getString(c.getColumnIndex(KEY_TODO)));
                    td.setTime(c.getLong(c.getColumnIndex(KEY_TIME)));
                    td.setNoteType(c.getInt(c.getColumnIndex(KEY_NOTE_TYPE)));
                    td.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
                    td.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
                    td.setAlarm(c.getInt(c.getColumnIndex(KEY_ALARM_STATE)));
                    td.setCreatedAt(c.getLong(c.getColumnIndex(KEY_CREATED_AT)));
                    td.setModifiedAt(c.getLong(c.getColumnIndex(KEY_MODIFIED_AT)));
                    td.setRepeatOrder(c.getString(c.getColumnIndex(KEY_REPEAT_ORDER)));
                    c.close();
                    db.close();

                    return td;
                }
            }
        }catch(CursorIndexOutOfBoundsException e){
            e.printStackTrace();
            return td;
        }
        return td;
    }

    public ModelMain getLastToDo(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.query("todos", null, null, null, null, null, null);

        if(c != null) {
            c.moveToLast();
        }
        ModelMain td = new ModelMain();
        td.setViewType(c.getInt(c.getColumnIndex(KEY_VIEW_TYPE)));
        td.setId(c.getLong(c.getColumnIndex(KEY_ID)));
        td.setNote(c.getString(c.getColumnIndex(KEY_TODO)));
        td.setTime(c.getLong(c.getColumnIndex(KEY_TIME)));
        td.setNoteType(c.getInt(c.getColumnIndex(KEY_NOTE_TYPE)));
        td.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
        td.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
        td.setAlarm(c.getInt(c.getColumnIndex(KEY_ALARM_STATE)));
        td.setCreatedAt(c.getLong(c.getColumnIndex(KEY_CREATED_AT)));
        td.setModifiedAt(c.getLong(c.getColumnIndex(KEY_MODIFIED_AT)));
        td.setRepeatOrder(c.getString(c.getColumnIndex(KEY_REPEAT_ORDER)));
        c.close();
        db.close();
        return td;
    }

    public List<ModelMain> getAllToDos(){

        String where = "viewtype = 1 AND status = 0";
        String sortOrder = KEY_TIME + " DESC";
        List<ModelMain> todos = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("todos", null, where, null, null, null, sortOrder);

        // looping through all rows and adding to list
        try {
            if (c != null)
                c.moveToFirst();
            do {
                ModelMain td = new ModelMain();
                td.setViewType(c.getInt(c.getColumnIndex(KEY_VIEW_TYPE)));
                td.setId(c.getLong(c.getColumnIndex(KEY_ID)));
                td.setNote(c.getString(c.getColumnIndex(KEY_TODO)));
                td.setTime(c.getLong(c.getColumnIndex(KEY_TIME)));
                td.setNoteType(c.getInt(c.getColumnIndex(KEY_NOTE_TYPE)));
                td.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
                td.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
                td.setAlarm(c.getInt(c.getColumnIndex(KEY_ALARM_STATE)));
                td.setCreatedAt(c.getLong(c.getColumnIndex(KEY_CREATED_AT)));
                td.setModifiedAt(c.getLong(c.getColumnIndex(KEY_MODIFIED_AT)));
                td.setRepeatOrder(c.getString(c.getColumnIndex(KEY_REPEAT_ORDER)));
                // adding to todo list
                todos.add(td);
            } while (c.moveToNext());
        }catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        c.close();
        db.close();

        return todos;
    }

    public List<ModelMain> getTodosWithChosenDate(String chosen_date){
        String where = "viewtype = 1 AND status = 0 AND alarm_state != 2";
        List<ModelMain> todos = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("todos", null, where, null, null, null, null);

        try{
            if (c != null)
                c.moveToFirst();
            do {
                long time = 0;
                String this_date = "";
                ModelMain model = new ModelMain();
                if(c.getLong(c.getColumnIndex(KEY_TIME)) != 0){
                    time = c.getLong(c.getColumnIndex(KEY_TIME));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(time);
                    Date date = calendar.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
                    this_date = sdf.format(date);
                }else if(c.getLong(c.getColumnIndex(KEY_MODIFIED_AT)) != 0){
                    time = c.getLong(c.getColumnIndex(KEY_MODIFIED_AT));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(time);
                    Date date = calendar.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
                    this_date = sdf.format(date);
                }else{
                    time = c.getLong(c.getColumnIndex(KEY_CREATED_AT));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(time);
                    Date date = calendar.getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
                    this_date = sdf.format(date);
                }
                if(chosen_date.equalsIgnoreCase(this_date)){
                    model.setViewType(c.getInt(c.getColumnIndex(KEY_VIEW_TYPE)));
                    model.setId(c.getLong(c.getColumnIndex(KEY_ID)));
                    model.setNote(c.getString(c.getColumnIndex(KEY_TODO)));
                    model.setTime(c.getLong(c.getColumnIndex(KEY_TIME)));
                    model.setNoteType(c.getInt(c.getColumnIndex(KEY_NOTE_TYPE)));
                    model.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
                    model.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
                    model.setAlarm(c.getInt(c.getColumnIndex(KEY_ALARM_STATE)));
                    model.setCreatedAt(c.getLong(c.getColumnIndex(KEY_CREATED_AT)));
                    model.setModifiedAt(c.getLong(c.getColumnIndex(KEY_MODIFIED_AT)));
                 //   model.setRepeatOrder(c.getString(c.getColumnIndex(KEY_REPEAT_ORDER)));
                    todos.add(model);
                }
            }while(c.moveToNext());

        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        c.close();
        db.close();

        return todos;
    }

    public List<CalendarDays> getAllNoTimeSetTodos(){
        String where = "viewtype = 1 AND status = 0 AND alarm_state = 0";
        List<CalendarDays> todos = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("todos", null, where, null, null, null, null);

        long time = 0;
        try {
            if (c != null)
                c.moveToFirst();
            do {
                CalendarDays cal_days = new CalendarDays();
                try {
                    time = c.getLong(c.getColumnIndex(KEY_MODIFIED_AT));
                }catch (NullPointerException e){
                    Log.e(TAG, "in getallnotimesettodos " + e.getMessage());
                    time = 0;
                }catch (IndexOutOfBoundsException e){
                    Log.e(TAG, "in getallnotimesettodos " + e.getMessage());
                    time = 0;
                }
                if(time == 0){
                    time = c.getLong(c.getColumnIndex(KEY_CREATED_AT));
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(time);
                Date date = calendar.getTime();
                calendar.setTime(date);
                cal_days.setDate(calendar);

                todos.add(cal_days);
            }while (c.moveToNext());
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        c.close();
        db.close();

        return todos;
    }

    public List<ModelMain> getToDosAboveCurrentTime(){
        String where = "viewtype = 1 AND status = 0 AND alarm_state = 1 AND DATETIME(time/1000, 'unixepoch') > DATETIME('now')";
        String sortOrder = KEY_TIME + " ASC";
        List<ModelMain> todos = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("todos", null, where, null, null, null, sortOrder);

        // looping through all rows and adding to list
        try {
            if (c != null)
                c.moveToFirst();
            do {
                ModelMain td = new ModelMain();
                td.setViewType(c.getInt(c.getColumnIndex(KEY_VIEW_TYPE)));
                td.setId(c.getLong(c.getColumnIndex(KEY_ID)));
                td.setNote(c.getString(c.getColumnIndex(KEY_TODO)));
                td.setTime(c.getLong(c.getColumnIndex(KEY_TIME)));
                td.setNoteType(c.getInt(c.getColumnIndex(KEY_NOTE_TYPE)));
                td.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
                td.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
                td.setAlarm(c.getInt(c.getColumnIndex(KEY_ALARM_STATE)));
                td.setCreatedAt(c.getLong(c.getColumnIndex(KEY_CREATED_AT)));
                td.setModifiedAt(c.getLong(c.getColumnIndex(KEY_MODIFIED_AT)));
                td.setRepeatOrder(c.getString(c.getColumnIndex(KEY_REPEAT_ORDER)));

                todos.add(td);
            } while (c.moveToNext());
        }catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        c.close();
        db.close();

        return todos;
    }

    public List<ModelMain> getToDosRepeatingAlarm(){
        String where = "viewtype = 1 AND status = 0 AND alarm_state = 2";
        String sortOrder = KEY_TIME + " ASC";
        List<ModelMain> todos = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("todos", null, where, null, null, null, sortOrder);

        // looping through all rows and adding to list
        try {
            if (c != null)
                c.moveToFirst();
            do {
                ModelMain td = new ModelMain();
                td.setViewType(c.getInt(c.getColumnIndex(KEY_VIEW_TYPE)));
                td.setId(c.getLong(c.getColumnIndex(KEY_ID)));
                td.setNote(c.getString(c.getColumnIndex(KEY_TODO)));
                td.setTime(c.getLong(c.getColumnIndex(KEY_TIME)));
                td.setNoteType(c.getInt(c.getColumnIndex(KEY_NOTE_TYPE)));
                td.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
                td.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
                td.setAlarm(c.getInt(c.getColumnIndex(KEY_ALARM_STATE)));
                td.setCreatedAt(c.getLong(c.getColumnIndex(KEY_CREATED_AT)));
                td.setModifiedAt(c.getLong(c.getColumnIndex(KEY_MODIFIED_AT)));
                td.setRepeatOrder(c.getString(c.getColumnIndex(KEY_REPEAT_ORDER)));

                todos.add(td);
            } while (c.moveToNext());
        }catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        c.close();
        db.close();

        return todos;
    }

    public List<ModelMain> getToDosBellowCurrentTime(){
        String where = "viewtype = 1 AND status = 0 AND alarm_state != 2 AND DATETIME(time/1000, 'unixepoch') <= DATETIME('now')";
        String sortOrder = KEY_TIME + " DESC";
        List<ModelMain> todos = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("todos", null, where, null, null, null, sortOrder);

        // looping through all rows and adding to list
        try {
            if (c != null)
                c.moveToFirst();
            do {
                ModelMain td = new ModelMain();
                td.setViewType(c.getInt(c.getColumnIndex(KEY_VIEW_TYPE)));
                td.setId(c.getLong(c.getColumnIndex(KEY_ID)));
                td.setNote(c.getString(c.getColumnIndex(KEY_TODO)));
                td.setTime(c.getLong(c.getColumnIndex(KEY_TIME)));
                td.setNoteType(c.getInt(c.getColumnIndex(KEY_NOTE_TYPE)));
                td.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
                td.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
                td.setAlarm(c.getInt(c.getColumnIndex(KEY_ALARM_STATE)));
                td.setCreatedAt(c.getLong(c.getColumnIndex(KEY_CREATED_AT)));
                td.setModifiedAt(c.getLong(c.getColumnIndex(KEY_MODIFIED_AT)));
                td.setRepeatOrder(c.getString(c.getColumnIndex(KEY_REPEAT_ORDER)));

                todos.add(td);
            } while (c.moveToNext());
        }catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        c.close();
        db.close();

        return todos;
    }

    public List<CalendarDays> getAllSingleOccuringAlarms(){
        String where = "status = 0 AND alarm_state = 1";
        List<CalendarDays> calendar_days = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_TODO, null, where, null, null, null, null);
        try {
            if (c != null)
                c.moveToFirst();
            do {
                CalendarDays cal_days = new CalendarDays();
                long time = c.getLong(c.getColumnIndex(KEY_TIME));
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(time);
                Date date = calendar.getTime();
                calendar.setTime(date);
                cal_days.setDate(calendar);

                calendar_days.add(cal_days);
            }while (c.moveToNext());
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        c.close();
        db.close();

        return calendar_days;
    }

    public List<ModelMain> getBusinessToDos(){
        String where = "notetype = 2 AND status = 0";
        String sortOrder = KEY_TIME + " ASC";
        List<ModelMain> busTodos = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_TODO, null, where, null, null, null, sortOrder);
        try {
            if (c != null)
                c.moveToFirst();
            do {
                ModelMain td = new ModelMain();
                td.setViewType(c.getInt(c.getColumnIndex(KEY_VIEW_TYPE)));
                td.setId(c.getLong(c.getColumnIndex(KEY_ID)));
                td.setNote(c.getString(c.getColumnIndex(KEY_TODO)));
                td.setTime(c.getLong(c.getColumnIndex(KEY_TIME)));
                td.setNoteType(c.getInt(c.getColumnIndex(KEY_NOTE_TYPE)));
                td.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
                td.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
                td.setAlarm(c.getInt(c.getColumnIndex(KEY_ALARM_STATE)));
                td.setCreatedAt(c.getLong(c.getColumnIndex(KEY_CREATED_AT)));
                td.setModifiedAt(c.getLong(c.getColumnIndex(KEY_MODIFIED_AT)));
                td.setRepeatOrder(c.getString(c.getColumnIndex(KEY_REPEAT_ORDER)));

                busTodos.add(td);
            } while (c.moveToNext());
        }catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        c.close();
        db.close();

        return busTodos;
    }

    public List<ModelMain> getBusAboveCurrentTime(){
        String where = "viewtype = 1 AND status = 0 AND notetype = 2 AND DATETIME(time/1000, 'unixepoch') > DATETIME('now')";
        String sortOrder = KEY_TIME + " ASC";
        List<ModelMain> todos = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("todos", null, where, null, null, null, sortOrder);

        // looping through all rows and adding to list
        try {
            if (c != null)
                c.moveToFirst();
            do {
                ModelMain td = new ModelMain();
                td.setViewType(c.getInt(c.getColumnIndex(KEY_VIEW_TYPE)));
                td.setId(c.getLong(c.getColumnIndex(KEY_ID)));
                td.setNote(c.getString(c.getColumnIndex(KEY_TODO)));
                td.setTime(c.getLong(c.getColumnIndex(KEY_TIME)));
                td.setNoteType(c.getInt(c.getColumnIndex(KEY_NOTE_TYPE)));
                td.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
                td.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
                td.setAlarm(c.getInt(c.getColumnIndex(KEY_ALARM_STATE)));
                td.setCreatedAt(c.getLong(c.getColumnIndex(KEY_CREATED_AT)));
                td.setModifiedAt(c.getLong(c.getColumnIndex(KEY_MODIFIED_AT)));
                td.setRepeatOrder(c.getString(c.getColumnIndex(KEY_REPEAT_ORDER)));

                todos.add(td);
            } while (c.moveToNext());
        }catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        c.close();
        db.close();

        return todos;
    }

    public List<ModelMain> getBusRepeatingAlarm(){
        String where = "viewtype = 1 AND status = 0 AND notetype = 2 AND alarm_state = 2";
        String sortOrder = KEY_TIME + " ASC";
        List<ModelMain> todos = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("todos", null, where, null, null, null, sortOrder);

        // looping through all rows and adding to list
        try {
            if (c != null)
                c.moveToFirst();
            do {
                ModelMain td = new ModelMain();
                td.setViewType(c.getInt(c.getColumnIndex(KEY_VIEW_TYPE)));
                td.setId(c.getLong(c.getColumnIndex(KEY_ID)));
                td.setNote(c.getString(c.getColumnIndex(KEY_TODO)));
                td.setTime(c.getLong(c.getColumnIndex(KEY_TIME)));
                td.setNoteType(c.getInt(c.getColumnIndex(KEY_NOTE_TYPE)));
                td.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
                td.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
                td.setAlarm(c.getInt(c.getColumnIndex(KEY_ALARM_STATE)));
                td.setCreatedAt(c.getLong(c.getColumnIndex(KEY_CREATED_AT)));
                td.setModifiedAt(c.getLong(c.getColumnIndex(KEY_MODIFIED_AT)));
                td.setRepeatOrder(c.getString(c.getColumnIndex(KEY_REPEAT_ORDER)));

                todos.add(td);
            } while (c.moveToNext());
        }catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        c.close();
        db.close();

        return todos;
    }

    public List<ModelMain> getBusBellowCurrentTime(){
        String where = "viewtype = 1 AND status = 0 AND notetype = 2 AND alarm_state != 2 AND DATETIME(time/1000, 'unixepoch') <= DATETIME('now')";
        String sortOrder = KEY_TIME + " DESC";
        List<ModelMain> todos = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("todos", null, where, null, null, null, sortOrder);

        // looping through all rows and adding to list
        try {
            if (c != null)
                c.moveToFirst();
            do {
                ModelMain td = new ModelMain();
                td.setViewType(c.getInt(c.getColumnIndex(KEY_VIEW_TYPE)));
                td.setId(c.getLong(c.getColumnIndex(KEY_ID)));
                td.setNote(c.getString(c.getColumnIndex(KEY_TODO)));
                td.setTime(c.getLong(c.getColumnIndex(KEY_TIME)));
                td.setNoteType(c.getInt(c.getColumnIndex(KEY_NOTE_TYPE)));
                td.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
                td.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
                td.setAlarm(c.getInt(c.getColumnIndex(KEY_ALARM_STATE)));
                td.setCreatedAt(c.getLong(c.getColumnIndex(KEY_CREATED_AT)));
                td.setModifiedAt(c.getLong(c.getColumnIndex(KEY_MODIFIED_AT)));
                td.setRepeatOrder(c.getString(c.getColumnIndex(KEY_REPEAT_ORDER)));

                todos.add(td);
            } while (c.moveToNext());
        }catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        c.close();
        db.close();

        return todos;
    }

    public List<ModelMain> getPersonalToDos(){

        String where = "notetype = 1 AND status = 0";
        String sortOrder = KEY_TIME + " ASC";
        List<ModelMain> perTodos = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_TODO, null, where, null, null, null, sortOrder);
        try {
            if (c != null) {
                c.moveToFirst();
                do {
                    ModelMain td = new ModelMain();
                    td.setViewType(c.getInt(c.getColumnIndex(KEY_VIEW_TYPE)));
                    td.setId(c.getLong(c.getColumnIndex(KEY_ID)));
                    td.setNote(c.getString(c.getColumnIndex(KEY_TODO)));
                    td.setTime(c.getLong(c.getColumnIndex(KEY_TIME)));
                    td.setNoteType(c.getInt(c.getColumnIndex(KEY_NOTE_TYPE)));
                    td.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
                    td.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
                    td.setAlarm(c.getInt(c.getColumnIndex(KEY_ALARM_STATE)));
                    td.setCreatedAt(c.getLong(c.getColumnIndex(KEY_CREATED_AT)));
                    td.setModifiedAt(c.getLong(c.getColumnIndex(KEY_MODIFIED_AT)));
                    td.setRepeatOrder(c.getString(c.getColumnIndex(KEY_REPEAT_ORDER)));

                    perTodos.add(td);
                } while (c.moveToNext());
            }
        }catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
            c.close();
            db.close();

        return perTodos;
    }

    public List<ModelMain> getPerAboveCurrentTime(){
        String where = "viewtype = 1 AND status = 0 AND notetype = 1 AND DATETIME(time/1000, 'unixepoch') > DATETIME('now')";
        String sortOrder = KEY_TIME + " ASC";
        List<ModelMain> todos = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("todos", null, where, null, null, null, sortOrder);

        // looping through all rows and adding to list
        try {
            if (c != null)
                c.moveToFirst();
            do {
                ModelMain td = new ModelMain();
                td.setViewType(c.getInt(c.getColumnIndex(KEY_VIEW_TYPE)));
                td.setId(c.getLong(c.getColumnIndex(KEY_ID)));
                td.setNote(c.getString(c.getColumnIndex(KEY_TODO)));
                td.setTime(c.getLong(c.getColumnIndex(KEY_TIME)));
                td.setNoteType(c.getInt(c.getColumnIndex(KEY_NOTE_TYPE)));
                td.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
                td.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
                td.setAlarm(c.getInt(c.getColumnIndex(KEY_ALARM_STATE)));
                td.setCreatedAt(c.getLong(c.getColumnIndex(KEY_CREATED_AT)));
                td.setModifiedAt(c.getLong(c.getColumnIndex(KEY_MODIFIED_AT)));
                td.setRepeatOrder(c.getString(c.getColumnIndex(KEY_REPEAT_ORDER)));

                todos.add(td);
            } while (c.moveToNext());
        }catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        c.close();
        db.close();

        return todos;
    }

    public List<ModelMain> getPerRepeatingAlarm(){
        String where = "viewtype = 1 AND status = 0 AND notetype = 1 AND alarm_state = 2";
        String sortOrder = KEY_TIME + " ASC";
        List<ModelMain> todos = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("todos", null, where, null, null, null, sortOrder);

        // looping through all rows and adding to list
        try {
            if (c != null)
                c.moveToFirst();
            do {
                ModelMain td = new ModelMain();
                td.setViewType(c.getInt(c.getColumnIndex(KEY_VIEW_TYPE)));
                td.setId(c.getLong(c.getColumnIndex(KEY_ID)));
                td.setNote(c.getString(c.getColumnIndex(KEY_TODO)));
                td.setTime(c.getLong(c.getColumnIndex(KEY_TIME)));
                td.setNoteType(c.getInt(c.getColumnIndex(KEY_NOTE_TYPE)));
                td.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
                td.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
                td.setAlarm(c.getInt(c.getColumnIndex(KEY_ALARM_STATE)));
                td.setCreatedAt(c.getLong(c.getColumnIndex(KEY_CREATED_AT)));
                td.setModifiedAt(c.getLong(c.getColumnIndex(KEY_MODIFIED_AT)));
                td.setRepeatOrder(c.getString(c.getColumnIndex(KEY_REPEAT_ORDER)));

                todos.add(td);
            } while (c.moveToNext());
        }catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        c.close();
        db.close();

        return todos;
    }

    public List<ModelMain> getPerBellowCurrentTime(){
        String where = "viewtype = 1 AND status = 0 AND notetype = 1 AND alarm_state != 2 AND DATETIME(time/1000, 'unixepoch') <= DATETIME('now')";
        String sortOrder = KEY_TIME + " DESC";
        List<ModelMain> todos = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("todos", null, where, null, null, null, sortOrder);

        // looping through all rows and adding to list
        try {
            if (c != null)
                c.moveToFirst();
            do {
                ModelMain td = new ModelMain();
                td.setViewType(c.getInt(c.getColumnIndex(KEY_VIEW_TYPE)));
                td.setId(c.getLong(c.getColumnIndex(KEY_ID)));
                td.setNote(c.getString(c.getColumnIndex(KEY_TODO)));
                td.setTime(c.getLong(c.getColumnIndex(KEY_TIME)));
                td.setNoteType(c.getInt(c.getColumnIndex(KEY_NOTE_TYPE)));
                td.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
                td.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
                td.setAlarm(c.getInt(c.getColumnIndex(KEY_ALARM_STATE)));
                td.setCreatedAt(c.getLong(c.getColumnIndex(KEY_CREATED_AT)));
                td.setModifiedAt(c.getLong(c.getColumnIndex(KEY_MODIFIED_AT)));
                td.setRepeatOrder(c.getString(c.getColumnIndex(KEY_REPEAT_ORDER)));

                todos.add(td);
            } while (c.moveToNext());
        }catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        c.close();
        db.close();

        return todos;
    }

    public List<ModelMain> getAlarmNotesID(){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_TODO + " WHERE "
                + KEY_ALARM_STATE + " > 0 " + "AND " + KEY_STATUS + " = 0";
        List<ModelMain> ids = new ArrayList<>();

        Cursor cursor = db.rawQuery(selectQuery, null);
        try{
            if(cursor != null){
                cursor.moveToFirst();
                do{
                    ModelMain td = new ModelMain();
                    td.setTime(cursor.getLong(cursor.getColumnIndex(KEY_TIME)));
                    td.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                    td.setRepeatOrder(cursor.getString(cursor.getColumnIndex(KEY_REPEAT_ORDER)));
                    td.setAlarm(cursor.getInt(cursor.getColumnIndex(KEY_ALARM_STATE)));
                    td.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                    ids.add(td);
                }while(cursor.moveToNext());
            }
        }catch(IndexOutOfBoundsException e){
            e.printStackTrace();
            ModelMain model = new ModelMain();
            model.setTime(0);
            model.setAlarm(0);
            ids.add(model);
        }
        cursor.close();
        db.close();

        return ids;
    }

    public void deleteNoteById(long id){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("todos", "id" + " = ? ", new String[]{Long.toString(id)});
        db.close();
    }

    public void deleteNote(ModelMain sentToDo){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("todos",
                "id" + " = ? ",
                new String[] {Long.toString(sentToDo.getId())});
        db.close();

    }

    public void deleteShoppingList(ModelMain list){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("lista", "id = ?", new String[]{Long.toString(list.getId())});
        db.close();
    }

    public void deleteShoppingListById(long id){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("lista", "id = ?", new String[]{Long.toString(id)});
        db.close();
    }

    public void addShoppingList(String title, String list, String boxes){
        long created_at_time = currentTime();
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("shoptitle", title);
        values.put("shoppinglist", list);
        values.put("boxstate", boxes);
        values.put("viewtype", 2);
        values.put("status", 0);
        values.put("createdat", created_at_time);

        db.insert("lista", null, values);
        db.close();
    }

    public List<ModelMain> getAllShoppingLists(){
        SQLiteDatabase db = this.getReadableDatabase();

        String where = KEY_STATUS + " = 0";
        String sortOrder = KEY_CREATED_AT + " ASC";
        List<ModelMain> shoppingList = new ArrayList<>();

        Cursor c = db.query(TABLE_LISTA, null, where, null, null, null, sortOrder);
        try{
            if(c != null) {
                c.moveToFirst();

                do {
                    ModelMain sl = new ModelMain();
                    sl.setId(c.getLong(c.getColumnIndex(KEY_ID)));
                    sl.setShoppingTitle(c.getString(c.getColumnIndex(KEY_SHOP_TITLE)));
                    sl.setShopList(c.getString(c.getColumnIndex(KEY_SHOP_LIST)));
                    sl.setBoxState(c.getString(c.getColumnIndex(KEY_BOX_STATE)));
                    sl.setViewType(c.getInt(c.getColumnIndex(KEY_VIEW_TYPE)));
                    sl.setCreatedAt(c.getLong(c.getColumnIndex(KEY_CREATED_AT)));
                    sl.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
                    shoppingList.add(sl);
                } while (c.moveToNext());
            }
        }catch(IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        c.close();
        db.close();

        return shoppingList;
    }

    public List<ModelMain> getArchivedNotes(){
        SQLiteDatabase db = getReadableDatabase();

        String where = KEY_STATUS + " = 1";
        String sortOrder = KEY_CREATED_AT + " ASC";
        List<ModelMain> noteList = new ArrayList<>();

        Cursor c = db.query(TABLE_TODO, null, where, null, null, null, sortOrder);
        try {
            if (c != null) {
                c.moveToFirst();

                do {
                    ModelMain td = new ModelMain();
                    td.setViewType(c.getInt(c.getColumnIndex(KEY_VIEW_TYPE)));
                    td.setId(c.getLong(c.getColumnIndex(KEY_ID)));
                    td.setNote(c.getString(c.getColumnIndex(KEY_TODO)));
                    td.setTime(c.getLong(c.getColumnIndex(KEY_TIME)));
                    td.setNoteType(c.getInt(c.getColumnIndex(KEY_NOTE_TYPE)));
                    td.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
                    td.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
                    td.setAlarm(c.getInt(c.getColumnIndex(KEY_ALARM_STATE)));
                    td.setCreatedAt(c.getLong(c.getColumnIndex(KEY_CREATED_AT)));
                    td.setModifiedAt(c.getLong(c.getColumnIndex(KEY_MODIFIED_AT)));

                    noteList.add(td);
                } while (c.moveToNext());
            }
        }catch(IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        c.close();
        db.close();

        return noteList;
    }

    public List<ModelMain> getArchivedShoppingLists(){
        SQLiteDatabase db = getReadableDatabase();

        String where = KEY_STATUS + " = 1";
        String sortOrder = KEY_CREATED_AT + " ASC";
        List<ModelMain> shoppingList = new ArrayList<>();

        Cursor c = db.query(TABLE_LISTA, null, where, null, null, null, sortOrder);
        try{
            if(c != null) {
                c.moveToFirst();

                do {
                    ModelMain sl = new ModelMain();
                    sl.setId(c.getLong(c.getColumnIndex(KEY_ID)));
                    sl.setShoppingTitle(c.getString(c.getColumnIndex(KEY_SHOP_TITLE)));
                    sl.setShopList(c.getString(c.getColumnIndex(KEY_SHOP_LIST)));
                    sl.setBoxState(c.getString(c.getColumnIndex(KEY_BOX_STATE)));
                    sl.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
                    sl.setViewType(c.getInt(c.getColumnIndex(KEY_VIEW_TYPE)));
                    sl.setCreatedAt(c.getLong(c.getColumnIndex(KEY_CREATED_AT)));

                    shoppingList.add(sl);
                } while (c.moveToNext());
            }
        }catch(IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        c.close();
        db.close();

        return shoppingList;
    }

    public ModelMain getShoppingList(long id){
        SQLiteDatabase db = this.getReadableDatabase();
        ModelMain sl = new ModelMain();
        String selectQuery = "SELECT  * FROM " + TABLE_LISTA + " WHERE "
                + KEY_ID + " = " + id;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null) {
            c.moveToFirst();
            sl.setId(c.getLong(c.getColumnIndex("id")));
            sl.setShoppingTitle(c.getString(c.getColumnIndex("shoptitle")));
            sl.setShopList(c.getString(c.getColumnIndex("shoppinglist")));
            sl.setBoxState(c.getString(c.getColumnIndex("boxstate")));
            sl.setStatus(c.getInt(c.getColumnIndex("status")));
        }
        c.close();
        db.close();

        return sl;
    }

    public void updateNote(long id, ModelMain model){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TODO, model.getNote());
        values.put(KEY_TITLE, model.getTitle());
        values.put(KEY_NOTE_TYPE, model.getNoteType());
        values.put(KEY_VIEW_TYPE, 1);
        values.put(KEY_TIME, model.getTime());
        values.put(KEY_STATUS, model.getStatus());
        values.put(KEY_ALARM_STATE, model.getAlarm());
        values.put(KEY_MODIFIED_AT, currentTime());
        values.put(KEY_REPEAT_ORDER, model.getRepeatOrder());
        db.update(TABLE_TODO, values, KEY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void sendNoteToArchive(long id, ModelMain model){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TODO, model.getNote());
        values.put(KEY_TITLE, model.getTitle());
        values.put(KEY_NOTE_TYPE, model.getNoteType());
        values.put(KEY_VIEW_TYPE, 1);
        values.put(KEY_TIME, 0);
        values.put(KEY_STATUS, 1);
        values.put(KEY_ALARM_STATE, 0);
        values.put(KEY_MODIFIED_AT, currentTime());
        values.put(KEY_REPEAT_ORDER, "");
        db.update(TABLE_TODO, values, KEY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void updateShoppingList(ModelMain sentList){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SHOP_TITLE, sentList.getShoppingTitle());
        values.put(KEY_BOX_STATE, sentList.getBoxState());
        values.put(KEY_SHOP_LIST, sentList.getShopList());
        values.put(KEY_STATUS, sentList.getStatus());
        db.update(TABLE_LISTA, values, KEY_ID + " = ?", new String[]{Long.toString(sentList.getId())});
        db.close();
    }

    public boolean isItEmpty(){
        SQLiteDatabase db = getReadableDatabase();
        boolean is_empty = true;
        try {
            int numOfRows = (int) DatabaseUtils.queryNumEntries(db, "todos");
            if (numOfRows == 0) {
                db.close();
                is_empty = true;
            } else {
                db.close();
                is_empty = false;
            }
        }catch(NullPointerException e){
            e.printStackTrace();
            db.close();
            is_empty = true;
        }
        db.close();
        return is_empty;
    }

    public boolean isItEmptyList(){
        SQLiteDatabase db = getReadableDatabase();
        boolean is_empty = true;
        try{
            int numOfRows = (int) DatabaseUtils.queryNumEntries(db,"lista");
            if(numOfRows == 0){
                is_empty = true;
            }else{
                is_empty = false;
            }
        }catch (NullPointerException e){
            e.printStackTrace();
            is_empty = true;
        }
        db.close();
        return is_empty;
    }

    public long currentTime(){
        Calendar cal = Calendar.getInstance();

        return cal.getTimeInMillis();
    }
}

