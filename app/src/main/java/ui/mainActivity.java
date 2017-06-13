package ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import dialog.CustomDialog;
import dialog.mydialog;
import listAdapter.playListAdapter;
import service.playMusicService;
import sqlite.sqler;

/**
 * Created by jinpei chen on 2017/5/25.
 */

public class mainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ImageView moreMusic,how_play,last_music,play_music,next_muisc;
    private Dialog mydialog;
    private Context context;
    private LinearLayout addMusicList;
    private publicData p;
    private List<Map<String,Object>> addnewlist=new ArrayList<>();
    private playListAdapter myplayadaper;
    private List<Map<String,Object>> list;
    private ListView playlist;
    private sqler database;
    private SQLiteDatabase db;
    private playReceiver preceiver;//广播
    private int clickposition;//点击的是第几项
    private int playposition = 0;//播放位置
    private int how = 0;//播放方式
    private boolean isplaying = false;// 是否播放
    private CustomDialog customDialog;
    public static final String CTL_ACTION = "com.example.jinpeichen.musicplay.CTL_ACTION";        //控制动作
    public static final String UPDATE_ACTION = "com.example.jinpeichen.musicplay.UPDATE_ACTION";  //更新动作
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.musicmain);
        //获取控件
        moreMusic = (ImageView) findViewById(R.id.more);
        addMusicList = (LinearLayout) findViewById(R.id.new_musiclist);
        playlist = (ListView) findViewById(R.id.music_playlist);
        how_play = (ImageView) findViewById(R.id.how_play);
        last_music = (ImageView) findViewById(R.id.last_music);
        play_music = (ImageView) findViewById(R.id.play_music);
        next_muisc = (ImageView) findViewById(R.id.next_music);
        //
        p = (publicData) this.getApplication();
        list = p.getLocalMusic();
        if(list.size() > 0){ // 音乐存在
            addMusicList.setVisibility(View.GONE);
        }
        db = this.openOrCreateDatabase("myMusic",MODE_PRIVATE,null);//判断数据库是否存在不存在就创建
        database = new sqler(this,"myMusic",null,1);
        context = this;
        ///////////////
        //播放列表listview设置点击事件
        playlist.setOnItemClickListener(this);
        moreMusic.setOnClickListener(this);
        addMusicList.setOnClickListener(this);
        how_play.setOnClickListener(this);
        last_music.setOnClickListener(this);
        play_music.setOnClickListener(this);
        next_muisc.setOnClickListener(this);
        //listview设置适配器
        myplayadaper = new playListAdapter(this,0);
        myplayadaper.setPlaylist(list);
        playlist.setAdapter(myplayadaper);
        //设置广播
        preceiver=new playReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_ACTION);
        registerReceiver(preceiver,filter);
        //点击删除
        myplayadaper.setMycallback(new playListAdapter.Callback() {
            //点击事件
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.delete_music:
                        clickposition = (int) v.getTag();
                        if(clickposition == playposition){//点击的是当前播放项

                        }else{
                            customDialog = new CustomDialog(context,R.style.MyDialog, new CustomDialog.LeaveMyDialogListener() {
                                @Override
                                //选择框的弹出
                                public void onClick(View view) {
                                    switch (view.getId()){
                                        case R.id.custom_sure:
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    deleteMusic();
                                                    handler.sendEmptyMessage(2);
                                                }
                                            }).start();//删除一首歌曲
                                            customDialog.dismiss();
                                            break;
                                        case R.id.custom_out:
                                            customDialog.dismiss();
                                            break;
                                    }
                                }
                                //
                            });
                            customDialog.show();
                        }
                        break;
                }
            }
            ///
        });
        //

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(preceiver);
    }

    //点击事件
    @Override
    public void onClick(View v) {
        final Intent intent = new Intent(this,playMusicService.class);//发送广播消息
        switch (v.getId()){
            case R.id.new_musiclist:
                Intent local=new Intent(context,foundLocalActivity.class);
                startActivityForResult(local,0);
                break;
            case R.id.more:
                mydialog=new mydialog(this,R.style.MyDialog,new mydialog.LeaveMyDialogListener() {

                    @Override
                    public void onClick(View view) {
                        switch (view.getId()){
                            case R.id.shao_s:
                                mydialog.dismiss();
                                Intent local=new Intent(context,foundLocalActivity.class);
                                startActivityForResult(local,0);
                                break;
                            case R.id.goSeach:
                                mydialog.dismiss();
                                Intent ins=new Intent(context,seachActivity.class);
                                startActivity(ins);
                                break;
                        }
                    }
                });
                //显示dialog
                Window dialogWindow = mydialog.getWindow();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
                int w;
                WindowManager m = this.getWindowManager();
                Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
                w=d.getWidth();
                Rect frame = new Rect();
                this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                int h = frame.top;
                lp.x=w-190;
                lp.y=h+50;
                dialogWindow.setAttributes(lp);
                mydialog.show();
                break;
            case R.id.how_play:
                how++;
                if(how>2){
                    how=0;
                }
                switch (how){
                    case 0:
                        how_play.setImageResource(R.mipmap.xunhuan);
                        Toast.makeText(this,"循环播放",Toast.LENGTH_SHORT).show();
                        readone();
                        break;
                    case 1:
                        how_play.setImageResource(R.mipmap.radommusic);
                        Toast.makeText(this,"随机播放",Toast.LENGTH_SHORT).show();
                        shuffleMusic();
                        break;
                    case 2:
                        how_play.setImageResource(R.mipmap.onemusic);
                        Toast.makeText(this,"单曲循环",Toast.LENGTH_SHORT).show();
                        onemusic();
                        break;
                }
                break;
            case R.id.play_music:
                if(isplaying){//正在播放
                    play_music.setImageResource(R.mipmap.stopmusic);
                    intent.setAction("MUSIC_SERVICE");
                    intent.putExtra("action",1);
                    intent.putExtra("position", playposition);
                    intent.putExtra("MSG", 1);//停止播放
                    startService(intent);
                    isplaying = false;
                }else{
                    play_music.setImageResource(R.mipmap.playmusic);
                    isplaying = true;
                    intent.setAction("MUSIC_SERVICE");
                    intent.putExtra("action",1);
                    intent.putExtra("position", playposition);
                    intent.putExtra("MSG", 3);//继续播放
                    startService(intent);
                }
                break;
            case R.id.last_music:
                play_music.setImageResource(R.mipmap.playmusic);
                isplaying = true;
                lastMusic();
                break;
            case R.id.next_music:
                play_music.setImageResource(R.mipmap.playmusic);
                isplaying = true;
                nextMuisc();
                break;
        }
    }
    // activity回传数据
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        List<Map<String,Object>> l=new ArrayList<>();
        l=p.getAddnewlocalmusic();//获取需要添加的数据
        addnewlist=new ArrayList<>();
        addnewlist=l;
        // 新线程写入歌曲信息到数据库
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<addnewlist.size();i++){
                    // 保存数据到本地数据库
                    ContentValues cv=new ContentValues();
                    cv.put("musician",addnewlist.get(i).get("musician").toString());
                    cv.put("musicname",addnewlist.get(i).get("musicname").toString().replaceAll("'",""));
                    cv.put("path",addnewlist.get(i).get("path").toString());
                    db.insert("localMusic",null,cv);//保存音乐列表到本地数据库
                    list.add(addnewlist.get(i));
                    int x=myplayadaper.getCount()-1;
                    playListAdapter.isplaying.put(x,false);
                }
                p.setLocalMusic(list);
                addMusicList.setVisibility(View.GONE);
                Looper.prepare();
                handler.sendEmptyMessage(0);
                Looper.loop();
            }
        }).start();

    }
    // 列表点击
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        for (int i = 0; i < playlist.getCount(); i++) {
            playListAdapter.isplaying.put(i, false);
        }
        playposition = position;
        playListAdapter.isplaying.put(position, true);
        myplayadaper.notifyDataSetChanged();
        play();
        if (isplaying) {
        } else {
            play_music.setImageResource(R.mipmap.playmusic);
            isplaying = true;
        }
    }
    //private function
    private void deleteMusic(){
        if(clickposition<playposition){
            playposition-=1;
        }
        sqlDelete(clickposition);//从本地数据库删除数据
        list.remove(clickposition);//移除要删除的项
        playListAdapter.isplaying=new HashMap<Integer, Boolean>();
        for(int i=0;i<list.size();i++){
            if(i!=playposition)
                playListAdapter.isplaying.put(i,false);
            else
                playListAdapter.isplaying.put(i,true);
        }
        p.setLocalMusic(list);
        handler.sendEmptyMessage(0);
    }
    //delete local sql music
    public void sqlDelete(int position){
        String c=list.get(position).get("musicname").toString();
        String b=list.get(position).get("musician").toString();
        String sql="select * from localMusic where musicname='"+c+"'and musician='"+b+"'";
        Cursor cursor=db.rawQuery(sql,null);
        if(cursor.getCount()>0){
            sql="delete from localMusic where musicname='"+c+"' and musician='"+b+"'";
            db.execSQL(sql);
        }
    }
