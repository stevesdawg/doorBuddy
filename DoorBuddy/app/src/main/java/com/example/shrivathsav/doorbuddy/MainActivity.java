package com.example.shrivathsav.doorbuddy;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

    private Socket s;
    private PrintWriter out;
    private Scanner in;
    private boolean closed;
    private ArrayBlockingQueue<String> messagesToShow;

    private static final String IP = "128.61.45.84";
    private static final int PORT = 1809;

    private static final DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        closed = true;
        initializeViews();
    }

    private void initializeViews()
    {
        doorStatusTextView = (TextView) findViewById(R.id.textViewDoorStatus);
        openDoorButton = (Button) findViewById(R.id.buttonOpenDoor);
        usernameField = (EditText) findViewById(R.id.editTextUsername);

        setButtonListener();
    }

    private void setButtonListener()
    {
        openDoorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = usernameField.getText().toString();
                Date now = new Date();
                String time = df.format(now);
                String outData = "<D:>ARDUINO<M:>OPEN<U:>" + userName + "<T:>" + time;
                if(closed)
                    new ConnectTask().execute(userName);
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
                out.println("ANDROID_OPEN_" + params[1]);
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
            out.println(outData);
            out.flush();
            return null;
        }
    }

    private class SocketReader implements Runnable
    {
        public void run()
        {
            while(!closed)
            {
                if(in.hasNextLine())
                {
                    String inData = in.nextLine();
                    Pattern p = Pattern.compile("<M:>(.*)<T:>(.*)");
                    Matcher m = p.matcher(inData);
                    if(m.matches())
                    {
                        String message = m.group(1);
                        String time = m.group(2);
                        doorStatusTextView.setText(time + "\n" + message);
                    }
                    else if(inData.equals("Disconnected From Server"))
                    {
                        closed = true;
                        doorStatusTextView.setText("Disconnected From Server");
                        out.close();
                        in.close();
                        try {
                            s.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            out.close();
            in.close();
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void onResume()
    {
        new ConnectTask().execute("steve");
        super.onResume();
    }

    protected void onPause()
    {
        closed = true;
        Date now = new Date();
        String time = df.format(now);
        new SocketWriter().execute("<D:>Server<M:>CLOSE<U:><T:>" + time);
        super.onPause();
        out.close();
        in.close();
        try {
            s.close();
        } catch (IOException e) {
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
}
