package com.example.android.finalhikaway;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity {

    private ImageView back,status;
    private TextView panic;
    private String phoneNumber = "09957663043",address,dummyText = "",dustText = "",humidityText = "",temperatureText = "",message = "";
    private String smsMessage = "WARNING!!! The user of the linked phone number 09957663045 is in danger.\n\nLatitude: 14.590577.\nLongitude: 120.978092";

    private static final UUID myUUID =  UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private SendReceive sendReceive;
    private Handler handler;
    private BluetoothAdapter myBluetoothAdapter;
    private BluetoothSocket bluetoothSocket;

    private TextView dust,humidity,temp;
    private int dustAboveCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        connectBluetooth();
        setupViews();
        requestPermission();
        activateListeners();
    }

    private void connectBluetooth() {

        address = getIntent().getStringExtra("bluetooth_address");
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice bt = myBluetoothAdapter.getRemoteDevice(address);

        try{

            bluetoothSocket = bt.createInsecureRfcommSocketToServiceRecord(myUUID);
            bluetoothSocket.connect();

            sendReceive = new SendReceive(bluetoothSocket);
            sendReceive.start();


        }catch(IOException e){
            e.printStackTrace();
        }


    }

    private void requestPermission() {

        if (ContextCompat.checkSelfPermission(HomeActivity.this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this,
                    Manifest.permission.SEND_SMS)) {

            } else {

                ActivityCompat.requestPermissions(HomeActivity.this,
                        new String[]{Manifest.permission.SEND_SMS},
                        0);

            }
        } else {

        }

    }

    private void activateListeners() {

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        panic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                askConfirmation();

            }
        });


    }

    private void askConfirmation() {

        Log.d("CONFIRMATION_CHECK","PRESSED!");

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Are you having an asthma attack?");
        builder.setMessage("Doing so will notify the linked phone number.");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                sendMessageToLinkedNumber();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void sendMessageToLinkedNumber() {

        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
                SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        // ---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        ContentValues values = new ContentValues();

                        values.put("address",phoneNumber);
                        values.put("body",smsMessage);

                        getContentResolver().insert(
                                Uri.parse("content://sms/sent"), values);
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, smsMessage, sentPI, deliveredPI);


    }

    private void setupViews() {

        back = (ImageView) findViewById(R.id.imageViewHomeBack);
        panic = (TextView) findViewById(R.id.textViewHomePanic);

        dust = (TextView) findViewById(R.id.textViewGoDust);
        temp = (TextView) findViewById(R.id.textViewGoTemp);
        humidity = (TextView) findViewById(R.id.textViewGoHumidity);

        status = (ImageView) findViewById(R.id.imageViewHomeStatus);

        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 1:
                        byte[] readBuff = (byte[]) msg.obj;
                        String tempMessage = new String(readBuff, 0, msg.arg1);
                        Log.d("MAIN", "MESSAGE IS " + message);
                        //-----------HMC INTEGRATION--------------//
                        message += tempMessage;

                        if(message.charAt(message.length() - 1) == '>') {

                            for (int x = 0; x < message.length(); x++) {

                                if (message.charAt(x) == '<') {
                                    dummyText = "";
                                } else if (message.charAt(x) == ':') {

                                    dustText = dummyText;
                                    dust.setText(dustText);
                                    dummyText = "";
                                } else if (message.charAt(x) == ';') {

                                    temperatureText = dummyText;
                                    temp.setText(temperatureText);
                                    dummyText = "";

                                } else if (message.charAt(x) == '>') {

                                    humidityText = dummyText;
                                    humidity.setText(humidityText);
                                    dummyText = "";

                                } else {
                                    dummyText += message.charAt(x);
                                }

                            }

                            Log.d("VALUE_CHECK",dustText + " " + temperatureText + " " + humidityText);

                            double dustInt = Double.parseDouble(dustText);
                            double tempInt = Double.parseDouble(temperatureText);
                            double humidityInt = Double.parseDouble(humidityText);

                            if(dustInt >= 40){
                                dustAboveCount++;
                            }else{
                                dustAboveCount = 0;
                            }

                            if(dustAboveCount >= 2){
                                vibratePhone();
                            }
                            else if(tempInt < 10 && humidityInt < 10){
                                vibratePhone();
                            }else if(tempInt < 37 && humidityInt < 15){
                                vibratePhone();
                            }else if(tempInt < 10 && humidityInt >= 50){
                                vibratePhone();
                            }else{

                                status.setImageResource(R.drawable.go_clean);

                            }

                            message = "";

                        }

                }
            }
        };

    }

    private void vibratePhone() {

        status.setImageResource(R.drawable.go_hazardous);

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            vibrator.vibrate(VibrationEffect.createOneShot(4000,VibrationEffect.DEFAULT_AMPLITUDE));
        }else{
            vibrator.vibrate(4000);
        }

        dustAboveCount = 0;


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {

                }
                return;
            }

        }
    }

    public class SendReceive extends Thread{

        InputStream inputStream;
        OutputStream outputStream;
        BluetoothSocket socket;

        public SendReceive(BluetoothSocket socket){

            this.socket = socket;
            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        public void run(){
            byte[] buffer = new byte[256];
            int bytes;

            while(true){
                try{

                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(1,bytes,-1,buffer).sendToTarget();

                }catch (Exception e){
                    break;
                }
            }

        }

        public void write(byte[] bytes){


        }

    }


}
