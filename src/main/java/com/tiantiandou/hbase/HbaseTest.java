package com.tiantiandou.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;

public class HbaseTest {

    public static void main(String[] args) throws Exception {
        Configuration config = new Configuration();
        config.addResource("hbase.properties");
        config = HBaseConfiguration.create(config);
        HBaseAdmin admin = new HBaseAdmin(config);
        if (admin.tableExists("table")) {
            System.out.println("table already exists!");
        }
        HTableDescriptor[] tables = admin.listTables();
        for (int i = 0; i < tables.length; i++) {
            System.out.println(tables[i].getNameAsString());
        }
        admin.close();
    }

}
