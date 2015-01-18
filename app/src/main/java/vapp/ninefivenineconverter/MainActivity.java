package vapp.ninefivenineconverter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    protected ConverterTask ctask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button1 = (Button) findViewById(R.id.convertNine);
        Button button2 = (Button) findViewById(R.id.addNine);
        Button button3 = (Button) findViewById(R.id.addZeroNine);
        TextView textView = (TextView) findViewById(R.id.text);
        ctask = new ConverterTask(getApplicationContext());
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        textView.setOnClickListener(this);
        setupActionBar();
    }

    private void setupActionBar() {
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#ff9800"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.convertNine:
                ctask.setActionCode(0);
                ctask.execute(0);
                break;
            case R.id.addNine:
                ctask.setActionCode(1);
                ctask.execute(0);
                break;
            case R.id.addZeroNine:
                ctask.setActionCode(2);
                ctask.execute(0);
                break;
            case R.id.text:
                Log.v("run", "run");
                Intent intent = getOpenFacebookIntent(getApplicationContext());
                startActivity(intent);
                break;
        }
    }

    public static Intent getOpenFacebookIntent(Context context) {
        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/100000857871556"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/aungkyawrex"));
        }
    }
}
