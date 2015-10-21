package com.tiantiandou.mybatis;

import com.google.common.base.Splitter;
import org.reflections.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;

import static org.reflections.ReflectionUtils.withPrefix;

/**
 * Created by tommy on 2015/9/2.
 */
public class MapperGenerator {
    private Class<?> clazz;

    private Map<String, String> fieldMap = new HashMap<String, String>();

    private LinkedList<String> names = new LinkedList<String>();

    public MapperGenerator(Class<?> clazz){
        this.clazz = clazz;
    }

    public void init(List<String> orderedNames){
        Set<Method> methods =  ReflectionUtils.getAllMethods(clazz, withPrefix("get"));
        List<Method> list = new ArrayList<Method>(methods);
        Collections.sort(list, new Comparator<Method>() {
            @Override
            public int compare(Method o1, Method o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        for(Method m : list){
            if(m.getDeclaringClass().getSimpleName().equals("Object")){
                continue;
            }
            String name = m.getName().substring(4);
            name = ("" + m.getName().charAt(3)).toLowerCase() + name;
            String type = getType(m.getReturnType());
            fieldMap.put(name, type);

            if(name.equalsIgnoreCase("id")){
                names.addFirst(name);
            }else if (name.equalsIgnoreCase("createTime") || name.equalsIgnoreCase("modifyTime")){
                continue;
            }else{
                names.add(name);
            }
        }
        names.add("createTime");
        names.add("modifyTime");
        if(orderedNames != null){
            names.clear();
            names.addAll(orderedNames);
        }

    }

    public void generateTable(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("CREATE TABLE " + NameUtils.camelToUnderline(clazz.getSimpleName()) + "(\n");
        for(String name : names){
            String type = fieldMap.get(name);
            String line = getTableColumn(NameUtils.camelToUnderline(name), type);
            if(name.equalsIgnoreCase("id")){
                line = line + " NOT NULL AUTO_INCREMENT";
            }
            line = line + ",\n";
            buffer.append(line);
        }
        String str  =  buffer.toString() + "PRIMARY KEY (id) \n";

        str = str + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
        System.out.println(str);
    }


    public void genenrateSelect(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("select ");
        int length = 1;
        for(String name : names){
            buffer.append(NameUtils.camelToUnderline(name) + ",");
            if(buffer.length() > length * 90) {
                buffer.append("\n");
                length++;
            }
        }
        String str = buffer.substring(0, buffer.length() - 1);
        str += " from " + NameUtils.camelToUnderline(clazz.getSimpleName());
        System.out.println(str);
    }

    public void generateInsert(boolean withId){
        StringBuffer start = new StringBuffer();
        start.append("insert into " + NameUtils.camelToUnderline(clazz.getSimpleName() + "\n("));

        StringBuffer db = new StringBuffer();
        int length = 1;
        for(String name : names){
            if(name.equalsIgnoreCase("id") && !withId){
                continue;
            }
            db.append(NameUtils.camelToUnderline(name) + ",");
            if(db.length() > length * 90) {
                db.append("\n");
                length++;
            }
        }
        String str = start.toString() + db.substring(0, db.length() - 1);
        str += ") \nVALUES(\n";

        StringBuffer values = new StringBuffer();
        length = 1;
        for(String name : names){
            if(name.equalsIgnoreCase("id") && !withId){
                continue;
            }
            values.append("#{" + name +  "},");
            if(values.length() > length * 90) {
                values.append("\n");
                length++;
            }
        }

        str += values.substring(0, values.length() - 1);
        str += ")";
        System.out.println(str);
    }

    public void generateUpdate(boolean withId){
        StringBuffer buffer = new StringBuffer();
        buffer.append("update " + NameUtils.camelToUnderline(clazz.getSimpleName()) + " set ");
        for(String name : names){
            if(name.equalsIgnoreCase("id") && !withId){
                continue;
            }
            buffer.append("\n" + NameUtils.camelToUnderline(name) + "=" + "#{" + name +  "},");
        }
        String str = buffer.substring(0, buffer.length() - 1);
        str += "\n where id=#{id}";
        System.out.println(str);
    }

    private String getType(Class<?> cl){
        String name = cl.getSimpleName();
        if(name.equals("Integer")){
            return "Integer";
        }else if (name.equals("Long")){
            return "Long";
        }else if (name.equals("String")){
            return "String";
        }else if (name.equals("Date")){
            return "Date";
        }else if (name.equals("Boolean")){
            return "Integer";
        }else if (name.equals("BigDecimal")){
            return "BigDecimal";
        }else{
            throw new RuntimeException("Unknown class name : " + cl.getSimpleName());
        }
    }

    private String getTableColumn(String name, String type){
        if(type.equals("Integer")){
            return name + " int(20)";
        }else if (type.equals("Long")){
            return name + " bigint(20)";
        }else if (type.equals("String")){
            return name + " varchar(128)";
        }else if (type.equals("Date")){
            return name + " datetime DEFAULT CURRENT_TIMESTAMP";
        }else if (type.equals("BigDecimal")){
            return name + " decimal(19,2)";
        }else{
            throw new RuntimeException("Unknown class name : " + type);
        }
    }

    public static void main(String[] args){
        MapperGenerator generator = new MapperGenerator(ScoreDetail.class);
        String names ="id,shopId,score,source,time,createTime,modifyTime";
        generator.init(Splitter.on(",").omitEmptyStrings().trimResults().splitToList(names));
        generator.generateTable();
        System.out.println("\n\n");
        generator.genenrateSelect();
        System.out.println("\n\n");
        generator.generateInsert(false);
        System.out.println("\n\n");
        generator.generateUpdate(false);
    }
}
//String names = "id,version,type,systemInfoId,ip,shopId,itemIds,action,operator,description,beforeSnapId,afterSnapId," +
//        "paramSnapId,status,allShop,totalNum,unchangedNum,successNum,failedNum,errorCode,errorMessage,informed,createTime,modifyTime";

//String names ="id,shopId,batchTaskId,itemId,action,param,status,description,errorCode,errorMessage,rewardIds,createTime,modifyTime";

//String names = "id,version,type,systemInfoId,ip,shopId,activityTypes,itemIds,action,operator,description,beforeSnapId,afterSnapId," +
//        "paramSnapId,status,allShop,totalNum,unchangedNum,successNum,failedNum,errorCode,errorMessage,informed,createTime,modifyTime";

