package fsegundo.lambda.calendar;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;

import fsegundo.lambda.AppController;

/**
 * Created by default on 20.10.2016.
 */
public class CalendarManager {
    private final long DEFAULT_DURATION_MS = 14400000; // 4 hours
    private long mCalendarID = -1; // id of the calendar we'll be using

    public CalendarManager(){
        mCalendarID = getCalendarId("Lambda");
        if (mCalendarID == -1)
            mCalendarID = AddCalendar("Lambda", "Lambda");
        // TODO: si no funciona AddCalendar? ... abrir en otro calendario?
    }

    // TODO: usar RemoveCalendar when uninstalling
    private void RemoveCalendar(long mCalendarID) {
        Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Calendars.CONTENT_URI, mCalendarID);
        int num_deleted = AppController.getInstance().getContentResolver().delete(deleteUri, null, null);
    }

    // return null or an error
    public long Add(long timestampStart, String title, String description) {
        return Add(timestampStart, timestampStart + DEFAULT_DURATION_MS, title, description);
    }

    public long Add(long timestampStart, long timestampEnd, String title, String description) {
// ALTERNATIVE WITH USER INTERACTION (delete permission in manifest)
//        Calendar cal = Calendar.getInstance();
//        Intent intent = new Intent(Intent.ACTION_EDIT);
//        intent.setType("vnd.android.cursor.item/event");
//        intent.putExtra("beginTime", cal.getTimeInMillis());
//        intent.putExtra("allDay", true);
//        intent.putExtra("rrule", "FREQ=YEARLY");
//        intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
//        intent.putExtra("title", "A Test Event from android app");
//        startActivity(intent);
//        return null;

        ContentResolver cr = AppController.getInstance().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, timestampStart);
        values.put(CalendarContract.Events.DTEND, timestampEnd);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.CALENDAR_ID, mCalendarID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Berlin");
