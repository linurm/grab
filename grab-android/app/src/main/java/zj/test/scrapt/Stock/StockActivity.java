package zj.test.scrapt.Stock;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import zj.test.scrapt.R;

import static zj.test.scrapt.Stock.StockEvent.EventType.COLOR_DIS;

/**
 * Created by Administrator on 2017/11/13.
 */

public class StockActivity extends Activity {

    ListView lv = null;
    TextView tvs = null;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            GrabYougu g = GrabYougu.newInstance().setContext(StockActivity.this);
            String s = "";
            EventBus.getDefault().post(new StockEvent(s, true));
            try {
                g.getAttUsers();
                List<UserInfo> sa = g.getListUser();
                for (UserInfo a : sa) {
                    s = g.getUserTrade(a.id + "");
                    if (s.equals("")) {
                        Log.e("ztag", " user not trade recently");
                    } else {
                        g.getAttUserInfo(a.id + "");
                        g.getAttUserInfo2(a.id + "");
                        g.getAttUserInfo3(a.id + "");
                        EventBus.getDefault().post(new StockEvent("==============================================", false));
                        EventBus.getDefault().post(new StockEvent(UserInfoToString(a), COLOR_DIS, false));
                        EventBus.getDefault().post(new StockEvent("------------------------------------------------------------------------------------------", false));
                        EventBus.getDefault().post(new StockEvent(s, false));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                s = e.getMessage();
                EventBus.getDefault().post(new StockEvent(s, true));
            }
            EventBus.getDefault().post(new StockEvent("================== END ======================", false));
        }
    };

    private String UserInfoToString(UserInfo uinfo) {
        String s;
        String RED_COLOR = "<font color=\'#ff0000\'>";
        String BLUE_COLOR = "<font color=\'#0000ff\'>";
        String GREEN_COLOR = "<font color=\'#00ff00\'>";
        String VIOLET_COLOR = "<font color=\'#9966cc\'>";
        String CORAL3_COLOR = "<font color=\'#cd5b45\'>";
        String COLOR_START = RED_COLOR;
        String COLOR_END = "</font>";

//        String wp = uinfo.toPercent(uinfo.wProfit);
//        String tp = uinfo.toPercent(uinfo.tProfit);
//        String mp = uinfo.toPercent(uinfo.mProfit);
        StringBuilder a = new StringBuilder();
        a.append(getResources().getString(R.string.id) + uinfo.id + " : " + uinfo.nickname + "<br>");


        COLOR_START = CORAL3_COLOR;
        a.append(getResources().getString(R.string.t_rank) + COLOR_START + uinfo.gettRank() + COLOR_END + " ");
        if (uinfo.tRise.indexOf("-") == -1) COLOR_START = RED_COLOR;
        else COLOR_START = BLUE_COLOR;
        a.append(getResources().getString(R.string.t_rise) + COLOR_START + uinfo.tRise + COLOR_END + " ");
        if (uinfo.tProfit.indexOf("-") == -1) COLOR_START = RED_COLOR;
        else COLOR_START = BLUE_COLOR;
        a.append(getResources().getString(R.string.t_profit) + COLOR_START + uinfo.tProfit + COLOR_END + " <br>");
        if (uinfo.mProfit.indexOf("-") == -1) COLOR_START = RED_COLOR;
        else COLOR_START = BLUE_COLOR;
        a.append(getResources().getString(R.string.m_profit) + COLOR_START + uinfo.mProfit + COLOR_END);
        COLOR_START = CORAL3_COLOR;
        a.append(" " + getResources().getString(R.string.m_rank) + " " + COLOR_START + uinfo.mRank + " " + COLOR_END);
        if (uinfo.mRise.indexOf("-") == -1) COLOR_START = RED_COLOR;
        else COLOR_START = BLUE_COLOR;
        a.append(getResources().getString(R.string.m_rise) + COLOR_START + uinfo.mRise + COLOR_END + " <br>");
        if (uinfo.wProfit.indexOf("-") == -1) COLOR_START = RED_COLOR;
        else COLOR_START = BLUE_COLOR;
        a.append(getResources().getString(R.string.w_profit) + COLOR_START + uinfo.wProfit + COLOR_END);
        COLOR_START = CORAL3_COLOR;
        a.append(" " + getResources().getString(R.string.w_rank) + " " + COLOR_START + uinfo.wRank + COLOR_END + " ");
        if (uinfo.wRise.indexOf("-") == -1) COLOR_START = RED_COLOR;
        else COLOR_START = BLUE_COLOR;
        a.append(getResources().getString(R.string.w_rise) + COLOR_START + uinfo.wRise + COLOR_END + " <br>");

        a.append(getResources().getString(R.string.avg_days) + uinfo.avgDays + " ");
        if (uinfo.avgProfit.indexOf("-") == -1) COLOR_START = RED_COLOR;
        else COLOR_START = BLUE_COLOR;
        a.append(getResources().getString(R.string.avg_profit) + COLOR_START + uinfo.avgProfit + COLOR_END + " ");
        a.append(getResources().getString(R.string.trade_freq) + uinfo.tradingFrequency + "<br>");
        s = uinfo.toPercent(uinfo.sucRate);
        if (s.indexOf("-") == -1) COLOR_START = VIOLET_COLOR;
        else COLOR_START = VIOLET_COLOR;
        a.append(getResources().getString(R.string.suc_rate) + COLOR_START + s + "% " + COLOR_END);
        a.append(getResources().getString(R.string.close_num) + uinfo.closeNum + " ");
        a.append(getResources().getString(R.string.suc_num) + uinfo.sucNum + "<br>");
        return a.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock);
        tvs = findViewById(R.id.tv_stock);
        tvs.setMovementMethod(ScrollingMovementMethod.getInstance());
        new Thread(runnable).start();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(StockEvent event) {
        if (event.clear == false) {
            if (event.type == StockEvent.EventType.MESSAGE_DIS) {
                tvs.append(event.message + "\n");
            } else if (event.type == StockEvent.EventType.COLOR_DIS) {
                tvs.append(Html.fromHtml(event.message));
//                tvs.append(Html.fromHtml("<font color=\'#ff0000\'>红色</font>其它颜色"));
            }
        } else {
            tvs.setText("" + event.message + "\n");
        }
    }

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
    protected void onDestroy() {
        super.onDestroy();
        tvs.setText("");
//        runnable.interrupt();
        Log.e("ZTAG", "StockActivity onDestroy");
    }
}
