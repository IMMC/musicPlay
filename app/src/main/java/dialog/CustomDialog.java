package dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import ui.R;

/**
 * Created by jinpei chen on 2017/6/11.
 */

public class CustomDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private TextView custom_text,custom_sure,custom_out;
    private LeaveMyDialogListener listener;
    public CustomDialog(Context context) {
        super(context);
    }
    public CustomDialog(Context context,int theme,LeaveMyDialogListener listener){
        super(context,theme);
        this.listener=listener;
        this.context=context;
    }
    @Override
    public void onClick(View v) {
        listener.onClick(v);
    }
    public interface LeaveMyDialogListener{
        public void onClick(View view);
    }/*
    ************
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.customdialog);
        custom_text= (TextView) findViewById(R.id.custom_text);
        custom_sure= (TextView) findViewById(R.id.custom_sure);
        custom_out= (TextView) findViewById(R.id.custom_out);
        custom_text.setText("确认把该歌曲从歌单中移除？");
        custom_sure.setOnClickListener(this);
        custom_out.setOnClickListener(this);
    }/*
    *******载入自定义样式
    */
}