//        values.put(Events.RRULE,
//                "FREQ=DAILY;COUNT=20;BYDAY=MO,TU,WE,TH,FR;WKST=MO");
//        values.put(Events.EVENT_LOCATION, "Münster");
//// reasonable defaults exist:
//        values.put(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE);
//        values.put(Events.SELF_ATTENDEE_STATUS,
//                Events.STATUS_CONFIRMED);
//        values.put(Events.ALL_DAY, 1);
//        values.put(Events.ORGANIZER, "some.mail@some.address.com");
//        values.put(Events.GUESTS_CAN_INVITE_OTHERS, 1);
//        values.put(Events.GUESTS_CAN_MODIFY, 1);
//        values.put(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
        if (ActivityCompat.checkSelfPermission(AppController.getInstance(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // Permissions must be added in Manifest
            return -1;
        }
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

// get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(uri.getLastPathSegment());
        /***************** Event: Reminder(with alert) Adding reminder to event *******************/
        // String to access default google calendar of device for reminder setting.
        String reminderUriString = "content://com.android.calendar/reminders";
        ContentValues reminderValues = new ContentValues();

        reminderValues.put(CalendarContract.Reminders.EVENT_ID, eventID);
        reminderValues.put(CalendarContract.Reminders.MINUTES, 1);
        reminderValues.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);

        //Setting reminder in calendar on Event.
        Uri reminderUri = cr.insert(Uri.parse(reminderUriString), reminderValues);

        return eventID;
    }

    // GetCalendar returns array of available calendars
    private class MyCalendar {
        private String mName;
        private String mID;
        private String mAccountName;
        private String mType;
        private String mPrimary;
        private String mVisible;

        public MyCalendar(String name, String id, String accountName, String type, String primary, String visible) {
            mName = name;
            mID = id;
            mAccountName = accountName;
            mType = type;
            mPrimary = primary;
            mVisible = visible;
        }

        public MyCalendar(String name, String id) {
            mName = name;
            mID = id;
            mAccountName = null;
            mType = null;
            mPrimary = "0";
        }
    }

    private MyCalendar[] m_calendars = null;

    public MyCalendar[] getCalendars() {
        final String projection[] = {CalendarContract.Calendars._ID,
                CalendarContract.Calendars.NAME,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.Calendars.IS_PRIMARY,
                CalendarContract.Calendars.VISIBLE};
        final Uri calendars = Uri.parse("content://com.android.calendar/calendars");
        final ContentResolver contentResolver = AppController.getInstance().getContentResolver();
        final Cursor managedCursor = contentResolver.query(calendars, projection,/*Calendars.VISIBLE + " = 1"*/ null, null, null);
        Cursor eventCursor = null;
        // Uri.Builder builder = Uri.parse("content://calendar/instances/when").buildUpon();
        //Uri.Builder builder = CalendarContract.Calendars.CONTENT_URI.buildUpon();
//        long now = new Date().getTime();
//        ContentUris.appendId(builder, now - DateUtils.WEEK_IN_MILLIS);
//        ContentUris.appendId(builder, now + DateUtils.WEEK_IN_MILLIS);


        if (managedCursor != null && managedCursor.moveToFirst()) {
            m_calendars = new MyCalendar[managedCursor.getCount()];
            String calID, calName, calAccName, calType, calPrimary, calVisible;
            int cont = 0;
            int NumEvents = 0;
            final int idCol = managedCursor.getColumnIndex(projection[0]);
            final int nameCol = managedCursor.getColumnIndex(projection[1]);
            final int accNameCol = managedCursor.getColumnIndex(projection[2]);
            final int typeCol = managedCursor.getColumnIndex(projection[3]);
            final int primaryCol = managedCursor.getColumnIndex(projection[4]);
            final int visibleCol = managedCursor.getColumnIndex(projection[5]);
            do {
                calID = managedCursor.getString(idCol);
                if (ActivityCompat.checkSelfPermission(AppController.getInstance(), Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                     eventCursor = contentResolver.query(CalendarContract.Events.CONTENT_URI,
                            new String[]{CalendarContract.Events.TITLE}, CalendarContract.Events.CALENDAR_ID + "=" + Long.parseLong(calID),
                            null, null);
                }
                if (eventCursor != null)
                    NumEvents = eventCursor.getCount();
                calName = managedCursor.getString(nameCol);
                calAccName = managedCursor.getString(accNameCol);
                calType = managedCursor.getString(typeCol);
                calPrimary = managedCursor.getString(primaryCol);
                calVisible = managedCursor.getString(visibleCol);
                m_calendars[cont] = new MyCalendar(calName, calID, calAccName, calType, calPrimary, calVisible);
                cont++;
            } while (managedCursor.moveToNext());
            managedCursor.close();
            eventCursor.close();
        }
        return m_calendars;

    }

    // Get local calendar created with AddCalendar
    private long getCalendarId(String AccountName) {
        long id = -1;

        String[] projection = new String[]{CalendarContract.Calendars._ID};
        String selection =
                CalendarContract.Calendars.ACCOUNT_NAME +
                        " = ? AND " +
                        CalendarContract.Calendars.ACCOUNT_TYPE +
                        " = ? ";
        // use the same values as above:
        String[] selArgs =
                new String[]{
                        AccountName,
                        CalendarContract.ACCOUNT_TYPE_LOCAL};

        if (ActivityCompat.checkSelfPermission(AppController.getInstance(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return -1;
        }
        Cursor cursor = null;
        try {
             cursor =
                    AppController.getInstance().getContentResolver().
                            query(CalendarContract.Calendars.CONTENT_URI,
                                    projection,
                                    selection,
                                    selArgs,
                                    null);
            if (cursor.moveToFirst()) {
                id = cursor.getLong(0);

            }
        } catch (Exception e) {
           id = -1;
        } finally {
            if(cursor != null){
                cursor.close();
            }
        }

        return id;
    }

    // Adds a new local calendar (not associated with an account) and returns the Uri ID
    public long AddCalendar(String AccountName, String CalendarName){
        ContentValues values = new ContentValues();
        values.put(
                CalendarContract.Calendars.ACCOUNT_NAME,
                AccountName);
        values.put(
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.ACCOUNT_TYPE_LOCAL);
        values.put(
                CalendarContract.Calendars.NAME,
                CalendarName);
        values.put(
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarName);
        values.put(
                CalendarContract.Calendars.CALENDAR_COLOR,
                0xffff0000);
        values.put(
                CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
                CalendarContract.Calendars.CAL_ACCESS_OWNER);
        values.put(
                CalendarContract.Calendars.OWNER_ACCOUNT,
                AccountName);
        values.put(
                CalendarContract.Calendars.CALENDAR_TIME_ZONE,
                "Europe/Berlin");
        values.put(
                CalendarContract.Calendars.SYNC_EVENTS,
                1);
        values.put(
                CalendarContract.Calendars.VISIBLE,
                1);
        Uri.Builder builder =
                CalendarContract.Calendars.CONTENT_URI.buildUpon();
        builder.appendQueryParameter(
                CalendarContract.Calendars.ACCOUNT_NAME,
                AccountName);
        builder.appendQueryParameter(
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.ACCOUNT_TYPE_LOCAL);
        builder.appendQueryParameter(
                CalendarContract.CALLER_IS_SYNCADAPTER,
                "true");
        Uri uri = AppController.getInstance().getContentResolver().insert(builder.build(), values);

        if (uri==null)
            return -1;
        return Long.valueOf(uri.getLastPathSegment());
    }
}
