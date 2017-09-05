package com.starringharsh.spamblocker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bFakeCall = (Button) findViewById(R.id.bFakeCall);

        bFakeCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.PHONE_STATE");
                intent.putExtra(TelephonyManager.EXTRA_STATE, TelephonyManager.CALL_STATE_RINGING);
                intent.putExtra("EXTRA_INCOMING_NUMBER", "1234567890");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                sendBroadcast(intent);
            }
        });


    }
}
