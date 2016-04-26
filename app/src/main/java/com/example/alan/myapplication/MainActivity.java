package com.example.alan.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.ryg.dynamicload.internal.DLIntent;
import com.ryg.dynamicload.internal.DLPluginManager;
import com.ryg.utils.DLUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;


public class MainActivity extends Activity implements View.OnClickListener {
    PluginItem item = new PluginItem();
    private String Tag = MainActivity.class.getName();
    /**
     * 用于显示返回的json 信息的四个TextView
     */
    private TextView lonInfo;
    private TextView levelInfo;
    private TextView alevelInfo;
    private TextView latInfo;

    /**
     * 用于输入城市名的，用于下一步的位置信息查询
     *
     * */
    EditText inputCityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        loadAPK();
//        Button btn= (Button)findViewById(R.id.btn_start_plugin);
//        btn.setOnClickListener(this);

        initView();
    }

    private void initView() {
        Button enterPassword = (Button) findViewById(R.id.enter_password);
        enterPassword.setOnClickListener(this);
        TextView tv_sendHttpRequest = (TextView) findViewById(R.id.tv_send_http_request);
        tv_sendHttpRequest.setOnClickListener(this);
        TextView tv_sendHttps= (TextView) findViewById(R.id.tv_send_https_request);
        tv_sendHttps.setOnClickListener(this);

        lonInfo = (TextView) findViewById(R.id.tv_lon);
        levelInfo = (TextView) findViewById(R.id.tv_level);
        alevelInfo = (TextView) findViewById(R.id.tv_alevel);
        latInfo= (TextView) findViewById(R.id.tv_lat);
        inputCityName= (EditText) findViewById(R.id.et_city_name);
        Button btn_show_message_dialog= (Button) findViewById(R.id.btn_show_message_dialog);
        btn_show_message_dialog.setOnClickListener(this);
        findViewById(R.id.tv_jumpto_leak_test).setOnClickListener(this);

        findViewById(R.id.tv_jump_to_loadMore_test).setOnClickListener(this);
    }

    private void loadAPK() {
        String pluginFolder = Environment.getExternalStorageDirectory() + "/DynamicLoadHost";
        Log.i(Tag, "pluginFolder-->" + pluginFolder);
        File file = new File(pluginFolder);
        File[] plugins = file.listFiles();
        Log.i(Tag, "plugins--Length:" + plugins.length);

        File plugin = plugins[0];
        Log.i("Tag", "File:" + plugin.getName());
        if (plugin != null) {
            item.pluginPath = plugin.getAbsolutePath();
            item.packageInfo = DLUtils.getPackageInfo(this, item.pluginPath);
            if (item.packageInfo.activities != null && item.packageInfo.activities.length > 0) {
                item.launcherActivityName = item.packageInfo.activities[0].name;
            }
            if (item.packageInfo.services != null && item.packageInfo.services.length > 0) {
                item.launcherServiceName = item.packageInfo.services[0].name;
            }
            DLPluginManager.getInstance(this).loadApk(item.pluginPath);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_plugin:
                DLPluginManager pluginManager = DLPluginManager.getInstance(MainActivity.this);
                pluginManager.startPluginActivity(MainActivity.this, new DLIntent(item.packageInfo.packageName, item.launcherActivityName));
                break;
            case R.id.enter_password:
                showPasswordDialog();
                break;
            case R.id.tv_send_http_request:
                String cityName=inputCityName.getText().toString();
                if (cityName!=null&&cityName.length()>0){
                    createHttpRequest(cityName);
                }

//                TestJson testJson=new TestJson(this);
//                testJson.sendRequest();
                break;
            case R.id.tv_send_https_request:
                HttpsRequest();
                break;
            case R.id.btn_show_message_dialog:
                showDialog("测试");
                break;
            case R.id.tv_jumpto_leak_test:
                startActivity(new Intent(this,LeakTestActivity.class));
                break;
            case R.id.tv_jump_to_loadMore_test:
                startActivity(new Intent(this,LoadMoreExpandableListActivity.class));
                break;
            default:
        }
    }

    private void HttpsRequest() {
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
//        if (cityName==null||cityName.equals(""))
//            cityName="苏州市";
        final Request request = new Request(Request.Method.GET, "https://cross-borderpay.bjpos.com:8997/IFP/", new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(Tag, "volleyError" + error);
            }
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String parsed;
                try {
                    parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                } catch (UnsupportedEncodingException var4) {
                    parsed = new String(response.data);
                }
                return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
            }

            @Override
            protected void deliverResponse(Object response) {
                //{"lon":120.58531,"level":2,"address":"","cityName":"","alevel":4,"lat":31.29888}
                Log.i(Tag, "deliverResponse=>" + response);
            }

            @Override
            public int compareTo(Object another) {
                return 0;
            }
        };
        requestQueue.add(request);
