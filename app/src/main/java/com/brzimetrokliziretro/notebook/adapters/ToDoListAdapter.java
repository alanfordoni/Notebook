package com.brzimetrokliziretro.notebook.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.brzimetrokliziretro.notebook.MainActivity;
import com.brzimetrokliziretro.notebook.R;
import com.brzimetrokliziretro.notebook.database.NotesData;
import com.brzimetrokliziretro.notebook.models.ModelMain;
import com.brzimetrokliziretro.notebook.models.ShoppingList;

import java.util.ArrayList;
import java.util.List;

public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ToDoListViewHolder> {
    private static final String TAG = "BEL.ToDoListAdapter";
    public static final String EXTRA_ID = "extra_id";

    private List<ShoppingList> mainList;
    private long id = 0;
    private NotesData mNotesData;
    private Context context;
    private String title;

    public ToDoListAdapter() {

    }

    public ToDoListAdapter(List<ShoppingList> mainList, long id, Context c, String title) {
        this.mainList = mainList;
        this.id = id;
        this.context = c;
        this.title = title;
    }

    @Override
    public int getItemCount() {
        if (mainList == null || mainList.isEmpty()) {
            return 0;
        } else
            return mainList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public ToDoListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_todo_list_detail, parent, false);
        return new ToDoListViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoListViewHolder holder, int position) {
        if (mainList != null && !mainList.isEmpty()) {
            ShoppingList listItem = mainList.get(position);
            holder.chbox.setChecked(listItem.getBox());
            holder.tv_text.setText(listItem.getText());
            if (listItem.getBox()){
                holder.tv_text.setPaintFlags(holder.tv_text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }
    }

    public class ToDoListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private NotesData notesData;
        private ShoppingList sentList;
        private CheckBox chbox;
        private TextView tv_text;
        private ImageButton btn;
        private EditText editText;
        private String changed_text;

        public ToDoListViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            chbox = (CheckBox) itemLayoutView.findViewById(R.id.todo_list_chbox);
            tv_text = (TextView) itemLayoutView.findViewById(R.id.todo_list_tv);
            btn = (ImageButton) itemLayoutView.findViewById(R.id.todo_list_btn_delete);
            chbox.setOnClickListener(this);
            btn.setOnClickListener(this);
            tv_text.setOnClickListener(this);
            changed_text = "";
            //  itemLayoutView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v instanceof ImageButton) {
                mainList.remove(getAdapterPosition());
                if(mainList == null || mainList.isEmpty()){
                    mNotesData = new NotesData(itemView.getContext());
                    mNotesData.deleteShoppingListById(id);
                    Intent intent = new Intent(itemView.getContext(), MainActivity.class);
                    itemView.getContext().startActivity(intent);
                }
                notifyDataSetChanged();
            } else if (v instanceof CheckBox) {
                int adapter_pos = getAdapterPosition();
                if (!mainList.get(adapter_pos).getBox()) {
                    mainList.get(adapter_pos).setBox(true);
                    tv_text.setPaintFlags(tv_text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    mainList.get(adapter_pos).setBox(false);
                    tv_text.setPaintFlags(tv_text.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }
            } else if (v instanceof TextView) {
                String text = mainList.get(getAdapterPosition()).getText();
                textDialog(text);
            }
        }

        public void textDialog(String text) {
            LayoutInflater inflater = (LayoutInflater) itemView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View view = inflater.inflate(R.layout.edit_text_dialog, null, false);
            editText = (EditText) view.findViewById(R.id.et_dialog);
            editText.setText(text);

            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext(), R.style.DialogStyle);
            builder.setView(view)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            changed_text = editText.getText().toString();
                            mainList.get(getAdapterPosition()).setText(changed_text);
                            notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog d = builder.create();
            d.setContentView(R.layout.dialogset);
            d.show();

            editText.requestFocus();
        }
    }

        public void addItem() {
            ShoppingList list_item = new ShoppingList();
            list_item.setBox(false);
            list_item.setText(" ");
            if(mainList == null || mainList.isEmpty()){
                mainList = new ArrayList<ShoppingList>();
                mainList.add(list_item);
            }else{
                mainList.add(list_item);
            }
            notifyDataSetChanged();
        }

        public void saveList() {
            mNotesData = new NotesData(context);
            String[] boxes = new String[mainList.size()];
            String[] texts = new String[mainList.size()];

            for (int i = 0; i < mainList.size(); i++) {
                boolean abc = mainList.get(i).getBox();
                boxes[i] = abc ? "1" : "0";
                texts[i] = mainList.get(i).getText();
            }
            StringBuilder builder1 = new StringBuilder();
            StringBuilder builder2 = new StringBuilder();
            for (int i = 0; i < mainList.size(); i++) {
                builder1.append(boxes[i] + "Q");
                builder2.append(texts[i] + "Q007Q");
            }
            String box = builder1.toString();
            String text = builder2.toString();
            ModelMain model = new ModelMain();
            model.setId(id);
            model.setBoxState(box);
            model.setShopList(text);
            model.setShoppingTitle(title);
            model.setStatus(0);
            mNotesData.updateShoppingList(model);
        }

        public boolean isListEmpty(){
            if(mainList == null || mainList.isEmpty()){
                return true;
            }else{
                return false;
            }
        }
    }

