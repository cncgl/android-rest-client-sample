package cogel.jp.volleysample;


import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private List<Todo> mTodoList = new ArrayList<>();

    private RequestQueue mQueue;

    private boolean mIsTablet = false;
    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Activity-onCreate");

        setContentView(R.layout.activity_main);
        mHandler = new Handler();

        //ダミーデータ作成
        //mTodoList = Todo.addDummyItem();
        mQueue = Volley.newRequestQueue(this);
        loadTasks();

        //タブレットレイアウトなら右側にフォーム画面を表示
        FrameLayout container2 = (FrameLayout) findViewById(R.id.container2);
        if (container2 != null) {
            mIsTablet = true;
            showTodoForm(mTodoList.get(0));
        }
    }

    public void loadTasks() {
        Log.d(TAG, "loadTasks");

        // 接続先
        String url = "http://cogel.jp:4001/api/todos";

        // キューにリクエストを追加
        mTodoList.clear();
        mQueue.add(new JsonObjectRequest(
            Request.Method.GET,
            url,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray todos = response.getJSONArray("todos");
                        //mTodoList = new ArrayList<Todo>();

                        for (int i = 0; i < todos.length(); i++) {
                            JSONObject todo = todos.getJSONObject(i);
                            mTodoList.add(new Todo(
                                    todo.getLong("id"),
                                    todo.getBoolean("status") ? Todo.ColorLabel.PINK : Todo.ColorLabel.GREEN,
                                    todo.getString("title"),
                                    0L
                            ));
                        }
                        showTodoList();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, error.toString());
                }
            }
        ));
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Activity-onBackPressed");
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            //フォーム画面を開いている場合は画面を閉じる
            getSupportFragmentManager().popBackStack();
            // 再取得する
            //loadTasks();
        } else {
            //リスト画面の場合は通常のバックキー処理(アプリを終了)
            super.onBackPressed();
        }
    }

    /**
     * アプリ開始時の処理
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "Activity-onStart");
    }
    /**
     * アプリ再表示時の処理
     */
    @Override
    public void onRestart() {
        super.onRestart();
        Log.d(TAG, "Activity-onRestart");
        loadTasks();
    }
    /**
     * アプリが終了した時の処理
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "Activity-onStop");
        mQueue.cancelAll(this);
    }

    /**
     * TODOリスト一覧を表示
     */
    public void showTodoList() {
        Log.d(TAG, "showTodoList");
        String tag = TodoListFragment.TAG;
        getSupportFragmentManager().beginTransaction().replace(R.id.container,
                TodoListFragment.newInstance(), tag).commit();
    }

    /**
     * TODOフォーム画面を表示
     *
     * @param item TODOリストデータ
     */
    public void showTodoForm(Todo item) {
        String tag = TodoFormFragment.TAG;
        TodoFormFragment fragment;
        if (item == null) {
            // 新規作成
            fragment = TodoFormFragment.newInstance();
        } else {
            // 編集
            fragment = TodoFormFragment.newInstance(item.getId(), item.getColorLabel(),
                    item.getValue(), item.getCreatedTime());
        }
        if (!mIsTablet) {
            //スマートフォンレイアウトの場合はcontainerに表示
            getSupportFragmentManager().beginTransaction().replace(R.id.container,
                    fragment, tag).addToBackStack(tag).commit();
        }else{
            //タブレットレイアウトの場合はcontainer2に表示
            getSupportFragmentManager().beginTransaction().replace(R.id.container2,
                    fragment, tag).addToBackStack(tag).commit();
        }
    }

    public List<Todo> getTodoList() {
        //return mTodoList == null ? new ArrayList<Todo>() : mTodoList;
        return mTodoList;
    }
    /**
     * タブレットか判定.
     * @return タブレトットのとき真
     */
    public boolean isTablet() {
        return mIsTablet;
    }
}
