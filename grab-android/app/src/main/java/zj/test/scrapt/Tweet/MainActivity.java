package zj.test.scrapt.Tweet;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import zj.test.scrapt.Odds.OddsActivity;
import zj.test.scrapt.R;
import zj.test.scrapt.Stock.StockActivity;
import zj.test.scrapt.Wifi.WifiControl;
import zj.zfenlly.gua.LoadInjectLib;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_ACCESS_EXTERNAL_STORAGE = 12334;

    static {
        System.loadLibrary("native-lib");
    }

    TextView tv = null;
    EditText uidedt = null;
    EditText pwdedt = null;
    Button login_btn = null;
    Button uc_btn = null;
    Button stock_btn = null;
    Button ball_btn = null;
    Button record_btn = null;
    Button odds_btn = null;
    String uid;
    String pwd;
    Runnable ballRunnable = new Runnable() {
        @Override
        public void run() {
            String s = "         ";
            EventBus.getDefault().post(new MainEvent(s, true));
            EventBus.getDefault().post(new MainEvent("" + random(6, 1, 34), false));
        }
    };
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String s = "";
            EventBus.getDefault().post(new MainEvent(s, true));
            if (checkForReady()) {
                GrabTweet a = new GrabTweet(MainActivity.this);
                s = a.login(uid, pwd);
                EventBus.getDefault().post(new MainEvent(s, false));
                s = a.getUserInfo("3373931552");
                EventBus.getDefault().post(new MainEvent(s, false));
                s = a.getUserInfo("1772392290");
                EventBus.getDefault().post(new MainEvent(s, false));
                s = a.getUserInfo("1789247505");
                EventBus.getDefault().post(new MainEvent(s, false));
                s = a.getUserInfo("1726640331");
                EventBus.getDefault().post(new MainEvent(s, false));
            } else {
                s = "NetWork is disconnect!!!";
                EventBus.getDefault().post(new MainEvent(s, false));
            }
        }
    };

    public String random(int num, int minValue, int maxValue) {
        if (num > maxValue) {
            num = maxValue;
        }
        if (num < 0 || maxValue < 0) {
            throw new RuntimeException("num or maxValue must be greater than zero");
        }
        List<Integer> result = new ArrayList<Integer>(num);
        String a = "";
        String b = "";

        int len = maxValue - minValue;
        int[] tmpArray = new int[len];
        int[] tmp2Array = new int[num];

        for (int i = 0; i < len; i++) {
            tmpArray[i] = i + minValue;
        }
        for (int i = 0; i < num; i++) {
            tmp2Array[i] = 0;
        }

        Random random = new Random();
        b = "" + (random.nextInt(16) + 1);
        for (int i = 0; i < num; i++) {
            int index = random.nextInt(len - i);
            int tmpValue = tmpArray[index];
            tmp2Array[i] = tmpValue;
            result.add(tmpValue);
            int lastIndex = len - i - 1;
            if (index == lastIndex) {
                continue;
            } else {
                tmpArray[index] = tmpArray[lastIndex];
            }
        }

        for (int i = 0; i < num; i++) {
            for (int j = i + 1; j < num; j++) {
                if (tmp2Array[i] > tmp2Array[j]) {
                    int z = tmp2Array[j];
                    tmp2Array[j] = tmp2Array[i];
                    tmp2Array[i] = z;
                }
            }
            a += tmp2Array[i] + " ";
        }

        a += " : " + b;
        return a;
    }

    private String getNumsFromRange(int n, int start, int end) {
        String a = "";
        long[] l = new long[n];
        boolean flag = false;

        for (int i = 0; i < n; i++) {
            int d = (int) (longFromJNI() % (end - start)) + start;
            flag = false;
            for (int j = 0; j < i; j++) {
                if (d == l[j]) {
                    flag = true;
                }
            }
            if (flag) {
                i--;
            } else {
                l[i] = d;
            }
        }
        for (int i = 0; i < n; i++) {
            a += l[i] + " ";
        }
        return a;
    }

    boolean checkForReady() {
        boolean isconnect = WifiControl.isConnect(MainActivity.this);
        boolean ready = false;
        if (isconnect == true) {
            uid = uidedt.getText().toString();
            pwd = pwdedt.getText().toString();
            if ((uid == null) || (uid.equals(""))) {
                Toast.makeText(MainActivity.this, "UID is null !!!", Toast.LENGTH_SHORT).show();
                return false;
            }
            if ((pwd == null) || (pwd.equals(""))) {
                Toast.makeText(MainActivity.this, "passwd is null !!!", Toast.LENGTH_SHORT).show();
                return false;
            }
            ready = true;
        }
        return ready;
    }

    private boolean allowWriteExternal() {
        if (selfPermissionGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ACCESS_EXTERNAL_STORAGE);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ACCESS_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission allowed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean selfPermissionGranted(Context context, String permission) {
        boolean result = true;
        int targetSdkVersion;
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                result = ContextCompat.checkSelfPermission(context, permission)
                        == PackageManager.PERMISSION_GRANTED;
            } else {
                result = PermissionChecker.checkSelfPermission(context, permission)
                        == PermissionChecker.PERMISSION_GRANTED;
            }
        }
        return result;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MainEvent event) {
        if (event.type == MainEvent.EventType.MESSAGE_DIS) {
            if (event.clear) {
                if (tv != null) tv.setText("" + event.message);
            } else {
                if (tv != null) tv.append(event.message);
            }
        } else if (event.type == MainEvent.EventType.RECORD_BTN) {
            if (event.clear) {
                if (record_btn != null) record_btn.setVisibility(View.VISIBLE);
            } else {
                if (record_btn != null) record_btn.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tv = findViewById(R.id.sample_text);

        login_btn = findViewById(R.id.login);
        uc_btn = findViewById(R.id.uc);
        stock_btn = findViewById(R.id.stock);
        ball_btn = findViewById(R.id.genDoubleBall);

        uidedt = findViewById(R.id.uid_et);
        pwdedt = findViewById(R.id.pswd_et);
        record_btn = findViewById(R.id.record);
        odds_btn = findViewById(R.id.odds);

        uidedt.setText(ShareP.getUidFromPref(this));
        pwdedt.setText(ShareP.getPwdFromPref(this));

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(runnable).start();
            }
        });

        ball_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(ballRunnable).start();
            }
        });
        odds_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, OddsActivity.class);
                startActivity(intent);
            }
        });
        record_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                String pkg = "zj.zfenlly.tools";
                String className = "zj.zfenlly.main.MainActivity";
                ComponentName cn = new ComponentName(pkg, className);
                intent.setComponent(cn);
                startActivity(intent);
            }
        });

        uc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String spec = "https://weibo.cn/u/1772392290";
                OpenWebView.open(MainActivity.this, spec);
            }
        });

        stock_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, StockActivity.class);
                startActivity(intent);
            }
        });

        LoadInjectLib.init(getPackageName());
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public native long longFromJNI();

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        tv.setText(stringFromJNI());
        if (allowWriteExternal()) {
            ;//new Thread(runnable).start();
        }
    }
}
