package com.amylover.mybatis.config;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.parsers.DynamicTableNameParser;
import com.baomidou.mybatisplus.extension.parsers.ITableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 用于分表处理
 */
public class ItemTableUtils {

    public static void itemNodes(PaginationInterceptor paginationInterceptor){
        DynamicTableNameParser dynamicTableNameParser = new DynamicTableNameParser();
        Map<String,ITableNameHandler> iTableNameHandlerMap =new HashMap<String, ITableNameHandler>(2);
        //对应的表
        iTableNameHandlerMap.put("pu_check_tissue", (metaObject, sql, tableName) -> {
            // metaObject 可以获取传入参数，这里实现你自己的动态规则
           return process(metaObject,sql,tableName);
        });
        dynamicTableNameParser.setTableNameHandlerMap(iTableNameHandlerMap);
        paginationInterceptor.setSqlParserList(Collections.singletonList(dynamicTableNameParser));
    }





    //骨骼问题表 按年做分表数据
    private static String process(MetaObject metaObject, String sql, String tableName){
        System.err.println(JSONObject.toJSONString(metaObject));
        System.err.println(sql);
        System.err.println(tableName);
        AtomicReference<String> year = new AtomicReference<String>();
        JSONObject originalObject = (JSONObject) JSONObject.toJSON(metaObject.getOriginalObject());
        try {

            JSONObject parameterObject = originalObject.getJSONObject("boundSql").getJSONObject("parameterObject");
            //判断是否为 新增和更新
            if(!StringUtils.startsWithAny(sql,"INSERT","UPDATE")){
                JSONObject ew = parameterObject.getJSONObject("ew");
                parameterObject = ew.getJSONObject("paramNameValuePairs");

                parameterObject.forEach((s, o) -> {
                    if (o instanceof String) {
                        String value = String.valueOf(o);
                        if (value.contains("item_")) {
                            year.set("_" + value);
                        }
                    }
                });
            }else {
                //批量 et，单个 param1
                if(StringUtils.startsWithAny(sql,"UPDATE")){
                    JSONObject param1 = parameterObject.getJSONObject("param1");
                    //单个跟新
                    if(ObjectUtils.allNotNull(param1)){
                        year.set("_" + param1.getString("itemTime"));
                    }else{
                        JSONObject et = parameterObject.getJSONObject("et");
                        if(ObjectUtils.allNotNull(et)){
                            year.set("_" + et.getString("itemTime"));
                        }
                    }
                }else{
                    year.set("_" + parameterObject.getString("itemTime"));
                }
            }
        }catch (Exception e){}
        return String.format("%s%s", tableName, StringUtils.isEmpty(year.get()) ? "" : year.get());
    }



}
