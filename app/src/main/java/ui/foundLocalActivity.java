package ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import listAdapter.musicListAdapter;

/**
 * Created by jinpei chen on 2017/5/26.
 */

public class foundLocalActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private TextView thepath,seaching,head,allchoose;
    private LinearLayout box;
    private ImageView out;
    private Button begin,sure;
    private ListView musiclist;
    private List<Map<String,Object>> list;
    private List<Map<String,Object>> addnewlist , localList;
    private musicListAdapter musicAdapter;
    private publicData p;
    private List<Map<String,Object>> temlist=new ArrayList<>();//临时存储list
    private boolean all=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.foundlocalmusic);
        p= (publicData) this.getApplication();
        seaching= (TextView) findViewById(R.id.seaching);//
        head= (TextView) findViewById(R.id.head_local);
        thepath= (TextView) findViewById(R.id.what_path);
        out= (ImageView) findViewById(R.id.out_local);
        begin= (Button) findViewById(R.id.begin_seach);
        sure= (Button) findViewById(R.id.sure_list);
        box= (LinearLayout) findViewById(R.id.show_local);
        musiclist= (ListView) findViewById(R.id.show_music);//获取list列表
        allchoose= (TextView) findViewById(R.id.allchoose);
//////////////////////
        localList = p.getLocalMusic();
        addnewlist=new ArrayList<>();
        musicAdapter=new musicListAdapter(this);//list适配器
        musicAdapter.setList(addnewlist);
        musiclist.setAdapter(musicAdapter);//绑定适配器
        musiclist.setOnItemClickListener(this);
        out.setOnClickListener(this);
        begin.setOnClickListener(this);
        sure.setOnClickListener(this);
        sure.setEnabled(false);//先设定为不可选
        allchoose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.out_local:
                p.setAddnewlocalmusic(new ArrayList<Map<String, Object>>());
                finish();
                break;
            case R.id.begin_seach:
                begin.setVisibility(View.GONE);
                seaching.setVisibility(View.VISIBLE);
                thepath.setVisibility(View.VISIBLE);
                new seachmusicThread().start();
                break;//开始搜索
            case R.id.allchoose:
                if(all){
                    allchoose.setText("全选");
                    all=false;
                    for(int i=0;i<addnewlist.size();i++){
                        musicListAdapter.isSelected.put(i, false);
                    }
                    musicAdapter.notifyDataSetChanged();
                }else{
                    allchoose.setText("取消全选");
                    all=true;
                    for(int i=0;i<addnewlist.size();i++){
                        musicListAdapter.isSelected.put(i, true);
                    }
                    musicAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.sure_list:
                List<Map<String,Object>> thelist=new ArrayList<>();
                for(int i=0;i<musicListAdapter.isSelected.size();i++){
                    if(musicListAdapter.isSelected.get(i)){
                        Map<String,Object> m=new HashMap<>();
                        String s=addnewlist.get(i).get("musicname").toString().replaceAll("\\[(.*?)\\]","").replaceAll("[\\[*\\]]","");
                        String[]a=s.split("-");
                        String[]b;
                        String mn="null";
                        if(a.length<2){
                            b=new String[2];
                            b[0]=a[0];
                            b[1]="";
                        }else{
                            mn="";
                            b=new String[a.length];
                            for(int x=0;x<a.length;x++){
                                b[x]=a[x];
                            }
                            for(int y=1;y<b.length;y++){
                                mn+=b[y];
                            }
                        }//分解名称
                        m.put("musician",b[0].trim());
                        m.put("musicname",mn.trim());
                        m.put("path",addnewlist.get(i).get("path").toString());
                        thelist.add(m);
                    }
                }//for循环结束
                if(thelist.size()==0){
                    Toast.makeText(this,"请至少选择一首歌曲",Toast.LENGTH_SHORT).show();
                }else{
                    p.setAddnewlocalmusic(thelist);
                    Intent intent=new Intent();
                    setResult(0,intent);
                    finish();
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        musicListAdapter.ViewHolder vHollder = (musicListAdapter.ViewHolder) view.getTag();
        vHollder.cb.toggle();
        musicListAdapter.isSelected.put(position,vHollder.cb.isChecked());
    }
/*
* ***处理音乐扫描
* */
    private class seachmusicThread extends Thread{
        @Override
        public void run() {
            boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
            if(sdCardExist){
                File sd=Environment.getExternalStorageDirectory();
                readfile(sd.getPath());
                Message m=new Message();
                m.what=1;
                m.obj=temlist;
                myhandler.sendMessage(m);
            }else{
                Toast.makeText(getApplicationContext(),"请插入sd卡",Toast.LENGTH_SHORT).show();
            }
        }//run方法结束
    }/*
    ******扫描音乐线程********
    */
    public void readfile(String p){
        File[] musicfile=new File(p).listFiles();//获取根目录下所有文件夹及文件
        if(musicfile==null){
            return;
        }
        for(int i=0;i<musicfile.length;i++){
            String path=musicfile[i].getAbsolutePath();
            Message m=new Message();
            m.what=0;
            m.obj=path;
            myhandler.sendMessage(m);//发送当前路径
            if(musicfile[i].isFile()&&musicfile[i].getName().endsWith("mp3")){//是文件并且后缀为mp3
                Map<String,Object> map= new HashMap<>();
                map.put("musicname",musicfile[i].getName().replace(".mp3",""));//文件名字
                map.put("path",musicfile[i].getAbsolutePath());
                temlist.add(map);
            }else if(musicfile[i].isDirectory()&&!musicfile[i].getAbsolutePath().endsWith("tencent")){//为文件夹递归扫描
                readfile(musicfile[i].getAbsolutePath());
            }
        }//循环遍历
    }/*
     *************扫描音乐
     */
    List<Map<String, Object>> l;
    Handler myhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    thepath.setText((String)msg.obj);
                    break;
                case 1:
                    l = new ArrayList<>();
                    l = (List<Map<String, Object>>) msg.obj;
                    // 循环比对未添加的音乐
                    for(int i = 0; i < l.size(); i++){
                        for(int j = 0; j < localList.size(); j++){
                            String s=l.get(i).get("musicname").toString().replaceAll("\\[(.*?)\\]","").replaceAll("[\\[*\\]]","");
                            String[]a=s.split("-");
                            String[]b;
                            String mn="";
                            if(a.length<2){
                                b=new String[2];
                                b[0]=a[0];
                                b[1]="";
                            }else{
                                mn="";
                                b=new String[a.length];
                                for(int x=0;x<a.length;x++){
                                    b[x]=a[x];
                                }
                                for(int y=1;y<b.length;y++){
                                    mn+=b[y];
                                }
                            }//分解名称
                            b[0]=b[0].trim();
                            mn=mn.trim();
                            if (b[0].equals(localList.get(j).get("musician").toString())&&mn.equals(localList.get(j).get("musicname").toString())) {
                                l.remove(i);
                                break;
                            }
                        }
                    }
                    head.setText("新增" + l.size() + "首可添加歌曲");
                    for (int i = 0; i < l.size(); i++) {
                        addnewlist.add(l.get(i));
                        musicListAdapter.isSelected.put(i, false);
                    }
                    //p.setAddnewlocalmusic(l);//新添加的数据
                    musicAdapter.notifyDataSetChanged();
                    box.setVisibility(View.GONE);
                    allchoose.setVisibility(View.VISIBLE);
                    sure.setBackgroundColor(Color.parseColor("#FF4040"));
                    sure.setEnabled(true);
                    //Toast.makeText(this,"共扫描到"+l.size()+"歌曲",Toast.LENGTH_SHORT).show();
                    //seaching.setText("共扫描到"+l.size()+"首歌曲");
                    temlist=new ArrayList<>();
                    break;
            }
        }
    };/*
    ******处理子线程改变ui请求********
    */
}
