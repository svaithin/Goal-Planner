package com.labs.svaithin.goalplanner_v0_01;

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

public class milestone_add extends AppCompatActivity {

    private ArrayList<String> milestone;
    private ArrayAdapter<String> milestoneAdapter;
    private ListView lvItems;
    private String TAG = "Mainactivity";
    public final static String GOAL = "com.example.myfirstapp.MESSAGE";
    private ListView mTaskListView;
    private TaskDbHelper mHelper;
    Integer goalID;

    //milestone = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milestone_add);



        setGoal();
        //Log.d("id",goal);



        lvItems = (ListView) findViewById(R.id.lvItems1);
        milestone = new ArrayList<String>();
       // milestoneAdapter = new ArrayAdapter<String>(this,
       //         android.R.layout.simple_list_item_1, milestone);

        milestoneAdapter =  new ArrayAdapter<String>(this,
                R.layout.milestone_row,R.id.row,milestone);
        lvItems.setAdapter(milestoneAdapter);
        milestone.add("First Item");
        milestone.add("Second Item");
        milestonlistener();
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
                        //Log.d(TAG, "Task to add: " + task);
                        //updateMilestone(task);
                        milestoneAdapter.add(task);
                        //Log.d(TAG,"milestone"+milestone);
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
                                        milestone.remove(pos);
                                        milestone.add(pos,task);
                                        lvItems.setAdapter(milestoneAdapter);
                                        Log.d( "AlertDialog", "Positive" );
                                    }
                                })
                                .setNegativeButton( "Delete", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d( "AlertDialog", "Negative" );
                                        milestone.remove(pos);
                                        lvItems.setAdapter(milestoneAdapter);
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
