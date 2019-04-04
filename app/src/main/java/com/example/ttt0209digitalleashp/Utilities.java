package com.example.ttt0209digitalleashp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utilities {

    public static String inputStreamToString(InputStream is){
    //converts InputStream to String
        try {
            //read input stream
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead, totalBytesRead = 0;

            byte[] data = new byte[2048];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
                totalBytesRead += nRead;
            }

            byte[] bytes = buffer.toByteArray();

            //put into JSON object
            String jsonStr = new String(bytes);

            return jsonStr;

        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }

    }

}