//        requestQueue.start();
    }

    private void showDialog(String showMessage) {
        Dialog dialog=new Dialog(this,R.style.add_dialog_normal);
        View layoutView=dialog.getLayoutInflater().inflate(R.layout.ll_dialog_show_message, null, false);
        dialog.setContentView(layoutView);
        dialog.setCancelable(true);
        TextView textView= (TextView) layoutView.findViewById(R.id.textView);
        textView.setText(showMessage);
        dialog.show();
    }

    private void createHttpRequest(String cityName) {
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        if (cityName==null||cityName.equals(""))
            cityName="苏州市";
        final Request request = new Request(Request.Method.GET, "http://gc.ditu.aliyun.com/geocoding" + "?" +
                "a="+cityName, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(Tag, "volleyError" + error);
            }
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String parsed;
                try {
                    parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                } catch (UnsupportedEncodingException var4) {
                    parsed = new String(response.data);
                }
                return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
            }

            @Override
            protected void deliverResponse(Object response) {
                //{"lon":120.58531,"level":2,"address":"","cityName":"","alevel":4,"lat":31.29888}
                Log.i(Tag, "deliverResponse=>" + response);
                try {
                    JSONObject jsonObject = new JSONObject((String) response);
                    final MapData mapData = new MapData();
                    mapData.setAlevel(jsonObject.getInt("alevel"));
                    mapData.setAddress(jsonObject.getString("address"));
                    mapData.setCityname(jsonObject.getString("cityName"));
                    mapData.setLat(jsonObject.getString("lat"));
                    mapData.setLon(jsonObject.getString("lon"));
                    mapData.setLevel(jsonObject.getInt("level"));
                    Runnable showText=new Runnable() {
                        @Override
                        public void run() {
                            lonInfo.setText("lon="+mapData.getLon());
                            levelInfo.setText("level="+String.valueOf(mapData.getLevel()));
                            alevelInfo.setText("alevel="+String.valueOf(mapData.getAlevel()));
                            latInfo.setText("lat="+mapData.getLat());
                        }
                    };
                    runOnUiThread(showText);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public int compareTo(Object another) {
                return 0;
            }
        };
        requestQueue.add(request);
        requestQueue.start();
    }

    private void showPasswordDialog() {
        Log.i(Tag, "before dialog init");
        TestPassWordDialog testPassWordDialog = new TestPassWordDialog(this, "69", new TestPassWordDialog.OnOkButtonClickedListener() {
            @Override
            public void onClicked(DialogInterface dialog, String pwd) {
                if (pwd.length() == 6) {
                    Log.i(Tag, "pwd=" + pwd);
                    Toast.makeText(MainActivity.this, "pwd=" + pwd,Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }
        });
        testPassWordDialog.createAndShow();
        Log.i(Tag, "after_showPass");
    }

    public static class PluginItem {
        public PackageInfo packageInfo;
        public String pluginPath;
        public String launcherActivityName;
        public String launcherServiceName;

        public PluginItem() {
        }
    }

    class MapData {
        private String lon;
        private int level;
        private String address;
        private String cityname;
        private int alevel;
        private String lat;

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCityname() {
            return cityname;
        }

        public void setCityname(String cityname) {
            this.cityname = cityname;
        }

        public int getAlevel() {
            return alevel;
        }

        public void setAlevel(int alevel) {
            this.alevel = alevel;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }
    }

}
