package ro.tkf.apps.fedsigremote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Button btn_off, btn_pa, btn_radio, btn_siren, btn_1, btn_2, btn_3, btn_4, btn_5, btn_6;
    private int siren_state = 0, alley_state = 0;
    private boolean strobes_enabled = false, lightbar_enabled = false, rearalley_enabled = false;

    static final UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    static final String REMOTE_ADDR = "98:D3:31:FD:4B:2F";

    private BluetoothSocket mBtSocket;

    private boolean btSetup = false;

    private OutputStream os = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btn_off = (Button)findViewById(R.id.btn_off);
        if(btn_off != null)
        {
            btn_off.setBackgroundColor(0x00000000);
            btn_off.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        btn_off.setBackground(getResources().getDrawable(R.drawable.btn_active, getTheme()));
                        return true;
                    }
                    else if(motionEvent.getAction() == MotionEvent.ACTION_UP)
                    {
                        btn_off.setBackgroundColor(0x00000000);

                        siren_state = 0;
                        if(btn_siren != null)
                            btn_siren.setBackgroundColor(0x00000000);

                        lightbar_enabled = false;
                        if(btn_1 != null)
                            btn_1.setBackgroundColor(0x00000000);

                        strobes_enabled = false;
                        if(btn_2 != null)
                            btn_2.setBackgroundColor(0x00000000);

                        rearalley_enabled = false;
                        if(btn_3 != null)
                            btn_3.setBackgroundColor(0x00000000);

                        alley_state = 0;
                        if(btn_4 != null)
                            btn_4.setBackgroundColor(0x00000000);

                        MainActivity.this.btSend("0".getBytes());
                        return true;
                    }
                    return false;
                }
            });
        }

        btn_pa = (Button)findViewById(R.id.btn_pa);
        if(btn_pa != null)
        {
            btn_pa.setBackgroundColor(0x00000000);
            btn_pa.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        btn_pa.setBackground(getResources().getDrawable(R.drawable.btn_active, getTheme()));
                        return true;
                    }
                    else if(motionEvent.getAction() == MotionEvent.ACTION_UP)
                    {
                        btn_pa.setBackgroundColor(0x00000000);
                        return true;
                    }
                    return false;
                }
            });
        }

        btn_radio = (Button)findViewById(R.id.btn_radio);
        if(btn_radio != null)
        {
            btn_radio.setBackgroundColor(0x00000000);
            btn_radio.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        btn_radio.setBackground(getResources().getDrawable(R.drawable.btn_active, getTheme()));
                        return true;
                    }
                    else if(motionEvent.getAction() == MotionEvent.ACTION_UP)
                    {
                        btn_radio.setBackgroundColor(0x00000000);
                        return true;
                    }
                    return false;
                }
            });
        }

        btn_siren = (Button)findViewById(R.id.btn_siren);
        if(btn_siren != null)
        {
            btn_siren.setBackgroundColor(0x00000000);
            btn_siren.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        btn_siren.setBackground(getResources().getDrawable(R.drawable.btn_active, getTheme()));
                        return true;
                    }
                    else if(motionEvent.getAction() == MotionEvent.ACTION_UP)
                    {
                        siren_state = (siren_state + 1) % 4;
                        MainActivity.this.btSend("S".getBytes());

                        if(siren_state == 0)
                            btn_siren.setBackgroundColor(0x00000000);
                        else
                        {
                            if(!lightbar_enabled)
                            {
                                lightbar_enabled = true;
                                if(btn_1 != null)
                                    btn_1.setBackground(getResources().getDrawable(R.drawable.btn_active, getTheme()));

                                if(!strobes_enabled)
                                {
                                    strobes_enabled = true;
                                    if(btn_2 != null)
                                        btn_2.setBackground(getResources().getDrawable(R.drawable.btn_active, getTheme()));
                                }
                            }
                        }
                        return true;
                    }
                    return false;
                }
            });
        }

        btn_1 = (Button)findViewById(R.id.btn_1); // Lightbar
        if(btn_1 != null)
        {
            btn_1.setBackgroundColor(0x00000000);
            btn_1.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        btn_1.setBackground(getResources().getDrawable(R.drawable.btn_active, getTheme()));
                        return true;
                    }
                    else if(motionEvent.getAction() == MotionEvent.ACTION_UP)
                    {
                        MainActivity.this.btSend("1".getBytes());

                        if(btn_2 != null && !strobes_enabled && !lightbar_enabled)
                        {
                            btn_2.setBackground(getResources().getDrawable(R.drawable.btn_active, getTheme()));
                            strobes_enabled = true;
                        }

                        lightbar_enabled = true;
                        return true;
                    }
                    return false;
                }
            });
        }

        btn_2 = (Button)findViewById(R.id.btn_2); // Strobes (Grille & Rear)
        if(btn_2 != null)
        {
            btn_2.setBackgroundColor(0x00000000);
            btn_2.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        btn_2.setBackground(getResources().getDrawable(R.drawable.btn_active, getTheme()));
                        return true;
                    }
                    else if(motionEvent.getAction() == MotionEvent.ACTION_UP)
                    {
                        MainActivity.this.btSend("2".getBytes());

                        strobes_enabled = !strobes_enabled;
                        if(!strobes_enabled)
                            btn_2.setBackgroundColor(0x00000000);

                        if(btn_1 != null && !lightbar_enabled) {
                            btn_1.setBackground(getResources().getDrawable(R.drawable.btn_active, getTheme()));
                            lightbar_enabled = true;
                        }
                        return true;
                    }
                    return false;
                }
            });
        }

        btn_3 = (Button)findViewById(R.id.btn_3); // Rear Alley
        if(btn_3 != null)
        {
            btn_3.setBackgroundColor(0x00000000);
            btn_3.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        btn_3.setBackground(getResources().getDrawable(R.drawable.btn_active, getTheme()));
                        return true;
                    }
                    else if(motionEvent.getAction() == MotionEvent.ACTION_UP)
                    {
                        MainActivity.this.btSend("3".getBytes());

                        rearalley_enabled = !rearalley_enabled;
                        if(!rearalley_enabled)
                            btn_3.setBackgroundColor(0x00000000);
                        return true;
                    }
                    return false;
                }
            });
        }

        btn_4 = (Button)findViewById(R.id.btn_4); // Side Alley
        if(btn_4 != null)
        {
            btn_4.setBackgroundColor(0x00000000);
            btn_4.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        btn_4.setBackground(getResources().getDrawable(R.drawable.btn_active, getTheme()));
                        return true;
                    }
                    else if(motionEvent.getAction() == MotionEvent.ACTION_UP)
                    {
                        MainActivity.this.btSend("4".getBytes());

                        alley_state = (alley_state + 1) % 4;

                        if(alley_state == 0)
                            btn_4.setBackgroundColor(0x00000000);
                        return true;
                    }
                    return false;
                }
            });
        }

        btn_5 = (Button)findViewById(R.id.btn_5); //Not mapped
        if(btn_5 != null)
        {
            btn_5.setBackgroundColor(0x00000000);
            btn_5.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        btn_5.setBackground(getResources().getDrawable(R.drawable.btn_active, getTheme()));
                        return true;
                    }
                    else if(motionEvent.getAction() == MotionEvent.ACTION_UP)
                    {
                        btn_5.setBackgroundColor(0x00000000);
                        return true;
                    }
                    return false;
                }
            });
        }

        btn_6 = (Button)findViewById(R.id.btn_6); //Not mapped
        if(btn_6 != null)
        {
            btn_6.setBackgroundColor(0x00000000);
            btn_6.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        btn_6.setBackground(getResources().getDrawable(R.drawable.btn_active, getTheme()));
                        return true;
                    }
                    else if(motionEvent.getAction() == MotionEvent.ACTION_UP)
                    {
                        btn_6.setBackgroundColor(0x00000000);
                        return true;
                    }
                    return false;
                }
            });
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
        if (id == R.id.action_bluetooth) {
            setupBluetooth();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mBtSocket != null) {
            try {
                mBtSocket.close();
                btSetup = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                mBtSocket = null;
            }
        }
    }

    private void setupBluetooth()
    {
        if(btSetup)
        {
            Toast.makeText(MainActivity.this, "Bluetooth connection is already established", Toast.LENGTH_SHORT).show();
            return;
        }

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null)
        {
            Toast.makeText(MainActivity.this, "This device does not support bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!mBluetoothAdapter.isEnabled())
        {
            Toast.makeText(MainActivity.this, "Bluetooth is not active", Toast.LENGTH_SHORT).show();
            return;
        }

        BluetoothDevice remote = mBluetoothAdapter.getRemoteDevice(MainActivity.REMOTE_ADDR);
        if(remote == null)
        {
            Toast.makeText(MainActivity.this, "The specified device could not be found", Toast.LENGTH_SHORT).show();
            return;
        }

        try
        {
            mBtSocket = remote.createRfcommSocketToServiceRecord(BT_UUID);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error creating BT Socket", Toast.LENGTH_SHORT).show();
            return;
        }

        mBluetoothAdapter.cancelDiscovery();
        try {
            mBtSocket.connect();
        }
        catch (IOException e) {
            Toast.makeText(MainActivity.this, "Error connecting to BT device, trying fallback", Toast.LENGTH_SHORT).show();

            try {
                mBtSocket = (BluetoothSocket) remote.getClass().getMethod("createRfcommSocket", int.class).invoke(remote, 1);
                mBtSocket.connect();
            }
            catch(Exception ex)
            {
                e.printStackTrace();
                try {
                    mBtSocket.close();
                }
                catch (IOException ex2) {
                    ex.printStackTrace();
                }
                Toast.makeText(MainActivity.this, "Error connecting to BT device", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        try {
            os = mBtSocket.getOutputStream();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(MainActivity.this, "Connection established", Toast.LENGTH_SHORT).show();
        btSetup = true;
    }

    private void btSend(byte[] msg)
    {
        if(this.os == null) return;

        try {
            this.os.write(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
