package com.zoo.whale.example.list;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Date: 2022年12月27日
 * @Time: 15时:34分
 * @Author: weixin.liu
 * @Version: 1.0
 */
public class RemoveDemo {

    public static void main(String[] args) {
        JSONArray jsonArray = new JSONArray();

        for (int i = 0;i < 10;i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(i+"",i+"_"+"value");
            jsonArray.add(jsonObject);
        }

        System.out.println(jsonArray.size());
        /*for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            if(jsonObject.containsKey("7")) {
                jsonArray.remove(jsonObject);
            }
        }*/

        Iterator<Object> iterator1 = jsonArray.iterator();
        while (iterator1.hasNext()) {
            Object next = iterator1.next();
            jsonArray.remove(next);
        }

        /*for (int i = 0;i < jsonArray.size();i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if(jsonObject.containsKey("7")) {
                jsonArray.remove(i);
                i--;
            }
            if(jsonObject.containsKey("8")) {
                jsonArray.remove(i);
            }
        }*/

        System.out.println(jsonArray.size());

        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("12");
        list.add("13");
        list.add("14");

        /*for (String  s : list) {
            list.remove(s);
        }*/

        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if("12".equals(next)){
                list.remove(next);
            }

        }

        System.out.println(list.size());


        //建立List集合
        List<String> s=new ArrayList<>();//多态

        //向该集合添加信息值
        s.add("鸡");
        s.add("你");
        s.add("不");
        s.add("美");
        System.out.println(s);

        //1
        /*for (int i = 0; i < s.size(); i++) {
            String s1 = s.get(i);
            if (s1.equals("不")){
                s.add("太");//给元素最后添加 "太" 元素
            }
            System.out.println(s1);
        }*/
        System.out.println("---------");

        //我现在又想通过for增强型和Iterator迭代器来把集合最后一个"太"字去掉

        //2
        Iterator<String> ii = s.iterator();
        while (ii.hasNext()) {
            String ss = ii.next();
            if (ss.equals("不")){
                s.remove("不");//ConcurrentModificationException
            }
            System.out.println(ss);
        }

        //3
        /*for (String sf:s) {
            if (sf.equals("太")){
                s.remove("太");//ConcurrentModificationException
            }
            System.out.println(sf);
        }*/
    }


}
