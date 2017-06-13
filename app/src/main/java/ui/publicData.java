package ui;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by jinpei chen on 2017/5/27.
 */

public class publicData extends Application {
    private List<Map<String, Object>> localMusic=new ArrayList<>();//本地音乐
    private List<Map<String,Object>> addnewlocalmusic=new ArrayList<>();//扫描后添加新的音乐
    public void setLocalMusic(List<Map<String,Object>> localmusic){
        this.localMusic=new ArrayList<>();
        this.localMusic=localmusic;
    }
    public List<Map<String, Object>> getLocalMusic(){
        return this.localMusic;
    }
    public void setAddnewlocalmusic(List<Map<String,Object>> music){
        this.addnewlocalmusic=new ArrayList<>();
        this.addnewlocalmusic=music;
    }
    public List<Map<String, Object>> getAddnewlocalmusic(){
        return this.addnewlocalmusic;
    }
}
