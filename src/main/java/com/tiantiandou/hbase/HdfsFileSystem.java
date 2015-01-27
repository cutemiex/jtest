package com.tiantiandou.hbase;

import java.util.Calendar;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.FsStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

import com.taobao.tesla.common.util.LogUtil;

public class HdfsFileSystem {
    public static void main(String[] args) throws Exception {
        System.out.println("===========================================================");
        Configuration hadoopConf = new Configuration();
        hadoopConf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        FileSystem fs = new Path("hdfs://hdfscluster-perf").getFileSystem(hadoopConf);
        
        System.out.println("fs=" + fs.getUri());
        System.out.println("fs2=" + fs.getCanonicalServiceName());

        FsStatus fsStatus = fs.getStatus();
        System.out.println("fs s =" + fsStatus.toString());

        FileStatus[] fileStatuses = fs.listStatus(new Path("/hbase-perf"));
        System.out.println("fs s =" + fileStatuses.length);

        Path path = new Path("/hbase-perf/.oldlogs");
        /** 获取子目录名称 */
        FileStatus[] fileStatusArray = fs.listStatus(path, getPathFilter(getCheckpoint(), false));

        Path[] oldPathArray = FileUtil.stat2Paths(fileStatusArray);
        System.out.println(oldPathArray.length);
        for (int i = 0; i < oldPathArray.length; i++) {
          Path p = oldPathArray[i] ;
          FileStatus st = fileStatusArray[i];
          System.out.println(" |name= " + p.getName()  +  " |len= "  + st.getLen() + " |bolck= "+ st.getBlockSize() +"|" + LogUtil.getRsNameFromFileName(p.getName()));
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(1421791358855L);
        System.out.println(cal.getTime());
    }
    
    private static long getCheckpoint() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        return cal.getTimeInMillis();
    }

    private static PathFilter getPathFilter(final long checkpoint, final boolean verifyServer) {
        return new PathFilter() {
            public boolean accept(Path p) {
                return true;
            }
        };
    }
}
