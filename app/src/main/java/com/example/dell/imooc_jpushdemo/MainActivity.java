package com.example.dell.imooc_jpushdemo;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.widget.Button;
import android.widget.Toast;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class MainActivity extends AppCompatActivity {
    //接口地址
    private final String url="http://192.168.0.124:8080/ygs/inf/pushMsg";
    //返回的结果
    private TextView tvResult;
    private Button btSend; //发送推送请求
    private Spinner mSpinner; //选择推送方式
    //推送种类
    private String pushType;
    //spinner的适配器
    private ArrayAdapter<String> adapter;
    //设置alias的按钮
    private Button btSetAlias;
    //显示用户设置的alias
    private TextView tvAlias;
    private String alias;

    //更新UI
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            StringBuffer sb = (StringBuffer) msg.obj;
            tvResult.setText(sb.toString());
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSpinner= (Spinner) findViewById(R.id.push_type);
        tvResult= (TextView) findViewById(R.id.tv_result);
        btSend= (Button) findViewById(R.id.bt_send);
        btSetAlias= (Button) findViewById(R.id.bt_set);
        tvAlias= (TextView) findViewById(R.id.tv_alias);
        String[] strings=getResources().getStringArray(R.array.push_type);
        List<String> list=new ArrayList<>();

        for(String s:strings){
            list.add(s);
        }

        adapter=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item,list);
        //第三步：为适配器设置下拉列表下拉时的菜单样式。
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //第四步：将适配器添加到下拉列表上
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pushType=adapter.getItem(i);
                    /* 将mySpinner 显示*/
                adapterView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                adapterView.setVisibility(View.VISIBLE);
            }
        });

        //设置alias
        btSetAlias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alias=((EditText)findViewById(R.id.et_set_alias)).getText().toString();
                //调用SDK接口
                JPushInterface.setAlias(getBaseContext(),alias, new TagAliasCallback() {
                    @Override
                    public void gotResult(int i, String s, Set<String> set) {
                        tvAlias.setText("当前alias："+alias);
                        Toast.makeText(MainActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title=((EditText)findViewById(R.id.et_title)).getText().toString();
                String alert=((EditText)findViewById(R.id.et_alert)).getText().toString();
                String alias=((EditText)findViewById(R.id.et_alias)).getText().toString();

                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("alias",alias);
                    jsonObject.put("alert",alert);
                    jsonObject.put("title",title);

                    sendRequest(jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void sendRequest(JSONObject jsonObject) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String title=((EditText)findViewById(R.id.et_title)).getText().toString();
                String alert=((EditText)findViewById(R.id.et_alert)).getText().toString();
                String alias=((EditText)findViewById(R.id.et_alias)).getText().toString();

                try {

                    // 传递的数据
                    String data = "alert=" + URLEncoder.encode(alert, "UTF-8")
                            + "&title=" + URLEncoder.encode(title, "UTF-8")
                            +"&alias="+URLEncoder.encode(alias,"UTF-8")
                            +"&push_type="+URLEncoder.encode(pushType,"UTF-8");


                    URL httpUrl = new URL(url);
                    HttpURLConnection urlConnection = (HttpURLConnection) httpUrl.openConnection();
                    urlConnection.setRequestMethod("POST");

                    // 设置请求的超时时间
                    urlConnection.setReadTimeout(5000);
                    urlConnection.setConnectTimeout(5000);

                    //调用conn.setDoOutput()方法以显式开启请求体
                    urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
                    urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入

                    //setDoInput的默认值就是true
                    OutputStream ost = urlConnection.getOutputStream();

                    PrintWriter pw = new PrintWriter(ost);
                    pw.print(data);
                    pw.flush();
                    pw.close();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuffer sb = new StringBuffer();
                    String s;
                    while ((s = bufferedReader.readLine()) != null) {
                        sb.append(s);
                    }

                    Message msg = new Message();
                    msg.obj = sb;

                    handler.sendMessage(msg);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
