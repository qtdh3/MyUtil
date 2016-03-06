package com.example.alan.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ryg.dynamicload.internal.DLIntent;
import com.ryg.dynamicload.internal.DLPluginManager;
import com.ryg.utils.DLUtils;

import java.io.File;




public class MainActivity extends Activity implements View.OnClickListener {
    PluginItem item = new PluginItem();
    private String Tag=MainActivity.class.getName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        loadAPK();
//        Button btn= (Button)findViewById(R.id.btn);
//        btn.setOnClickListener(this);

        Button enterPassword= (Button) findViewById(R.id.enter_password);
        enterPassword.setOnClickListener(this);
        EditText et= (EditText) findViewById(R.id.et_common);
        Log.i(Tag,"addTextChangedListener");
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(Tag + "onTextChanged", "CharSequence=" + s + "\nstart" + start + ";count" + count + ";before" + before);
                System.out.println("onTextChanged::"+s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.i(Tag,"onKey");
                return false;
            }
        });
    }

    private void loadAPK() {
        String pluginFolder = Environment.getExternalStorageDirectory() + "/DynamicLoadHost";
        Log.i(Tag, "pluginFolder-->" + pluginFolder);
        File file = new File(pluginFolder);
        File[] plugins = file.listFiles();
        Log.i(Tag, "plugins--Length:" + plugins.length);

        File plugin=plugins[0];
        Log.i("Tag", "File:" + plugin.getName());
        if (plugin!=null){
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
        switch (v.getId()){
            case R.id.btn:
                DLPluginManager pluginManager = DLPluginManager.getInstance(MainActivity.this);
                pluginManager.startPluginActivity(MainActivity.this, new DLIntent(item.packageInfo.packageName, item.launcherActivityName));
                break;
            case R.id.enter_password:
                showPasswordDialog();
                break;
            default:
        }
    }

    private void showPasswordDialog() {
        Log.i(Tag, "before dialog init");
        TestPassWordDialog testPassWordDialog=new TestPassWordDialog(this,"69",new TestPassWordDialog.OnOkButtonClickedListener(){
            @Override
            public void onClicked(DialogInterface dialog, String pwd) {
                if (pwd.length()==6){
                    Log.i(Tag,"pwd="+pwd);
                    dialog.dismiss();
                }
            }
        });
        testPassWordDialog.createAndShow();
        Log.i(Tag,"after_showPass");
    }

    public static class PluginItem {
        public PackageInfo packageInfo;
        public String pluginPath;
        public String launcherActivityName;
        public String launcherServiceName;

        public PluginItem() {
        }
    }

}
