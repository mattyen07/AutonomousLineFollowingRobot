package com.example.robotcontrol;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private Socket socket; //Create the socket instance
//    private BufferedInputStream in; //input stream instance
    private InputStream in;
    private PrintWriter out; //output stream instance
    private InetAddress address; //Put address of the raspberry pi here
    private int port; //Put the port number of the raspberry pi here
    private ImageView robotCamera; //create the imageView instance
    private Switch modeToggle;
    private TextView modeStatus;
    private Thread liveFeedThread;
    private Button forwardButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Instantiate the port
        port = 5059;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        try {
            address = InetAddress.getByName("137.82.226.227");
//            address = InetAddress.getByName("137.82.226.222");

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //Instantiate the mode toggle
        modeToggle = (Switch) findViewById(R.id.modeToggle);
        //Instantiate the mode status
        modeStatus = (TextView) findViewById(R.id.ModeStatus);
        //Get the forwardButton
        forwardButton = (Button) findViewById(R.id.forward);
        //Get the leftButton
        final Button leftButton = (Button) findViewById(R.id.leftwards);
        //Get the rightbutton
        final Button rightButton = (Button) findViewById(R.id.rightwards);
        //Get the stopButton
        final Button stopButton = (Button) findViewById(R.id.stop);
        //Get the startButton
        final Button startButton = (Button) findViewById(R.id.Start);

        //Instantiate the imageView
        robotCamera = findViewById(R.id.imageView);
        robotCamera.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        robotCamera.setVisibility(View.VISIBLE);
        //Set a default image
//        robotCamera.setImageResource(R.drawable.ic_launcher_background);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sendRequest("Null", "Autonomous");
                mainFunctionality();
            }
        });

        //Add an event listener for the forwardsButton to call moveForward onclick
        forwardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                moveForward();
            }
        });

        //Add an event listener
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });

//        forwardButton.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch(event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        // PRESSED
////                        while(event.getAction() == MotionEvent.ACTION_DOWN) {
//                            moveForward();
////                        }
//                        return true; // if you want to handle the touch event
//                    case MotionEvent.ACTION_UP:
//                        // RELEASED
//                        return true; // if you want to handle the touch event
//                }
//                return false;
//            }
//        });

//        leftButton.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch(event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        // PRESSED
//                        turnLeft();
//                        return true; // if you want to handle the touch event
//                    case MotionEvent.ACTION_UP:
//                        // RELEASED
//                        return true; // if you want to handle the touch event
//                }
//                return false;
//            }
//        });

        //Add an event listener for the leftButton to call turnLeft onclick
        leftButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                turnLeft();
            }
        });

//        rightButton.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch(event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        // PRESSED
//                        turnRight();
//                        return true; // if you want to handle the touch event
//                    case MotionEvent.ACTION_UP:
//                        // RELEASED
//                        return true; // if you want to handle the touch event
//                }
//                return false;
//            }
//        });

        //Add an event listener for the rightButton to call turnRight onclick
        rightButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                turnRight();
            }
        });

        //Add an event listener for the toggle
        modeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //If the switch is on
                if(isChecked) {
                    modeStatus.setText("Mode:Remote Control");
                }
                else {
                    modeStatus.setText("Mode:Autonomous");
                }
                ChangeMode(isChecked);
            }
        });

        //Try to serve
            try {
                serve();
            } catch (Exception e) {
                //If exception, print stack trace
                e.printStackTrace();
            }
    }

    //Start the sever
    public void serve() throws IOException{
        //Set the onscreen message to say that there is a good connection
        final TextView connectedStatus = (TextView) findViewById(R.id.textView);

        //While loop for handling until we close sockets
//        while (true) {
            try {
                //Connect it to the specified port and ipaddress
                socket = new Socket(address, port);
                System.out.println("CONNECTED");
                //Setup the input and output streams
//                in = new BufferedInputStream(socket.getInputStream());
                in = socket.getInputStream();
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

                //If no exception, display a message so we know that it is connected
                connectedStatus.setText("Connected");
//                mainFunctionality();
//                liveFeedThread = new Thread(recieveFeed());
//                liveFeedThread.start();


                //Start the mainFunction
//                mainFunctionality();
            } catch (Exception e) {
                System.out.println("Not connected");
                //If execption, print stack trace
                e.printStackTrace();
            //Once the try catch finishes
            }
//            finally {
//                //Close the input stream
//                in.close();
//                //Close the output stream
//                out.close();
//                //Close the socket
//                socket.close();
//            }
//        }

    }

    private final Runnable recieveFeed() {
        return new Runnable() {
            @Override
            public void run() {
                mainFunctionality();
            }
        };
    }

    //Start to listen and serve requests
    public void mainFunctionality() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 32;
        //Continually update the photo
//        while (true) {
            //create a new bitmap
            Bitmap image = BitmapFactory.decodeStream(in, null, options);
            //set the image as the newly converted bitma[
            if (image != null) {
//                forwardButton.setBackground(new BitmapDrawable(getResources(),image));
//                robotCamera.setImageBitmap(null);
                robotCamera.setImageBitmap(image);
//                robotCamera.setImageBitmap(image);

                robotCamera.setBackground(new BitmapDrawable(getResources(), image));
            }
            else {
                System.out.println("YEEET");
            }


//            robotCamera.setImageBitmap(Bitmap.createScaledBitmap(image, robotCamera.getWidth() , robotCamera.getHeight() ,false));
//            robotCamera.s
//            robotCamera.setImageDrawable(new BitmapDrawable(getResources(), image));
//        }
    }

    //Send a request
    public void sendRequest(String movement, String mode) {
        //Create a new JSONObject
        JSONObject request = new JSONObject();

        //Try adding fields to the JSON
        try {
            request.put("Type", movement);
            request.put("Mode", mode);
            //Send the request out over the socket
            out.print(request);
            //Flush the socket
            out.flush();
        }
        catch (Exception e) {
            //If exception, print stack trace
            e.printStackTrace();
        }
    }

    //Tell the pi to change modes
    public void ChangeMode(boolean mode) {
        if(mode) {
            sendRequest("NULL", "Remote");
        }
        else {
            sendRequest("NULL", "Autonomous");
        }
    }

    //send a request to turn left
    public void turnLeft()  {
        if(modeStatus.getText() == "Mode:Remote Control") {
            sendRequest("Left", "Remote");
        }
    }

    //send a request to turn right
    public void turnRight() {
        if(modeStatus.getText() == "Mode:Remote Control") {
            sendRequest("Right", "Remote");
        }
    }

    //send a request to move forwards
    public void moveForward() {
        if(modeStatus.getText() == "Mode:Remote Control") {
            sendRequest("Forward", "Remote");
        }
    }

    public void stop() {
        if(modeStatus.getText() == "Mode:Remote Control") {
            sendRequest("Stop", "Remote");
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
}
