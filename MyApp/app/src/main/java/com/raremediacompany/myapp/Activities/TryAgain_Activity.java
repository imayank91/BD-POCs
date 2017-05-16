package com.raremediacompany.myapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.raremediacompany.myapp.R;

public class TryAgain_Activity extends AppCompatActivity {
private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try_again_);

        button = (Button) findViewById(R.id.check_In_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TryAgain_Activity.this, CheckInSuccessfull.class);
                startActivity(intent);
            }
        });
    }
}
