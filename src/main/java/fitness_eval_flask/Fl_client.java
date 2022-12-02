package fitness_eval_flask;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import com.google.gson.Gson;

public class Fl_client {

    public double FlaskClient(double[] numbers){
        HttpURLConnection conn = null;
        DataOutputStream os = null;
        boolean success = true;
        double val;

        try{
            URL url = new URL("http://127.0.0.1:5000/add/");//important to add the trailing slash after add

            Gson gson = new Gson();
            String input  = gson.toJson(numbers);

            byte[] postData = input.getBytes(StandardCharsets.UTF_8);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty( "charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(input.length()));
            os = new DataOutputStream(conn.getOutputStream());
            os.write(postData);
            os.flush();

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output = br.readLine();

                val = Double.parseDouble(output);

            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
            val = -100000;

        }catch (IOException e){
            e.printStackTrace();
            val = -100000;
        }finally
        {
            if(conn != null)
            {
                conn.disconnect();
            }
        }
        //System.out.println(val);
        return val;
    }
}