////
// music control
    // begin play music
public void play(){
    Intent in=new Intent(this, playMusicService.class);
    in.setAction("MUSIC_SERVICE");
    in.putExtra("position",playposition);
    in.putExtra("MSG",0);
    startService(in);
}
//
    /*
    *****开始播放*****
    */
public void readone(){
    Intent in=new Intent();
    in.setAction(CTL_ACTION);
    in.putExtra("control",1);
    sendBroadcast(in);
}/*
    *******全部循环
    */
    public void shuffleMusic() {
        Intent in=new Intent();
        in.setAction(CTL_ACTION);
        in.putExtra("control",2);
        sendBroadcast(in);
    }/*
    *****随机播放
    */
    public void onemusic(){
        Intent in=new Intent();
        in.setAction(CTL_ACTION);
        in.putExtra("control",3);
        sendBroadcast(in);
    }/*
    ******单曲播放******
    */
    //下一首歌曲
    public void nextMuisc(){
        if(how == 1 && list.size() > 3){
            if(playposition == list.size() - 1){
                playposition = 0;
            }else{
                playposition = new Random().nextInt(list.size() - playposition -1) + playposition + 1;
            }
        }else{
            playposition = playposition + 1;
            if(playposition>list.size()-1){
                playposition = 0;
            }
        }
        for(int i = 0;i<playlist.getCount();i++){
            playListAdapter.isplaying.put(i,false);
        }
        playListAdapter.isplaying.put(playposition,true);//改变状态
        myplayadaper.notifyDataSetChanged();
        Intent in=new Intent(this,playMusicService.class);
        in.setAction("MUSIC_SERVICE");
        in.putExtra("action",1);
        in.putExtra("position",playposition);
        in.putExtra("MSG",5);
        startService(in);
    }
    //上已首歌曲
    private void lastMusic(){
        if(how == 1 && list.size() > 3){
            if(playposition == 0){
                playposition = list.size() - 1;
            }else{
                playposition = new Random().nextInt(playposition);
            }
        }else{
            playposition = playposition - 1;
            if(playposition < 0){
                playposition = list.size() - 1;
            }
        }
        for(int i=0;i<playlist.getCount();i++){
            playListAdapter.isplaying.put(i,false);
        }
        playListAdapter.isplaying.put(playposition,true);//改变状态
        myplayadaper.notifyDataSetChanged();
        Intent in=new Intent(this,playMusicService.class);
        in.setAction("MUSIC_SERVICE");
        in.putExtra("action",1);
        in.putExtra("position",playposition);
        in.putExtra("MSG",4);
        startService(in);
    }
//接收广播
    public class playReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int from = intent.getIntExtra("from", -1);
            if (from == 0) {
                int c = intent.getIntExtra("curt", -1);
                Message m = new Message();
                m.what = 1;
                m.obj = c;
                handler.sendMessage(m);
            }
        }//
    }/*
    *****接收回传广播
    */
    //handler消息处理
    Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    myplayadaper.notifyDataSetChanged();
                    break;
                case 1:
                    int c = (int) msg.obj;
                    for(int i = 0; i < playlist.getCount(); i++){
                        playListAdapter.isplaying.put(i,false);
                    }
                    playposition = c;
                    playListAdapter.isplaying.put(c,true);
                    myplayadaper.notifyDataSetChanged();
                    isplaying = true;
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(),"删除成功",Toast.LENGTH_SHORT).show();
                    break;
            }
        }//
    };
}
