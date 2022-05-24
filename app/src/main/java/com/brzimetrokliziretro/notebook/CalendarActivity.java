package com.brzimetrokliziretro.notebook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.brzimetrokliziretro.notebook.database.NotesData;
import com.brzimetrokliziretro.notebook.models.CalendarDays;
import com.brzimetrokliziretro.notebook.models.ModelMain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalendarActivity extends ParentActivity {
    private static final String TAG = "BEL.calendar";
    public static final String ID = "extra_id";
    public static final String RV_PARENT_NUMBER = "rv_parent";

    private CalendarView mCalendarView;
    private ListView mListView;
    private List<String> titleList;
    private ArrayAdapter<String> mAdapter;
    private NotesData mNotesData;
    private List<ModelMain> list;
    private int parentNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        mCalendarView = (CalendarView)findViewById(R.id.calendar_view);
        mListView = (ListView)findViewById(R.id.calendar_listview);

        mCalendarView.showCurrentMonthPage();
        parentNumber = 10;
        titleList = new ArrayList<>();
        mAdapter = new ArrayAdapter<String>(CalendarActivity.this, android.R.layout.simple_list_item_1
        , android.R.id.text1, titleList);

        mNotesData = new NotesData(this);

        mCalendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                mAdapter.clear();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
                Date dateformat = eventDay.getCalendar().getTime();
                String date = sdf.format(dateformat);
                list = mNotesData.getTodosWithChosenDate(date);

                for(int i = 0; i < list.size(); i++){
                    titleList.add(list.get(i).getTitle());
                }

                mListView.setAdapter(mAdapter);
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(CalendarActivity.this, NoteActivity.class);
                intent.putExtra(ID, list.get(i).getId());
                intent.putExtra(RV_PARENT_NUMBER, parentNumber);
                startActivity(intent);
            }
        });

        List<CalendarDays> calendarDays = new ArrayList<>();
        calendarDays.addAll(mNotesData.getAllSingleOccuringAlarms());
        calendarDays.addAll(mNotesData.getAllNoTimeSetTodos());
        List<EventDay> mEventDays = new ArrayList<>();

        for (int i = 0; i < calendarDays.size(); i++){
        EventDay event = new EventDay(calendarDays.get(i).getDate(), R.drawable.ic_event_note_black_24dp);
        mEventDays.add(event);
        }

        mCalendarView.setEvents(mEventDays);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
