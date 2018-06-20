package pl.ilo.erasmus.helfer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class Main2Activity extends Activity implements
        android.view.View.OnClickListener{


    public Activity c;
    public Button yes, no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        setContentView(pl.ilo.erasmus.helfer.R.layout.activity_main2);
        yes = (Button) findViewById(pl.ilo.erasmus.helfer.R.id.btn_yes);
        no = (Button) findViewById(pl.ilo.erasmus.helfer.R.id.btn_no);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case pl.ilo.erasmus.helfer.R.id.btn_yes:
                break;
            case pl.ilo.erasmus.helfer.R.id.btn_no:
                this.finish();
                break;
            default:
                break;
        }
        this.finish();
    }

}
