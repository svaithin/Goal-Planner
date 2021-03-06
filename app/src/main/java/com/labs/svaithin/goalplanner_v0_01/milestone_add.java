package com.labs.svaithin.goalplanner_v0_01;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v4.app.NotificationCompat;
import android.app.TaskStackBuilder;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.labs.svaithin.goalplanner_v0_01.db.TaskContract;
import com.labs.svaithin.goalplanner_v0_01.db.TaskDbHelper;

import java.util.ArrayList;
import java.util.HashMap;

import static android.R.attr.dialogTitle;
import static android.R.attr.resizeable;
import static android.os.Build.VERSION_CODES.M;
import static com.labs.svaithin.goalplanner_v0_01.R.id.etNewItem;
import static com.labs.svaithin.goalplanner_v0_01.R.id.textView;
import static com.labs.svaithin.goalplanner_v0_01.db.TaskContract.TaskEntry.NGRESULT;

public class milestone_add extends AppCompatActivity {

    private ArrayList<String> milestone;
    private ArrayAdapter<String> milestoneAdapter;
    private ListView lvItems;
    private String TAG = "Milestone";
    public final static String GOAL = "com.example.myfirstapp.MESSAGE";
    private ListView mTaskListView;
    private TaskDbHelper mHelper;
    private Typeface custom_font;
    Integer goalID;
    HashMap<Integer, Integer> pos_id_map;
    HashMap<Integer, Integer> pos_done_map;
    private AdView mAdView;
    private String reason;
    private String effort;
    private String okResult;
    private String ngResult;
    NotificationCompat.Builder notification;
    TaskStackBuilder stackBuilder;
    Intent resultIntent;
    PendingIntent pIntent;
    NotificationManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milestone_add);

        custom_font = Typeface.createFromAsset(getAssets(), "fonts/Copperplate.ttc");
        Button addbttn = (Button) findViewById(R.id.button2);
        //EditText etNewItem = (EditText) findViewById(R.id.etNewMilestone);
        //etNewItem.setTypeface(custom_font);
        addbttn.setTypeface(custom_font);
        mHelper = new TaskDbHelper(this);

        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "font.otf");
        fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
        // Set Goal As Text View
        setGoal();



        //Update UI
        updateUI();

        // Starting up Listners
        milestonlistener();

        //Add admob
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
    }


    public  void addMilestone(View v){

        final EditText taskEditText = new EditText(getApplicationContext());
        taskEditText.setTextColor(Color.BLACK);
        taskEditText.setTypeface(custom_font);
        AlertDialog dialog = new AlertDialog.Builder( milestone_add.this )
                .setView(taskEditText)
                .setPositiveButton( "Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(taskEditText.getText());
                        if(!task.trim().isEmpty()) {
                            milestoneAdapter.add(task);
                            // Adding to DB

                            SQLiteDatabase db = mHelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put(TaskContract.TaskEntry.MILESTONETITLE, task);
                            values.put(TaskContract.TaskEntry.MGOALID, goalID);
                            values.put(TaskContract.TaskEntry.MILESTONEDONE, 0);
                            db.insertWithOnConflict(TaskContract.TaskEntry.MILESTONE,
                                    null,
                                    values,
                                    SQLiteDatabase.CONFLICT_REPLACE);
                            db.close();
                            updateUI();
                        }
                        //lvItems.setAdapter(milestoneAdapter);
                        //Log.d( "AlertDialog", "Positive" );
                    }
                })
                .setNegativeButton( "Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Log.d( "AlertDialog", "Negative" );

                        SQLiteDatabase remove_db = mHelper.getWritableDatabase();
                        remove_db.execSQL("delete from " + TaskContract.TaskEntry.MILESTONE +
                                " where _id =" + goalID.toString());
                        remove_db.close();
                        updateUI();
                        //milestone.remove(pos);
                        //lvItems.setAdapter(milestoneAdapter);
                    }
                } )
                .show();
        TextView dialogTitle = (TextView) dialog.findViewById(android.R.id.title);
        //Log.d(TAG,"dialogtitle"+dialogTitle);
        Button save = (Button) dialog.findViewById(android.R.id.button1);
        save.setTypeface(custom_font);
        Button delete = (Button) dialog.findViewById(android.R.id.button2);
        delete.setTypeface(custom_font);
        Button completed = (Button) dialog.findViewById(android.R.id.button3);
        completed.setTypeface(custom_font);


    }



    //Notification Icon need to have new silhouette
    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.ic_launcher : R.mipmap.ic_launcher;
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:
                //Log.d(TAG, "Add a new task");
                EditText etNewItem = (EditText) findViewById(R.id.ewhy);

                String reason = etNewItem.getText().toString().trim();
                SQLiteDatabase db = mHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                int valid = 0;

                if(!reason.trim().isEmpty()) {
                    values.put(TaskContract.TaskEntry.REASON, reason);
                    valid = 1;
                }

                EditText effort = (EditText) findViewById(R.id.eEffert) ;
                String efforttext = effort.getText().toString().trim();
                if(!efforttext.trim().isEmpty()){
                    values.put(TaskContract.TaskEntry.EFFORT,efforttext);
                    valid =1;
                }

                EditText impact = (EditText) findViewById(R.id.editText3);
                String okresult = impact.getText().toString().trim();
                if(!okresult.trim().isEmpty()){
                    values.put(TaskContract.TaskEntry.OKRESULT,okresult);
                    valid= 1;
                }

                EditText ngimpact = (EditText)findViewById(R.id.editText);
                String ngres = ngimpact.getText().toString().trim();
                if(!ngres.trim().isEmpty()){
                    values.put(NGRESULT,ngres);
                    valid=1;
                }

                if(valid > 0){
                    values.put(TaskContract.TaskEntry.DGOALID, goalID);
                    db.insertWithOnConflict(TaskContract.TaskEntry.GOALDETAIL,
                            null,
                            values,
                            SQLiteDatabase.CONFLICT_REPLACE);
                    db.close();
                }

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void getReasonFromDB(){

        SQLiteDatabase db = mHelper.getReadableDatabase();

        //Log.d(TAG,"goalid"+goalID.toString());

        Cursor cursor = db.query(TaskContract.TaskEntry.GOALDETAIL,
                new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.REASON,
                        TaskContract.TaskEntry.DGOALID, NGRESULT,
                        TaskContract.TaskEntry.EFFORT, TaskContract.TaskEntry.OKRESULT},
                ""+TaskContract.TaskEntry.DGOALID+" = ?",new String[]{goalID.toString()}, null, null, null);

        while(cursor.moveToNext()) {
            int idt = cursor.getColumnIndex(TaskContract.TaskEntry.REASON);
            //Log.d(TAG, "idt" + idt);
            reason = cursor.getString(idt);
            idt = cursor.getColumnIndex(TaskContract.TaskEntry.EFFORT);
            effort = cursor.getString(idt);
            idt = cursor.getColumnIndex(TaskContract.TaskEntry.OKRESULT);
            okResult = cursor.getString(idt);
            idt = cursor.getColumnIndex(NGRESULT);
            ngResult = cursor.getString(idt);
            //Log.d(TAG, "reason" + reason + "effort" + effort);
        }

        EditText etNewItem = (EditText) findViewById(R.id.ewhy);
        etNewItem.setText(reason);
        EditText effort1 = (EditText) findViewById(R.id.eEffert);
        effort1.setText(effort);
        EditText okres = (EditText) findViewById(R.id.editText3);
        okres.setText(okResult);
        EditText ngres = (EditText) findViewById(R.id.editText);
        ngres.setText(ngResult);

    }


    private void updateUI() {
        ArrayList<String> taskList = new ArrayList<>();
        TextView tv = (TextView) findViewById(R.id.goal);
        tv.setTextColor(Color.GRAY);
        tv =(TextView) findViewById(R.id.why);
        tv.setTextColor(Color.BLACK);
        tv =(TextView) findViewById(R.id.tEffert);
        tv.setTextColor(Color.BLACK);
        tv =(TextView) findViewById(R.id.textView5);
        tv.setTextColor(Color.BLACK);
        tv =(TextView) findViewById(R.id.textView);
        tv.setTextColor(Color.BLACK);
        tv = (TextView) findViewById(R.id.textView7);
        tv.setTextColor(Color.GRAY);

        if(lvItems ==null) {
            lvItems = (ListView) findViewById(R.id.lvItems1);
        }
        if(milestone == null) {
            milestone = new ArrayList<String>();
        }


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
            //Log.d(TAG, "row" + pos_id_map);

        }

        if(milestoneAdapter == null) {
            milestoneAdapter = new ArrayAdapter<String>(this,
                    R.layout.milestone_row, R.id.row, milestone) {

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);

                    TextView textView = (TextView) view.findViewById(R.id.row);
                    textView.setTypeface(custom_font);
                    //Log.d(TAG, "postiton" + position);
                    //Log.d(TAG, "pos_done_map:" + pos_done_map);

                    if (pos_done_map.get(position) > 0) {
            /*YOUR CHOICE OF COLOR*/
                        textView.setTextColor(Color.GRAY);
                        textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        textView.setTextColor(Color.BLACK);
                        textView.setPaintFlags(textView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                    }

                    return view;
                }
            };
        }


        lvItems.setAdapter(milestoneAdapter);

        milestoneAdapter.clear();
        milestoneAdapter.addAll(taskList);
        milestoneAdapter.notifyDataSetChanged();


        // Get and Set Reasons from DB
        getReasonFromDB();

        cursor.close();
        db.close();
        //Log.d(TAG,"endof UI");
    }


    public void setGoal(){

        Intent intent = getIntent();

        goalID = intent.getIntExtra("ID",0);
        //Log.d("ID", "ID"+goalID);

        // DB operations

        SQLiteDatabase db = mHelper.getReadableDatabase();

        String goalquery = "Select "+ TaskContract.TaskEntry.GOALTITLE + " from "+ TaskContract.TaskEntry.GOAL +
                " where "+TaskContract.TaskEntry._ID + " = ?";

        Cursor cursor = db.rawQuery(goalquery, new String[] {goalID.toString()});
        String goal = "";
        if (cursor.moveToFirst()) {
            goal = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.GOALTITLE));
        }

        RelativeLayout titlebar = (RelativeLayout) findViewById(R.id.goal_title1);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.goal_title1);

        TextView Goal = (TextView)findViewById(R.id.goal);

        TextView titleBar = (TextView)getWindow().findViewById(R.id.header_text);
        final Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Copperplate.ttc");
        titleBar.setTypeface(custom_font);
        Goal.setTypeface(custom_font);
        Goal.setText(goal);
        /*TextView milestone = new TextView(this);
        milestone = (TextView)findViewById(R.id.milestone);
        milestone.setTypeface(custom_font);
        milestone.setTextColor(Color.parseColor("#3f3f3f"));*/

    }

    /*public void onAddMilestone(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewMilestone);
        String task = etNewItem.getText().toString().trim();
        if(!task.trim().isEmpty()) {

            milestoneAdapter.add(task);
            // Adding to DB

            SQLiteDatabase db = mHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(TaskContract.TaskEntry.MILESTONETITLE, task);
            values.put(TaskContract.TaskEntry.MGOALID, goalID);
            values.put(TaskContract.TaskEntry.MILESTONEDONE, 0);
            db.insertWithOnConflict(TaskContract.TaskEntry.MILESTONE,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE);
            db.close();
        }
        updateUI();
        etNewItem.setText("");
    }*/

    private void milestonlistener(){
        //Log.d(TAG,"milestone listner");
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, final int pos, long id) {
                        final EditText taskEditText = new EditText(getApplicationContext());
                        taskEditText.setText(milestone.get(pos));
                        taskEditText.setTextColor(Color.BLACK);
                        taskEditText.setTypeface(custom_font);
                        AlertDialog dialog = new AlertDialog.Builder( milestone_add.this )
                                .setView(taskEditText)
                                .setPositiveButton( "Save", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        String task = String.valueOf(taskEditText.getText());
                                        if(!task.trim().isEmpty()) {
                                            SQLiteDatabase update_db = mHelper.getWritableDatabase();

                                            //update_db.update(TaskContract.TaskEntry.GOAL, )
                                            update_db.execSQL("update " + TaskContract.TaskEntry.MILESTONE +
                                                    " set " + TaskContract.TaskEntry.MILESTONETITLE + " = '" +
                                                    task.toString() + "' where _id = " + pos_id_map.get(pos));

                                            updateUI();
                                            update_db.close();
                                        }
                                        //lvItems.setAdapter(milestoneAdapter);
                                        //Log.d( "AlertDialog", "Positive" );
                                    }
                                })
                                .setNegativeButton( "Delete", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Log.d( "AlertDialog", "Negative" );

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
                        TextView dialogTitle = (TextView) dialog.findViewById(android.R.id.title);
                        //Log.d(TAG,"dialogtitle"+dialogTitle);
                        Button save = (Button) dialog.findViewById(android.R.id.button1);
                        save.setTypeface(custom_font);
                        Button delete = (Button) dialog.findViewById(android.R.id.button2);
                        delete.setTypeface(custom_font);
                        Button completed = (Button) dialog.findViewById(android.R.id.button3);
                        completed.setTypeface(custom_font);
                        return true;
                    }

                });
        lvItems.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter,
                                            View item, final int pos, long id) {
                        //Log.d(TAG,"inside milestone click");
                        //Logic to strike through
                        SQLiteDatabase update_db = mHelper.getWritableDatabase();

                        if (pos_done_map.get(pos) == 0) {

                            update_db.execSQL("update " + TaskContract.TaskEntry.MILESTONE +
                                    " set " + TaskContract.TaskEntry.MILESTONEDONE +
                                    " = 1" + " where _id = " + pos_id_map.get(pos));

                        } else {
                            //item1.setPaintFlags(item1.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                            update_db.execSQL("update " + TaskContract.TaskEntry.MILESTONE +
                                    " set " + TaskContract.TaskEntry.MILESTONEDONE +
                                    " = 0" + " where _id = " + pos_id_map.get(pos));
                        }
                        updateUI();

                        return ;
                    }

                });
    }
}
