package com.fastspider.fastcat.test;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.fastspider.fastcat.Common;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;

public class CommonTest extends InstrumentationTestCase {

    /*
     * 测试类中的方法
     */
    public void testAdd() throws Exception {
        String tag = "testAdd";
        Log.v(tag, "test the method");
        JSONObject json = Common.postServer(new ArrayList<NameValuePair>());
        assertEquals(json, new JSONObject());
    }

}
