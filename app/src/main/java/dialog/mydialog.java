package dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;

import ui.R;

/**
 * Created by jinpei chen on 2017/5/26.
 */

public class mydialog extends Dialog implements View.OnClickListener{
    private Context context;
    private LinearLayout l1,l2;
    private LeaveMyDialogListener listener;
    public mydialog(@NonNull Context context) {
        super(context);
        this.context=context;
    }
    public mydialog(Context context, int theme, LeaveMyDialogListener listener){
        super(context, theme);
        this.context = context;
        this.listener = listener;
    }
    @Override
    public void onClick(View v) {
        listener.onClick(v);
    }
    public interface LeaveMyDialogListener{
        public void onClick(View view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog);
        l1=(LinearLayout) findViewById(R.id.shao_s);
        l2= (LinearLayout) findViewById(R.id.goSeach);
        l1.setOnClickListener(this);
        l2.setOnClickListener(this);
    }
}
