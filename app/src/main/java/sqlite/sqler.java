package sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jinpei chen on 2017/5/25.
 */

public class sqler extends SQLiteOpenHelper {
    public sqler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    /*
    * 判断库是否存在****
    * */
    public boolean tableIsExist(String tablename){
        boolean result=false;
        if(tablename.equals("null")){
            return false;
        }
        SQLiteDatabase db=null;
        Cursor cursor=null;//游标
        try{
            db=this.getReadableDatabase();
            String sql="select name from sqlite_master where name ='"+tablename.trim()+"' and type='table'";
            cursor=db.rawQuery(sql,null);
            if(cursor.getCount()!=0){
                result=true;
            }else{
                result=false;
            }
        }catch (Exception e){}
        return result;
    }//
}
