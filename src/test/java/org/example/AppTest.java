package org.example;

import com.alibaba.fastjson.JSONObject;
import org.example.exception.HttpRequestException;
import org.example.utils.HttpUtil;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    @Test
    void contextLoads() {
    }

    @Test(description = "用户名或密码错误")
    public void loginTest001() throws HttpRequestException {
        String baseurl = "http://localhost:8081/api/login?name=%s&pass=%s";
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        String name = "admin";
        String pass = "123";
        String result = HttpUtil.sendPostRequest(String.format(baseurl,name,pass), header, "");
        System.out.println(result);
        Assert.assertEquals(JSONObject.parseObject(result).get("code"), 20003);
    }
    @Test(description = "登录成功")
    public void loginTest002() throws HttpRequestException {
        String baseurl = "http://localhost:8081/api/login?name=%s&pass=%s";
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        String name = "admin";
        String pass = "123456";
        String result = HttpUtil.sendPostRequest(String.format(baseurl,name,pass), header, "");
        System.out.println(result);
        Assert.assertEquals(JSONObject.parseObject(result).get("code"), 0);
    }

    @Test(description = "")
    public void getRequirementListTest001() throws HttpRequestException {
        String url = "http://localhost:8081/api/query";
        HashMap header = new HashMap();
        header.put("Content-Type", "application/json");
        JSONObject body = new JSONObject();
        body.put("page", 1);
        body.put("size", 10);
        String result = HttpUtil.sendPostRequest(url, header, body.toString());
        Assert.assertEquals(JSONObject.parseObject(result).get("code"),0);
//        Assert.assertEquals(JSONObject.parseObject(result).get("totalRecord"),requirementService.getRequirementCount());
//        Assert.assertEquals(JSONObject.parseObject(result).getJSONArray("data"),requirementService.getRequirementList(0,10));
    }


}
