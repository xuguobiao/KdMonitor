package com.kido.sdk.kdmonitor.db.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.kido.sdk.kdmonitor.db.KdNote;

import java.util.ArrayList;

public class WriteDataBaseAccess {
    private DataBaseHandler handler = null;
    private static WriteDataBaseAccess writeAccess = null;
    private Context context;

    public WriteDataBaseAccess(Context context) {
        handler = DataBaseHandler.writeInstance(context);
        this.context = context;
    }

    public static WriteDataBaseAccess shareInstance(Context context) {
        if (writeAccess == null) {
            writeAccess = new WriteDataBaseAccess(context);
        }
        return writeAccess;
    }

    /**
     * 数据库插入相关接口
     */
    //添加接口
    public boolean insertData(Object obj) {
        if (obj instanceof KdNote) {
            return insertNote((KdNote) obj);
        } else {
            System.out.println("Unknown object!");
            return false;
        }
    }


    private boolean insertNote(KdNote note) {
        ContentValues values = new ContentValues();
        values.put("firstcloumn", note.getFirstCloumn());
        values.put("secondCloumn", note.getSecondCloumn());
        values.put("thirdCloumn", note.getThirdCloumn());
        values.put("forthCloumn", note.getForthCloumn());
        SQLiteDatabase connection = handler.getWriteConnection(Thread.currentThread().getStackTrace()[2].getMethodName());
        long result = connection.insert("T_Note", null, values);
        if (!connection.inTransaction()) {
            handler.closeConnection(connection, Thread.currentThread().getStackTrace()[2].getMethodName());
        }
        return (result != -1);
    }

    //批量添加接口
    public boolean insertDatas(ArrayList<?> list) {
        if (list.isEmpty()) {
            System.out.println("no datas");
            return false;
        } else {
            if (list.get(0) instanceof KdNote) {
                return insertNotes(list);
            } else {
                System.out.println("Unknown object!");
                return false;
            }
        }
    }

    private boolean insertNotes(ArrayList<?> notes) {
        boolean result = false;
        SQLiteDatabase connection = handler.getWriteConnection(Thread.currentThread().getStackTrace()[2].getMethodName());
        String sql = "insert into T_Note (firstCloumn,secondCloumn,thirdCloumn,forthCloumn) values (?,?,?,?)";
        connection.beginTransaction();
        try {
            SQLiteStatement stmt = connection.compileStatement(sql);
            for (int i = 0; i < notes.size(); i++) {
                KdNote note = (KdNote) notes.get(i);
                stmt.bindString(1, note.getFirstCloumn());
                stmt.bindString(2, note.getSecondCloumn());
                stmt.bindString(3, note.getThirdCloumn());
                stmt.bindString(4, note.getForthCloumn());
                stmt.execute();
                stmt.clearBindings();
            }
            connection.setTransactionSuccessful();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            connection.endTransaction();
            handler.closeConnection(connection, Thread.currentThread().getStackTrace()[2].getMethodName());
        }
        return result;
    }

    public boolean deleteAllNote() {
        SQLiteDatabase connection = handler.getWriteConnection(Thread.currentThread().getStackTrace()[2].getMethodName());
        int result = connection.delete("T_Note", "1=?", new String[]{"1"});
        if (!connection.inTransaction()) {
            handler.closeConnection(connection, Thread.currentThread().getStackTrace()[2].getMethodName());
        }
        return result != -1;
    }
}
