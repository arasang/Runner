package park.sangeun.runner.Common;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by user on 2017-03-31.
 */

public class DBManager extends SQLiteOpenHelper {

    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE RECORD_TABLE (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ACTIVITY VARCHAR(2), " +
                "DISTANCE REAL, " +
                "TIME REAL," +
                "CALORIES INTEGER," +
                "RECORD_DATE VARCHAR(9)" +
                ");";

        sqLiteDatabase.execSQL(sql);

        sql = "CREATE TABLE USER_INFO (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "USER_IMAGE BLOB," +
                "USER_NAME VARCHAR(10)," +
                "USER_FIRST_NAME VARCHAR(10)," +
                "USER_GIVEN_NAME VARCHAR(20)," +
                "USER_EMAIL VARCHAR(25), " +
                "USER_AGE LONG(3), " +
                "USER_SEX VARCHAR(1), " +
                "USER_HEIGHT LONG(3), " +
                "USER_WEIGHT LONG(3)," +
                "ANNUAL_GOAL LONG(4)" +
                ");";

        sqLiteDatabase.execSQL(sql);

        sql = "CREATE TABLE RECORD_DETAIL (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "START_LATITUDE DOUBLE, " +
                "START_LONGITUDE DOUBLE, " +
                "END_LATITUDE DOUBLE, " +
                "END_LONGITUDE DOUBLE, " +
                "HIGHEST_ALTITUDE DOUBLE, " +
                "LOWEST_ALTITUDE DOUBLE, " +
                "AVG_SPEED DOUBLE, " +
                "HIGHEST_SPEED DOUBLE(3), " +
                "TOTAL_DISTANCE DOUBLE(5), " +
                "TOTAL_CALORIES INTEGER(5)"+
                ");";

        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sql = "DROP TABLE IF EXISTS RECORD_TABLE;";
        sqLiteDatabase.execSQL(sql);

        sql = "DROP TABLE IF EXISTS USER_INFO;";
        sqLiteDatabase.execSQL(sql);

        sql = "DROP TABLE IF EXISTS RECODE_DETAIL";
        sqLiteDatabase.execSQL(sql);

        onCreate(sqLiteDatabase);
    }

    public void onInsert(String query){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
        db.close();
    }


    public void onInsertUser(ArrayList<Object> paramsValue){
        SQLiteDatabase db = getWritableDatabase();

        String query = "INSERT INTO USER_INFO(" +
                "USER_IMAGE, " +
                "USER_NAME, " +
                "USER_FIRST_NAME, " +
                "USER_GIVEN_NAME, " +
                "USER_EMAIL, " +
                "USER_AGE, " +
                "USER_SEX, " +
                "USER_HEIGHT, " +
                "USER_WEIGHT" +
                ") VALUES(?,?,?,?,?,?,?,?,?);";

        SQLiteStatement insertState = db.compileStatement(query);
        insertState.clearBindings();
        insertState.bindBlob(1, (byte[]) paramsValue.get(0));
        insertState.bindString(2, (String) paramsValue.get(1));
        insertState.bindString(3, (String) paramsValue.get(2));
        insertState.bindString(4, (String) paramsValue.get(3));
        insertState.bindString(5, (String) paramsValue.get(4));
        insertState.bindLong(6, (long) paramsValue.get(5));
        insertState.bindString(7, (String) paramsValue.get(6));
        insertState.bindLong(8, (long) paramsValue.get(7));
        insertState.bindLong(9, (long) paramsValue.get(8));
        insertState.executeInsert();
        db.close();
    }

    public void onUpdateProfile(ArrayList<Object> arrayList){
        SQLiteDatabase db = getWritableDatabase();

        String query = "UPDATE USER_INFO SET USER_IMAGE=?, USER_NAME=?, USER_FIRST_NAME=?, USER_GIVEN_NAME=?, USER_EMAIL=?, USER_SEX=?, USER_HEIGHT=?, USER_WEIGHT=? WHERE _id=1;";

        SQLiteStatement updateState = db.compileStatement(query);
        updateState.clearBindings();
        updateState.bindBlob(1, (byte[]) arrayList.get(0));
        updateState.bindString(2, (String) arrayList.get(1));
        updateState.bindString(3, (String) arrayList.get(2));
        updateState.bindString(4, (String) arrayList.get(3));
        updateState.bindString(5, (String) arrayList.get(4));
        updateState.bindString(6, (String) arrayList.get(5));
        updateState.bindLong(7, (long) arrayList.get(6));
        updateState.bindLong(8, (long) arrayList.get(7));

        updateState.executeUpdateDelete();
        db.close();
    }

    public void onUpdateGoal(long goal){
        SQLiteDatabase db = getWritableDatabase();

        String query = "UPDATE USER_INFO SET ANNUAL_GOAL=? WHERE _id=1;";

        SQLiteStatement updateState = db.compileStatement(query);

        updateState.clearBindings();
        updateState.bindLong(1, goal);

        updateState.executeUpdateDelete();
        db.close();
    }


