package my.cinemax.app.free.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import my.cinemax.app.free.R;

public class FinishActivity extends AppCompatActivity {

    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        ((TextView) findViewById(R.id.tv_finish_title)).setText(getIntent().getExtras().getString("title"));
    }
}
