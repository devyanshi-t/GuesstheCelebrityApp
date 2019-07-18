package com.example.et.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    public class DownloadTask extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... urls)

        {
            URL url;
            HttpURLConnection urlConnection = null;
            String result = "";
            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream is = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(is);
                int data = reader.read();
                while (data != -1) {
                    char ch = (char) data;
                    result += ch;
                    data = reader.read();

                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return "FAILED";
        }

    }
    public class ImageDownloader extends AsyncTask<String,Void,Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls)

        {
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream is = urlConnection.getInputStream();
                Bitmap myBitMap = BitmapFactory.decodeStream(is);
                return myBitMap;


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    Button b0,b1,b2,b3;
    String ans[]=new String[4];
    ArrayList<String> celebNames=new ArrayList<String>();
    ArrayList<String> celebUrl=new ArrayList<String>();
    int chosenCeleb=0;
        ImageView imageView;
    //Random rand;
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b0=(Button)findViewById(R.id.button0);
        b1=(Button)findViewById(R.id.button1);
        b2=(Button)findViewById(R.id.button2);
        b3=(Button)findViewById(R.id.button3);
        imageView=(ImageView)findViewById(R.id.imageView);
        //Random rand;
        DownloadTask task=new DownloadTask();
        String result;
        try{

           result=task.execute("http://www.posh24.se/kandisar").get();

            //processing the html
            String[] splitResult=result.split("<div class=\"sidebarContainer\">");//to remove rest of unnesseary data on the html page
            Pattern p=Pattern.compile("<img src=\"(.*?)\"");
            Matcher m=p.matcher(splitResult[0]);
            while(m.find())
            {

                celebUrl.add(m.group(1));
            }
            p=Pattern.compile("alt=\"(.*?)\"");
             m=p.matcher(splitResult[0]);
            while(m.find())
            {

               celebNames.add(m.group(1));
            }
          //rand=new Random();



        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

       generateOptions();
    }
    //String correctAns;
    int correctLocation=0;

    public void generateOptions()

    {
          Random rand=new Random();
        chosenCeleb=rand.nextInt(celebUrl.size());
        ImageDownloader imageDownloader=new ImageDownloader();
        Bitmap mybit;
        try{
            mybit=imageDownloader.execute(celebUrl.get(chosenCeleb)).get();
            imageView.setImageBitmap(mybit);
           correctLocation=rand.nextInt(4);
            int  incorrectLoaction;
            for(int i=0;i<4;i++)
           {
            if(i==correctLocation)
            {
              ans[i]=celebNames.get(chosenCeleb);
            }
            else
            {
              incorrectLoaction=rand.nextInt(celebUrl.size());
                while(chosenCeleb==incorrectLoaction)
                {
                    incorrectLoaction=rand.nextInt(celebUrl.size());
                }
                ans[i]=celebNames.get(incorrectLoaction);
            }
        }
        b0.setText(ans[0]);
        b1.setText(ans[1]);
        b2.setText(ans[2]);
        b3.setText(ans[3]);
        }

        catch (Exception e)
        {e.printStackTrace();}
    }

    public void celebrityChosen(View view)
    {    String s1=view.getTag().toString();
        String s2=Integer.toString(correctLocation);
        if(view.getTag().toString().equals(Integer.toString(correctLocation)))
        {
            Toast.makeText(getApplicationContext(),"CORRECT!",Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Incorrect !\n"+"The correct answer is  "+celebNames.get(chosenCeleb),Toast.LENGTH_LONG).show();
        }

    generateOptions();

    }
}