    public HashMap<String,Object> onSelectUser(String tableName, String[] paramSelect, HashMap<String,String> paramWhere) throws Exception {
        HashMap<String,Object> resultSelect = new HashMap<String, Object>();

        SQLiteDatabase db = getReadableDatabase();

        String strSelect = castParamsSelect(paramSelect);
        String strWhere = castParamsWhere(paramWhere);

        String query = "SELECT " + strSelect + " FROM " + tableName + strWhere + ";";

        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            resultSelect.put(paramSelect[0], cursor.getBlob(0));
            resultSelect.put(paramSelect[1], cursor.getString(1));
            resultSelect.put(paramSelect[2], cursor.getString(2));
            resultSelect.put(paramSelect[3], cursor.getString(3));
            resultSelect.put(paramSelect[4], cursor.getString(4));
            resultSelect.put(paramSelect[5], cursor.getLong(5));
            resultSelect.put(paramSelect[6], cursor.getString(6));
            resultSelect.put(paramSelect[7], cursor.getLong(7));
            resultSelect.put(paramSelect[8], cursor.getLong(8));
            resultSelect.put(paramSelect[9], cursor.getLong(9));
        }

        db.close();
        return resultSelect;
    }


    public HashMap<String,Object> onSelect(String tableName, String[] paramSelect, HashMap<String,String> paramWhere) throws Exception {
        HashMap<String,Object> resultSelect = new HashMap<String, Object>();

        SQLiteDatabase db = getReadableDatabase();

        String strSelect = castParamsSelect(paramSelect);
        String strWhere = castParamsWhere(paramWhere);

        String query = "SELECT " + strSelect + " FROM " + tableName + strWhere + ";";

        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            for (int i=0; i<paramSelect.length; i++){
                Log.d("상은", "상은 : " + cursor.getType(i));
                resultSelect.put(paramSelect[i], cursor.getString(i));
            }
        }

        db.close();
        return resultSelect;
    }

    public ArrayList<HashMap<String, Object>> onSelectRecord(String tableName, String[] paramsSelect, HashMap<String,String> paramsWhere) throws Exception{
        ArrayList<HashMap<String,Object>> result = new ArrayList<HashMap<String,Object>>();

        HashMap<String,Object> value = new HashMap<String,Object>();

        SQLiteDatabase db = getReadableDatabase();

        String strSelect = castParamsSelect(paramsSelect);
        String strWhere = castParamsWhere(paramsWhere);

        String query = "SELECT " + strSelect + " FROM " + tableName + strWhere + ";";

        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            for (int i=0; i<paramsSelect.length; i++){
                value.put(paramsSelect[i], cursor.getString(i));
            }
            result.add(value);
        }

        return result;
    }

    public Cursor getCursor(String tableName, String[] paramSelect, HashMap<String,String> paramWhere) throws Exception {
        HashMap<String,String> resultSelect = new HashMap<String, String>();

        SQLiteDatabase db = getReadableDatabase();

        String strSelect = castParamsSelect(paramSelect);
        String strWhere = castParamsWhere(paramWhere);

        String query = "SELECT " + strSelect + " FROM " + tableName + strWhere + ";";

        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            for (int i=0; i<paramSelect.length; i++){
                resultSelect.put(paramSelect[i], cursor.getString(i));
            }
        }

        db.close();
        return cursor;
    }


    public void onDelete(String tableName, HashMap<String,String> paramWhere){
        SQLiteDatabase db = getReadableDatabase();

        String query = "DELETE FROM " + tableName + paramWhere;

        db.execSQL(query);
        db.close();
    }

    public static String castParamsUpdate(HashMap<String,String> params){
        if( params == null )
        {
            return "" ;
        }
        StringBuilder sb = new StringBuilder() ;

        for( Iterator<String> i = params.keySet().iterator()   ; i.hasNext()  ;  )
        {
            String key = (String) i.next();
            sb.append(key);
            sb.append(" = ");
            sb.append(String.valueOf(params.get(key)));
            if (i.hasNext())
                sb.append(" , ");
        }
        return sb.toString();
    }

    public static String castParamsSelect(String[] param){
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<param.length; i++){
            if(i > 0)
                sb.append(",");
            sb.append(" " + param[i]);
        }
        return sb.toString();

    }

    public static String castParamsWhere(HashMap<String,String> param) throws Exception{
        if (param == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        if (param.keySet().iterator().hasNext()) {
            sb.append(" WHERE ");
        }

        Iterator<String> iterator = param.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            sb.append(key);
            sb.append("=");
            sb.append("'" + URLEncoder.encode(String.valueOf(param.get(key)), "UTF-8") + "'");

            if (iterator.hasNext()) {
                sb.append("and");
            }
        }

        return sb.toString();
    }
}
