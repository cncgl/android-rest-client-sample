package cogel.jp.volleysample;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by shigeru on 15/09/11.
 */
public class TodoFormFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = TodoFormFragment.class.getSimpleName();

    private static final int MENU_ADD = 1;
    private static final int MENU_SET = 2;

    public static final String ARGS_ID = "key-id";

    public static final String ARGS_COLORLABEL = "key-colorlabel";

    public static final String ARGS_VALUE = "key-value";

    public static final String ARGS_CREATEDTIME = "key-createdtime";

    private int mColorLabel = Todo.ColorLabel.NONE;

    private long mCreatedTime = 0;

    private EditText mEtInput;

    private boolean mIsTextEdited = false;

    private RequestQueue mQueue;

    private MenuItem mMenuAdd;
    private MenuItem mMenuSet;

    // シングルトンファクトリー
    //------------------------------
    public static TodoFormFragment newInstance() {
        return new TodoFormFragment();
    }

    // Fragment のコンストラクタには引数を渡せない制限があるので、static メソッドで生成
    public static TodoFormFragment newInstance(long id, int colorLabel, String value, long createdTime) {
        TodoFormFragment fragment = new TodoFormFragment();
        Bundle args = new Bundle();
        args.putLong(ARGS_ID, id);
        args.putInt(ARGS_COLORLABEL, colorLabel);
        args.putString(ARGS_VALUE, value);
        args.putLong(ARGS_CREATEDTIME, createdTime);
        fragment.setArguments(args);
        return fragment;
    }

    // オーバーライド
    //-----------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MenuItemの追加を許可
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_todo, container, false);

        //カラーラベルのインスタンスを取得
        //rootView.findViewById(R.id.color_none).setOnClickListener(this);
        //rootView.findViewById(R.id.color_amber).setOnClickListener(this);
        rootView.findViewById(R.id.color_green).setOnClickListener(this);
        //rootView.findViewById(R.id.color_indigo).setOnClickListener(this);
        rootView.findViewById(R.id.color_pink).setOnClickListener(this);

        //入力フォームのインスタンスを取得
        mEtInput = (EditText) rootView.findViewById(R.id.input);
        mEtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //テキストの中身が変更されたら編集したと判定
                mIsTextEdited = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //編集データを受け取っていたらセット
        Bundle args = getArguments();
        if (args != null) {
            //カラーラベルをセット
            mColorLabel = args.getInt(ARGS_COLORLABEL, Todo.ColorLabel.NONE);
            mEtInput.setTextColor(getColorResource(mColorLabel));

            //値をセット
            String value = args.getString(ARGS_VALUE);
            mEtInput.setText(value);

            //作成時間をセット
            mCreatedTime = args.getLong(ARGS_CREATEDTIME, 0);
        }
        return rootView;
    }

    /**
     * メニューを設定する
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem menuItem = menu.findItem(MENU_ADD);
        if (menuItem == null) {
            mMenuAdd = menu.add(Menu.NONE, MENU_ADD, Menu.NONE, getString(R.string.add));
            mMenuSet = menu.add(Menu.NONE, MENU_SET, Menu.NONE, getString(R.string.set));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mMenuAdd.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                mMenuSet.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }
            Bundle b = getArguments();
            if( b == null ) {
                mMenuAdd.setVisible(true);
                mMenuSet.setVisible(false);
            } else {
                mMenuAdd.setVisible(false);
                mMenuSet.setVisible(true);
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * メニューが選択された時に処理をする
     * @param item
     * @return 正常に処理がされた時真
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_ADD || item.getItemId() == MENU_SET) {
            //TODOリストを追加
            final String value = mEtInput.getText().toString();
            if (!TextUtils.isEmpty(value) && mIsTextEdited) {
                /*
                 * Ｉｎｔｅｎｔ で渡す必要はないので追加の　ＡＰＩ　を呼び出す。
                Intent resultData = new Intent();
                resultData.putExtra(ARGS_COLORLABEL, mColorLabel);
                resultData.putExtra(ARGS_VALUE, value);
                if (mCreatedTime == 0) {
                    //作成時間がない場合は新規データとして作成時間を生成
                    resultData.putExtra(ARGS_CREATEDTIME, System.currentTimeMillis());
                } else {
                    //作成時間がある場合は既存のデータを更新
                    resultData.putExtra(ARGS_CREATEDTIME, mCreatedTime);
                }

                //Broadcastを送信
                resultData.setAction(TodoListFragment.ACTION_CREATE_TODO);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(resultData);
                */
                String url = "http://cogel.jp:4001/api/todos";
                if( item.getItemId() == MENU_SET ) {
                    url += "/" + getArguments().getLong(ARGS_ID);
                }
                mQueue = Volley.newRequestQueue(getActivity());
                int method = item.getItemId() == MENU_ADD ? Request.Method.POST : Request.Method.PUT;
                StringRequest sr = new StringRequest(
                        method,
                        url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, response);
                                //MainActivity activity = (MainActivity)getActivity();
                                //activity.loadTasks();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, error.toString());
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String>params = new HashMap<String, String>();
                        params.put("todo[status]", "true");
                        params.put("todo[title]", value);
                        return params;
                    }
                };
                mQueue.add(sr);

                boolean isTablet = ((MainActivity) getActivity()).isTablet();
                if (!isTablet) {
                    //スマートフォンレイアウトの場合はリスト画面に戻る
                    getFragmentManager().popBackStack();
                    final MainActivity activity = (MainActivity)getActivity();
                    activity.mHandler.post(new Runnable() {
                        public void run() {
                            activity.loadTasks();
                        }
                    });
                } else {
                    //タブレットレイアウトで新規TODOを作成した場合はテキストをクリア
                    if (getArguments() == null) {
                        mEtInput.getText().clear();
                    }
                }

                //ソフトウェアキーボードを閉じる
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(mEtInput.getWindowToken(), 0);

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * View でクリックされた時の処理
     * @param v
     */
    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        /*
        if (viewId == R.id.color_none) {
            mColorLabel = Todo.ColorLabel.NONE;
        } else if (viewId == R.id.color_amber) {
            mColorLabel = Todo.ColorLabel.AMBER;
        } else if (viewId == R.id.color_pink) {
            mColorLabel = Todo.ColorLabel.PINK;
        } else if (viewId == R.id.color_indigo) {
            mColorLabel = Todo.ColorLabel.INDIGO;
        } else if (viewId == R.id.color_green) {
            mColorLabel = Todo.ColorLabel.GREEN;
        }
        */
        if (viewId == R.id.color_green) {
            mColorLabel = Todo.ColorLabel.GREEN;
        } else {
            mColorLabel = Todo.ColorLabel.PINK;
        }
        mEtInput.setTextColor(getColorResource(mColorLabel));
    }

    /**
     * カラーラベルに応じたカラーリソースを返却.
     *
     * @param color : カラー
     */
    private int getColorResource(int color) {
        int resId = Todo.ColorLabel.NONE;
        if (color == Todo.ColorLabel.NONE) {
            resId = getResources().getColor(R.color.material_grey_500);
        } else if (color == Todo.ColorLabel.AMBER) {
            resId = getResources().getColor(R.color.material_amber_500);
        } else if (color == Todo.ColorLabel.PINK) {
            resId = getResources().getColor(R.color.material_pink_500);
        } else if (color == Todo.ColorLabel.INDIGO) {
            resId = getResources().getColor(R.color.material_indigo_500);
        } else if (color == Todo.ColorLabel.GREEN) {
            resId = getResources().getColor(R.color.material_green_500);
        }
        return resId;
    }

}
