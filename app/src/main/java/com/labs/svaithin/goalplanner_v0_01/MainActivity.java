package com.labs.svaithin.goalplanner_v0_01;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.labs.svaithin.goalplanner_v0_01.db.TaskContract;
import com.labs.svaithin.goalplanner_v0_01.db.TaskDbHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.id;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;
    private String TAG = "Mainactivity";
    public final static String GOAL = "com.example.myfirstapp.MESSAGE";
    private TaskDbHelper mHelper;
    HashMap<Integer, Integer> map;
    HashMap<Integer, Integer> doneMap;
    MySimpleStringAdapter mySimpleNewAdapter;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private com.google.android.gms.common.api.GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Uncomment the below line to delete the DB when DB error occurs
        //getApplicationContext().deleteDatabase(TaskContract.DB_NAME);

        mHelper = new TaskDbHelper(this);



        //mySimpleNewAdapter = new MySimpleStringAdapter(getApplicationContext(), R.layout.milestone_row, items);
        //lvItems.setAdapter(mySimpleNewAdapter);


        updateUI();
        setupListViewListener();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new com.google.android.gms.common.api.GoogleApiClient.Builder(this).addApi(com.google.android.gms.appindexing.AppIndex.API).build();
    }


    private void updateUI() {

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
            Log.d(TAG, "row" + doneMap);

        }

        if(itemsAdapter == null) {
            itemsAdapter = new ArrayAdapter<String>(this,
                    R.layout.milestone_row, R.id.row, items) {

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);

                    TextView textView = (TextView) view.findViewById(R.id.row);
                    Log.d(TAG,"postiton"+position);

                    if (doneMap.get(position) > 0) {

            /*YOUR CHOICE OF COLOR*/
                        textView.setTextColor(Color.BLUE);
                        textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }else {
                        textView.setTextColor(Color.RED);
                    }

                    return view;
                }
            };
        }

        lvItems.setAdapter(itemsAdapter);

        itemsAdapter.clear();
        itemsAdapter.addAll(taskList);
        itemsAdapter.notifyDataSetChanged();
        //itemsAdapter.addAll(taskList);

        //mySimpleNewAdapter.clear();
        //mySimpleNewAdapter.addAll(taskList);
        /*mySimpleNewAdapter.addAll(taskList);*/
        //mySimpleNewAdapter.notifyDataSetChanged();


        cursor.close();
        db.close();
    }

    public void onAddItem(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String goalTitle = etNewItem.getText().toString();
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.GOALTITLE, goalTitle);
        values.put(TaskContract.TaskEntry.GOALDONE, 0);
        db.insertWithOnConflict(TaskContract.TaskEntry.GOAL,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE);
        db.close();

        updateUI();
        //itemsAdapter.add(goalTitle);
        etNewItem.setText("");
    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   final View item, final int pos, long id) {
                        final EditText taskEditText = new EditText(getApplicationContext());
                        taskEditText.setText(items.get(pos));
                        String completeButtonName = new String();
                        if (doneMap.get(pos) == 0) {
                            completeButtonName = "Completed";
                        }
                        else {
                            completeButtonName = "Incomplete";
                        }
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Goal")
                                .setView(taskEditText)
                                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        String task = String.valueOf(taskEditText.getText());

                                        SQLiteDatabase update_db = mHelper.getWritableDatabase();

                                        //update_db.update(TaskContract.TaskEntry.GOAL, )
                                        update_db.execSQL("update " + TaskContract.TaskEntry.GOAL +
                                                " set " + TaskContract.TaskEntry.GOALTITLE + " = '" +
                                                task.toString() + "' where _id = " + map.get(pos));

                                        updateUI();
                                        update_db.close();
                                        //items.remove(pos);
                                        //items.add(pos,task);
                                        //lvItems.setAdapter(itemsAdapter);
                                        Log.d("AlertDialog", "Positive");
                                        updateUI();
                                    }
                                })
                                .setNeutralButton(completeButtonName, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // write code to strike text

                                        TextView item1 = (TextView) item.findViewById(R.id.row);

                                        SQLiteDatabase update_db = mHelper.getWritableDatabase();

                                        if (doneMap.get(pos) == 0) {
                                            //item1.setPaintFlags(item1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                            //SQLiteDatabase update_db = mHelper.getWritableDatabase();

                                            //update_db.update(TaskContract.TaskEntry.GOAL, )
                                            update_db.execSQL("update " + TaskContract.TaskEntry.GOAL +
                                                    " set " + TaskContract.TaskEntry.GOALDONE +
                                                    " = 1" + " where _id = " + map.get(pos));

                                        } else {
                                            //item1.setPaintFlags(item1.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                                            update_db.execSQL("update " + TaskContract.TaskEntry.GOAL +
                                                    " set " + TaskContract.TaskEntry.GOALDONE +
                                                    " = 0" + " where _id = " + map.get(pos));
                                        }
                                        Log.d("AlertDialog", "Positive");
                                        updateUI();


                                    }
                                })
                                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d("AlertDialog", "Negative");
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
                        return true;
                    }

                });
        lvItems.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter,
                                            View item, int pos, long id) {
                        Log.d(TAG, "inside click" + pos + ":" + map.get(pos));
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


