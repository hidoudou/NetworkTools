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

    private boolean mStopSend = true;

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

        FloatingActionButton fab_send = (FloatingActionButton) findViewById(R.id.fab_send);
        fab_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, "start sending broadcast,lalalalalala~", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                mStopSend = false;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while(!mStopSend) {
                                Log.d("receive", "begin sending awesome broadcast ");
                                //准备待发送数据
                                byte[] bytesToSend = new byte[32];
                                bytesToSend[0] = (byte)0xA3;
                                bytesToSend[1] = 1;
                                bytesToSend[16] = 2;
                                try {
                                    final DatagramPacket packetToSend = new DatagramPacket(bytesToSend, bytesToSend.length, InetAddress.getByName("255.255.255.255"), BROADCAST_PORT);

                                    mSendSocket.send(packetToSend);
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }

                                Thread.sleep(1000);
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_recv);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, "starting receving broadcast balabalabala~", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while(!mStopReceive) {
                                Log.d("receive", "begin receive awesome broadcast ");
                                //开始收包
                                byte[] result = new byte[1024];
                                final DatagramPacket receiveBuffer = new DatagramPacket(result, result.length);
                                receiveBuffer.setData(result);
                                mReceiveSocket.receive(receiveBuffer);

                                if(receiveBuffer.getLength() > 0) {
                                    String src = receiveBuffer.getAddress().getHostAddress() + ":" + receiveBuffer.getPort();

                                    byte[] bytes = receiveBuffer.getData();
                                    byte oriHdr = bytes[0];
                                    /*
                                    这里将第一个字节以二进制形式打印出来，在windows机器上测试，不会显示很多位，但是在mac上测试却显示32位，
                                    比如如果第一个字节是0xa3，转换为二进制串后是：
                                    11111111111111111111111110100011
                                    ，注意高位全部是1，怀疑和不同平台的默认填充方式有关系，待进一步验证。
                                     */
                                    String hdr = Integer.toBinaryString(oriHdr);
                                    Log.d("receive", "receive awesome " + hdr + "\n from " + src);

                                    if((byte)0xA3 == bytes[0]) {
                                        Log.d("receive", "received awesome a3");
                                    }

                                }
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
                mReceiveSocket = new DatagramSocket(null);

                mReceiveSocket.setReuseAddress(true);
                mReceiveSocket.bind(new InetSocketAddress(BROADCAST_PORT));
                mReceiveSocket.setBroadcast(true);
                mReceiveSocket.setSoTimeout(0);

                Log.d("receive", "receive socket bind " + String.valueOf(mReceiveSocket.getLocalPort()));
            }

            if(null == mSendSocket) {
                mSendSocket = new DatagramSocket(5051);
                mSendSocket.setReuseAddress(true);
                mSendSocket.setBroadcast(true);
                mSendSocket.setSoTimeout(1);

                Log.d("receive", "send socket bind " + String.valueOf(mSendSocket.getLocalPort()));
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
