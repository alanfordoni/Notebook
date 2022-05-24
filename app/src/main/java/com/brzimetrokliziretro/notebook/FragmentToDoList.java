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

public class FragmentToDoList extends Fragment {
    private static final String TAG = "bel.all_fragment";

    public static List<ModelMain> shopping_list;
    private FloatingActionButton mAddShoppingList;
    private NotesData mNotesData;
    private NoteAdapter mNoteAdapter;

    public FragmentToDoList() {
        // Required empty public constructor
    }

    public static FragmentToDoList newInstance() {
        FragmentToDoList fragment = new FragmentToDoList();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_todo_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        int rv_parent = 4;
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recyclerview_sl);

        mAddShoppingList = (FloatingActionButton)view.findViewById(R.id.fab_add_shopping_list_sl);
        mAddShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addShoppingList();
            }
        });

        mNotesData = new NotesData(getActivity());
        shopping_list = new ArrayList<>();
        try{
            if(!mNotesData.isItEmptyList()) {
                shopping_list.addAll(mNotesData.getAllShoppingLists());
            }
            if(shopping_list.size() != 0) {
                mNoteAdapter = new NoteAdapter(shopping_list, rv_parent);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(mNoteAdapter);
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "Problem with loading data in shopping_list fragment");
        }
    }

    public void addShoppingList(){
        Intent shoppingListIntent = new Intent(getActivity(), AddToDoListActivity.class);
        startActivity(shoppingListIntent);
        getActivity().finish();
    }
}
