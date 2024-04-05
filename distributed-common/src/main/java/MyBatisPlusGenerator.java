import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

public class MyBatisPlusGenerator {

    public static void main(String[] args) {
        GlobalConfig config = new GlobalConfig();
        config.setActiveRecord(true)
                .setAuthor("zzf")
                .setOutputDir("distributed-order-service/src/main/java")
                .setFileOverride(true)
                .setIdType(IdType.AUTO)

                .setDateType(DateType.ONLY_DATE)
                .setServiceName("%sService")

                .setEntityName("%sDO")

                .setBaseResultMap(true)

                .setActiveRecord(false)

                .setBaseColumnList(true);

        DataSourceConfig dsConfig = new DataSourceConfig();
        dsConfig.setDbType(DbType.MYSQL)
                .setDriverName("com.mysql.cj.jdbc.Driver")
                .setUrl("jdbc:mysql://127.0.0.1:3306/cafe_order?useSSL=false")
                .setUsername("root")
                .setPassword("zzf19970112472X");

        StrategyConfig stConfig = new StrategyConfig();

        stConfig.setCapitalMode(true)
                .setNaming(NamingStrategy.underline_to_camel)

                .setEntityLombokModel(true)

                .setRestControllerStyle(true)

                .setInclude("product_order","product_order_item");

        PackageConfig pkConfig = new PackageConfig();
        pkConfig.setParent("com.group10")
                .setMapper("mapper")
                .setService("service")
                .setController("controller")
                .setEntity("model")
                .setXml("mapper");

        AutoGenerator ag = new AutoGenerator();
        ag.setGlobalConfig(config)
                .setDataSource(dsConfig)
                .setStrategy(stConfig)
                .setPackageInfo(pkConfig);

        ag.execute();
        System.out.println("======= Code generation finished  ========");
    }
}
