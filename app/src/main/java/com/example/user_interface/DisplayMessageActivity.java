package com.example.user_interface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class DisplayMessageActivity extends AppCompatActivity {

    public boolean ekg = false;
    public boolean pox = false;
    public boolean hr = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        final CheckBox cb1 = (CheckBox)findViewById(R.id.checkBox);
        final CheckBox cb2 = (CheckBox)findViewById(R.id.checkBox2);
        final CheckBox cb3 = (CheckBox)findViewById(R.id.checkBox3);

        cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cb1.isChecked()) { ekg = true; }

        }});

        cb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cb2.isChecked()) { pox = true; }

            }});

        cb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (cb3.isChecked()) { hr = true; }

            }});

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.TAG);
        Button button = (Button) findViewById(R.id.establish_connection);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ekg_bool = String.valueOf(ekg);
                String pox_bool = String.valueOf(pox);
                String hr_bool = String.valueOf(hr);

                String message = ekg_bool + ", " + pox_bool + ", " + hr_bool;
                Intent intent = new Intent();
                intent.putExtra("Sensors Data", message);
                setResult(2, intent);
                finish();
            }
        });
    }
}
