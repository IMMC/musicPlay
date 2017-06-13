package listAdapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ui.R;

/**
 * Created by jinpei chen on 2017/6/11.
 */

public class playListAdapter extends BaseAdapter implements View.OnClickListener  {
    private List<Map<String,Object>> playlist;
    private LayoutInflater inflater;//反射器
    private Dialog mydialogs;
    private Context context;
    private int from;
    public static Map<Integer, Boolean> isplaying=new HashMap<Integer, Boolean>();
    private Callback mycallback;
    public playListAdapter(Context context, int from){
        this.inflater=LayoutInflater.from(context);
        this.context=context;
        this.from = from;
    }
    public void setMycallback(Callback c){
        mycallback=c;
    }
    public void setPlaylist(List<Map<String, Object>> playlist) {
        this.playlist = playlist;
        for(int i=0;i<playlist.size();i++){
            isplaying.put(i,false);
        }
    }
    public interface Callback {
        public void onClick(View v);
    }
    //回调方法
    @Override
    public int getCount() {
        return playlist.size();
    }

    @Override
    public Object getItem(int position) {
        return playlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.playmusiclist,null);
            holder=new ViewHolder();
            holder.playing= (ImageView) convertView.findViewById(R.id.when_playing);
            holder.playname= (TextView) convertView.findViewById(R.id.paly_music_name);
            holder.delete= (ImageView) convertView.findViewById(R.id.delete_music);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        Map m=playlist.get(position);
        if(!m.get("musicname").toString().equals("null")){
            holder.playname.setText(m.get("musician").toString()+" - "+m.get("musicname").toString());
        }else{
            holder.playname.setText(m.get("musician").toString());
        }
        if( from == 1){
            holder.delete.setVisibility(View.INVISIBLE);
        }
        if (isplaying.get(position)){
            holder.playing.setVisibility(View.VISIBLE);
            holder.delete.setVisibility(View.INVISIBLE);
        }else{
            holder.playing.setVisibility(View.INVISIBLE);
            if(from == 0){
                holder.delete.setVisibility(View.VISIBLE);
            }
        }
        holder.delete.setOnClickListener(this);
        holder.delete.setTag(position);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        mycallback.onClick(v);
    }
    /*
    * 点击事件删除
    * */
    public class ViewHolder{
        ImageView playing,delete;
        TextView playname;
    }/*
    */
}
