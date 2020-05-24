package gaokao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class MysqlClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MysqlClient.class);

    private static Map<String, Properties> url2Properties = new HashMap<String, Properties>();

    static{
        Properties properties = new Properties();
        properties.put("user", "root");
        properties.put("password", "root");
        url2Properties.put("jdbc:mysql://10.120.163.32:9030/dm", properties);
        properties = new Properties();
        properties.put("user", "root");
        properties.put("password", "root");
        url2Properties.put("jdbc:mysql://10.120.163.32:9030/resident", properties);
        properties = new Properties();
        properties.put("user", "root");
        properties.put("password", "root");
        url2Properties.put("jdbc:mysql://10.120.163.32:9030/magicbean", properties);
        properties = new Properties();
        properties.put("user", "root");
        properties.put("password", "root");
        url2Properties.put("jdbc:mysql://10.120.163.32:9030/working_dm", properties);
        properties = new Properties();
        properties.put("user", "w_datateam");
        properties.put("password", "EAaoaFy!pmUnT$eb");
        url2Properties.put("jdbc:mysql://10.103.32.196:3306/magicbean", properties);
        properties = new Properties();
        properties.put("user", "w_datateam");
        properties.put("password", "EAaoaFy!pmUnT$eb");
        url2Properties.put("jdbc:mysql://10.103.32.196:3306/skynet", properties);
        properties = new Properties();
        properties.put("user", "root");
        properties.put("password", "root123456");
        url2Properties.put("jdbc:mysql://10.136.21.3:3306/umeng_dump", properties);
    }

    /**
     * 目前只支持天级且含有字段day的表
     *
     * @param dbAndTable
     * @param timestamp
     * @param url
     * @return
     */
    public static boolean isTableReady(String dbAndTable, String timestamp, String url){
        String p_day = timestamp.substring(0,10);
        String sql = String.format("select count(1) as cnt from %s where day='%s'", dbAndTable, p_day);
        int cnt = Integer.parseInt(query(sql, url).get(0).get("cnt"));
        return cnt>0;
    }

    private static Connection getConnection(String url) throws SQLException {
        return DriverManager.getConnection(url, url2Properties.get(url));
    }

    public static void saveQueryTime(String sql, String sqlSource, long time) {
        String saveSql = String.format("insert into monitor_query_time (day,data_source,query_sql,seconds) " +
                "values('%s','%s','%s',%d)"
                , StringUtil.getNowString()
                ,sqlSource
                ,sql.replaceAll("\\'","\\\\'")
                ,time);
        update(saveSql, "jdbc:mysql://10.103.32.196:3306/magicbean");
    }


    public static int update(String sql, String url){
        Connection con = null;
        int ret = 0;
        try {
            con = getConnection(url);
            PreparedStatement stmt = con.prepareStatement(sql);
            ret = stmt.executeUpdate();
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            try {
                if (null != con)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public static List<Map<String,String>> query(String sql, String url) {
        Connection con = null;
        List<Map<String,String>> ret = new ArrayList<Map<String,String>>();
        try {
            con = getConnection(url);
            PreparedStatement stmt = con.prepareStatement(sql);
            LOGGER.info(String.format("Executing ... %s", sql));
            ResultSet res = stmt.executeQuery();
            LOGGER.info(String.format("Query Result ... %s", sql));
            int colCnt = res.getMetaData().getColumnCount();//结果集列数
            // 遍历结果集
            while (res.next()) {
                StringBuffer tag = new StringBuffer();
                // 可保留sql结果的列顺序
                Map<String,String> row = new LinkedHashMap<String,String>();
                String metric = null;
                for (int i = 1; i <= colCnt; i++) {
                    Object o = res.getObject(i);
                    row.put(res.getMetaData().getColumnName(i),o==null?"0":o.toString());
                }
                ret.add(row);
            }

        } catch(Exception e){
            e.printStackTrace();
        } finally {
            try {
                if (null != con)
                    con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return ret;
        }
    }

    public static void main(String[] args){
        // System.out.println(query("select substring(day,1,19) from fact_kafka_msg_count limit 1","jdbc:mysql://10.103.32.196:3306/magicbean"));
//        String sql = "select app_id,env,avg(view_doc_pv) from (select p_day,app_id,env_exp as env,sum(view_doc_pv) as view_doc_pv from working_dw.etl_odw_event LATERAL VIEW explode(env) env_tab as env_exp  where p_day>='2019-02-24' and p_day<='2019-02-24'  and app_id in ('oppobrowser','mibrowser','vivobrowser','xiaomi','oppo','yidian','pro','leshi','s3rd_mjweather','s3rd_zmweather','s3rd_mzbrowser','s3rd_op396','s3rd_op546') and env_exp in ('agg-aiorecall','agg-demand','agg-demand_ens','agg-fromid','agg-model2news','agg-news2news','agg-rel-news','agg-usercf','agg-usercfclickset2news','agg-video','agg-video-aiorecall') group by p_day,app_id,env_exp) a group by app_id,env".replaceAll("\\'","\\\\'");
//        System.out.println(sql);
//        saveQueryTime(sql,"",12);
        Double a = 1.0/6;
        System.out.println(String.format("%.2f", a));
    }


}
