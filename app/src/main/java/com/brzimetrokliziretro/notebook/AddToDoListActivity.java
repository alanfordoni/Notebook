package com.brzimetrokliziretro.notebook;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.Nullable;

import com.brzimetrokliziretro.notebook.database.NotesData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AddToDoListActivity extends ParentActivity {
    private static final String TAG = "BEL.addtodo";
    private LinearLayout mLinearLayout;
    private Button mBtnAddItem;
    private FloatingActionButton mBtnSaveList;
    private EditText etTitle;

    List<EditText> list;
    String[] textList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo_list);

        mBtnAddItem = (Button) findViewById(R.id.btn_add_item);
        mLinearLayout = (LinearLayout) findViewById(R.id.linear_layout);
        mBtnSaveList = (FloatingActionButton) findViewById(R.id.btn_save_shop_list);
        etTitle = (EditText) findViewById(R.id.shop_list_title);
        list = new ArrayList<>();

    }

    public void addItem(View view) {
        mLinearLayout.addView(tableLayout());
    }

    public void saveList(View v) {
        int size = list.size();
        if (size != 0) {
            textList = new String[size];
            for (int i = 0; i < size; i++) {
                textList[i] = list.get(i).getText().toString();
            }
            boolean empty = true;
            for (String s : textList) {
                if (s != null && !TextUtils.isEmpty(s)) {
                    empty = false;
                    break;
                }
            }
            if (textList != null && !empty) {
                saveToDo();
            } else {
                Toast.makeText(AddToDoListActivity.this, R.string.empty_todo_list_message_save, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(AddToDoListActivity.this, R.string.empty_todo_list_message_save, Toast.LENGTH_SHORT).show();
        }
    }

    public TableLayout tableLayout() {
        TableLayout tableLayout = new TableLayout(this);
        TableRow tableRow = new TableRow(this);
        tableRow.setPadding(0, 10, 0, 0);

        CheckBox checkBox = new CheckBox(this);
        checkBox.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
        //  checkBox.setGravity(Gravity.CENTER);
        tableRow.addView(checkBox);

        EditText editText = new EditText(this);
        editText.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 5.0f));
        //  editText.setMaxLines(1);
        //  editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.requestFocus();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            editText.setBackgroundColor(getColor(R.color.colorTransparent));
        } else {
            editText.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
        }
        tableRow.addView(editText);
        list.add(editText);

        ImageButton btnDelete = new ImageButton(this);
        btnDelete.setImageResource(R.drawable.ic_close_black_24dp);
        btnDelete.setBackgroundColor(Color.argb(1, 255, 255, 255));
        btnDelete.setOnClickListener(imageButtonDeleteListener);
        btnDelete.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
        tableRow.addView(btnDelete);

        View v = new View(this);
        v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            v.setBackgroundColor(getResources().getColor(R.color.colorDarkGray, null));
        } else {
            v.setBackgroundColor(getResources().getColor(R.color.colorDarkGray));
        }
        tableLayout.addView(tableRow);
        tableLayout.addView(v);

        return tableLayout;
    }

    private View.OnClickListener imageButtonDeleteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TableRow tableRow = (TableRow) v.getParent();
            TableLayout table = (TableLayout) tableRow.getParent();
            mLinearLayout.removeView(table);
            list.remove(tableRow.getChildAt(1));
        }
    };

    private void saveToDo() {
        String list[] = new String[textList.length];
        int boxes[] = new int[textList.length];
        for (int i = 0; i < textList.length; i++) {
            list[i] = textList[i] + "Q007Q";
            boxes[i] = 0;
        }
        StringBuilder builder1 = new StringBuilder();
        for (int s : boxes) {
            builder1.append(s + "Q");
        }
        String boxesStates = builder1.toString();
        StringBuilder builder = new StringBuilder();
        for (String s : list) {
            builder.append(s);
        }
        String string = builder.toString();
        String title = etTitle.getText().toString();
        NotesData notesData = new NotesData(this);
        notesData.addShoppingList(title, string, boxesStates);

        backToMain();
    }

    public void backToMain() {
        Intent intent = new Intent(AddToDoListActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        backToMain();
    }
}
