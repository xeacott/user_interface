package com.example.user_interface;

import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private Handler handler;
    ConnectedThread mConnectedThread;

    // Widget information
    static String TAG = "MainActivity";
    EditText send_data;
    TextView view_data;
    StringBuilder messages;

    public boolean electro__graph_bool= false;
    public boolean pulseox_graph_bool = false;

    // Create a bluetooth adapter to get the devices bluetooth adapter
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    /*
    This hooks to "Establish Connection" UI button is Start Session. In order to begin
    collecting information, bluetooth for both devices must be on. This method will
    open a socket over bluetooth to connect each device.
     */
    public void pairDevice(int ... params) {

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        Log.e("MainActivity", "" + pairedDevices.size());
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices ) {
                mDevice = device;
            }

            // Pass the paired device to create a socket between devices
            BluetoothSocket tmp = null;
            try {
                tmp = mDevice.createRfcommSocketToServiceRecord(MY_UUID_INSECURE);
            } catch (IOException e) {
                Toast.makeText(this, "Could not establish socket, check UUID", Toast.LENGTH_SHORT).show();
            }
            mSocket = tmp;

            // First open the socket by connecting it
            try {
                mSocket.connect();
            } catch (IOException connectException) {
                try {
                    mSocket.close();
                } catch (IOException closeException ) { }
                Toast.makeText(this, "Device did not pair.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Pass the open socket to a thread to send/receive messages and start it
            ConnectedThread mConnectThread = new ConnectedThread(mSocket);
            mConnectThread.start();
        }
    }

    /*
     Open a socket using a thread.
     This thread is responsible for maintaining the BTconnection, sending the data,
     and receiving incoming data through input/output streams.
      */

    // Allow messages to be sent back and forth using input stream and output stream.
    // Note:: This is a thread that is continuously watching the socket to either
    // receive information or send information.
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Obtain a handle to the socket's input and output streams
            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream

            int begin = 0;
            int bytes = 0; // bytes returned from read()
            while (true) {
                // Read from the InputStream
                try {
                    bytes += mmInStream.read(buffer, bytes, buffer.length - bytes);
                    for(int i = begin; i < bytes; i++) {
                        if(buffer[i] == "#".getBytes()[0]) {
                            final String incomingMessage = new String(buffer, 0, bytes);
                            Log.d(TAG, "InputStream: " + incomingMessage);

                            /*
                            Runs the specified action on the UI thread. If the current thread is the UI
                            thread, then the action is executed immediately. If the current thread is
                            not the UI thread, the action is posted to the event queue of the UI thread.
                            */
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // This is the exact spot where the UI can be updated in real time
                                    // without slowing down the UI.
                                    // See: https://developer.android.com/guide/components/processes-and-threads#java
                                    view_data.setText(incomingMessage);
                                }
                            });
                            begin = i +1;
                            if(i == bytes -1) {bytes = 0; begin = 0;
                                }
                            }
                        }
                } catch (IOException e) {
                    Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage());
                    break;
                }
            }
        }

        /* Do not call directly, call send message instead */
        public void write(byte[] bytes) {
            String text = new String(bytes, Charset.defaultCharset());
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "write: Error writing to output stream. " + e.getMessage());
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    /*
    This method will send a message directly to the connected device and is currently unhooked.
    It may be used to end a session, change parameters on the fly, etc.
    This is the handle to the input stream from the live socket.
     */
    public void SendMessage(View v) {
        byte[] bytes = send_data.getText().toString().getBytes(Charset.defaultCharset());
        mConnectedThread.write(bytes);
    }

    public void EndConnection(View v) {
        mConnectedThread.cancel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (mBluetoothAdapter == null) {
            // Device does not support bluetooth
            Toast.makeText(this, "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
    }
    public void onStartSessionClick(MenuItem item) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.action_start_session);
        String message = item.toString();
        intent.putExtra(TAG, message);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2) {
            pairDevice();
            //Toast.makeText(this, "Came back from StartSession with params available.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onEndSessionClick(MenuItem item) {
        Toast toast = Toast.makeText(this, "Session has ended!", Toast.LENGTH_LONG);
        toast.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case R.id.action_bluetooth_menu:
                // Call method to open bluetooth menu
                return true;
            case R.id.action_start_session:
                return true;
            case R.id.action_end_session:
                // Call method to end a session
                return true;
            case R.id.action_checkbox_ekg:

                if (item.isChecked()) {
                    item.setChecked(false);
                    electro__graph_bool = false;
                }
                else {
                    item.setChecked(true);
                    electro__graph_bool = true;
                }
                return true;

            case R.id.action_checkbox_pulse_ox:

                if (item.isChecked()) {
                    item.setChecked(false);
                    pulseox_graph_bool = false;
                }
                else {
                    item.setChecked(true);
                    pulseox_graph_bool = true;
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            // Fragment 1
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                // Set the header text for the fragment
                TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                textView.setText(getString(R.string.section_format, getString(R.string.electro_cardiograph), 25));

                // Draw the graph
                GraphView graph = (GraphView) rootView.findViewById(R.id.graph);
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                        new DataPoint(0, 1),
                        new DataPoint(1, 5),
                        new DataPoint(2, 3),
                        new DataPoint(3, 2),
                        new DataPoint(4, 6)
                });
                graph.addSeries(series);
            }

            // Fragment 2
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
                // Set the header text for the fragment
                TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                textView.setText(getString(R.string.section_format, getString(R.string.pulse_oximetry), 1200));

                // Draw the graph
                GraphView graph = (GraphView) rootView.findViewById(R.id.graph);
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                        new DataPoint(0, 1),
                        new DataPoint(1, 5),
                        new DataPoint(2, 3),
                        new DataPoint(3, 2),
                        new DataPoint(4, 6)
                });
                graph.addSeries(series);
            }

            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }
}
