package com.labs.svaithin.goalplanner_v0_01;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.labs.svaithin.goalplanner_v0_01.db.TaskContract;
import com.labs.svaithin.goalplanner_v0_01.db.TaskDbHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class milestone_add extends AppCompatActivity {

    private ArrayList<String> milestone;
    private ArrayAdapter<String> milestoneAdapter;
    private ListView lvItems;
    private String TAG = "Mainactivity";
    public final static String GOAL = "com.example.myfirstapp.MESSAGE";
    private ListView mTaskListView;
    private TaskDbHelper mHelper;
    Integer goalID;
    HashMap<Integer, Integer> pos_id_map;
    HashMap<Integer, Integer> pos_done_map;

    //milestone = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milestone_add);

        // Set Goal As Text View
        setGoal();

        lvItems = (ListView) findViewById(R.id.lvItems1);
        milestone = new ArrayList<String>();
       // milestoneAdapter = new ArrayAdapter<String>(this,
       //         android.R.layout.simple_list_item_1, milestone);

        milestoneAdapter =  new ArrayAdapter<String>(this,
                R.layout.milestone_row,R.id.row,milestone);
        lvItems.setAdapter(milestoneAdapter);
        //milestone.add("First Item");
        //milestone.add("Second Item");
        updateUI();
        milestonlistener();
    }

    private void updateUI() {
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        if (pos_id_map == null) {
            pos_id_map = new HashMap<Integer, Integer>();
        }
        if (pos_done_map == null) {
            pos_done_map = new HashMap<Integer, Integer>();
        }

        //mySimpleNewAdapter.clear();
        int row = 0;
        Cursor cursor = db.query(TaskContract.TaskEntry.MILESTONE,
                new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.MILESTONETITLE,
                        TaskContract.TaskEntry.MGOALID,TaskContract.TaskEntry.MILESTONEDONE},
                        ""+TaskContract.TaskEntry.MGOALID+" = ?",new String[]{goalID.toString()}, null, null, null);
        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(TaskContract.TaskEntry.MILESTONETITLE);
            //mySimpleNewAdapter.add(cursor.getString(idx));
            taskList.add(cursor.getString(idx));
            int idt = cursor.getColumnIndex(TaskContract.TaskEntry._ID);
            pos_id_map.put(row, cursor.getInt(idt));
            int idd = cursor.getColumnIndex(TaskContract.TaskEntry.MILESTONEDONE);
            pos_done_map.put(row, cursor.getInt(idd));
            row++;
            Log.d(TAG, "row" + pos_id_map);

        }


        milestoneAdapter.clear();
        milestoneAdapter.addAll(taskList);
        milestoneAdapter.notifyDataSetChanged();
        //itemsAdapter.addAll(taskList);

        //mySimpleNewAdapter.clear();
        //mySimpleNewAdapter.addAll(taskList);
        /*mySimpleNewAdapter.addAll(taskList);*/
        //mySimpleNewAdapter.notifyDataSetChanged();


        cursor.close();
        db.close();
    }


    public void setGoal(){

        Intent intent = getIntent();

        goalID = intent.getIntExtra("ID",0);
        Log.d("ID", "ID"+goalID);

        // DB operations
        mHelper = new TaskDbHelper(this);
        SQLiteDatabase db = mHelper.getReadableDatabase();

        String goalquery = "Select "+ TaskContract.TaskEntry.GOALTITLE + " from "+ TaskContract.TaskEntry.GOAL +
                " where "+TaskContract.TaskEntry._ID + " = ?";

        Cursor cursor = db.rawQuery(goalquery, new String[] {goalID.toString()});
        String goal = "";
        if (cursor.moveToFirst()) {
            goal = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.GOALTITLE));
        }

        TextView tGoal = new TextView(this);
        tGoal=(TextView)findViewById(R.id.goaltext);
        tGoal.setText(goal);

    }

    public void onAddMilestone(View v) {
        final EditText taskEditText = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add a milestone")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(taskEditText.getText());

                        milestoneAdapter.add(task);
                        // Adding to DB

                        SQLiteDatabase db = mHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put(TaskContract.TaskEntry.MILESTONETITLE, task);
                        values.put(TaskContract.TaskEntry.MGOALID, goalID);
                        values.put(TaskContract.TaskEntry.MILESTONEDONE,0);
                        db.insertWithOnConflict(TaskContract.TaskEntry.MILESTONE,
                                null,
                                values,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        db.close();


                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void milestonlistener(){
        Log.d(TAG,"milestone listner");
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, final int pos, long id) {
                        final EditText taskEditText = new EditText(getApplicationContext());
                        taskEditText.setText(milestone.get(pos));
                        new AlertDialog.Builder( milestone_add.this )
                                .setTitle( "Milestone" )
                                .setView(taskEditText)
                                .setPositiveButton( "Save", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        String task = String.valueOf(taskEditText.getText());
                                        SQLiteDatabase update_db = mHelper.getWritableDatabase();

                                        //update_db.update(TaskContract.TaskEntry.GOAL, )
                                        update_db.execSQL("update " + TaskContract.TaskEntry.MILESTONE +
                                                " set " + TaskContract.TaskEntry.MILESTONETITLE + " = '" +
                                                task.toString() + "' where _id = " + pos_id_map.get(pos));

                                        updateUI();
                                        update_db.close();
                                        //lvItems.setAdapter(milestoneAdapter);
                                        Log.d( "AlertDialog", "Positive" );
                                    }
                                })
                                .setNegativeButton( "Delete", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d( "AlertDialog", "Negative" );

                                        SQLiteDatabase remove_db = mHelper.getWritableDatabase();
                                        remove_db.execSQL("delete from " + TaskContract.TaskEntry.MILESTONE +
                                                " where _id =" + pos_id_map.get(pos));
                                        remove_db.close();
                                        updateUI();
                                        //milestone.remove(pos);
                                        //lvItems.setAdapter(milestoneAdapter);
                                    }
                                } )
                                .show();
                        return true;
                    }

                });
        lvItems.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter,
                                            View item, final int pos, long id) {
                        Log.d(TAG,"inside milestone click");
                        //Logic to strike through

                        TextView item1 = (TextView) item.findViewById(R.id.row);

                        if(item1.getPaintFlags() != Paint.STRIKE_THRU_TEXT_FLAG ) {
                            item1.setPaintFlags(item1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        }
                        else {
                            item1.setPaintFlags(item1.getPaintFlags() &  (~ Paint.STRIKE_THRU_TEXT_FLAG));
                        }



                        return ;
                    }

                });
    }
}
