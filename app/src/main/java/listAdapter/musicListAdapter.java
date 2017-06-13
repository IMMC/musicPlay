package listAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ui.R;

/**
 * Created by jinpei chen on 2017/5/26.
 */

public class musicListAdapter extends BaseAdapter {
    private List<Map<String,Object>> musiclist;
    private LayoutInflater inflater;//反射器
    public static Map<Integer, Boolean> isSelected=new HashMap<Integer, Boolean>();
    public musicListAdapter(Context context){
        this.inflater=LayoutInflater.from(context);
    };
    public void setList(List<Map<String, Object>> list) {
        this.musiclist = list;
        for(int i=0;i<list.size();i++){
            isSelected.put(i,false);
        }
    }
    @Override
    public int getCount() {
        return musiclist.size();
    }

    @Override
    public Object getItem(int position) {
        return musiclist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.musicitem,null);
            holder=new ViewHolder();
            holder.cb= (CheckBox) convertView.findViewById(R.id.check_music);
            holder.musicname= (TextView) convertView.findViewById(R.id.music_name);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        Map m=musiclist.get(position);
        holder.cb.setChecked(isSelected.get(position));
        holder.musicname.setText(m.get("musicname").toString());
        return convertView;
    }
    public class ViewHolder{
        public CheckBox cb;
        TextView musicname;
    }/*
    */
}
