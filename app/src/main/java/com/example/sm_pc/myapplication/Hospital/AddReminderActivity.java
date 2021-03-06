package com.example.sm_pc.myapplication.Hospital;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sm_pc.myapplication.Hospital.Data.AlarmReminderContract;
import com.example.sm_pc.myapplication.Hospital.Reminder.AlarmScheduler;
import com.example.sm_pc.myapplication.R;

import java.util.Calendar;

public class AddReminderActivity extends AppCompatActivity
        implements com.example.sm_pc.myapplication.Hospital.DatePicker.DatePickerDialog.OnDateSetListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_VEHICLE_LOADER = 0;


    private Toolbar mToolbar;
    private EditText mTitleText, mWeightText, mUterusText, mCircumferenceText, mBlood_pressure_highText, mBlood_pressure_lowText;
    private TextView mDateText, mTimeText, mRepeatText, mRepeatNoText, mRepeatTypeText;
    private FloatingActionButton mFAB1;
    private FloatingActionButton mFAB2;
    private Calendar mCalendar;
    private int mYear, mMonth, mHour, mMinute, mDay;
    private long mRepeatTime;
    private Switch mRepeatSwitch;
    private String mTitle;
    private String mTime;
    private String mDate;
    private String mRepeat;
    private String mRepeatNo;
    private String mRepeatType;
    private String mActive;
    private String mWeight, mUterus, mCircumference, mBlood_pressure_high, mBlood_pressure_low, mEdema, mProtein_urine, mUrine_glucose;

    private Uri mCurrentReminderUri;
    private boolean mVehicleHasChanged = false;

    // Values for orientation change
    private static final String KEY_TITLE = "title_key";
    private static final String KEY_TIME = "time_key";
    private static final String KEY_DATE = "date_key";
    private static final String KEY_ACTIVE = "active_key";
    private static final String KEY_WEIGHT = "weight_key";
    private static final String KEY_UTERUS = "uterus_key";
    private static final String KEY_CIRCUMFERENCE = "circumference_key";
    private static final String KEY_BLOOD_PRESSURE_HIGH = "blood_pressure_high_key";
    private static final String KEY_BLOOD_PRESSURE_LOW = "blood_pressure_low_key";


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mVehicleHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        Intent intent = getIntent();
        mCurrentReminderUri = intent.getData();

        if (mCurrentReminderUri == null) {

            setTitle(getString(R.string.editor_activity_title_new_reminder));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a reminder that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {

            setTitle(getString(R.string.editor_activity_title_edit_reminder));
            getLoaderManager().initLoader(EXISTING_VEHICLE_LOADER, null, this);
        }


        // Initialize Views
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitleText = (EditText) findViewById(R.id.reminder_title);
        mDateText = (TextView) findViewById(R.id.set_date);
        mWeightText = (EditText) findViewById(R.id.set_weight);
        mUterusText = (EditText) findViewById(R.id.set_uterus);
        mCircumferenceText = (EditText) findViewById(R.id.set_circumference);
        mBlood_pressure_highText = (EditText) findViewById(R.id.set_blood_pressure_high);
        mBlood_pressure_lowText = (EditText) findViewById(R.id.set_blood_pressure_low);

        // Initialize default values
        mActive = "true";
        mRepeat = "true";
        mRepeatNo = Integer.toString(1);


        mCalendar = Calendar.getInstance();
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH) + 1;
        mDay = mCalendar.get(Calendar.DATE);

        mDate = mYear + "년 " + mMonth + "월 " + mDay+"일";
        mTime = mHour + ":" + mMinute;

        // Setup Reminder Title EditText
        mTitleText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTitle = s.toString().trim();
                mTitleText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        // Setup Reminder Weight EditText
        mWeightText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mWeight = s.toString().trim();
                mWeightText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Setup Reminder Weight EditText
        mDateText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mDate = s.toString().trim();
                mDateText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        // Setup Reminder Uterus EditText
        mUterusText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mUterus = s.toString().trim();
                mUterusText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Setup Reminder Circumference EditText
        mCircumferenceText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCircumference = s.toString().trim();
                mCircumferenceText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Setup Reminder Blood_pressure(high) EditText
        mBlood_pressure_highText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBlood_pressure_high = s.toString().trim();
                mBlood_pressure_highText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Setup Reminder Blood_pressure(low) EditText
        mBlood_pressure_lowText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mBlood_pressure_low = s.toString().trim();
                mBlood_pressure_lowText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });



        // Setup TextViews using reminder values
        mDateText.setText(mDate);
        ///////mTimeText.setText(mTime);

        // To save state on device rotation
        if (savedInstanceState != null) {
            String savedTitle = savedInstanceState.getString(KEY_TITLE);
            mTitleText.setText(savedTitle);
            mTitle = savedTitle;

            String savedTime = savedInstanceState.getString(KEY_TIME);
            mTimeText.setText(savedTime);
            mTime = savedTime;

            String savedDate = savedInstanceState.getString(KEY_DATE);
            mDateText.setText(savedDate);
            mDate = savedDate;

            String savedWeight = savedInstanceState.getString(KEY_WEIGHT);
            mWeightText.setText(savedWeight);
            mWeight = savedWeight;

            String savedUterus = savedInstanceState.getString(KEY_UTERUS);
            mUterusText.setText(savedUterus);
            mUterus = savedUterus;

            String savedCircumference = savedInstanceState.getString(KEY_CIRCUMFERENCE);
            mCircumferenceText.setText(savedCircumference);
            mCircumference = savedCircumference;

            String savedBlood_pressure_high = savedInstanceState.getString(KEY_BLOOD_PRESSURE_HIGH);
            mBlood_pressure_highText.setText(savedBlood_pressure_high);
            mBlood_pressure_high = savedBlood_pressure_high;

            String savedBlood_pressure_low = savedInstanceState.getString(KEY_BLOOD_PRESSURE_LOW);
            mBlood_pressure_lowText.setText(savedBlood_pressure_low);
            mBlood_pressure_low = savedBlood_pressure_low;


            mActive = savedInstanceState.getString(KEY_ACTIVE);
        }

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.title_activity_add_reminder);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharSequence(KEY_TITLE, mTitleText.getText());
        outState.putCharSequence(KEY_TIME, mTimeText.getText());
        outState.putCharSequence(KEY_DATE, mDateText.getText());
        outState.putCharSequence(KEY_ACTIVE, mActive);
        outState.putCharSequence(KEY_WEIGHT, mWeightText.getText());
        outState.putCharSequence(KEY_UTERUS, mUterusText.getText());
        outState.putCharSequence(KEY_CIRCUMFERENCE, mCircumferenceText.getText());
        outState.putCharSequence(KEY_BLOOD_PRESSURE_HIGH, mBlood_pressure_highText.getText());
        outState.putCharSequence(KEY_BLOOD_PRESSURE_LOW, mBlood_pressure_lowText.getText());

    }

    // On clicking Date picker
    public void setDate(View v){
        if(mCurrentReminderUri == null){
            Toast.makeText(this, "click again on the reminder list to set date alarm", Toast.LENGTH_LONG).show();
            return;
        }
        Calendar now = Calendar.getInstance();
        com.example.sm_pc.myapplication.Hospital.DatePicker.DatePickerDialog dpd = com.example.sm_pc.myapplication.Hospital.DatePicker.DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    // Obtain date from date picker
    @Override
    public void onDateSet(com.example.sm_pc.myapplication.Hospital.DatePicker.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear ++;
        mDay = dayOfMonth;
        mMonth = monthOfYear;
        mYear = year;
        mDate = year + "년 " + monthOfYear + "월 " + dayOfMonth+"일";
        mDateText.setText(mDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_add_reminder, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new reminder, hide the "Delete" menu item.
        if (mCurrentReminderUri == null) {
            MenuItem menuItem = menu.findItem(R.id.discard_reminder);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.save_reminder:
                if (mTitleText.getText().toString().length() == 0){
                    mTitleText.setError("제목을 입력해주세요");
                }
                else {
                    saveReminder();
                    finish();
                }
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.discard_reminder:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the reminder hasn't changed, continue with navigating up to parent activity
                // which is the {@link MainActivity}.
                if (!mVehicleHasChanged) {
                    NavUtils.navigateUpFromSameTask(AddReminderActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(AddReminderActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the reminder.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the reminder.
                deleteReminder();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the reminder.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteReminder() {
        // Only perform the delete if this is an existing reminder.
        if (mCurrentReminderUri != null) {
            // Call the ContentResolver to delete the reminder at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentreminderUri
            // content URI already identifies the reminder that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentReminderUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_reminder_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_reminder_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    // On clicking the save button
    public void saveReminder(){

        if (mCurrentReminderUri == null ) {
            // Since no fields were modified, we can return early without creating a new reminder.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        ContentValues values = new ContentValues();

        values.put(AlarmReminderContract.AlarmReminderEntry.KEY_TITLE, mTitle);
        values.put(AlarmReminderContract.AlarmReminderEntry.KEY_DATE, mDate);
        values.put(AlarmReminderContract.AlarmReminderEntry.KEY_TIME, mTime);
        values.put(AlarmReminderContract.AlarmReminderEntry.KEY_ACTIVE, mActive);
        values.put(AlarmReminderContract.AlarmReminderEntry.KEY_WEIGHT, mWeight);
        values.put(AlarmReminderContract.AlarmReminderEntry.KEY_UTERUS, mUterus);
        values.put(AlarmReminderContract.AlarmReminderEntry.KEY_CIRCUMFERENCE, mCircumference);
        values.put(AlarmReminderContract.AlarmReminderEntry.KEY_BLOOD_PRESSURE_HIGH, mBlood_pressure_high);
        values.put(AlarmReminderContract.AlarmReminderEntry.KEY_BLOOD_PRESSURE_LOW, mBlood_pressure_low);
        // Set up calender for creating the notification
        mCalendar.set(Calendar.MONTH, --mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        mCalendar.set(Calendar.HOUR_OF_DAY, 21);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.SECOND, 0);

        long selectedTimestamp =  mCalendar.getTimeInMillis();

        if (mCurrentReminderUri == null) {
            // This is a NEW reminder, so insert a new reminder into the provider,
            // returning the content URI for the new reminder.
            Uri newUri = getContentResolver().insert(AlarmReminderContract.AlarmReminderEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_reminder_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_reminder_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentReminderUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_reminder_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(getApplicationContext(), "저장되었습니다",
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Create a new notification
        if (mActive.equals("true")) {
            new AlarmScheduler().setAlarm(getApplicationContext(), selectedTimestamp, mCurrentReminderUri);
        }
        // Create toast to confirm new reminder
        Toast.makeText(getApplicationContext(), "저장되었습니다",
                Toast.LENGTH_SHORT).show();
    }

    // On pressing the back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                AlarmReminderContract.AlarmReminderEntry._ID,
                AlarmReminderContract.AlarmReminderEntry.KEY_TITLE,
                AlarmReminderContract.AlarmReminderEntry.KEY_DATE,
                AlarmReminderContract.AlarmReminderEntry.KEY_ACTIVE,
                AlarmReminderContract.AlarmReminderEntry.KEY_WEIGHT,
                AlarmReminderContract.AlarmReminderEntry.KEY_UTERUS,
                AlarmReminderContract.AlarmReminderEntry.KEY_CIRCUMFERENCE,
                AlarmReminderContract.AlarmReminderEntry.KEY_BLOOD_PRESSURE_HIGH,
                AlarmReminderContract.AlarmReminderEntry.KEY_BLOOD_PRESSURE_LOW,
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentReminderUri,         // Query the content URI for the current reminder
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            int titleColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_TITLE);
            int dateColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_DATE);
            int activeColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_ACTIVE);
            int weightColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_WEIGHT);
            int uterusColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_UTERUS);
            int circumferenceColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_CIRCUMFERENCE);
            int blood_pressure_highColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_BLOOD_PRESSURE_HIGH);
            int blood_pressure_lowColumnIndex = cursor.getColumnIndex(AlarmReminderContract.AlarmReminderEntry.KEY_BLOOD_PRESSURE_LOW);

            // Extract out the value from the Cursor for the given column index
            String title = cursor.getString(titleColumnIndex);
            String date = cursor.getString(dateColumnIndex);
            String active = cursor.getString(activeColumnIndex);
            String weight = cursor.getString(weightColumnIndex);
            String uterus = cursor.getString(uterusColumnIndex);
            String circumference = cursor.getString(circumferenceColumnIndex);
            String blood_pressure_high = cursor.getString(blood_pressure_highColumnIndex);
            String blood_pressure_low = cursor.getString(blood_pressure_lowColumnIndex);


            // Update the views on the screen with the values from the database
            mTitleText.setText(title);
            mDateText.setText(date);
            mWeightText.setText(weight);
            mUterusText.setText(uterus);
            mCircumferenceText.setText(circumference);
            mBlood_pressure_highText.setText(blood_pressure_high);
            mBlood_pressure_lowText.setText(blood_pressure_low);
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}