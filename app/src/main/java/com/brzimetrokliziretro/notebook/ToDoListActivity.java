package com.brzimetrokliziretro.notebook;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.brzimetrokliziretro.notebook.adapters.ToDoListAdapter;
import com.brzimetrokliziretro.notebook.database.NotesData;
import com.brzimetrokliziretro.notebook.models.ModelMain;
import com.brzimetrokliziretro.notebook.models.ShoppingList;

import java.util.ArrayList;
import java.util.List;

public class ToDoListActivity extends ParentActivity {
    private static final String TAG = "BEL.TodoList";

    private static List<ShoppingList> list;
    private ModelMain mModel;
    private NotesData mNotesData;
    private TextView tv_title;
    private ToDoListAdapter mToDoListAdapter;
    private Button btn_save, btn_add_item;
    private Toolbar mToolbar;
    private ImageButton btn_trash;
    private long id = 0;
    private int rv_parent_num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);

        Log.d(TAG, "onCreate: ");

        mToolbar = (Toolbar)findViewById(R.id.toolbar_show_list);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tv_title = (TextView)findViewById(R.id.text_view);
        RecyclerView mRecycler = (RecyclerView)findViewById(R.id.shopping_rv);

        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        if(bd != null){
            id = (long) bd.getLong("extra_id");
            rv_parent_num = bd.getInt("rv_parent");
        }
        mNotesData = new NotesData(this);
        mModel = mNotesData.getShoppingList(id);
        if(mModel != null && !TextUtils.isEmpty(mModel.getBoxState())) {
            String box = mModel.getBoxState();
            String title = mModel.getShoppingTitle();
            String text = mModel.getShopList();

            String[] textlist;
            textlist = text.split("Q007Q");
            String[] boxes_string = box.split("Q");
            int[] boxes = new int[boxes_string.length];
            boolean boxesStates[] = new boolean[boxes.length];
            for (int i = 0; i < boxes_string.length; i++) {
                boxes[i] = Integer.parseInt(boxes_string[i]);
                boxesStates[i] = boxes[i] == 1 ? true : false;
            }
            list = new ArrayList<ShoppingList>();
            for (int i = 0; i < textlist.length; i++) {
                ShoppingList sl = new ShoppingList();
                sl.setBox(boxesStates[i]);
                sl.setText(textlist[i]);
                list.add(sl);
            }
            tv_title.setText(title);

            if (list != null) {
                if (!list.isEmpty()) {
                    mToDoListAdapter = new ToDoListAdapter(list, id, this, title);
                    mRecycler.setLayoutManager(new LinearLayoutManager(this));
                    mRecycler.setHasFixedSize(true);
                    mRecycler.setAdapter(mToDoListAdapter);
                    mRecycler.setFocusable(false);
                    ViewCompat.setNestedScrollingEnabled(mRecycler, false);
                }
            }
        }

        btn_trash = (ImageButton)findViewById(R.id.trash_sl);
        btn_save = (Button) findViewById(R.id.list_save);
        btn_add_item = (Button) findViewById(R.id.list_add_item);
        btn_add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(!mToDoListAdapter.isListEmpty()) {
                        mToDoListAdapter.addItem();
                    }
                }catch(Exception e){
                    Toast.makeText(ToDoListActivity.this, getString(R.string.empty_todo_list_message_add), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rv_parent_num == 5){
                    Toast.makeText(ToDoListActivity.this, R.string.archived_items_message, Toast.LENGTH_SHORT).show();
                }else {
                    try{
                        if(!mToDoListAdapter.isListEmpty()) {
                            mToDoListAdapter.saveList();
                            Log.d(TAG, "savelist method started");
                        }
                    }catch (Exception e) {
                        Toast.makeText(ToDoListActivity.this, R.string.empty_todo_list_message_save, Toast.LENGTH_SHORT).show();
                    }
                    backToMain();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.show_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if(item_id == R.id.trash_sl){
            AlertDialog.Builder builder = new AlertDialog.Builder(ToDoListActivity.this);
            builder.setMessage("Do you want to delete this list?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mNotesData.deleteShoppingListById(id);
                    backToMain();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return true;
    }

    public void backToMain(){
        if(rv_parent_num == 5) {
            Intent intent = new Intent(ToDoListActivity.this, ArchiveActivity.class);
            startActivity(intent);
            finish();
        }else {
            Intent intent = new Intent(ToDoListActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backToMain();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
