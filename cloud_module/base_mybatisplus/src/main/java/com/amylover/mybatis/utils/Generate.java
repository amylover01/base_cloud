//package com.amylover.mybatis.utils;
//
//import com.baomidou.mybatisplus.annotation.DbType;
//import com.baomidou.mybatisplus.generator.AutoGenerator;
//import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
//import com.baomidou.mybatisplus.generator.config.GlobalConfig;
//import com.baomidou.mybatisplus.generator.config.PackageConfig;
//import com.baomidou.mybatisplus.generator.config.StrategyConfig;
//import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
//import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * 生成代码工具
// */
//public class Generate {
//
//    public static void main(String[] args) {
//        Generate g = new Generate();
//        boolean startWithI = true;
//        List<String> stringList=new ArrayList<>();
//        stringList.add("resource");
//        stringList.add("resource_button");
//        stringList.add("role");
//        stringList.add("role_resource");
//        stringList.add("role_resource_button");
//        stringList.add("sys_area");
//        stringList.add("user_app");
//        stringList.add("user_cloud");
//        stringList.add("user_role");
//        stringList.add("user_site");
//
//
//        String[] tableName = stringList.toArray(new String[stringList.size()]);
//        //String tableName = "user_reg";
//        String projectName = "hg_brigade";
//            String packageName = "com.honggv.cloud.user";
//            g.generateByTables(startWithI,projectName,packageName,tableName);
//
//    }
//    /**
//     * 根据表自动生成
//     *
//     * @param serviceNameStartWithI 默认为false
//     * @param packageName           包名
//     * @param tableNames            表名
//     */
//    private void generateByTables(boolean serviceNameStartWithI,String projectName, String packageName, String... tableNames) {
//        //配置数据源
//        DataSourceConfig dataSourceConfig = getDataSourceConfig();
//        // 策略配置
//        StrategyConfig strategyConfig = getStrategyConfig(tableNames);
//        //全局变量配置
//        GlobalConfig globalConfig = getGlobalConfig(serviceNameStartWithI,projectName);
//        //包名配置
//        PackageConfig packageConfig = getPackageConfig(packageName);
//        //自动生成
//        atuoGenerator(dataSourceConfig, strategyConfig, globalConfig, packageConfig);
//    }
//
//    /**
//     * 集成
//     *
//     * @param dataSourceConfig 配置数据源
//     * @param strategyConfig   策略配置
//     * @param config           全局变量配置
//     * @param packageConfig    包名配置
//     */
//    private void atuoGenerator(DataSourceConfig dataSourceConfig, StrategyConfig strategyConfig, GlobalConfig config, PackageConfig packageConfig) {
//        new AutoGenerator()
//                .setGlobalConfig(config)
//                .setDataSource(dataSourceConfig)
//                .setStrategy(strategyConfig)
//                .setPackageInfo(packageConfig)
//                .setTemplateEngine(new VelocityTemplateEngine())
//                .execute();
//    }
//
//    /**
//     * 设置包名
//     *
//     * @param packageName 父路径包名
//     * @return PackageConfig 包名配置
//     */
//    private PackageConfig getPackageConfig(String packageName) {
//        return new PackageConfig()
//                .setParent(packageName)
//                .setXml("mapper")
//                .setMapper("mapper")
//                .setController("controller")
//                .setService("service")
//                .setEntity("entity");
//    }
//
//    /**
//     * 全局配置
//     *
//     * @param serviceNameStartWithI false
//     * @return GlobalConfig
//     */
//    private GlobalConfig getGlobalConfig(boolean serviceNameStartWithI,String projectName) {
//        GlobalConfig globalConfig = new GlobalConfig();
//        globalConfig
//                .setBaseColumnList(true)
//                .setBaseResultMap(true)
//                .setActiveRecord(false)
//                .setAuthor("hg")
//                //设置输出路径
//                .setOutputDir("D:\\mybatis"+"/"+projectName+"/src/main/java/")
//                .setFileOverride(true);
//        if (!serviceNameStartWithI) {
//            //设置service名
//            globalConfig.setServiceName("%sService");
//        }
//        return globalConfig;
//    }
//
//    /**
//     * 策略配置
//     *
//     * @param tableNames 表名
//     * @return StrategyConfig
//     */
//    private StrategyConfig getStrategyConfig(String... tableNames) {
//        return new StrategyConfig()
//                // 全局大写命名 ORACLE 注意
//                .setCapitalMode(true)
//                .setEntityLombokModel(true)
//                .setRestControllerStyle(false)
//                .setSuperEntityClass(Convert.class)
//                //从数据库表到文件的命名策略
//                .setNaming(NamingStrategy.underline_to_camel)
//                .setColumnNaming(NamingStrategy.underline_to_camel)
//                .setEntityColumnConstant(true)
//                //需要生成的的表名，多个表名传数组
//                .setInclude(tableNames);
//    }
//
//    /**
//     * 配置数据源
//     *
//     * @return 数据源配置 DataSourceConfig
//     */
//    private DataSourceConfig getDataSourceConfig() {
//        return new DataSourceConfig().setDbType(DbType.MYSQL)
//                .setUrl("jdbc:mysql://192.168.10.35:3306/brigade_user?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai")
//                .setUsername("root")
//                .setPassword("root")
//                .setDriverName("com.mysql.cj.jdbc.Driver");
//    }
//
//
//}
