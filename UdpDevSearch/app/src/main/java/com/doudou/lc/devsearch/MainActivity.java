package com.doudou.lc.devsearch;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
//for udp communication
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;


public class MainActivity extends AppCompatActivity {

    private DatagramSocket mSendSocket = null;

    private DatagramSocket mReceiveSocket = null;

    private int BROADCAST_PORT = 5050;

    private int MULTICAST_PORT = 37810;

    private String mStrToSend;

    private boolean mStopSend = false;

    private boolean mStopReceive = false;



    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(null != mReceiveSocket) {
            mReceiveSocket.close();
            mReceiveSocket = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                //开始收包
                byte[] result = new byte[1024];
                final DatagramPacket receiveBuffer = new DatagramPacket(result, result.length);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while(!mStopReceive) {
                                Log.d("receive", "begin receive awesome broadcast ");
                                mReceiveSocket.receive(receiveBuffer);

                                byte[] bytes = receiveBuffer.getData();
                                if((byte)0xA3 == bytes[0]) {
                                    Log.d("receive", "received awesome a3!!!");
                                }
                                Log.d("receive", "receive awesome " + String.valueOf(bytes[0]));
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
    }

    private void initData() {
        try {
            if(null == mReceiveSocket) {
                mReceiveSocket = new DatagramSocket(5050);

                mReceiveSocket.setReuseAddress(true);
                //mReceiveSocket.bind(new InetSocketAddress(MULTICAST_PORT));
                mReceiveSocket.setBroadcast(true);
                mReceiveSocket.setSoTimeout(0);
            }
        } catch (Exception e) {
            Log.d("receive", "create receive socket failed!");
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
