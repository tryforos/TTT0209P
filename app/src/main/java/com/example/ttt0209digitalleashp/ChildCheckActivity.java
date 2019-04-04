package com.example.ttt0209digitalleashp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChildCheckActivity extends AppCompatActivity {

    ImageView imageChildCheck;
    byte[] bytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_check);

        imageChildCheck = (ImageView)findViewById(R.id.imageChildCheck);

        //set image from passed url
        String url = getIntent().getStringExtra("EXTRA_URL");
        new ImageDownloader().execute(url);

        //pull extra data
        double dblDistance = getIntent().getDoubleExtra("EXTRA_DISTANCE",-1.0);
        double dblRadius = getIntent().getDoubleExtra("EXTRA_RADIUS",-1.0);

        //give msg
        Toast.makeText(this, "Radius: " + dblRadius + "\nDistance: " + dblDistance, Toast.LENGTH_LONG).show();

    }

    public void buttonClickedChildCheckClose(View view) {
        //return to Main when closed
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    ////////
    //HOLLER INTERNET
    //
    private byte[] downloadData(String urlString) {
        try {

            URL url = new URL(urlString);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();

            InputStream is = httpCon.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead, totalBytesRead = 0;

            byte[] data = new byte[2048];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
                totalBytesRead += nRead;
            }

            bytes = buffer.toByteArray();

            return bytes;

        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }

    }

    private class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPostExecute(Bitmap result) {

            imageChildCheck.setImageBitmap(result);
        }


        @Override
        protected Bitmap doInBackground(String... param) {

            // TODO Auto-generated method stub
            return downloadBitmap(param[0]);
        }


        private Bitmap downloadBitmap(String urlString) {

            bytes = downloadData(urlString);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            return bitmap;
        }
    }
    //
    //
    ////////
}