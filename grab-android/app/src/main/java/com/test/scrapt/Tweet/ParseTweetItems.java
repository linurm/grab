package com.test.scrapt.Tweet;

import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by Administrator on 2017/11/10.
 */

public class ParseTweetItems extends ParseItem{
    public static String parse(TweetItem items, Document document) {
        Elements e = document.select("div.tip2");
        String s = e.toString();
        String[] f = e.text().split(" ");
        items.tweet = Integer.parseInt(splitsqrt(f[0]));
        items.follow = Integer.parseInt(splitsqrt(f[1]));
        items.fan = Integer.parseInt(splitsqrt(f[2]));
        s = (f[0]) + " " + (f[1]) + " " + (f[2]);
        Log.e("ZTAG", s);

        return s;
    }



}