package com.nifcloud.ncmbandroid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.nifcloud.mbaas.core.DoneCallback;
import com.nifcloud.mbaas.core.ExecuteScriptCallback;
import com.nifcloud.mbaas.core.FetchFileCallback;
import com.nifcloud.mbaas.core.FindCallback;
import com.nifcloud.mbaas.core.NCMB;
import com.nifcloud.mbaas.core.NCMBAcl;
import com.nifcloud.mbaas.core.NCMBException;
import com.nifcloud.mbaas.core.NCMBFile;
import com.nifcloud.mbaas.core.NCMBInstallation;
import com.nifcloud.mbaas.core.NCMBObject;
import com.nifcloud.mbaas.core.NCMBPush;
import com.nifcloud.mbaas.core.NCMBQuery;
import com.nifcloud.mbaas.core.NCMBScript;
import com.nifcloud.mbaas.core.NCMBUser;
import com.nifcloud.mbaas.core.TokenCallback;
import com.nifcloud.ncmbandroid.ui.login.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static String APP_KEY = "APPLICATION_KEY";
    private static String CLIENT_KEY = "CLIENT_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        NCMB.initialize(this, APP_KEY, CLIENT_KEY);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        if (NCMBUser.getCurrentUser().getObjectId() == null) {
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(myIntent);
        }

        Map<String, String> installationCustomFields = new LinkedHashMap<>();
        installationCustomFields.put("OS", Build.VERSION.RELEASE);
        NCMB.initialize(this,APP_KEY,CLIENT_KEY, installationCustomFields);

        findViewById(R.id.saveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveObject();
            }
        });

        findViewById(R.id.getBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getObject();
            }
        });

        findViewById(R.id.saveFileBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFile();
            }
        });

        findViewById(R.id.getFileBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFile();
            }
        });

        findViewById(R.id.scriptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runScript();
            }
        });

        findViewById(R.id.installationBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NCMBInstallation.getCurrentInstallation().getDeviceTokenInBackground(new TokenCallback() {
                    @Override
                    public void done(String s, NCMBException e) {
                        if ( e == null) {
                            alert("Info", " InstallationID: " + s);
                        }
                    }
                });
            }
        });

        findViewById(R.id.pushBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPush();
            }
        });

        findViewById(R.id.pushDialogBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDialogPush();
            }
        });

        findViewById(R.id.richPushBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRichPush();
            }
        });

        findViewById(R.id.logOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        NCMBPush.richPushHandler(this, getIntent());
        NCMBPush.trackAppOpened(getIntent());
        //リッチプッシュを再表示させたくない場合はintentからURLを削除します
        getIntent().removeExtra("com.nifty.RichUrl");


    }

    public void saveObject(){
        NCMBObject obj = new NCMBObject("AndroidTest");
        try {
            obj.put("Time", new Date().toString());
            obj.saveInBackground(new DoneCallback() {
                @Override
                public void done(NCMBException e) {
                    alert("Info", "Save object " + ( e == null ? "successfully" : "failure"));
                }
            });
        } catch (NCMBException e) {
            e.printStackTrace();
        }
    }

    public void getObject(){
        //TestClassを検索するためのNCMBQueryインスタンスを作成
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("AndroidTest");
        query.findInBackground(new FindCallback<NCMBObject>() {
            @Override
            public void done(List<NCMBObject> results, NCMBException e) {
                if (e == null) {
                    for (NCMBObject obj : results) {
                        Log.d("Info", "Object id:" + obj.getObjectId());

                    }
                    alert("Get objects", "Total: " + results.size());
                }
            }
        });
    }

    private void alert(String title, String messsage){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(messsage)
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

    }
    String dateString = null;
    public void saveFile(){
        final Date date = new Date();
        dateString = DateFormat.format("yyyy-MM-dd_HH-mm", date).toString();
        byte[] data = dateString.getBytes();
        NCMBFile file = null;
        try {
            file = new NCMBFile(dateString + ".txt", data, new NCMBAcl());
            file.saveInBackground(new DoneCallback() {
                @Override
                public void done(NCMBException e) {
                    alert("Info", "Save file " + ( e == null ? "successfully" : "failure"));
                }
            });
        } catch (NCMBException e) {
            e.printStackTrace();
        }
    }

    public void getFile(){
        NCMBFile file2 = null;
        try {
            file2 = new NCMBFile(dateString + ".txt");
            file2.fetchInBackground(new FetchFileCallback() {
                @Override
                public void done(byte[] data, NCMBException e) {
                    if (e == null) {
                        alert("Info", "File content: " + new String(data));
                    } else {
                        alert("Error", "Get file failure");
                    }
                }
            });
        } catch (NCMBException ex) {
            ex.printStackTrace();
        }
    }

    public void runScript() {
        NCMBScript script = new NCMBScript("testScript_GET.js", NCMBScript.MethodType.GET);
        JSONObject query = null;
        script.executeInBackground(null, null, null, new ExecuteScriptCallback() {
            @Override
            public void done(byte[] result, NCMBException e) {
                if (e == null) {
                    String data = new String(result);
                    alert("Info", "Result: " + data);
                }
            }
        });
    }

    public static void updateInstallation(final NCMBInstallation installation) {

        installation.getDeviceTokenInBackground(new TokenCallback() {
            @Override
            public void done(String s, NCMBException e) {
                //installationクラスを検索するクエリの作成
                NCMBQuery<NCMBInstallation> query = NCMBInstallation.getQuery();

                //同じRegistration IDをdeviceTokenフィールドに持つ端末情報を検索する
                query.whereEqualTo("deviceToken", s);

                //データストアの検索を実行
                query.findInBackground(new FindCallback<NCMBInstallation>() {
                    @Override
                    public void done(List<NCMBInstallation> results, NCMBException e) {

                        //検索された端末情報のobjectIdを設定
                        try {
                            installation.setObjectId(results.get(0).getObjectId());
                            installation.put("status", new Date().toString());
                        } catch (NCMBException e1) {
                            e1.printStackTrace();
                        }

                        //端末情報を更新する
                        installation.saveInBackground();
                    }
                });
            }
        });

    }

    void getInstallation(){

        final NCMBInstallation installation = NCMBInstallation.getCurrentInstallation();
        //GCMからRegistrationIdを取得しinstallationに設定する
        installation.getDeviceTokenInBackground(new TokenCallback() {
            @Override
            public void done(String s, NCMBException e) {
                if (e == null) {
                    installation.saveInBackground(new DoneCallback() {
                        @Override
                        public void done(NCMBException e) {
                            if (e == null) {
                                //保存成功
                            } else if (NCMBException.DUPLICATE_VALUE.equals(e.getCode())) {
                                //保存失敗 : registrationID重複
                                updateInstallation(installation);
                            } else {
                                //保存失敗 : その他
                            }
                        }
                    });
                } else {
                    Log.d("APP", "GET TOKEN ERROR");
                }
            }
        });
    }

    void sendPush(){

        final Date date = new Date();
        String time = DateFormat.format("yyyy/MM/dd HH:mm", date).toString();

        NCMBPush push = new NCMBPush();
        try {
            push.setTarget(new JSONArray("[android]"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        push.setMessage("Normal push " + time);
        push.sendInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                alert("Info", "Send push " + (e == null? "Successful": "Failure"));
            }
        });
    }

    void sendRichPush(){

        final Date date = new Date();
        String time = DateFormat.format("yyyy/MM/dd HH:mm", date).toString();

        NCMBPush push = new NCMBPush();
        push.setRichUrl("https://google.com");
        try {
            push.setTarget(new JSONArray("[android]"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        push.setMessage("Rich push " + time);
        push.sendInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                alert("Info", "Send push " + (e == null? "Successful": "Failure"));
            }
        });
    }

    void sendDialogPush(){

        final Date date = new Date();
        String time = DateFormat.format("yyyy/MM/dd HH:mm", date).toString();

        NCMBPush push = new NCMBPush();
        push.setDialog(true);
        try {
            push.setTarget(new JSONArray("[android]"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        push.setMessage("Dialog push " + time);
        push.sendInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                alert("Info", "Send push " + (e == null? "Successful": "Failure"));
            }
        });
    }

    public void logOut() {
        NCMBUser.logoutInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(myIntent);
            }
        });
    }
}