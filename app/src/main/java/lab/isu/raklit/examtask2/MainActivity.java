package lab.isu.raklit.examtask2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sp = getSharedPreferences("TOKEN_STORAGE",MODE_PRIVATE);
        if (sp.contains("token")) {
            Toast toast = Toast.makeText(
                    this,
                    String.valueOf(sp.getInt("token", -1)),
                    Toast.LENGTH_SHORT
            );
            toast.show();
            go_to_game_field();
        }
    }

    public void enter_click(View view) {
        if (getPreferences(MODE_PRIVATE).contains("token")) go_to_game_field();
        String answer = get_answer();
        boolean status;
        if (answer != null) {
            status = parse_answer(answer);
            if (status) go_to_game_field();
        }
    }

    protected String get_answer() {
        String answer;
        AsyncTask<String, Void, String> loginTask = new LoginTask();
        EditText login_txt = findViewById(R.id.login_txt);
        String[] args = new String[]{login_txt.getText().toString()};
        loginTask.execute(args);
        try {
            answer = loginTask.get();
        } catch (ExecutionException e) {
            answer = null;
            e.printStackTrace();
        } catch (InterruptedException e) {
            answer = null;
            e.printStackTrace();
        }
        return answer;
    }

    protected boolean parse_answer(String answer) {
        boolean status;
        try {
            JSONObject jsonObject = new JSONObject(answer);
            if (jsonObject.getString("status").equals("ok")) {
                status = true;
                int token = jsonObject.getInt("token");
                SharedPreferences sp = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("token", token);
                editor.apply();
            } else {
                status = false;
            }
        } catch (JSONException e) {
            status = false;
            e.printStackTrace();
        }
        return status;
    }

    protected void go_to_game_field() {
        startActivity(new Intent(this, GameFieldActivity.class));
    }
}
