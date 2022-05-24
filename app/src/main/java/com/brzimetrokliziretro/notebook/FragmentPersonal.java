package com.brzimetrokliziretro.notebook;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brzimetrokliziretro.notebook.adapters.NoteAdapter;
import com.brzimetrokliziretro.notebook.database.NotesData;
import com.brzimetrokliziretro.notebook.models.ModelMain;

import java.util.ArrayList;
import java.util.List;

public class FragmentPersonal extends Fragment {
    public static final String NOTE_TYPE = "note_type";
    public static List<ModelMain> notesListPersonal;

    private static final String TAG = "bel.personal_frag";

    private RecyclerView recyclerView;
    private NotesData notesData;
    private NoteAdapter noteAdapter;
    private FloatingActionButton mAddNote;

    public FragmentPersonal() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notes, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int rv_parent = 2;
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerview_pn);
        mAddNote = (FloatingActionButton)view.findViewById(R.id.fab_add_note_pb);
        notesData = new NotesData(getActivity());

        mAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote();
            }
        });
        notesListPersonal = new ArrayList<>();
        try {
            if (!notesData.isItEmpty()) {
                notesListPersonal.addAll(notesData.getPerAboveCurrentTime());
                notesListPersonal.addAll(notesData.getPerRepeatingAlarm());
                notesListPersonal.addAll(notesData.getPerBellowCurrentTime());
                noteAdapter = new NoteAdapter(notesListPersonal, rv_parent);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(noteAdapter);
            }
        }catch(Exception e){
            e.printStackTrace();
            Log.d(TAG, "Problem with loading data in personal fragment");
        }
    }

    private void addNote(){
        Intent addNoteIntent = new Intent(getActivity(), NoteActivity.class);
        addNoteIntent.putExtra(NOTE_TYPE, 1);
        startActivity(addNoteIntent);
        getActivity().finish();
    }
}
