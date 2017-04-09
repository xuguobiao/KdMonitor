package com.kido.sdk.kdmonitor.db.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kido.sdk.kdmonitor.db.KdNote;

import java.util.ArrayList;

public class ReadDataBaseAccess {
    private DataBaseHandler handler = null;
    private static ReadDataBaseAccess readAccess = null;
    private static boolean isConnectionBusy = false;

    protected ReadDataBaseAccess(Context context) {
        handler = DataBaseHandler.readInstance(context);
    }

    public static synchronized ReadDataBaseAccess shareInstance(Context context) {
        readAccess = new ReadDataBaseAccess(context);
        return readAccess;
    }


    //查询所有的note
    public ArrayList<KdNote> loadAll() {
        SQLiteDatabase connection = handler.getReadConnection(Thread.currentThread().getStackTrace()[2].getMethodName());
        Cursor cursor = connection.rawQuery("select * from T_Note", null);
        ArrayList<KdNote> notes = new ArrayList<>();
        while (cursor.moveToNext()) {
            KdNote note = new KdNote();
            note.setFirstCloumn(cursor.getString(1));
            note.setSecondCloumn(cursor.getString(2));
            note.setThirdCloumn(cursor.getString(3));
            note.setForthCloumn(cursor.getString(4));
            notes.add(note);
        }
        cursor.close();
        handler.closeConnection(connection, Thread.currentThread().getStackTrace()[2].getMethodName());
        return notes;
    }
}
