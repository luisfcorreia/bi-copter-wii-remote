package net.loide.games.bicopter;


import fi.sulautetut.android.tblueclient.TBlue;
import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Bluetest extends Activity {
 
    TBlue tBlue;
    TextView messagesTv; 
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initGUI();
    }
 
    @Override
    public void onResume()
    {
        super.onResume();
        tBlue=new TBlue(""); 
        if (tBlue.streaming()) {
            messagesTv.append("Connected succesfully! ");
        } else {
            messagesTv.append("Error: Failed to connect. ");
        } 
        String s="";
        while (tBlue.streaming() && (s.length()<10) ) {
            s+=tBlue.read();
        }
        messagesTv.append("Read from Bluetooth: \n"+s);
    }
 
    @Override
    public void onPause()
    {
        super.onPause();
        tBlue.close();
    } 
 
    public void initGUI()
    {
        LinearLayout container=new LinearLayout(this);
        messagesTv = new TextView(this);
        container.addView(messagesTv);
        setContentView(container); 
    }
}