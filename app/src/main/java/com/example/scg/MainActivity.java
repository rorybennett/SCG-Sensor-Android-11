package com.example.scg;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;

import org.tools4j.meanvar.MeanVarianceSampler;
import org.tools4j.meanvar.MeanVarianceSlidingWindow;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static java.lang.Math.pow;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {


    ////Bluetooth related constant////
    private BluetoothAdapter mBluetoothAdapter = null;
    public BluetoothDevice device;
    private BluetoothService mBluetoothService = null;
    private String mConnectedDeviceName = null;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_CONNECT_DEVICE = 1;


    ///Display//////
    private static Canvas canvas1, canvas2, canvas3;
    private static Bitmap bitmap1, bitmap2, bitmap3;
    private ImageView imageView1, imageView2, imageView3;
    private Thread displayThread10;
    private Thread displayThread100;
    private Thread displayThread1000;
    private LineChart mChart1;
    private LineChart mChart2;
    private LineChart mChart3;
    private LineChartManager lineChartManager1;
    private LineChartManager lineChartManager2;
    private LineChartManager lineChartManager3;
    private List<Integer> qColour = new ArrayList<Integer>(Arrays.asList(Color.BLACK, Color.GREEN, Color.BLUE, Color.GRAY));
    private static TextView mTextView1;
    private static TextView mTextView2;
    private static TextView sync;
    private static ArrayList<Double> data_HR_disp = new ArrayList<Double>();
    private static ArrayList<Double> HR_disp = new ArrayList<Double>();
    private static int N_disp = 0;


    ////Saving File Related/////////////////////
    private static final int SAVE_DATA = 2;
    private static final int SAVE_T = 3;
    private static final int SAVE_P = 4;
    private static final int SAVE_T1 = 9;
    private static final int SAVE_P1 = 10;
    private static final int SAVE_MAX1 = 5;
    private static final int SAVE_MIN1 = 6;
    private static final int SAVE_MAX2 = 7;
    private static final int SAVE_MIN2 = 8;


    ////Data processing and data collection variables//////////
    private static double[] ac = new double[]{0, 0, 0};
    private static double[] ac1 = new double[]{0, 0, 0};
    private static double[] ac_p = new double[]{0, 0, 0};
    private static double vSum;
    private static double vSum_p;
    private static ArrayList<Double> data_HR = new ArrayList<Double>();
    private static ArrayList<Double> data_RR = new ArrayList<Double>();
    private static ArrayList<Double> saveDatax = new ArrayList<Double>();
    private static ArrayList<Double> saveDatay = new ArrayList<Double>();
    private static ArrayList<Double> saveDataz = new ArrayList<Double>();
    private static ArrayList<Double> HR_P = new ArrayList<Double>();
    private static ArrayList<Integer> HR_P_POS = new ArrayList<Integer>();
    private static ArrayList<Double> HR = new ArrayList<Double>();
    private static ArrayList<Integer> HR_POS = new ArrayList<Integer>();
    private static ArrayList<Double> HR_T = new ArrayList<Double>();
    private static ArrayList<Integer> HR_T_POS = new ArrayList<Integer>();
    private static ArrayList<Double> HR_P1 = new ArrayList<Double>();
    private static ArrayList<Integer> HR_P_POS1 = new ArrayList<Integer>();
    private static ArrayList<Double> HR_T1 = new ArrayList<Double>();
    private static ArrayList<Integer> HR_T_POS1 = new ArrayList<Integer>();
    private static ArrayList<Double> RR = new ArrayList<Double>();
    static Queue<Byte> queueBuffer = new LinkedList<Byte>();
    private static int ar = 16, av = 2000;
    static int N = 0;
    static int N1 = 0;
    private static double[] a_LHR = new double[]{1.0, 0.0};
    private static double[] b_LHR = new double[]{0.5, 0.5};
    private static double[] ah_LHR = new double[]{1.0, -0.7265};
    private static double[] bh_LHR = new double[]{0.8633, -0.8633};
    private static double[] a_LRR = new double[]{1.0, -0.93};
    private static double[] b_LRR = new double[]{0.0305, 0.0305};
    private static double[] ah_LRR = new double[]{1.0, -0.9937};
    private static double[] bh_LRR = new double[]{0.9969, -0.9969};
    private static double low_pass = 0;
    private static double low_pass_RR = 0;
    private static int count = 0;
    private static int count1 = 0;
    private static int dataCount = 200;
    private static int windowNormal = 100;
    private static int windowRespiration = 1000;
    private static int refractoryWindow = 25;
    private static int winPT = 10;
    private static double threshold_peak = 0;
    private static double threshold_trough = 0;
    private static double threshold_peak_new = 0;
    private static double threshold_trough_new = 0;
    private static double hrP;
    private static double hrT;
    private static long startTime;
    private static long endTime;
    private static final MeanVarianceSampler overAll = new MeanVarianceSampler();
    private static boolean f;
    private static int initiationIndex = 850;
    private static int respiatoryIndex = 850;
    private static int samplingFrequency = 100;
    private static int detectIndex = initiationIndex + 10 * samplingFrequency;
    private static String text = "Please move for synchronisation";
    private static ArrayList<Double> MAX = new ArrayList<>();
    private static ArrayList<Double> MIN = new ArrayList<>();
    private static ArrayList<Integer> MAX_pos = new ArrayList<>();
    private static ArrayList<Integer> MIN_pos = new ArrayList<>();
    private static ArrayList<Double> MAXW = new ArrayList<>();
    private static ArrayList<Double> MINW = new ArrayList<>();
    private static ArrayList<Integer> MAX_posW = new ArrayList<>();
    private static ArrayList<Integer> MIN_posW = new ArrayList<>();
    private static ArrayList<Double> RR_T = new ArrayList<>();
    private static ArrayList<Integer> RR_P = new ArrayList<>();
    private static MeanVarianceSlidingWindow RR_HR = new MeanVarianceSlidingWindow(8);
    private static ArrayList<Integer> RR_RR = new ArrayList<>();
    final static MeanVarianceSampler thresholdPeak = new MeanVarianceSampler();
    final static MeanVarianceSampler thresholdTrough = new MeanVarianceSampler();
    static int iError = 0;
    final static MeanVarianceSlidingWindow sampler = new MeanVarianceSlidingWindow(10 * samplingFrequency);
    private static int windowIndex = detectIndex;
    private static int heartIndex = 0;
    private static int maxP = 0;
    private static int maxT = 0;
    private byte sHead;
    public static byte[] packBuffer;
    private byte[] aBuffer;


    /////Fucntion to receive information from bluetooth and process information////////
    ///Also main function to call learning and heartbeat and respiratory detect funtions//////////
    public static void CopeSerialData(byte sHead, byte[] packBuffer) {



                if (count1 == 0)
                    Log.d("Start time",String.valueOf(Calendar.getInstance().getTimeInMillis()));
                if(count1==1000)
                    Log.d("End time",String.valueOf(Calendar.getInstance().getTimeInMillis()));

                for (int i = 0; i < 3; i++)
                    ac[i] = ((((short) packBuffer[i * 2 + 1]) << 8) | ((short) packBuffer[i * 2] & 0xff)) / 32768.0f * ar;

                vSum = sqrt(pow(ac[0], 2) + pow(ac[1], 2) + pow(ac[2], 2));


                if (count1 >= 500) {
                    saveDatax.add(count, ac[0]);
                    saveDatay.add(count, ac[1]);
                    saveDataz.add(count, ac[2]);
                    count = count + 1;
                    double LPF_HR = (b_LHR[0] * ac[2] + b_LHR[1] * ac_p[2]); //Calculate low pass filter for HR
                    double HPF_HR = bh_LHR[0] * LPF_HR + bh_LHR[1] * low_pass - ah_LHR[1] * data_HR.get(N); //Calculate High pass filter for HR
                    double LPF_RR = b_LRR[0] * vSum + b_LRR[1] * vSum_p - a_LRR[1] * low_pass_RR; //Calculate low pass filter for RR
                    double HPF_RR = bh_LRR[0] * LPF_RR + bh_LRR[1] * low_pass_RR - ah_LRR[1] * data_RR.get(N); //Calculate high pass filter for HR

                    if (data_HR.size() >= initiationIndex && data_HR.size() < (initiationIndex + sampler.getWindowSize())) //Start a list for calculating window size and
                        sampler.update(HPF_HR);

                    N = N + 1;

                    data_HR.add(N, HPF_HR);
                    data_RR.add(N, HPF_RR);

                    // if(MAX_pos.get(N)==0 && MAX_posW.get(N)==0 && MIN_pos.get(N)==0 && MIN_posW.get(N)==0 && HR_T_POS.get(N)==0 && HR_P_POS.get(N)==0)
                    HR.add(N, (double) 0);


                    ac_p = ac;
                    low_pass = LPF_HR; //assign current values
                    low_pass_RR = LPF_RR;
                    vSum_p = vSum;
                    // Log.d("N in serial",String.valueOf(N));

                    //// Show a window to assist with synchronisation/////////////

                    if (data_HR.size() == 200) {

                        runOnUI(new Runnable() {
                            @Override
                            public void run() {

                                sync.setVisibility(View.VISIBLE);
                                sync.setText("Please move to synchronise");
                            }
                        });

                    }
                    ////Shut the window for synchronisation
                    if (data_HR.size() == 700) {

                        runOnUI(new Runnable() {
                            @Override
                            public void run() {

                                sync.setVisibility(View.GONE);

                            }
                        });
                    }

                    ////// Perform learning of the thresholds for 10 seconds/////////
                    if (data_HR.size() == (initiationIndex + 10 * samplingFrequency + winPT)) {


                        initialise();
                    }
                    /// Perform sampler update///////////
                    if (data_HR.size() >= windowIndex && data_HR.size() < windowIndex + windowNormal) {
                        sampler.update(data_HR.get(N));
                        windowIndex = windowIndex + windowNormal;
                    }
                    ////Perform peak, trough, HR detection///////

                    if (data_HR.size() >= (detectIndex + windowNormal + winPT)) {

                        detect(detectIndex);


                        //Log.d("Start index",String.valueOf(detectIndex))
                        detectIndex = detectIndex + windowNormal;
                        // Log.d("Heart Index",String.valueOf(heartIndex));

                    }
                    if (HR_P_POS.size() > 2) {
                        Log.d("g", String.valueOf(RR_T.size()));
                        //Log.d("size",String.valueOf(HR_T_POS.size()));
                        if (heartIndex != 0) {
                            for (int g = heartIndex; g < HR_P_POS.size(); g++) {
                                double hrp = HR_P_POS.get(g) - HR_P_POS.get(g - 1);
                                //   Log.d("Hrp",String.valueOf(round(60 / (hrp / 100))));
                                if (hrp > 35) {
                                    RR_HR.update(round(60 / (hrp / 100)));
                                    RR_T.add(hrp);
                                }
                            }
                            heartIndex = HR_P_POS.size();
                        } else {
                            for (int g = 0; g < HR_P_POS.size() - 1; g++) {
                                double hrp = HR_P_POS.get(g + 1) - HR_P_POS.get(g);
                                if (hrp > 35) {
                                    RR_HR.update(round(60 / (hrp / 100)));
                                    RR_T.add(hrp);
                                }
                                // Log.d("Hrp",String.valueOf(round(60 / (hrp / 100))));


                            }
                            heartIndex = HR_P_POS.size();
                        }
                        if (RR_HR.getCount() >= 8) {
                            runOnUI(new Runnable() {
                                @Override
                                public void run() {

                                    mTextView1.setText(String.valueOf(round(RR_HR.getMean())) + "" + "bpm");


                                }
                            });
                        }

                    } else if (HR_P_POS.size() == 2) {
                        heartIndex = 0;
                        double hrp = HR_P_POS.get(1) - HR_P_POS.get(0);
                        if (hrp > 35) {
                            RR_T.add(hrp);
                            RR_HR.update(round(60 / (hrp / 100)));
                        }
                        heartIndex = HR_P_POS.size();
                        //Log.d("Hrp",String.valueOf(round(60 / (hrp / 100))));

                    }


                    //// Perfrom respiration index///////
                    if (data_RR.size() > (respiatoryIndex + windowRespiration)) {
                        calcRR(respiatoryIndex);
                        respiatoryIndex = respiatoryIndex + windowRespiration;


                    }

                    if (N >= 2000) {

                        N_disp = N_disp + 1;
                        data_HR_disp.add(N_disp, data_HR.get(N_disp));


                    } else if (N == 1999) {
                        data_HR_disp.add(0, (double) 0);
                        HR_disp.add(0, (double) 0);
                    }
                    // mTextView1.setText("/hello");


                    // Log.d("Size",String.valueOf(N));
                    endTime = Calendar.getInstance().getTimeInMillis();
                    // Log.d("Start Time", String.valueOf(startTime));
                    //Log.d("End time", String.valueOf(endTime));


                } else if (count1 == 499) {
                    data_HR.add(0, (double) 0);
                    data_RR.add(0, (double) 0);
                    saveDatax.add(0, ac[0]);
                    saveDatay.add(0, ac[1]);
                    saveDataz.add(0, ac[2]);
                    low_pass_RR = 0;
                    ac_p = ac;
                    // if(MAX_pos.size()==0 || MIN_pos.size() ==0 || MAXW.size()==0 || MINW.size() ==0 )
                    HR.add(0, (double) 0);
                    vSum_p = vSum;

                }
                count1 = count1 + 1;



            // if(count == 100)
            //{Log.d("time1",String.valueOf(Calendar.getInstance().getTimeInMillis()));
            //Log.d("count",String.valueOf(count1));}
            //count++;





    }



    public static Handler UIHandler;
    static
    {
        UIHandler = new Handler(Looper.getMainLooper());
    }
    public static void runOnUI(Runnable runnable) {
        UIHandler.post(runnable);
    }

    public void filtering()
    {
        if(data_HR.size()==0)
            data_HR.add(0, (double) 0);
        double LPF_HR = (b_LHR[0] * ac[2] + b_LHR[1] * ac_p[2]); //Calculate low pass filter for HR
        double HPF_HR = bh_LHR[0] * LPF_HR + bh_LHR[1] * low_pass - ah_LHR[1] * data_HR.get(N); //Calculate High pass filter for HR

        N = N + 1;

        data_HR.add(N, HPF_HR);




    }





    //// Thread to connect with Bluetooth sensor and start display///////////
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        // 匿名内部类写法，实现接口Handler的一些方法
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case MESSAGE_STATE_CHANGE:
                    break;
                case MESSAGE_READ:
                    byte[] packBuffer =  (byte[])msg.obj;
                    //aBuffer = packBuffer;
                    for (int i = 0; i < 3; i++)
                           ac[i] = ((((short) packBuffer[i * 2 + 1]) << 8) | ((short) packBuffer[i * 2] & 0xff)) / 32768.0f * ar;
                    filtering();

                    //Log.d("handler",String.valueOf(ac[2]));
                    ac_p = ac;

                    break;

                case MESSAGE_DEVICE_NAME:
                    mConnectedDeviceName = msg.getData().getString("device_name");
                    Toast.makeText(getApplicationContext(),"Connected to " + mConnectedDeviceName,Toast.LENGTH_SHORT).show();
                    //displayThread10.start();
                    displayThread100.start();
                    //displayThread1000.start();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(),msg.getData().getString("toast"), Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_WRITE:
                    Toast.makeText(getApplicationContext(),msg.getData().getString("Data Sent"), Toast.LENGTH_SHORT).show();

            }
        }
    };

    ///Function on Clicking Start Button - Connects to Bluetooth////////////
    public void startData()
    {

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled())
        { mBluetoothAdapter.enable();
            Log.d("Main task","Bluetooth enabled");
        }
        if (mBluetoothService == null)
            mBluetoothService = new BluetoothService(this, mHandler); // 用来管理蓝牙的连接
        Intent serverIntent = new Intent(this,DeviceListActivity.class);
        startActivityForResult(serverIntent,REQUEST_CONNECT_DEVICE);
    }
    ///Acitivity Response to Connect to Bluetooth////////////

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:// When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);// Get the device MAC address
                    Log.d("Address", address);
                    device = mBluetoothAdapter.getRemoteDevice(address);// Get the BLuetoothDevice object
                    mBluetoothService.connect(device);// Attempt to connect to the device
                }
                break;
            case SAVE_DATA:

                Log.d("Saving data", "Intent has been initialised");
                super.onActivityResult(requestCode, resultCode, data);
                f = true;


                Uri uri = null;
                uri = data.getData();
                try {
                    ParcelFileDescriptor p = getContentResolver().openFileDescriptor(uri, "wa");
                    FileOutputStream fileOutputStream = new FileOutputStream(p.getFileDescriptor());

                    for (int i = 0; i < data_HR.size(); i++) {


                        double n = saveDatax.get(i);
                        double m = saveDatay.get(i);
                        double l = saveDataz.get(i);
                        // String t = time.get(i);
                        // Log.d("time",t);


                        double k = data_HR.get(i);
                        double t = data_RR.get(i);
                        //Log.d("K",String.valueOf(k));
                        fileOutputStream.write((Double.toString(n) + "," + Double.toString(m) + "," + Double.toString(l) + "," + Double.toString(k) + ","+Double.toString(t)+"\n").getBytes());

                    }


                } catch (IOException e) {

                    e.printStackTrace();
                }

                //mBluetoothService.stop();


                if(HR_T_POS.size()!=0)
                    saveResult1();


                break;
            case SAVE_T:

                Log.d("Saving data", "Intent has been initialised");
                super.onActivityResult(requestCode, resultCode, data);
                f = true;


                Uri uri1 = null;
                uri1 = data.getData();
                try {
                    ParcelFileDescriptor p = getContentResolver().openFileDescriptor(uri1, "wa");
                    FileOutputStream fileOutputStream = new FileOutputStream(p.getFileDescriptor());

                    for (int i = 0; i < HR_T_POS.size(); i++) {


                        double n = HR_T.get(i);
                        int m = HR_T_POS.get(i);

                        // String t = time.get(i);
                        // Log.d("time",t);


                        //double k = data_HR.get(i);
                        //Log.d("K",String.valueOf(k));
                        fileOutputStream.write((Double.toString(n) + "," + Integer.toString(m) + "\n").getBytes());

                    }


                } catch (IOException e) {

                    e.printStackTrace();
                }

                if(HR_P_POS.size()!=0)
                    saveResult2();


                break;
            case SAVE_P:

                Log.d("Saving data", "Intent has been initialised");
                super.onActivityResult(requestCode, resultCode, data);
                f = true;


                Uri uri2 = null;
                uri2 = data.getData();
                try {
                    ParcelFileDescriptor p = getContentResolver().openFileDescriptor(uri2, "wa");
                    FileOutputStream fileOutputStream = new FileOutputStream(p.getFileDescriptor());

                    for (int i = 0; i < HR_P_POS.size(); i++) {


                        double r = HR_P.get(i);
                        int q = HR_P_POS.get(i);
                        // String t = time.get(i);
                        // Log.d("time",t);


                        //double k = data_HR.get(i);
                        //Log.d("K",String.valueOf(k));
                        fileOutputStream.write((Double.toString(r) + "," + Integer.toString(q) + "\n").getBytes());

                    }


                } catch (IOException e) {

                    e.printStackTrace();
                }
                if(MAX_pos.size()!=0)
                    saveResult3();



                break;
            case SAVE_MAX1:

                Log.d("Saving data", "Intent has been initialised");
                super.onActivityResult(requestCode, resultCode, data);
                f = true;


                Uri uri3 = null;
                uri3 = data.getData();
                try {
                    ParcelFileDescriptor p = getContentResolver().openFileDescriptor(uri3, "wa");
                    FileOutputStream fileOutputStream = new FileOutputStream(p.getFileDescriptor());

                    for (int i = 0; i < MAX_pos.size(); i++) {


                        double r = MAX.get(i);
                        int q = MAX_pos.get(i);
                        // String t = time.get(i);
                        // Log.d("time",t);


                        //double k = data_HR.get(i);
                        //Log.d("K",String.valueOf(k));
                        fileOutputStream.write((Double.toString(r) + "," + Integer.toString(q) + "\n").getBytes());

                    }


                } catch (IOException e) {

                    e.printStackTrace();
                }
                if(MIN_pos.size()!=0)
                    saveResult4();


                break;
            case SAVE_MIN1:

                Log.d("Saving data", "Intent has been initialised");
                super.onActivityResult(requestCode, resultCode, data);
                f = true;


                Uri uri4 = null;
                uri4 = data.getData();
                try {
                    ParcelFileDescriptor p = getContentResolver().openFileDescriptor(uri4, "wa");
                    FileOutputStream fileOutputStream = new FileOutputStream(p.getFileDescriptor());

                    for (int i = 0; i < MIN_pos.size(); i++) {


                        double r = MIN.get(i);
                        int q = MIN_pos.get(i);
                        // String t = time.get(i);
                        // Log.d("time",t);


                        //double k = data_HR.get(i);
                        //Log.d("K",String.valueOf(k));
                        fileOutputStream.write((Double.toString(r) + "," + Integer.toString(q) + "\n").getBytes());

                    }


                } catch (IOException e) {

                    e.printStackTrace();
                }
                if(MAX_posW.size()!=0)
                    saveResult5();


                break;
            case SAVE_MAX2:

                Log.d("Saving data", "Intent has been initialised");
                super.onActivityResult(requestCode, resultCode, data);
                f = true;


                Uri uri5 = null;
                uri5 = data.getData();
                try {
                    ParcelFileDescriptor p = getContentResolver().openFileDescriptor(uri5, "wa");
                    FileOutputStream fileOutputStream = new FileOutputStream(p.getFileDescriptor());

                    for (int i = 0; i<MAX_posW.size(); i++) {


                        double r = MAXW.get(i);
                        int q = MAX_posW.get(i);
                        // String t = time.get(i);
                        // Log.d("time",t);


                        //double k = data_HR.get(i);
                        //Log.d("K",String.valueOf(k));
                        fileOutputStream.write((Double.toString(r) + "," + Integer.toString(q) + "\n").getBytes());

                    }


                } catch (IOException e) {

                    e.printStackTrace();
                }
                if(MIN_posW.size()!=0)
                    saveResult6();


                break;
            case SAVE_MIN2:

                Log.d("Saving data", "Intent has been initialised");
                super.onActivityResult(requestCode, resultCode, data);
                f = true;


                Uri uri6 = null;
                uri6 = data.getData();
                try {
                    ParcelFileDescriptor p = getContentResolver().openFileDescriptor(uri6, "wa");
                    FileOutputStream fileOutputStream = new FileOutputStream(p.getFileDescriptor());

                    for (int i = 0; i < MIN_posW.size(); i++) {


                        double r = MINW.get(i);
                        int q = MIN_posW.get(i);
                        // String t = time.get(i);
                        // Log.d("time",t);


                        //double k = data_HR.get(i);
                        //Log.d("K",String.valueOf(k));
                        fileOutputStream.write((Double.toString(r) + "," + Integer.toString(q) + "\n").getBytes());

                    }


                } catch (IOException e) {

                    e.printStackTrace();
                }

                if(HR.size()!=0)
                    saveResult7();



                break;
            case SAVE_T1:

                Log.d("Saving data", "Intent has been initialised");
                super.onActivityResult(requestCode, resultCode, data);
                f = true;


                Uri uri7 = null;
                uri7 = data.getData();
                try {
                    ParcelFileDescriptor p = getContentResolver().openFileDescriptor(uri7, "wa");
                    FileOutputStream fileOutputStream = new FileOutputStream(p.getFileDescriptor());

                    for (int i = 0; i < HR.size(); i++) {


                        double n = HR.get(i);
                        //int m = HR_T_POS1.get(i);

                        // String t = time.get(i);
                        // Log.d("time",t);


                        //double k = data_HR.get(i);
                        //Log.d("K",String.valueOf(k));
                        fileOutputStream.write((Double.toString(n) + "\n").getBytes());

                    }


                } catch (IOException e) {

                    e.printStackTrace();
                }

                if(RR_T.size()!=0)
                    saveResult8();


                break;
            case SAVE_P1:

                Log.d("Saving data", "Intent has been initialised");
                super.onActivityResult(requestCode, resultCode, data);
                f = true;


                Uri uri8 = null;
                uri8 = data.getData();
                try {
                    ParcelFileDescriptor p = getContentResolver().openFileDescriptor(uri8, "wa");
                    FileOutputStream fileOutputStream = new FileOutputStream(p.getFileDescriptor());

                    for (int i = 0; i < RR_T.size(); i++) {


                        double r = RR_T.get(i);
                        // int q = HR_P_POS1.get(i);
                        // String t = time.get(i);
                        // Log.d("time",t);


                        //double k = data_HR.get(i);
                        //Log.d("K",String.valueOf(k));
                        fileOutputStream.write((Double.toString(r) + "\n").getBytes());

                    }


                } catch (IOException e) {

                    e.printStackTrace();
                }




                break;

        }
    }







    //// Thread to handle display calls for graph/////////////////


    @SuppressLint("HandlerLeak")
    private final Handler refreshhandler10 = new Handler() {
        @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg)
        {

            if(HR.size()>2001)
            {
                // double[] p = new double[]{0,0};
                // p[0] = data_HR_disp.get(N_disp);
                // p[1] = HR_disp.get(N_disp);
                int N2 = N-2000;
          //      lineChartManager2.addEntry(Arrays.asList(HR.get(N2)));
            }


        }

    };

    @SuppressLint("HandlerLeak")
    private final Handler refreshhandler100 = new Handler() {
        @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg)
        {
            if(data_HR.size()!=0)
                lineChartManager1.addEntry(Arrays.asList(data_HR.get(N)));


        }

    };
    @SuppressLint("HandlerLeak")
    private final Handler refreshhandler1000 = new Handler() {
        @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg)
        {

            if(data_RR.size()!=0)
            {
             //   lineChartManager3.addEntry(Arrays.asList(data_RR.get(N)));
            }

        }

    };


    ///// OnCreate method when Acitivty/App starts////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mChart1 = (LineChart) findViewById(R.id.overallGraphs);

     //   mChart2 = (LineChart) findViewById(R.id.heartGraph);
      //  mChart3 = (LineChart) findViewById(R.id.respiratoryGraph);



        lineChartManager1 = new LineChartManager(mChart1, Arrays.asList("az"), qColour);
        lineChartManager1.setDescription("Acceleration");

       // lineChartManager2 = new LineChartManager(this,mChart2, Arrays.asList("aHr"), qColour);
       // lineChartManager2.setDescription("Heart Graph");
       // lineChartManager3 = new LineChartManager(this,mChart3, Arrays.asList("az"), qColour);
       // lineChartManager3.setDescription("Respiratory Graph");

        mTextView1 = findViewById(R.id.heartRate);
        mTextView2 = findViewById(R.id.respiratoryRate);

        final Button testButton = (Button) findViewById(R.id.Start);
        testButton.setTag(1);
        testButton.setText("Start");
        testButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                final int status =(Integer) v.getTag();
                if(status == 1) {
                    startData();
                    testButton.setText("Finish");
                    v.setTag(0); //pause
                } else {
                    disconnect();
                    testButton.setText("Start");
                    v.setTag(1); //pause
                }
            }
        });
        sync = findViewById(R.id.sync);
        sync.setVisibility(View.INVISIBLE);

        displayThread10 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    refreshhandler10.sendMessage(Message.obtain());
                    try {
                        Thread.sleep(0);
                    } catch (Exception err) {
                    }
                }
            }
        });
        displayThread100 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    refreshhandler100.sendMessage(Message.obtain());
                    try {
                        Thread.sleep(20);
                    } catch (Exception err) {
                    }
                }
            }
        });
        displayThread1000 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    refreshhandler1000.sendMessage(Message.obtain());
                    try {
                        Thread.sleep(1000);
                    } catch (Exception err) {
                    }
                }
            }
        });

    }

    ////Function to save data///////////////////////////////////////
    public void disconnect()
    {

        displayThread10.interrupt();
        displayThread1000.interrupt();
        displayThread1000.interrupt();
        mBluetoothService.stop();
        //mBluetoothAdapter.disable();

        if(data_HR.size()!=0) {

            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/csv");
            String filename = "sensorData.csv";
            intent.putExtra(Intent.EXTRA_TITLE, filename);
            startActivityForResult(intent, SAVE_DATA);
        }

    }

    private void saveResult1() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        String filename = "sensorTrough.csv";
        intent.putExtra(Intent.EXTRA_TITLE, filename);
        startActivityForResult(intent, SAVE_T);
    }
    private void saveResult2() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        String filename = "sensorPeak.csv";
        intent.putExtra(Intent.EXTRA_TITLE, filename);
        startActivityForResult(intent, SAVE_P);
    }
    private void saveResult3() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        String filename = "sensorMaxLearning1.csv";
        intent.putExtra(Intent.EXTRA_TITLE, filename);
        startActivityForResult(intent, SAVE_MAX1);
    }
    private void saveResult4() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        String filename = "sensorMinLearning1.csv";
        intent.putExtra(Intent.EXTRA_TITLE, filename);
        startActivityForResult(intent, SAVE_MIN1);
    }
    private void saveResult5() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        String filename = "sensorMaxLearning2.csv";
        intent.putExtra(Intent.EXTRA_TITLE, filename);
        startActivityForResult(intent, SAVE_MAX2);
    }
    private void saveResult6() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        String filename = "sensorMinLearning2.csv";
        intent.putExtra(Intent.EXTRA_TITLE, filename);
        startActivityForResult(intent, SAVE_MIN2);
    }
    private void saveResult7() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        String filename = "sensorTrough1.csv";
        intent.putExtra(Intent.EXTRA_TITLE, filename);
        startActivityForResult(intent, SAVE_T1);
    }
    private void saveResult8() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        String filename = "sensorPeak1.csv";
        intent.putExtra(Intent.EXTRA_TITLE, filename);
        startActivityForResult(intent, SAVE_P1);
    }




    ///// Initialise threshold parameters for detecting peak and trough//////////////
    ////// Includes learning part 1 and learing part 2//////////////////////////////

    public static void initialise()
    {

        startTime  = Calendar.getInstance().getTimeInMillis();

        int end = initiationIndex + 500;
        float sum = 0;
        double max = 0, min = 0;
        int max_pos = 0, min_pos = 0;
        //ArrayList<Double> MAX = new ArrayList<>();
        //ArrayList<Double> MIN = new ArrayList<>();
        //ArrayList<Integer> MAX_pos = new ArrayList<>();
        //ArrayList<Integer> MIN_pos = new ArrayList<>();
        ArrayList<Double> T_peak = new ArrayList<>() ;
        ArrayList<Double> T_trough = new ArrayList<>();


        double mean, stdDev;
        mean = sampler.getMean();
        stdDev = sampler.getStdDev();



        Log.d("MeanOverall",String.valueOf(mean));
        Log.d("StdOverall",String.valueOf(stdDev));

        for (int i = initiationIndex; i < end; i = i + windowNormal)
        {
            //sum = sum + data_HR.get(i);
            max = 0;
            max_pos = 0;
            min = 0;
            min_pos = 0;
            for (int j = i; j < i+windowNormal; j++) {
                if (data_HR.get(j) > max) {
                    max = data_HR.get(j);
                    max_pos = j;
                } else if (data_HR.get(j) < min) {
                    min = data_HR.get(j);
                    min_pos = j;
                }

            }

            MAX.add(max);
            MAX_pos.add(max_pos);
            MIN.add(min);
            MIN_pos.add(min_pos);
            if(max_pos > min_pos) {
                HR.set(min_pos, min);
                HR.set(max_pos, max);
            }else if(min_pos > max_pos)
            {
                HR.set(max_pos, max);
                HR.set(min_pos, min);

            }
            double p =  ((max - mean) / stdDev);
            T_peak.add(p);


            double t =  ((min - mean) / stdDev);

            T_trough.add(t);
        }

        double min1 = Collections.min(T_peak);
        double max1 = Collections.max(T_trough);


        for (int i = 0; i < T_peak.size(); i++)
        {
            thresholdPeak.add(T_peak.get(i));
        }

        for (int i = 0; i < T_peak.size(); i++)
        {
            thresholdTrough.add(T_trough.get(i));
        }

        threshold_peak = 0.8*thresholdPeak.getMean();
        threshold_trough =  0.8*thresholdTrough.getMean();
        //thresholdPeak.reset();
        //thresholdTrough.reset();



        //threshold_peak = min1;
        if(threshold_peak<min1)
            threshold_peak = threshold_peak;
        else threshold_peak = min1;
        if(threshold_trough>max1)
            threshold_trough = threshold_trough;
        else threshold_trough = max1;



        dataCount = end;
        end = end + 500;



        //ArrayList<Double> MAXW = new ArrayList<>();
        //ArrayList<Double> MINW = new ArrayList<>();
        //ArrayList<Integer> MAX_posW = new ArrayList<>();
        //ArrayList<Integer> MIN_posW = new ArrayList<>();
        int maxP1 = 0, maxT1 = 0;
        int withinRfP = 0;
        int withinRfT = 0;

        for(int i=dataCount;i<end;i=i+refractoryWindow)
        {
            double mw = 0, minw = 0;
            int mwpos = 0, minwpos = 0;
            withinRfP = 0;
            withinRfT = 0;


            for (int j = 0; j < refractoryWindow; j++) {
                if ((data_HR.get(i + j) - mean >= threshold_peak * stdDev) && data_HR.get(i + j) > mw){
                    if((i + j) - maxP1 > refractoryWindow)
                    {
                        mw = data_HR.get(i + j);
                        mwpos = i + j;
                        withinRfP = 1;
                    }else withinRfP = -1;
                } else if ((data_HR.get(i + j) - mean <= threshold_trough * stdDev) && data_HR.get(i + j) < minw){
                    if((i + j) - maxT1 > refractoryWindow)
                    {
                        minw = data_HR.get(i + j);
                        minwpos = i + j;
                        withinRfT = 1;
                    }else withinRfT = -1;
                }
            }

            if (mwpos != 0 && minwpos != 0) // Both peak and trough has been detected
            {
                //if(abs(mwpos - minwpos) <= winPT) // They fall under the category of correct detection
                //{
                MAXW.add(mw);
                MAX_posW.add(mwpos);
                maxP1 = mwpos;
                MINW.add(minw);
                MIN_posW.add(minwpos);
                maxT1 = minwpos;
                if(mwpos>minwpos) {
                    HR.set(minwpos, minw);
                    HR.set(mwpos,mw);

                }else{
                    HR.set(mwpos,mw);
                    HR.set(minwpos, minw);
                }
            }else if(mwpos == 0 && minwpos != 0 && withinRfP != -1) {////Else if a trough has been detected and a peak has not been detected
                double threshold = 0.7 * threshold_peak;
                double n = 0;
                int m = 0;
                for (int t = minwpos - winPT; t <= minwpos + winPT; t++) {/////Check if a peak has been misdetected
                    if (data_HR.get(t) - mean >= threshold * stdDev && data_HR.get(t) > n) {
                        n = data_HR.get(t);
                        m = t;
                        // missDetection = 2;

                    }

                }
                if (n != 0)
                {
                    MAXW.add(n);
                    MAX_posW.add(m);
                    maxP1 = m;
                    MINW.add(minw);
                    MIN_posW.add(minwpos);
                    maxT1 = minwpos;
                    if(m>minwpos) {
                        HR.set(minwpos, minw);
                        HR.set(m,n);

                    }else{
                        HR.set(m,n);
                        HR.set(minwpos, minw);
                    }
                    Log.d("Error","Peak as underdetected");

                } else {///Else a trough has been overdetected
                    Log.d("Error", "Trough was overdetected");
                }

            }else if(mwpos != 0 && minwpos == 0 && withinRfT != -1)////If a peak has been detected and a trough has not been detected
            {
                double threshold = 0.8 * threshold_trough;
                double n = 0;
                int m = 0;
                for (int t = mwpos - winPT; t <= mwpos + winPT; t++) {
                    if (data_HR.get(t) - mean <= threshold * stdDev && data_HR.get(t) < n) {///Check if a trough has been missed
                        n = data_HR.get(t);
                        m = t;
                        // missDetection = 2;

                    }

                }
                if (n != 0) {
                    MAXW.add(mw);
                    MAX_posW.add(mwpos);
                    maxP1 = mwpos;
                    MINW.add(n);
                    MIN_posW.add(m);
                    maxT1 = m;
                    if(mwpos>m) {
                        HR.set(m, n);
                        HR.set(mwpos,mw);

                    }else{
                        HR.set(mwpos,mw);
                        HR.set(m, n);
                    }
                    Log.d("Error","Trough was underdetected");

                } else {
                    Log.d("Error", "Peak was overdetected");///Else a peak has been overdetected
                }

            }







        }




        Log.d("Size of Max",String.valueOf(MAX_posW.size()));
        Log.d("Size of Min",String.valueOf(MIN_posW.size()));
        int missDetection = 0;


        thresholdPeak.reset();
        thresholdTrough.reset();


        ArrayList<Double> T_peak_new = new ArrayList<>() ;
        for(int i=0;i<MAX_posW.size();i++)
        {
            double t = (MAXW.get(i) - mean)/stdDev;
            T_peak_new.add(t);

        }
        ArrayList<Double> T_trough_new = new ArrayList<>();
        for(int i=0;i<MIN_posW.size();i++)
        {
            double t = (MINW.get(i) - mean)/stdDev;
            T_trough_new.add(t);

        }
        for (int i = 0; i < T_peak_new.size(); i++)
        {
            thresholdPeak.add(T_peak_new.get(i));
        }
        double b1 = Collections.min(T_peak_new);
        for (int i = 0; i < T_trough_new.size(); i++)
        {
            thresholdTrough.add(T_trough_new.get(i));
        }
        double b = Collections.max(T_trough_new);

        threshold_peak_new = thresholdPeak.getMean();
        threshold_trough_new = thresholdTrough.getMean();

        if(threshold_peak_new<b1)
            threshold_peak_new = threshold_peak_new;
        else threshold_peak_new = b1;
        if(threshold_trough_new>b)
            threshold_trough_new = threshold_trough_new;
        else threshold_trough_new = b;





    }

    private static void detect(int startIndex)
    {


        double std = sampler.getStdDev();
        double mean = sampler.getMean();

        int peak = 0;
        int trough = 0;
        int peak1 = 0;
        int trough1 = 0;
        int withinRfP = 0;
        int withinRfT = 0;

        int start = HR_P_POS.size();

        for(int i=startIndex;i<startIndex+windowNormal;i=i+refractoryWindow) {
            double mw = 0, minw = 0;
            int mwpos = 0, minwpos = 0;
            withinRfP = 0;
            withinRfT = 0;


            for (int j = 0; j < refractoryWindow; j++) {
                if ((data_HR.get(i + j) - mean >= threshold_peak_new * std) && data_HR.get(i + j) > mw) {
                    if ((i + j) - maxP > refractoryWindow) {
                        mw = data_HR.get(i + j);
                        mwpos = i + j;
                        withinRfP = 1;
                    } else withinRfP = -1;
                } else if ((data_HR.get(i + j) - mean <= threshold_trough_new * std) && data_HR.get(i + j) < minw) {
                    if ((i + j) - maxT > refractoryWindow) {
                        minw = data_HR.get(i + j);
                        minwpos = i + j;
                        withinRfT = 1;
                    } else withinRfT = -1;
                }
            }

            if (mwpos != 0 && minwpos != 0) // Both peak and trough has been detected
            {
                //if(abs(mwpos - minwpos) <= winPT) // They fall under the category of correct detection
                //{
                Log.d("Detection", "Proper Detection");
                HR_P.add(mw);
                HR_P_POS.add(mwpos);
                maxP = mwpos;
                HR_T.add(minw);
                HR_T_POS.add(minwpos);
                maxT = minwpos;
                if (mwpos > minwpos) {
                    HR.set(minwpos, minw);
                    HR.set(mwpos, mw);

                } else {
                    HR.set(mwpos, mw);
                    HR.set(minwpos, minw);
                }
            } else if (mwpos == 0 && minwpos != 0 && withinRfP != -1) {
                Log.d("Detection", "Trough has been detected");////Else if a trough has been detected and a peak has not been detected
                double threshold = 0.75 * threshold_peak_new;
                double n = 0;
                int m = 0;
                for (int t = minwpos - winPT; t <= minwpos + winPT; t++) {/////Check if a peak has been misdetected
                    if (data_HR.get(t) - mean >= threshold * std && data_HR.get(t) > n) {
                        n = data_HR.get(t);
                        m = t;
                        // missDetection = 2;
                    }

                }
                if (n != 0) {
                    HR_P.add(n);
                    HR_P_POS.add(m);
                    maxP = m;
                    HR_T.add(minw);
                    HR_T_POS.add(minwpos);
                    maxT = minwpos;
                    if (m > minwpos) {
                        HR.set(minwpos, minw);
                        HR.set(m, n);

                    } else {
                        HR.set(m, n);
                        HR.set(minwpos, minw);
                    }
                    Log.d("Detection", "Peak was misdetected");

                } else {///Else a trough has been overdetected
                    Log.d("Detection", "Trough was overdetected");
                }

            } else if (mwpos != 0 && minwpos == 0 && withinRfT != -1)////If a peak has been detected and a trough has not been detected
            {
                Log.d("Detection", "Peak has been Detected");
                double threshold = 0.75 * threshold_trough_new;
                double n = 0;
                int m = 0;
                for (int t = mwpos - winPT; t <= mwpos + winPT; t++) {
                    if (data_HR.get(t) - mean <= threshold * std && data_HR.get(t) < n) {///Check if a trough has been missed
                        n = data_HR.get(t);
                        m = t;
                        // missDetection = 2;

                    }

                }
                if (n != 0) {
                    HR_P.add(mw);
                    HR_P_POS.add(mwpos);
                    maxP = mwpos;
                    HR_T.add(n);
                    HR_T_POS.add(m);
                    maxT = m;
                    if (mwpos > m) {
                        HR.set(m, n);
                        HR.set(mwpos, mw);

                    } else {
                        HR.set(mwpos, mw);
                        HR.set(m, n);
                    }
                    Log.d("Detection", "Peak detetced");

                } else {
                    Log.d("Detection", "Trough was overdeteced");///Else a peak has been overdetected
                }

            }



        }


        if(HR_P_POS.size()==start)
        {
            Log.d("Is it entering this","yes");
            double tp = 0.7*threshold_peak_new;
            double tt = 0.7*threshold_peak_new;

            for(int i=startIndex;i<startIndex+windowNormal;i=i+refractoryWindow)
            {
                double mw = 0, minw = 0;
                int mwpos = 0, minwpos = 0;
                withinRfP = 0;
                withinRfT = 0;




                for (int j = 0; j < refractoryWindow; j++) {
                    if ((data_HR.get(i + j) - mean >= tp * std) && data_HR.get(i + j) > mw){
                        if((i + j) - maxP > refractoryWindow)
                        {
                            mw = data_HR.get(i + j);
                            mwpos = i + j;
                            withinRfP = 1;
                        }else withinRfP = -1;
                    } else if ((data_HR.get(i + j) - mean <= tt * std) && data_HR.get(i + j) < minw){
                        if((i + j) - maxT > refractoryWindow)
                        {
                            minw = data_HR.get(i + j);
                            minwpos = i + j;
                            withinRfT = 1;
                        }else withinRfT = -1;
                    }
                }

                if (mwpos != 0 && minwpos != 0) // Both peak and trough has been detected
                {
                    //if(abs(mwpos - minwpos) <= winPT) // They fall under the category of correct detection
                    //{
                    HR_P.add(mw);
                    HR_P_POS.add(mwpos);
                    maxP = mwpos;
                    HR_T.add(minw);
                    HR_T_POS.add(minwpos);
                    maxT = minwpos;
                    if(mwpos>minwpos) {
                        HR.set(minwpos, minw);
                        HR.set(mwpos,mw);

                    }else{
                        HR.set(mwpos,mw);
                        HR.set(minwpos, minw);
                    }
                }else if(mwpos == 0 && minwpos != 0 && withinRfP != -1) {////Else if a trough has been detected and a peak has not been detected
                    double threshold = 0.9 * tp;
                    double n = 0;
                    int m = 0;
                    for (int t = minwpos - winPT; t <= minwpos + winPT; t++) {/////Check if a peak has been misdetected
                        if (data_HR.get(t) - mean >= threshold * std && data_HR.get(t) > n) {
                            n = data_HR.get(t);
                            m = t;
                            // missDetection = 2;

                        }

                    }
                    if (n != 0)
                    {
                        HR_P.add(n);
                        HR_P_POS.add(m);
                        maxP = m;
                        HR_T.add(minw);
                        HR_T_POS.add(minwpos);
                        maxT = minwpos;
                        if(m>minwpos) {
                            HR.set(minwpos, minw);
                            HR.set(m,n);

                        }else{
                            HR.set(m,n);
                            HR.set(minwpos, minw);
                        }
                        Log.d("Error","Peak as underdetected");

                    } else {///Else a trough has been overdetected
                        Log.d("Error", "Trough was overdetected");
                    }

                }else if(mwpos != 0 && minwpos == 0 && withinRfT != -1)////If a peak has been detected and a trough has not been detected
                {
                    double threshold = 0.9 * tt;
                    double n = 0;
                    int m = 0;
                    for (int t = mwpos - winPT; t <= mwpos + winPT; t++) {
                        if (data_HR.get(t) - mean <= threshold * std && data_HR.get(t) < n) {///Check if a trough has been missed
                            n = data_HR.get(t);
                            m = t;
                            // missDetection = 2;

                        }

                    }
                    if (n != 0) {
                        HR_P.add(mw);
                        HR_P_POS.add(mwpos);
                        maxP = mwpos;
                        HR_T.add(n);
                        HR_T_POS.add(m);
                        maxT = m;
                        if(mwpos>m) {
                            HR.set(m, n);
                            HR.set(mwpos,mw);

                        }else{
                            HR.set(mwpos,mw);
                            HR.set(m, n);
                        }
                        Log.d("Error","Trough was underdetected");

                    } else {
                        Log.d("Error", "Peak was overdetected");///Else a peak has been overdetected
                    }

                }
            }

        }

        Log.d("Start size",String.valueOf(start));
        Log.d("End size",String.valueOf(HR_P_POS.size()));


        // if(start!=0)
        //{



        //for(int h=start;h<=HR_P_POS.size()-1;h++) {
        //  int hrp = HR_P_POS.get(h) - HR_P_POS.get(h-1);
        // int hrT = HR_T_POS.get(h) - HR_T_POS.get(h-1);
        // RR_P.add(hrp);
        //RR_T.add(hrT);

        //int avg = (60 / (hrp + hrT) / 200);
        //RR_HR.update(avg);
        //heartIndex = heartIndex + 1;
        //}




        //  Log.d("RR Peak",String.valueOf(hrp));
        // Log.d("RR Trough", String.valueOf(hrT));


        //  }
        // Log.d("HR Size",String.valueOf(HR_P.size()));
        // Log.d("HR Size1", String.valueOf(HR_T.size()));


    }



    ///// Respiration Calculation/////////////////
    private static void calcRR(int index2) {

        double zeroCrossing = 0;
        for(int i = index2;i<index2+windowRespiration;i=i+100)
        {
            if((data_RR.get(i)>=0 && data_RR.get(i+100)<0) || data_RR.get(i)<0 && data_RR.get(i+100)>=0) {
                zeroCrossing++;
            }


            mTextView2.setText(String.valueOf(6*(zeroCrossing/2))+""+"bpm");
            //Log.d("Respiratory",String.valueOf(zeroCrossing));


        }

    }










}