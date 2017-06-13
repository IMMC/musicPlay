package ui;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.os.Handler;

import listAdapter.playListAdapter;
import sqlite.sqler;

/**
 * Created by jinpei chen on 2017/6/12.
 */

public class seachActivity extends Activity implements View.OnClickListener, TextWatcher {
    private ImageView back;
    private EditText seachText;
    private ListView seachCount;
    private TextView msg;
    private sqler database;
    private SQLiteDatabase db;
    private playListAdapter seachadaper;
    private List<Map<String, Object>> seachList = new ArrayList<>(),
            ls = new ArrayList<>();
    private String text;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.seach);
        back = (ImageView) findViewById(R.id.seach_back);
        seachText = (EditText) findViewById(R.id.seach_text);
        seachCount = (ListView) findViewById(R.id.return_seach);
        msg = (TextView) findViewById(R.id.seach_ts);
        db = this.openOrCreateDatabase("myMusic",MODE_PRIVATE,null);//判断数据库是否存在不存在就创建
        database = new sqler(this,"myMusic",null,1);
        //设置事件
        back.setOnClickListener(this);
        seachText.addTextChangedListener(this);
        seachadaper = new playListAdapter(this,1);
        seachadaper.setPlaylist(seachList);
        seachCount.setAdapter(seachadaper);
    }

    //点击处理
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.seach_back:
                finish();
                break;
        }
    }
    //文本内容变化处理
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        text = seachText.getText().toString();
        if ( text.length() > 0 ) {//有文字再搜索
            seachList.clear();
            handler.sendEmptyMessage(3);
            seachMusic(text);
            if(seachList.size() > 0){
                handler.sendEmptyMessage(0);
            }else{
                handler.sendEmptyMessage(1);
            }
        }
    }
    //handler
    Handler handler = new Handler(){
        public void handleMessage(Message m) {
            super.handleMessage(m);
            switch (m.what){
                case 0:
                    seachadaper.notifyDataSetChanged();
                    seachCount.setVisibility(View.VISIBLE);
                    msg.setVisibility(View.GONE);
                    break;
                case 1:
                    seachCount.setVisibility(View.GONE);
                    seachadaper.notifyDataSetChanged();
                    msg.setText("未找到与'"+text+"'相关歌曲");
                    msg.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    seachCount.setVisibility(View.GONE);
                    msg.setText("搜索中.....");
                    msg.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    seachadaper.notifyDataSetChanged();
                    break;
            }
        }
    };
    //private function
    //以歌名查询
    private void seachMusic(String name){
        //模糊查询
        String sql="select * from localMusic where musicname like '%"+name+"%'";
        Cursor cursor=db.rawQuery(sql,null);
        while (cursor.moveToNext()){
            Map<String,Object> m=new HashMap<>();
            m.put("musician",cursor.getString(cursor.getColumnIndex("musician")));
            m.put("musicname",cursor.getString(cursor.getColumnIndex("musicname")));
            m.put("path",cursor.getString(cursor.getColumnIndex("path")));
            seachList.add(m);
            int x=seachadaper.getCount()-1;
            playListAdapter.isplaying.put(x,false);
        }
        seachName(name);
    }
    //以歌手查询
    private boolean has = false;
    private void seachName(String name){
        String sql="select * from localMusic where musician like '%"+name+"%'";
        Cursor cursor=db.rawQuery(sql,null);
        while (cursor.moveToNext()){
            String musicname = cursor.getString(cursor.getColumnIndex("musicname"));
            String musician = cursor.getString(cursor.getColumnIndex("musician"));
            for(int i = 0; i < seachList.size(); i++){
                if(musicname.equals(seachList.get(i).get("musicname").toString())&&musician.equals(seachList.get(i).get("musician").toString())){
                    has = true;
                    break;
                }
            }
            if(!has){
                Map<String,Object> m=new HashMap<>();
                m.put("musician",musician);
                m.put("musicname",musicname);
                m.put("path",cursor.getString(cursor.getColumnIndex("path")));
                seachList.add(m);
                int x=seachadaper.getCount()-1;
                playListAdapter.isplaying.put(x,false);
            }
        }//查询到的数据
    }

}
