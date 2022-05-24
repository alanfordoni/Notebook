package com.brzimetrokliziretro.notebook.adapters;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brzimetrokliziretro.notebook.ArchiveActivity;
import com.brzimetrokliziretro.notebook.FragmentAll;
import com.brzimetrokliziretro.notebook.FragmentBusiness;
import com.brzimetrokliziretro.notebook.FragmentPersonal;
import com.brzimetrokliziretro.notebook.FragmentToDoList;
import com.brzimetrokliziretro.notebook.NoteActivity;
import com.brzimetrokliziretro.notebook.R;
import com.brzimetrokliziretro.notebook.ToDoListActivity;
import com.brzimetrokliziretro.notebook.broadcast.AlarmReceiver;
import com.brzimetrokliziretro.notebook.database.NotesData;
import com.brzimetrokliziretro.notebook.models.ModelMain;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NoteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "BEL.NoteAdapter";

    public static final String EXTRA_ID = "extra_id";
    public static final String RV_PARENT_NUMBER = "rv_parent";

    private List<ModelMain> mainList;
    private Context context;
    private int rv_parent_id = 0;

    public NoteAdapter(){}

    public NoteAdapter(List<ModelMain> mainList, int parent_number) {
        this.mainList = mainList;
        this.rv_parent_id = parent_number;
    }

    @Override
    public long getItemId(int position) {
        return (position);
    }

    @Override
    public int getItemViewType(int position) {
        switch(mainList.get(position).getViewType()) {
            case 1:
                return ModelMain.TYPE_NOTE;
            case 2:
                return ModelMain.TYPE_SHOPPING_LIST;
            default:
                return -1;
        }
    }

    @Override
    public int getItemCount() {
        if(mainList == null){
            return 0;
        }else
            return mainList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch(viewType){
            case ModelMain.TYPE_NOTE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_note, parent, false);
                return new NoteAdapterViewHolder(view);
            case ModelMain.TYPE_SHOPPING_LIST:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_todo_list, parent, false);
                return new ShoppingListViewHolder(view);
            default:
                break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(mainList != null || !mainList.isEmpty()) {
            ModelMain listItem = mainList.get(position);
            if (listItem != null) {
                switch (listItem.getViewType()) {
                    case ModelMain.TYPE_NOTE:
                        DateTime date = new DateTime(listItem.getTime());
                        DateTime created_date = new DateTime(listItem.getCreatedAt());
                        DateTime modified_date = new DateTime(listItem.getModifiedAt());

                        String note_date, created_at, modified_at;

                        if (Locale.getDefault().toString().equalsIgnoreCase("US")){
                            note_date = date.toString(DateTimeFormat.forPattern("MM/dd/yy"));
                            created_at = created_date.toString(DateTimeFormat.forPattern("MM/dd/yy"));
                            modified_at = modified_date.toString(DateTimeFormat.forPattern("MM/dd/yy"));
                        }else{
                            note_date = date.toString(DateTimeFormat.forPattern("dd/MM/yy"));
                            created_at = created_date.toString(DateTimeFormat.forPattern("dd/MM/yy"));
                            modified_at = modified_date.toString(DateTimeFormat.forPattern("dd/MM/yy"));
                        }

                            SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm");
                            String note_time = sdf_time.format(listItem.getTime());

                            ((NoteAdapterViewHolder) holder).tv_title.setText(listItem.getTitle());
                            ((NoteAdapterViewHolder) holder).tv_note.setText(listItem.getNote());
                            if(listItem.getModifiedAt() < 1){
                                ((NoteAdapterViewHolder) holder).tv_created_modified.setText(holder.itemView
                                        .getContext().getResources().getString(R.string.created_at)
                                        + " " + created_at);
                            }else {
                                ((NoteAdapterViewHolder) holder).tv_created_modified.setText(holder.itemView
                                        .getContext().getResources().getString(R.string.created_modified_at)
                                        + " " + created_at + "  " + modified_at);
                            }
                            if(listItem.getNoteType() == 1){
                                ((NoteAdapterViewHolder) holder).tv_note_type.setText(holder.itemView
                                        .getContext().getResources().getString(R.string.bt_personal));
                            }else{
                                ((NoteAdapterViewHolder) holder).tv_note_type.setText(holder.itemView
                                .getContext().getResources().getString(R.string.bt_business));
                            }
                            if (listItem.getAlarm() == 2) {
                                ((NoteAdapterViewHolder) holder).tv_date.setVisibility(View.VISIBLE);
                                ((NoteAdapterViewHolder) holder).tv_time.setVisibility(View.VISIBLE);
                                String days[] = holder.itemView.getContext().getResources()
                                        .getStringArray(R.array.days_short);
                                String week_days = getWeekDays(days, listItem);
                                ((NoteAdapterViewHolder) holder).tv_date.setText(week_days);
                                ((NoteAdapterViewHolder) holder).tv_date.setTextColor(Color.rgb(40, 40, 40));
                                ((NoteAdapterViewHolder) holder).tv_time.setTextColor(Color.rgb(255, 0, 0));
                                ((NoteAdapterViewHolder) holder).tv_time.setText(note_time);

                            }else if(listItem.getAlarm() == 1) {
                                ((NoteAdapterViewHolder) holder).tv_date.setVisibility(View.VISIBLE);
                                ((NoteAdapterViewHolder) holder).tv_time.setVisibility(View.VISIBLE);
                                if (listItem.getTime() > 1 && listItem.getTime() <= System.currentTimeMillis()) {
                                    ((NoteAdapterViewHolder) holder).tv_date.setText(note_date);
                                    ((NoteAdapterViewHolder) holder).tv_time.setText(note_time);
                                    ((NoteAdapterViewHolder) holder).tv_time.setTextColor(Color.rgb(40, 40, 40));
                                    ((NoteAdapterViewHolder) holder).tv_date.setTextColor(Color.rgb(40, 40, 40));

                                } else if (listItem.getTime() > 1 && listItem.getTime() > System.currentTimeMillis()) {
                                    ((NoteAdapterViewHolder) holder).tv_date.setTextColor(Color.rgb(40, 40, 40));
                                    ((NoteAdapterViewHolder) holder).tv_time.setTextColor(Color.rgb(255, 0, 0));
                                    ((NoteAdapterViewHolder) holder).tv_date.setText(note_date);
                                    ((NoteAdapterViewHolder) holder).tv_time.setText(note_time);
                                }
                            }else{
                                ((NoteAdapterViewHolder) holder).tv_date.setVisibility(View.INVISIBLE);
                                ((NoteAdapterViewHolder) holder).tv_time.setVisibility(View.INVISIBLE);
                            }
                        break;
                    case ModelMain.TYPE_SHOPPING_LIST:
                            String text = mainList.get(position).getShopList();
                            String[] textArray = new String[10];
                            String[] textlist;
                            textlist = text.split("Q007Q");
                            int k = textlist.length;
                            if (k > 0) {
                                for (int i = 0; i < 5; i++) {
                                    if (i < k) {
                                        textArray[i] = textlist[i];
                                    } else {
                                        textArray[i] = " ";
                                    }
                                }
                            }
                            String box = mainList.get(position).getBoxState().trim();
                            String boxes[] = box.split("Q");
                            boolean boxesStates[] = new boolean[10];
                            int g = boxes.length;
                            if (g > 0) {
                                for (int i = 0; i < 5; i++) {
                                    if (i < g) {
                                        int d = Integer.parseInt(boxes[i]);
                                        boxesStates[i] = d == 1 ? true : false;
                                    } else {
                                        boxesStates[i] = false;
                                    }
                                }
                            }
                            ((ShoppingListViewHolder) holder).title.setText(listItem.getShoppingTitle());
                            for (int i = 0; i < ((ShoppingListViewHolder)holder).boxes_array.length; i++){
                                ((ShoppingListViewHolder)holder).boxes_array[i].setChecked(boxesStates[i]);
                                ((ShoppingListViewHolder)holder).boxes_array[i].setText(textArray[i]);
                                if (boxesStates[i]){
                                    ((ShoppingListViewHolder)holder).boxes_array[i].setPaintFlags(
                                            ((ShoppingListViewHolder)holder).boxes_array[i].getPaintFlags()
                                            | Paint.STRIKE_THRU_TEXT_FLAG);
                                }
                            }
                        break;
                }
            }
        }
    }

    public String getWeekDays(String[] shortdays, ModelMain item){
        String days = item.getRepeatOrder();
        String[] days_split = days.split("Q");
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < days_split.length; i++){
            if(i == 0 && days_split[i].equals("true")){
                builder.append(shortdays[i] + ", ");
            }else if(i == 1 && days_split[i].equals("true")){
                builder.append(shortdays[i] + ", ");
            }else if(i == 2 && days_split[i].equals("true")){
                builder.append(shortdays[i] + ", ");
            }else if(i == 3 && days_split[i].equals("true")){
                builder.append(shortdays[i] + ", ");
            }else if(i == 4 && days_split[i].equals("true")){
                builder.append(shortdays[i] + ", ");
            }else if(i == 5 && days_split[i].equals("true")){
                builder.append(shortdays[i] + ", ");
            }else if(i == 6 && days_split[i].equals("true")){
                builder.append(shortdays[i]);
            }
        }
        String week_days = builder.toString();
        if(days_split[6].equals("false")){
            week_days = week_days.substring(0, week_days.length() - 2);
        }
        if(week_days.equalsIgnoreCase("Sun, Mon, Tue, Wed, Thu, Fri, Sat")){
            week_days = "Everyday";
        }
        if(week_days.equalsIgnoreCase("Ned, Pon, Uto, Sre, Čet, Pet, Sub")){
            week_days = "Svakog dana";
        }
        if(week_days.equalsIgnoreCase("Ned, Pon, Uto, Sri, Čet, Pet, Sub")){
            week_days = "Svakog dana";
        }
        if (week_days.equalsIgnoreCase("Son, Mon, Die, Mit, Don, Fre, Sam")){
            week_days = "Jeden Tag";
        }
        return week_days;
    }

    public void updateAll(List<ModelMain> list){
        if(mainList.size() != 0) {
            mainList.clear();
        }else{
            mainList = new ArrayList<>();
        }
        mainList.addAll(list);
        notifyDataSetChanged();
    }

    public void updateAllFromAddNote(List<ModelMain> newlist){
        mainList = new ArrayList<>();
        if(mainList.size() != 0)
            mainList.clear();
        mainList.addAll(newlist);
        notifyDataSetChanged();
    }

    public void updateAllShowList(List<ModelMain> list){
        mainList = new ArrayList<>();
        if(mainList.size() != 0)
            mainList.clear();

        mainList.addAll(list);
        notifyDataSetChanged();
    }

    public class NoteAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private NotesData notesData;
        private ModelMain modelMain;
        private ModelMain sentToDo;

        public TextView tv_title, tv_note, tv_date, tv_time, tv_created_modified, tv_note_type;

        public NoteAdapterViewHolder(View itemLayoutView){
            super(itemLayoutView);
            tv_title = (TextView)itemLayoutView.findViewById(R.id.title_rv);
            tv_note = (TextView)itemLayoutView.findViewById(R.id.note_rv);
            tv_date = (TextView)itemLayoutView.findViewById(R.id.date_rv);
            tv_time = (TextView)itemLayoutView.findViewById(R.id.time_rv);
            tv_created_modified = (TextView)itemLayoutView.findViewById(R.id.date_created_modified);
            tv_note_type = (TextView)itemLayoutView.findViewById(R.id.note_type);
            itemLayoutView.setOnClickListener(this);
            itemLayoutView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ModelMain noteSelected = mainList.get(getAdapterPosition());
            long id = noteSelected.getId();

            Intent note_intent = new Intent(v.getContext(), NoteActivity.class);
            note_intent.putExtra(EXTRA_ID, id);
            note_intent.putExtra(RV_PARENT_NUMBER, rv_parent_id);
            v.getContext().startActivity(note_intent);
        }

        @Override
        public boolean onLongClick(View v) {
            if (rv_parent_id == 1) {
                modelMain = FragmentAll.mainList.get(getAdapterPosition());
                sentToDo = modelMain;
                showDialog(sentToDo);
            } else if (rv_parent_id == 2) {
                modelMain = FragmentPersonal.notesListPersonal.get(getAdapterPosition());
                sentToDo = modelMain;
                showDialog(sentToDo);
            } else if (rv_parent_id == 3) {
                modelMain = FragmentBusiness.notesListBusiness.get(getAdapterPosition());
                sentToDo = modelMain;
                showDialog(sentToDo);
            } else if (rv_parent_id == 5) {
                modelMain = ArchiveActivity.mainList.get(getAdapterPosition());
                sentToDo = modelMain;
                showArcDialog(sentToDo);
            }
            return false;
        }

        public void showDialog(final ModelMain sentToDo){
            new androidx.appcompat.app.AlertDialog.Builder(itemView.getContext())
                    .setPositiveButton(R.string.alert_dialog_delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            aDialogDeleteClick(sentToDo);
                            dialog.dismiss();
                        }
                    })
                    .setNeutralButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.alert_dialog_archive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            aDialogArchiveClick(sentToDo);
                            dialog.dismiss();
                        }
                    })
                    .show();
        }

        public void showArcDialog(final ModelMain sentToDo){
            new androidx.appcompat.app.AlertDialog.Builder(itemView.getContext())
                    .setPositiveButton(R.string.alert_dialog_delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            aDialogDeleteClick(sentToDo);
                            dialog.dismiss();
                        }
                    })
                    .setNeutralButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.alert_dialog_dearchive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            aDialogDeArchive(sentToDo);
                            dialog.dismiss();
                        }
                    })
                    .show();
        }

        public void aDialogDeleteClick(ModelMain toDo){
            int cancel_id = (int) toDo.getId();
            Intent cancel_intent = new Intent(itemView.getContext(), AlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(itemView.getContext(), cancel_id, cancel_intent ,PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager)itemView.getContext().getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pi);

            notesData = new NotesData(itemView.getContext());
            notesData.deleteNote(toDo);
            refreshList();
        }

        public void aDialogDeArchive(ModelMain sentToDo){
            sentToDo.setStatus(0);
            notesData = new NotesData(itemView.getContext());
            notesData.updateNote(sentToDo.getId(), sentToDo);
            try {
                ArchiveActivity.mainList = new ArrayList<>();
                ArchiveActivity.mainList.addAll(notesData.getArchivedNotes());
                ArchiveActivity.mainList.addAll(notesData.getArchivedShoppingLists());
                updateAll(ArchiveActivity.mainList);
            }catch (NullPointerException e){
                Log.d(TAG, "NPI throwed in dearchive method");
                e.printStackTrace();
            }
        }

        public void refreshList(){
            if(rv_parent_id == 1){
                try {
                    FragmentAll.mainList = new ArrayList<>();
                    FragmentAll.mainList.addAll(notesData.getToDosAboveCurrentTime());
                    FragmentAll.mainList.addAll(notesData.getToDosRepeatingAlarm());
                    FragmentAll.mainList.addAll(notesData.getToDosBellowCurrentTime());
                    FragmentAll.mainList.addAll(notesData.getAllShoppingLists());
                    updateAll(FragmentAll.mainList);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

            }
            else if(rv_parent_id == 2){
                try{
                    FragmentPersonal.notesListPersonal = new ArrayList<>();
                    FragmentPersonal.notesListPersonal.addAll(notesData.getPerAboveCurrentTime());
                    FragmentPersonal.notesListPersonal.addAll(notesData.getPerRepeatingAlarm());
                    FragmentPersonal.notesListPersonal.addAll(notesData.getPerBellowCurrentTime());
                    updateAll(FragmentPersonal.notesListPersonal);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

            }
            else if(rv_parent_id == 3){
                try{
                    FragmentBusiness.notesListBusiness = new ArrayList<>();
                    FragmentBusiness.notesListBusiness.addAll(notesData.getBusAboveCurrentTime());
                    FragmentBusiness.notesListBusiness.addAll(notesData.getBusRepeatingAlarm());
                    FragmentBusiness.notesListBusiness.addAll(notesData.getBusBellowCurrentTime());
                    updateAll(FragmentBusiness.notesListBusiness);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

            }else if(rv_parent_id == 5){
                try {
                    ArchiveActivity.mainList = new ArrayList<>();
                    ArchiveActivity.mainList.addAll(notesData.getArchivedNotes());
                    ArchiveActivity.mainList.addAll(notesData.getArchivedShoppingLists());
                    updateAll(ArchiveActivity.mainList);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }

        public void aDialogArchiveClick(ModelMain toDo){
            int cancel_id = (int) toDo.getId();
            Intent cancel_intent = new Intent(itemView.getContext(), AlarmReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(itemView.getContext(), cancel_id, cancel_intent ,PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager)itemView.getContext().getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pi);

            notesData = new NotesData(itemView.getContext());
            toDo.setStatus(1);
            toDo.setTime(0);
            toDo.setAlarm(0);
            notesData.sendNoteToArchive(toDo.getId(), toDo);
            refreshList();
        }
    }

    public class ShoppingListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private NotesData notesData;
        private ModelMain sentList;

        private TextView title;
        private CheckBox[] boxes_array;

        public ShoppingListViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            title = (TextView) itemLayoutView.findViewById(R.id.checkbox_list_title);
            boxes_array = new CheckBox[5];
            boxes_array[0] = (CheckBox) itemLayoutView.findViewById(R.id.checkbox_rv);
            boxes_array[1] = (CheckBox) itemLayoutView.findViewById(R.id.checkbox2_rv);
            boxes_array[2] = (CheckBox) itemLayoutView.findViewById(R.id.checkbox3_rv);
            boxes_array[3] = (CheckBox) itemLayoutView.findViewById(R.id.checkbox4_rv);
            boxes_array[4] = (CheckBox) itemLayoutView.findViewById(R.id.checkbox5_rv);

            itemLayoutView.setOnClickListener(this);
            itemLayoutView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ModelMain listSelected = mainList.get(getAdapterPosition());
            long id = listSelected.getId();
            Intent intent = new Intent(v.getContext(), ToDoListActivity.class);
            intent.putExtra(EXTRA_ID, id);
            intent.putExtra(RV_PARENT_NUMBER, rv_parent_id);
            v.getContext().startActivity(intent);
            ((Activity) v.getContext()).finish();
        }

        @Override
        public boolean onLongClick(View v) {
            if (rv_parent_id == 1) {
                sentList = FragmentAll.mainList.get(getAdapterPosition());
                showDialog(sentList);
            } else if (rv_parent_id == 4) {
                sentList = FragmentToDoList.shopping_list.get(getAdapterPosition());
                showDialog(sentList);

            } else {
                sentList = ArchiveActivity.mainList.get(getAdapterPosition());
                showArcDialog(sentList);
            }
            return false;
        }

        public void showDialog(final ModelMain sentList) {
            new androidx.appcompat.app.AlertDialog.Builder(itemView.getContext())
                    .setPositiveButton(R.string.alert_dialog_delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            aDialogDeleteClick(sentList);
                            dialog.dismiss();
                        }
                    })
                    .setNeutralButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.alert_dialog_archive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            aDialogArchiveClick(sentList);
                            dialog.dismiss();
                        }
                    })
                    .show();
        }

        public void showArcDialog(final ModelMain sentList) {
            new androidx.appcompat.app.AlertDialog.Builder(itemView.getContext())
                    .setPositiveButton(R.string.alert_dialog_delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            aDialogDeleteClick(sentList);
                            dialog.dismiss();
                        }
                    })
                    .setNeutralButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.alert_dialog_dearchive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            aDialogDeArchive(sentList);
                            dialog.dismiss();
                        }
                    })
                    .show();
        }

        public void aDialogDeArchive(ModelMain sentToDo) {
            sentToDo.setStatus(0);
            notesData = new NotesData(itemView.getContext());
            notesData.updateShoppingList(sentToDo);

            ArchiveActivity.mainList = new ArrayList<>();
            ArchiveActivity.mainList.addAll(notesData.getArchivedNotes());
            ArchiveActivity.mainList.addAll(notesData.getArchivedShoppingLists());
            updateAll(ArchiveActivity.mainList);
        }

        public void aDialogDeleteClick(ModelMain shoppingList) {
            notesData = new NotesData(itemView.getContext());
            notesData.deleteShoppingList(shoppingList);

            if (rv_parent_id == 1) {
                FragmentAll.mainList = new ArrayList<>();
                FragmentAll.mainList.addAll(notesData.getToDosAboveCurrentTime());
                FragmentAll.mainList.addAll(notesData.getToDosRepeatingAlarm());
                FragmentAll.mainList.addAll(notesData.getToDosBellowCurrentTime());
                FragmentAll.mainList.addAll(notesData.getAllShoppingLists());
                updateAll(FragmentAll.mainList);
            } else if (rv_parent_id == 4) {
                FragmentToDoList.shopping_list = new ArrayList<>();
                FragmentToDoList.shopping_list.addAll(notesData.getAllShoppingLists());
                updateAllShowList(FragmentToDoList.shopping_list);
            } else {
                ArchiveActivity.mainList = new ArrayList<>();
                ArchiveActivity.mainList.addAll(notesData.getArchivedNotes());
                ArchiveActivity.mainList.addAll(notesData.getArchivedShoppingLists());
                updateAll(mainList);
            }
        }

        public void aDialogArchiveClick(ModelMain shoppingList) {
            shoppingList.setStatus(1);
            notesData = new NotesData(itemView.getContext());
            notesData.updateShoppingList(shoppingList);
            try{
                if (rv_parent_id == 1) {
                    FragmentAll.mainList = new ArrayList<>();
                    FragmentAll.mainList.addAll(notesData.getToDosAboveCurrentTime());
                    FragmentAll.mainList.addAll(notesData.getToDosRepeatingAlarm());
                    FragmentAll.mainList.addAll(notesData.getToDosBellowCurrentTime());
                    FragmentAll.mainList.addAll(notesData.getAllShoppingLists());
                    updateAll(FragmentAll.mainList);
                } else if (rv_parent_id == 4) {
                    FragmentToDoList.shopping_list = new ArrayList<>();
                    FragmentToDoList.shopping_list.addAll(notesData.getAllShoppingLists());
                    updateAllShowList(FragmentToDoList.shopping_list);
                }
            }catch (Exception e){
                e.printStackTrace();
                notifyDataSetChanged();
            }
        }
    }
}

