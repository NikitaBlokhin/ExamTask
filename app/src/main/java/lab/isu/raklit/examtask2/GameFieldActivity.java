package lab.isu.raklit.examtask2;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class GameFieldActivity extends AppCompatActivity {

    public static  List<Card> cards = null;
    public static List<Integer> current_set = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_field);
        current_set = new ArrayList<Integer>();
        build_game_field_data();
        bind_adapter();
    }

    protected void bind_adapter() {
        ListView gameView = findViewById(R.id.game_view);
        ListAdapter adapter = new ArrayAdapter<Card>(this, android.R.layout.simple_list_item_1, cards);
        gameView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                click_on_item(view, position, id);
            }
        });
        gameView.setAdapter(adapter);
    }

    protected void build_game_field_data() {
        cards = new ArrayList<Card>();
        try {
            String answer = fetch_cards_answer();
            if (answer != null) {
                JSONObject jsonObject = new JSONObject(answer);
                if (jsonObject.getString("status").equals("ok")) {
                    JSONArray cardsJSON = jsonObject.getJSONArray("cards");
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    Card card;
                    JSONObject cardJSON;
                    int n = cardsJSON.length();
                    for (int i = 0; i < n; i++) {
                        cardJSON = cardsJSON.getJSONObject(i);
                        card = gson.fromJson(cardJSON.toString(), Card.class);
                        cards.add(card);
                    }
                }
            }
        } catch (JSONException e) {
            cards = new ArrayList<Card>();
            e.printStackTrace();
        }
    }

    protected String fetch_cards_answer() {
        String result;
        try {
            SharedPreferences sp = getSharedPreferences("TOKEN_STORAGE", MODE_PRIVATE);
            String[] args = new String[]{String.valueOf(sp.getInt("token", -1))};
            AsyncTask<String, Void, String> fetchCardsTask = new FetchCardsTask();
            fetchCardsTask.execute(args);
            result = fetchCardsTask.get();
        } catch (ExecutionException e) {
            result = null;
            e.printStackTrace();
        } catch (InterruptedException e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }

    protected  boolean compute_condition() {
        ListView gameView = findViewById(R.id.game_view);
        Card card;
        boolean result;
        boolean p1e, p2e, p3e, p4e;
        boolean p1d, p2d, p3d, p4d;
        p1e = true;
        p2e = true;
        p3e = true;
        p4e = true;
        int p1_first, p2_first, p3_first, p4_first;
        p1_first = cards.get(0).count;
        p2_first = cards.get(0).color;
        p3_first = cards.get(0).fill;
        p4_first = cards.get(0).shape;
        List<Integer> p1_unique = new ArrayList<Integer>();
        List<Integer> p2_unique = new ArrayList<Integer>();
        List<Integer> p3_unique = new ArrayList<Integer>();
        List<Integer> p4_unique = new ArrayList<Integer>();
        for (int i = 0; i < 3; i++) {
            card = cards.get(i);
            if (card.count != p1_first) p1e = false;
            if (card.color != p2_first) p2e = false;
            if (card.fill != p3_first) p3e = false;
            if (card.shape != p4_first) p4e = false;
            if (!p1_unique.contains(card.count)) p1_unique.add(card.count);
            if (!p2_unique.contains(card.color)) p2_unique.add(card.color);
            if (!p3_unique.contains(card.fill)) p3_unique.add(card.fill);
            if (!p4_unique.contains(card.shape)) p4_unique.add(card.shape);
        }
        p1d = (p1_unique.size() == 3);
        p2d = (p2_unique.size() == 3);
        p3d = (p3_unique.size() == 3);
        p4d = (p4_unique.size() == 3);

        result = ((p1e || p1d) && (p2e || p2d) && (p3e || p3d) && (p4e || p4d));
        return result;
    }

    protected void check_condition() {
        ListView gameView = findViewById(R.id.game_view);
        View item;
        boolean status = compute_condition();
        int n = gameView.getChildCount();
        if (status) {
            for (int i = 0; i < n; i++) {
                item = gameView.getChildAt(i);
                if (current_set.contains((int) gameView.getItemIdAtPosition(i))) {
                    item.setBackgroundColor(Color.GREEN);
                }
            }
        } else {
                for (int i = 0; i < n; i++) {
                    item = gameView.getChildAt(i);
                    if (current_set.contains((int) gameView.getItemIdAtPosition(i))) {
                        item.setBackgroundColor(Color.RED);
                    }
                }
        }
    }

    protected void reset_set() {
        ListView gameView = findViewById(R.id.game_view);
        View item;
        int n = gameView.getChildCount();
        for (int i = 0; i < n; i++) {
            item = gameView.getChildAt(i);
            item.setBackgroundColor(Color.WHITE);
        }
        current_set.clear();
    }

    public void click_on_item(View v, int pos, long id) {
        ListView gameView = findViewById(R.id.game_view);
        int abs_id = (int)gameView.getItemIdAtPosition(pos);
        if (!current_set.contains(abs_id)) {
            current_set.add(abs_id);
            v.setBackgroundColor(Color.BLUE);
        }
        if (current_set.size() == 3) check_condition();
        else if (current_set.size() > 3) reset_set();
    }
}
