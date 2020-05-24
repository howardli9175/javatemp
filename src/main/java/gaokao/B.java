package gaokao;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class B {
    public static void main(String[] args) throws Exception {
//        gkSp();
//        gkSc();
//        gkScline("2019");
//        gkScline("2018");
//        gkScline("2017");
//        gkScline("2016");
//        gkScline("2015");
//        gkScline("2014");
        xueke();
    }

    public static void xueke() throws Exception{
        Map<String,Integer> gradeMap = new HashMap();
        gradeMap.put("A+",9);
        gradeMap.put("A",8);
        gradeMap.put("A-",7);
        gradeMap.put("B+",6);
        gradeMap.put("B",5);
        gradeMap.put("B-",4);
        gradeMap.put("C+",3);
        gradeMap.put("C",2);
        gradeMap.put("C-",1);
        BufferedReader br = new BufferedReader(new FileReader("/Users/admin/Applications/meerkat/xueke_raw.txt"));
        BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/admin/Applications/meerkat/xueke_format.txt"));
        String line = null;
        Pattern p = Pattern.compile("(\\d+)([^\\d]+)");
        while((line=br.readLine())!=null){
            String[] cell = line.split("\t");
            String code = "", cod1 = "", cod2 = "", name = "";
            int gradeInt = 0;
            for (int i = 0; i < cell.length; i++) {
                if(i==0){
                    String c = cell[i].replace("一级学科代码及名称：","");
                    Matcher m = p.matcher(c);
                    m.find();
                    code = m.group(1);
                    name = m.group(2);
                    cod1 = code.substring(0,2);
                    cod2 = code.substring(2,4);
                }else if(i==1){
                    try {
                        gradeInt = gradeMap.get(cell[i].trim());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    String c = cell[i];
                    Matcher m = p.matcher(c);
                    m.find();
//                    System.out.println(String.format("%s||%s||%s||%s||%d||%s||%s", code,cod1, cod2, name, gradeInt, m.group(1), m.group(2)));
                    bw.write(String.format("%s||%s||%s||%s||%d||%s||%s", code,cod1, cod2, name, gradeInt, m.group(1), m.group(2)));
                    bw.write("\n");
                }
            }
            bw.flush();
        }
        bw.close();
        br.close();
    }



    public static void gkScline(String yearParam) throws Exception{
        String url = "https://api.eol.cn/gkcx/api/?access_token=&admissions=&central=&department=&dual_class=&f211=&f985=&is_dual_class=&keyword=&local_batch_id=&page=aaaaa&province_id=&school_type=&signsafe=&size=20&type=&uri=apidata/api/gk/score/province&year=bbbbb";
        url = url.replace("bbbbb",yearParam);
        int batch_count=0;
        int page = 1;
        int total_cnt = 0;
        long begin = System.currentTimeMillis();
        BufferedWriter bw = new BufferedWriter(new FileWriter(String.format("scline%s.txt", yearParam), true));
        JsonArray items = null;
        do{
            String res = HttpUtil.get(url.replace("aaaaa",page+""));
            try {
                items = new JsonParser().parse(res).getAsJsonObject().getAsJsonObject("data").getAsJsonArray("item");
            }catch(Exception e){
                e.printStackTrace();
                System.err.println(res);
                System.exit(-1);
            }
            batch_count = items.size();
            for(int i =0;i<batch_count;i++){
                JsonObject item = items.get(i).getAsJsonObject();
                int year = item.getAsJsonPrimitive("year").getAsInt();
                int school_id = item.getAsJsonPrimitive("school_id").getAsInt();
                String name = item.getAsJsonPrimitive("name").getAsString();
                String local_province_name = item.getAsJsonPrimitive("local_province_name").getAsString();
                String local_batch_name = item.getAsJsonPrimitive("local_batch_name").getAsString();
                String local_type_name = item.getAsJsonPrimitive("local_type_name").getAsString();
                String min = item.getAsJsonPrimitive("min").getAsString();
                String max = item.getAsJsonPrimitive("max").getAsString();
                String average = item.getAsJsonPrimitive("average").getAsString();
                JsonPrimitive filingObj = item.getAsJsonPrimitive("filing");
                String filing = filingObj==null?"0":filingObj.getAsString();
                String proscore = item.getAsJsonPrimitive("proscore").getAsString();
                String dataStr = String.format("%d||%d||%s||%s||%s||%s||%s||%s||%s||%s||%s"
                        , year,school_id,name,local_province_name,local_batch_name,local_type_name,min,max,average,filing,proscore);
                bw.write(dataStr);
                bw.write("\n");
                total_cnt++;
            }
            long end = System.currentTimeMillis();
            System.out.println(String.format("year %s, page %d, get total %d, use %d seconds", yearParam, page, total_cnt, (end-begin)/1000));
            page++;
            bw.flush();
            Thread.sleep(100);
//            break;
        }
        while(batch_count>=20);
        bw.close();
    }

    public static void gkSc() throws Exception{
        String url = "https://api.eol.cn/gkcx/api/?access_token=&admissions=&central=&department=&dual_class=&f211=&f985=&is_dual_class=&keyword=&page=aaaaa&province_id=&request_type=1&school_type=&signsafe=&size=20&sort=view_total&type=&uri=apigkcx/api/school/hotlists";
        int batch_count=0;
        int page = 1;
        int total_cnt = 0;
        long begin = System.currentTimeMillis();
        Properties properties = new Properties();
        properties.put("user", "w_datateam");
        properties.put("password", "EAaoaFy!pmUnT$eb");
        Connection conn = DriverManager.getConnection("jdbc:mysql://10.103.32.196:3306/data", properties);
//        BufferedWriter bw = new BufferedWriter(new FileWriter("sp2016.txt", true));
        JsonArray items = null;
        do{
            String res = HttpUtil.get(url.replace("aaaaa",page+""));
            try {
                items = new JsonParser().parse(res).getAsJsonObject().getAsJsonObject("data").getAsJsonArray("item");
            }catch(Exception e){
                e.printStackTrace();
                System.err.println(res);
            }
            batch_count = items.size();
            for(int i =0;i<batch_count;i++){
                JsonObject item = items.get(i).getAsJsonObject();
                int school_id = item.getAsJsonPrimitive("school_id").getAsInt();
                String name = item.getAsJsonPrimitive("name").getAsString();
                int rank = item.getAsJsonPrimitive("rank").getAsInt();
                int rank_type = item.getAsJsonPrimitive("rank_type").getAsInt();
                String type_name = item.getAsJsonPrimitive("type_name").getAsString();
                String view_total_str = item.getAsJsonPrimitive("view_total").getAsString();
                int view_total = 0;
                if(view_total_str.contains("万")){
                    view_total = (int)Double.parseDouble(view_total_str.replaceAll("万",""))*10000;
                }else{
                    view_total = (int)Double.parseDouble(view_total_str);
                }
                int view_month = item.getAsJsonPrimitive("view_month").getAsInt();
                int view_week = item.getAsJsonPrimitive("view_week").getAsInt();
                String level_name = item.getAsJsonPrimitive("level_name").getAsString();
                String provice_name = item.getAsJsonPrimitive("province_name").getAsString();
                String city_name = item.getAsJsonPrimitive("city_name").getAsString();
                int f211 = item.getAsJsonPrimitive("f211").getAsInt();
                int f985 = item.getAsJsonPrimitive("f985").getAsInt();
                int is_top = item.getAsJsonPrimitive("is_top").getAsInt();
                int dual_class = item.getAsJsonPrimitive("dual_class").getAsInt();
                int central = item.getAsJsonPrimitive("central").getAsInt();
                int department = item.getAsJsonPrimitive("department").getAsInt();
                int admissions = item.getAsJsonPrimitive("admissions").getAsInt();
                String sql = String.format("INSERT INTO `zzz_gk_sch` (`school_id`, `name`, `rank`, `rank_type`, `type_name`, `view_total`, `view_month`, `view_week`, `level_name`, `provice_name`, `city_name`, `f211`, `f985`, `is_top`, `dual_class`, `central`, `department`, `admissions`)" +
                                " values(%d, '%s', %d, %d, '%s', %d, %d, %d, '%s', '%s', '%s', %d, %d, %d, %d, %d, %d, %d)"
                        , school_id,name,rank, rank_type, type_name, view_total, view_month ,view_week, level_name,provice_name,city_name,f211,f985,is_top,dual_class,central,department,admissions);
                String dataStr = String.format("%d||%s||%d||%d||%s||%d||%d||%d||%s||%s||%s||%d||%d||%d||%d||%d||%d||%d"
                        , school_id,name,rank, rank_type, type_name, view_total, view_month ,view_week, level_name,provice_name,city_name,f211,f985,is_top,dual_class,central,department,admissions);
//                 System.out.println(sql);
                 MysqlClient.update(sql, "jdbc:mysql://10.103.32.196:3306/data");
//                total_cnt += conn.prepareStatement(sql).executeUpdate();
//                bw.write(dataStr);
//                bw.write("\n");
                total_cnt++;
            }
            long end = System.currentTimeMillis();
            System.out.println(String.format("page %d, get total %d, use %d seconds", page, total_cnt, (end-begin)/1000));
            page++;
            Thread.sleep(100);
//            break;
        }
        while(batch_count>=20);
//        bw.close();
        conn.close();
    }

    public static void gkSp() throws Exception{
        String yearParam = "2014";
        String url ="https://api.eol.cn/gkcx/api/?school_type=&local_batch_id=&central=&dual_class=&f211=&access_token=&uri=apidata%2Fapi%2Fgk%2Fscore%2Fspecial&admissions=&local_type_id=&keyword=&is_dual_class=&year=bbbbb&department=&f985=&province_id=&type=&page=aaaaa&size=100";
        url = url.replace("bbbbb",yearParam);
        int batch_count=0;
        int page = 1;
        int total_cnt = 0;
        long begin = System.currentTimeMillis();
        Properties properties = new Properties();
        properties.put("user", "w_datateam");
        properties.put("password", "EAaoaFy!pmUnT$eb");
        // Connection conn = DriverManager.getConnection("jdbc:mysql://10.103.32.196:3306/data", properties);
        BufferedWriter bw = new BufferedWriter(new FileWriter(String.format("sp%s.txt", yearParam), true));
        JsonArray items = null;
        do{
            String res = HttpUtil.get(url.replace("aaaaa",page+""));
            try {
                items = new JsonParser().parse(res).getAsJsonObject().getAsJsonObject("data").getAsJsonArray("item");
            }catch(Exception e){
                e.printStackTrace();
                System.err.println(res);
            }
            batch_count = items.size();
            for(int i =0;i<batch_count;i++){
                JsonObject item = items.get(i).getAsJsonObject();
                int year = item.getAsJsonPrimitive("year").getAsInt();
                int school_id = item.getAsJsonPrimitive("school_id").getAsInt();
                String name = item.getAsJsonPrimitive("name").getAsString();
                int special_id = item.getAsJsonPrimitive("special_id").getAsInt();
                String spname = item.getAsJsonPrimitive("spname").getAsString();
                String local_province_name = item.getAsJsonPrimitive("local_province_name").getAsString();
                String local_batch_name = item.getAsJsonPrimitive("local_batch_name").getAsString();
                String local_type_name = item.getAsJsonPrimitive("local_type_name").getAsString();
                String dual_class_name = item.getAsJsonPrimitive("dual_class_name").getAsString();
                String zslx_name = item.getAsJsonPrimitive("zslx_name").getAsString();
                String min = item.getAsJsonPrimitive("min").getAsString();
                String max = item.getAsJsonPrimitive("max").getAsString();
                String average = item.getAsJsonPrimitive("average").getAsString();
                String min_section = item.getAsJsonPrimitive("min_section").getAsString();
                String proscore = item.getAsJsonPrimitive("proscore").getAsString();
                String sql = String.format("insert into zzz_gk_sp(year,school_id,name,special_id,spname,local_province_name,local_batch_name,local_type_name,dual_class_name,zslx_name,min,max,average,min_section,proscore)" +
                                " values(%d, %d, '%s', %d, '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')"
                        , year,school_id,name,special_id,spname,local_province_name,local_batch_name,local_type_name,dual_class_name,zslx_name,min,max,average,min_section,proscore);
                String dataStr = String.format("%d||%d||%s||%d||%s||%s||%s||%s||%s||%s||%s||%s||%s||%s||%s"
                        , year,school_id,name,special_id,spname,local_province_name,local_batch_name,local_type_name,dual_class_name,zslx_name,min,max,average,min_section,proscore);
//                 System.out.println(sql);
                // MysqlClient.update(sql, "jdbc:mysql://10.103.32.196:3306/data");
//                total_cnt += conn.prepareStatement(sql).executeUpdate();
                bw.write(dataStr);
                bw.write("\n");
                total_cnt++;
            }
            long end = System.currentTimeMillis();
            System.out.println(String.format("page %d, get total %d, use %d seconds", page, total_cnt, (end-begin)/1000));
            page++;
            bw.flush();
            Thread.sleep(100);
//            break;
        }
        while(batch_count>=30);
        bw.close();
//        conn.close();
    }
}
