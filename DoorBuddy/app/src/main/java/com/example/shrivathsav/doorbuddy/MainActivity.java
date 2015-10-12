package com.example.shrivathsav.doorbuddy;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private TextView doorStatusTextView;
    private Button openDoorButton;
    private EditText usernameField;
    private String username;

    private Socket s;
    private PrintWriter out;
    private Scanner in;
    private boolean closed;

    private static final String IP = "192.168.1.7";
    private static final int PORT = 1809;

    private static final DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private static final String APPTAG = "DOORBUDDY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = "";
        closed = true;
        initializeViews();
        setButtonListener();
    }

    private void initializeViews()
    {
        doorStatusTextView = (TextView) findViewById(R.id.textViewDoorStatus);
        openDoorButton = (Button) findViewById(R.id.buttonOpenDoor);
        usernameField = (EditText) findViewById(R.id.editTextUsername);
    }

    private void setButtonListener()
    {
        openDoorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameField.getText().toString();
                Date now = new Date();
                String time = df.format(now);
                String outData = "<D:>ARDUINO<M:>OPEN<U:>" + username + "<T:>" + time;
                if(closed)
                    new ConnectTask().execute(username);
                new SocketWriter().execute(outData);
            }
        });
    }

    private class ConnectTask extends AsyncTask<String, Void, Void>
    {
        protected Void doInBackground(String... params)
        {
            try {
                s = new Socket(IP, PORT);
                out = new PrintWriter(s.getOutputStream());
                out.println("ANDROID_OPEN_" + params[0]);
                out.flush();
                in = new Scanner(s.getInputStream());
                closed = false;
                new Thread(new SocketReader()).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class SocketWriter extends AsyncTask<String, Void, Void>
    {
        protected Void doInBackground(String... params)
        {
            String outData = params[0];
            if(out != null)
            {
                out.println(outData);
                out.flush();
            }
            return null;
        }
    }

    private class CloseConnectionTask extends AsyncTask<Void, Void, Void>
    {
        protected Void doInBackground(Void... params)
        {
            if(s != null && out != null)
            {
                Date now = new Date();
                String time = df.format(now);
                out.println("<D:>SERVER<M:>CLOSE<U:>" + username + "<T:>" + time);
                out.flush();
            }
            return null;
        }

        protected void onPostExecute(Void result)
        {
            try {
                out.close();
                in.close();
                s.close();
                closed = true;
                Log.v(APPTAG, "On Stop Executed, Socket Closed.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SocketReader implements Runnable
    {
        public void run() {
            while (!closed) {
                if (in != null) {
                    if (in.hasNextLine()) {
                        String inData = in.nextLine();
                        Pattern p = Pattern.compile("<M:>(.*)<T:>(.*)");
                        Matcher m = p.matcher(inData);
                        if (m.matches()) {
                            String message = m.group(1);
                            String time = m.group(2);
                            doorStatusTextView.setText(time + "\n" + message);
                        }
                    }
                }
            }
        }
    }

    protected void onStop()
    {
        if(!closed)
            new CloseConnectionTask().execute();
        super.onStop();
    }

    protected void onDestroy()
    {
        if(!closed)
            new CloseConnectionTask().execute();
        super.onDestroy();
    }
}
