package com.amylover.mybatis.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.function.Supplier;


/**
 * 注入公共字段自动填充,任选注入方式即可
 */
public class MyMetaObjectHandler implements MetaObjectHandler {


    /**
     * 创建时间
     */
    private final String createTime = "createTime";

    /**
     * 修改时间
     */
    private final String updateTime = "updateTime";
    /**
     * 创建人
     */
    private final String createdBy = "createdBy";
    /**
     * 更新人
     */
    private final String updatedBy = "updatedBy";


    @Override
    public void insertFill(MetaObject metaObject) {
//        if (testType == null) {
        //测试实体没有的字段，配置在公共填充，不应该set到实体里面
        // this.strictInsertFill(metaObject, createTime, Long.class, DateUtils.getLocalUnix());
        // this.strictInsertFill(metaObject, updateTime, Long.class, DateUtils.getLocalUnix());
        try {
            //   this.strictInsertFill(metaObject, createdBy, String.class, tokenUtil.getParamsToken("id"));
            // this.strictInsertFill(metaObject, updatedBy, String.class, tokenUtil.getParamsToken("id"));
        } catch (Exception ignored) {
        }
        // }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        //测试实体没有的字段，配置在公共填充，不应该set到实体里面
        this.strictInsertFill(metaObject, updateTime, Long.class, System.currentTimeMillis());
        try {
            // this.strictInsertFill(metaObject, updatedBy, String.class, tokenUtil.getParamsToken("id"));
        } catch (Exception ignored) {
        }

    }

    @Override
    public MetaObjectHandler strictFillStrategy(MetaObject metaObject, String fieldName, Supplier<Object> fieldVal) {
        if (metaObject.getValue(fieldName) == null) {
            Object obj = fieldVal.get();
            if (Objects.nonNull(obj)) {
                metaObject.setValue(fieldName, obj);
            }
        }

        return this;
    }
}
