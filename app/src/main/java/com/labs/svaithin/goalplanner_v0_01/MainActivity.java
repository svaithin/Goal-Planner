package com.labs.svaithin.goalplanner_v0_01;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.labs.svaithin.goalplanner_v0_01.db.TaskContract;
import com.labs.svaithin.goalplanner_v0_01.db.TaskDbHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.animationDuration;
import static android.R.attr.button;
import static android.R.attr.dialogTitle;
import static android.R.attr.id;
import static android.R.attr.title;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;
    private String TAG = "Mainactivity";
    public final static String GOAL = "com.example.myfirstapp.MESSAGE";
    private TaskDbHelper mHelper;
    HashMap<Integer, Integer> map;
    HashMap<Integer, Integer> doneMap;
    //MySimpleStringAdapter mySimpleNewAdapter;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private com.google.android.gms.common.api.GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Uncomment the below line to delete the DB when DB error occurs
        //getApplicationContext().deleteDatabase(TaskContract.DB_NAME);

        setContentView(R.layout.activity_main);

        //Set Custom title bar
        setCustomTitlebar();


        mHelper = new TaskDbHelper(this);


        //FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "font.otf");
        //fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));

        updateUI();
        setupListViewListener();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new com.google.android.gms.common.api.GoogleApiClient.Builder(this).addApi(com.google.android.gms.appindexing.AppIndex.API).build();
    }

    /*@Override
    public void setContentView(View view)
    {
        super.setContentView(view);
        Log.d(TAG,"setContentView");

        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "font.otf");
        fontChanger.replaceFonts((ViewGroup)this.findViewById(android.R.id.content));
    }*/

    private void setCustomTitlebar(){
        RelativeLayout titlebar = (RelativeLayout) findViewById(R.id.goal_title1);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.goal_title1);

        TextView titleBar = (TextView)getWindow().findViewById(R.id.header_text);
        final Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Copperplate.ttc");
        titleBar.setTypeface(custom_font);


    }

    private void updateUI() {

        final Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Copperplate.ttc");
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        etNewItem.setTypeface(custom_font);
        Button addbutton = (Button) findViewById(R.id.btnAddItem);
        addbutton.setTypeface(custom_font);


        if(lvItems == null) {
            lvItems = (ListView) findViewById(R.id.lvItems);
        }
        if(items ==null) {
            items = new ArrayList<String>();
        }


        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        if (map == null) {
            map = new HashMap<Integer, Integer>();
        }
        if (doneMap == null) {
            doneMap = new HashMap<Integer, Integer>();
        }

        //mySimpleNewAdapter.clear();
        int row = 0;
        Cursor cursor = db.query(TaskContract.TaskEntry.GOAL,
                new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.GOALTITLE,
                        TaskContract.TaskEntry.GOALDONE}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(TaskContract.TaskEntry.GOALTITLE);
            //mySimpleNewAdapter.add(cursor.getString(idx));
            taskList.add(cursor.getString(idx));
            int idt = cursor.getColumnIndex(TaskContract.TaskEntry._ID);
            map.put(row, cursor.getInt(idt));
            int idd = cursor.getColumnIndex(TaskContract.TaskEntry.GOALDONE);
            doneMap.put(row, cursor.getInt(idd));
            row++;
            //Log.d(TAG, "row" + doneMap);

        }

        if(itemsAdapter == null) {
            itemsAdapter = new ArrayAdapter<String>(this,
                    R.layout.goal_row, R.id.row, items) {

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);

                    TextView textView = (TextView) view.findViewById(R.id.row);
                    textView.setTypeface(custom_font);
                    //Log.d(TAG,"postiton"+position);

                    if (doneMap.get(position) > 0) {

            /*YOUR CHOICE OF COLOR*/
                        textView.setTextColor(Color.GRAY);

                        textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }else {
                        textView.setTextColor(Color.BLACK);

                        textView.setPaintFlags(textView.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                    }

                    return view;
                }
            };
        }

        lvItems.setAdapter(itemsAdapter);

        itemsAdapter.clear();
        itemsAdapter.addAll(taskList);
        itemsAdapter.notifyDataSetChanged();

        cursor.close();
        db.close();
    }

    public void onAddItem(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String goalTitle = etNewItem.getText().toString().trim();
        Log.d(TAG,"goal:"+goalTitle.isEmpty());
        if(!goalTitle.isEmpty()) {
            Log.d(TAG,"goalemplty:"+goalTitle);
            SQLiteDatabase db = mHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(TaskContract.TaskEntry.GOALTITLE, goalTitle);
            values.put(TaskContract.TaskEntry.GOALDONE, 0);
            db.insertWithOnConflict(TaskContract.TaskEntry.GOAL,
                    null,
                    values,
                    SQLiteDatabase.CONFLICT_REPLACE);
            db.close();
        }
        updateUI();
        etNewItem.setText("");

    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   final View item, final int pos, long id) {
                        final EditText taskEditText = new EditText(getApplicationContext());
                        final Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Copperplate.ttc");
                        taskEditText.setText(items.get(pos));
                        taskEditText.setTypeface(custom_font);
                        taskEditText.setTextColor(Color.BLACK);
                        taskEditText.setHint("Goal");
                        Log.d(TAG,"edit text:"+items.get(pos));
                        String completeButtonName = new String();
                        if (doneMap.get(pos) == 0) {
                            completeButtonName = "Completed";
                        }
                        else {
                            completeButtonName = "Incomplete";
                        }
                        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                                .setView(taskEditText)
                                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        String task = String.valueOf(taskEditText.getText());
                                        if(!task.trim().isEmpty()) {

                                            SQLiteDatabase update_db = mHelper.getWritableDatabase();


                                            update_db.execSQL("update " + TaskContract.TaskEntry.GOAL +
                                                    " set " + TaskContract.TaskEntry.GOALTITLE + " = '" +
                                                    task.toString() + "' where _id = " + map.get(pos));

                                            updateUI();
                                            update_db.close();
                                        }

                                    }
                                })
                                .setNeutralButton(completeButtonName, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // write code to strike text

                                        TextView item1 = (TextView) item.findViewById(R.id.row);

                                        SQLiteDatabase update_db = mHelper.getWritableDatabase();

                                        if (doneMap.get(pos) == 0) {


                                            update_db.execSQL("update " + TaskContract.TaskEntry.GOAL +
                                                    " set " + TaskContract.TaskEntry.GOALDONE +
                                                    " = 1" + " where _id = " + map.get(pos));

                                        } else {

                                            update_db.execSQL("update " + TaskContract.TaskEntry.GOAL +
                                                    " set " + TaskContract.TaskEntry.GOALDONE +
                                                    " = 0" + " where _id = " + map.get(pos));
                                        }
                                        //Log.d("AlertDialog", "Positive");
                                        updateUI();


                                    }
                                })
                                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Log.d("AlertDialog", "Negative");
                                        SQLiteDatabase remove_db = mHelper.getWritableDatabase();
                                        remove_db.execSQL("delete from " + TaskContract.TaskEntry.GOAL +
                                                " where _id =" + map.get(pos));
                                        remove_db.close();
                                        //items.remove(pos);
                                        //lvItems.setAdapter(itemsAdapter);
                                        updateUI();
                                    }
                                })
                                .show();

                        TextView dialogTitle = (TextView) dialog.findViewById(android.R.id.title);
                        Log.d(TAG,"dialogtitle"+dialogTitle);
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
                                            View item, int pos, long id) {
                        //Log.d(TAG, "inside click" + pos + ":" + map.get(pos));
                        Intent intent = new Intent(getApplicationContext(), milestone_add.class);
                        intent.putExtra("ID", map.get(pos));
                        startActivity(intent);

                    }

                });
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public com.google.android.gms.appindexing.Action getIndexApiAction() {
        com.google.android.gms.appindexing.Thing object = new com.google.android.gms.appindexing.Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new com.google.android.gms.appindexing.Action.Builder(com.google.android.gms.appindexing.Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(com.google.android.gms.appindexing.Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        com.google.android.gms.appindexing.AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        com.google.android.gms.appindexing.AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}


