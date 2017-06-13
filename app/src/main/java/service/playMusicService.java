package service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ui.mainActivity;
import ui.publicData;

/**
 * Created by jinpei chen on 2017/6/12.
 */

public class playMusicService extends Service {
    private publicData p;
    private MediaPlayer musicplayer;
    private myreceiver myReceiver;  //自定义广播接收器
    //判断是否处于暂停状态
    private boolean ispause = false;
    private int status = 1;//音乐播放顺序1为循环2为随机3为单曲循环
    private int curt = -1;//当前播放音乐序列号
    private String path;//播放路径
    private List<Map<String,Object>> playlist;//音乐列表
    private int lastposition = -1;// 上一首播放歌曲的列表
    private int msg;//接收的信息
    public static final String UPDATE_ACTION = "com.example.jinpeichen.musicplay.UPDATE_ACTION";  //更新动作
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    // service初始化
    @Override
    public void onCreate() {
        musicplayer=new MediaPlayer();
        p= (publicData) this.getApplication();
        playlist = p.getLocalMusic();
        // 播放完成事件监听
        musicplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i("cjp","status is :"+status);
                if(status == 1){ //循环播放
                    lastposition = curt;
                    curt = curt + 1;
                    if(curt > playlist.size() - 1){
                        curt = 0;
                    }
                    path = playlist.get(curt).get("path").toString();
                    Intent in = new Intent(UPDATE_ACTION);
                    in.putExtra("from",0);
                    in.putExtra("curt",curt);//发送当前播放歌曲
                    sendBroadcast(in);
                    handler.sendEmptyMessage(0);
                }else if(status == 2){
                    if(playlist.size() > 1){//歌曲数量大于1
                        int cx = new Random().nextInt(playlist.size());
                        while (cx == curt){
                            cx = new Random().nextInt(playlist.size());
                        }
                        lastposition = curt;
                        curt = cx;
                    }
                    path = playlist.get(curt).get("path").toString();
                    Intent in = new Intent(UPDATE_ACTION);
                    in.putExtra("from",0);
                    in.putExtra("curt",curt);//发送当前播放歌曲
                    sendBroadcast(in);
                    handler.sendEmptyMessage(0);
                }else if(status == 3){
                    handler.sendEmptyMessage(0);
                }
            }
        });
        myReceiver = new myreceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(mainActivity.CTL_ACTION);
        registerReceiver(myReceiver, filter);
        super.onCreate();
    }
    //service恢复

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        curt = intent.getIntExtra("position", -1);//播放位置
        path = playlist.get(curt).get("path").toString(); // 歌曲路径
        msg = intent.getIntExtra("MSG", -1);
        lastposition = curt -1;
        if(lastposition < 0){
            lastposition = playlist.size() - 1;
        }
        switch (msg) {
            case 0:
                play(0);
                break;
            case 1://暂停播放
                pause();
                break;
            case 2://停止播放
                stop();
                break;
            case 3://继续播放
                resume();
                break;
            case 4:
                lastmusic();
                break;
            case 5:
                nextmusic();
                break;
        }
        return START_NOT_STICKY;
    }
    // pravite function
    // begin play
    private void play (int curtTime){
        try{
            musicplayer.reset();//恢复初始化
            musicplayer.setDataSource(path);
            musicplayer.prepare();//缓冲音乐
            musicplayer.start();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    //
    private void pause(){
        if(musicplayer!=null){
            if(musicplayer.isPlaying())
            {
                musicplayer.pause();
                ispause=true;
            }
        }//
    }/*
    *******暂停播放
    */
    private void stop(){
        if (musicplayer!=null){
            ispause=false;
            musicplayer.pause();
            musicplayer.stop();
            musicplayer.release();
        }
    }/*
    *****停止播放*****
    */
    private void resume(){
        if(ispause){
            musicplayer.start();
            ispause=false;
        }
    }
    /*
    */
    private void lastmusic(){
        if(curt > -1){
            path = playlist.get(curt).get("path").toString(); // 歌曲路径
            handler.sendEmptyMessage(0);
        }
    }
    //下一首歌曲
    private void nextmusic(){
        if(curt > -1){
            path = playlist.get(curt).get("path").toString(); // 歌曲路径
            handler.sendEmptyMessage(0);
        }
    }
    //接受广播消息
    public class myreceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int control = intent.getIntExtra("control", -1);
            //Toast.makeText(getApplicationContext(),""+control,Toast.LENGTH_SHORT).show();
            switch (control){
                case 1: status = 1; break;
                case 2: status = 2; break;
                case 3: status = 3; break;
            }
        }
    }/*
    ******广播接收
    */
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    play(0);
                    break;
                case 1:resume();break;
                case 2:pause();break;
            }
        }
    };/*
    ************
    */
}
