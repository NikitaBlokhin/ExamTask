package lab.isu.raklit.examtask2;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


class LoginTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... args) {
        String result = null;
        String addr = "http://194.176.114.21:8050";
        try {
            URL url = new URL(addr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            String login = args[0];
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("action", "register");
            jsonObject.put("nickname", login);
            wr.writeBytes(jsonObject.toString());
            wr.flush();
            wr.close();
            Scanner reader = new Scanner(con.getInputStream(), "UTF-8");
            StringBuilder builder = new StringBuilder();
            while (reader.hasNext()) {
                builder.append(reader.nextLine());
            }
            result = builder.toString();
            reader.close();
        } catch (IOException e) {
            result = null;
            e.printStackTrace();
        } catch (JSONException e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }
}
