package com.starringharsh.spamblocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

import java.lang.reflect.Method;

/**
 * Created by user2 on 05-09-2017.
 */

public class PhoneCallReceiver extends BroadcastReceiver {
    Context context = null;
    private static final String TAG = "Phone call";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals("android.intent.action.PHONE_STATE"))
            return;
        else {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                answerPhoneHeadsethook(context, intent);
                return;
            }
            else if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                Log.d(TAG, "CALL ANSWERED NOW!!");
                try {
                    synchronized(this) {
                        Log.d(TAG, "Waiting for 10 sec ");
                        this.wait(10000);
                    }
                }
                catch(Exception e) {
                    Log.d(TAG, "Exception while waiting !!");
                    e.printStackTrace();
                }
                disconnectPhoneItelephony(context);
                return;
            }
            else {
                Log.d(TAG, "ALL DONE ...... !!");
            }
        }
    }

    public void answerPhoneHeadsethook(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.d(TAG, "Incoming call from: " + number);
            Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
            buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
            try {
                context.sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");
                Log.d(TAG, "ACTION_MEDIA_BUTTON broadcasted...");
            }
            catch (Exception e) {
                Log.d(TAG, "Catch block of ACTION_MEDIA_BUTTON broadcast !");
            }

            Intent headSetUnPluggedintent = new Intent(Intent.ACTION_HEADSET_PLUG);
            headSetUnPluggedintent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
            headSetUnPluggedintent.putExtra("state", 1); // 0 = unplugged  1 = Headset with microphone 2 = Headset without microphone
            headSetUnPluggedintent.putExtra("name", "Headset");
            // TODO: Should we require a permission?
            try {
                context.sendOrderedBroadcast(headSetUnPluggedintent, null);
                Log.d(TAG, "ACTION_HEADSET_PLUG broadcasted ...");
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
                Log.d(TAG, "Catch block of ACTION_HEADSET_PLUG broadcast");
                Log.d(TAG, "Call Answered From Catch Block !!");
            }
            Log.d(TAG, "Answered incoming call from: " + number);
        }
        Log.d(TAG, "Call Answered using headsethook");
    }

    public static void disconnectPhoneItelephony(Context context) {
        ITelephony telephonyService;
        Log.v(TAG, "Now disconnecting using ITelephony....");
        TelephonyManager telephony = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Log.v(TAG, "Get getTeleService...");
            Class c = Class.forName(telephony.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            telephonyService = (ITelephony) m.invoke(telephony);
            //telephonyService.silenceRinger();
            Log.v(TAG, "Disconnecting Call now...");
            //telephonyService.answerRingingCall();
            //telephonyService.endcall();
            Log.v(TAG, "Call disconnected...");
            telephonyService.endCall();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,
                    "FATAL ERROR: could not connect to telephony subsystem");
            Log.e(TAG, "Exception object: " + e);
        }
    }
}
