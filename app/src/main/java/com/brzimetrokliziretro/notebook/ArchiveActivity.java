package com.brzimetrokliziretro.notebook;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.brzimetrokliziretro.notebook.adapters.NoteAdapter;
import com.brzimetrokliziretro.notebook.database.NotesData;
import com.brzimetrokliziretro.notebook.models.ModelMain;

import java.util.ArrayList;
import java.util.List;

public class ArchiveActivity extends ParentActivity {
    private static final String TAG = "BEL.archive";
    private NotesData mNotesData;
    private NoteAdapter mNoteAdapter;
    public static List<ModelMain> mainList;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        int rv_parent = 5;
        mToolbar = (Toolbar) findViewById(R.id.toolbar_archive);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview_arc);
        mNotesData = new NotesData(this);
        mainList = new ArrayList<>();
        try {
            if (mNotesData != null && !mNotesData.isItEmpty()) {
                mainList.addAll(mNotesData.getArchivedNotes());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Problem with loading notes for archive");
        }

        try {
            if (mNotesData != null && !mNotesData.isItEmptyList()) {
                mainList.addAll(mNotesData.getArchivedShoppingLists());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Problem with loading lists for archive");
        }
        if (mainList.size() != 0) {
            mNoteAdapter = new NoteAdapter(mainList, rv_parent);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(mNoteAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
