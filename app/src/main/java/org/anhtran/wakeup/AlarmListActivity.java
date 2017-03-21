package org.anhtran.wakeup;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.anhtran.wakeup.data.AlarmContract.AlarmEntry;

/**
 * Created by anhtran on 2/25/17.
 */

public class AlarmListActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = AlarmListActivity.class.getSimpleName();

    private static final int ALARM_LOADER = 0;

    AlarmCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AlarmListActivity.this, AlarmEditorActivity.class);
                startActivity(intent);
            }
        });
        mCursorAdapter = new AlarmCursorAdapter(this, null);
        ListView alarmList = (ListView) findViewById(R.id.list_alarm);

        alarmList.setAdapter(mCursorAdapter);

        getLoaderManager().initLoader(ALARM_LOADER, null, this);

        alarmList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AlarmListActivity.this, AlarmEditorActivity.class);
                Uri currentAlarmUri = ContentUris.withAppendedId(AlarmEntry.CONTENT_URI, id);

                intent.setData(currentAlarmUri);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alarm_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete_all_alarms:
                DialogInterface.OnClickListener deleteAllClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int rowsEffected = getContentResolver()
                                        .delete(AlarmEntry.CONTENT_URI, null, null);
                                if (rowsEffected == 0) {
                                    Toast.makeText(AlarmListActivity.this,
                                            "Delete all alarms failed", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(AlarmListActivity.this,
                                            "Delete successful", Toast.LENGTH_LONG).show();
                                }
                            }
                        };
                showDeleteConfirmationDialog(deleteAllClickListener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        String[] projection = {AlarmEntry._ID,
                AlarmEntry.COLUMN_ALARM_NAME,
                AlarmEntry.COLUMN_ALARM_DATETIME,
                AlarmEntry.COLUMN_ALARM_STATE};

        return new CursorLoader(this, AlarmEntry.CONTENT_URI,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {

        mCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader loader) {
        mCursorAdapter.swapCursor(null);
    }

    private void showDeleteConfirmationDialog(
            DialogInterface.OnClickListener deleteClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_this_alarm);
        builder.setPositiveButton(R.string.delete, deleteClickListener);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
