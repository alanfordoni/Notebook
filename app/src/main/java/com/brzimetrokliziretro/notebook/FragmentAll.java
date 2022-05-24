package com.brzimetrokliziretro.notebook;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brzimetrokliziretro.notebook.adapters.NoteAdapter;
import com.brzimetrokliziretro.notebook.database.NotesData;
import com.brzimetrokliziretro.notebook.models.ModelMain;


import java.util.ArrayList;
import java.util.List;

public class FragmentAll extends Fragment {
    public static List<ModelMain> mainList;
    private static final String TAG = "bel.all_fragment";

    private NotesData notesData;
    private FloatingActionButton mAddTextNoteAll;
    private FloatingActionButton mAddShoppingList;
    private NoteAdapter noteAdapter;

    public FragmentAll() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int rv_parent = 1;
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recyclerview);
        mAddTextNoteAll = (FloatingActionButton)view.findViewById(R.id.fab_add_note_all);
        mAddShoppingList = (FloatingActionButton)view.findViewById(R.id.fab_add_shopping_list);

        mAddTextNoteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote();
            }
        });
        mAddShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addShoppingList();
            }
        });

        notesData = new NotesData(getActivity());
        mainList = new ArrayList<>();

            if (!notesData.isItEmpty()){
                mainList.addAll(notesData.getToDosAboveCurrentTime());
                mainList.addAll(notesData.getToDosRepeatingAlarm());
                mainList.addAll(notesData.getToDosBellowCurrentTime());
            }
            if(!notesData.isItEmptyList()) {
                mainList.addAll(notesData.getAllShoppingLists());
            }
            if(mainList.size() != 0) {
                noteAdapter = new NoteAdapter(mainList, rv_parent);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(noteAdapter);
            }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void addNote(){
        Intent addNoteIntent = new Intent(getActivity(), NoteActivity.class);
        startActivity(addNoteIntent);
        getActivity().finish();
    }

    public void addShoppingList(){
        Intent shoppingListIntent = new Intent(getActivity(), AddToDoListActivity.class);
        startActivity(shoppingListIntent);
        getActivity().finish();
    }
}
