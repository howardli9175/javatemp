package gaokao;


import com.google.gson.*;
import org.javatuples.Triplet;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static final Pattern REGEX_HIVE_QUERY_SQL_DAY=Pattern.compile("\\s*select\\s+.+\\s+from\\s+(\\S+)\\.(\\S+).+where\\s+p_day.+group\\s+by.+");

    public static final Pattern REGEX_HIVE_QUERY_SQL_HOUR=Pattern.compile("\\s*select\\s+.+\\s+from\\s+(\\S+)\\.(\\S+).+where\\s+p_day.+p_hour.+group\\s+by.+");


    public static final String SEPERATOR = "|";

    public static final String SPLIT_SEPERATOR = "\\|";

    public static boolean isHiveDataSource(String ds){
//        return ds.startsWith("hive");
        return ds.contains("hive");
    }

    public static boolean isMysqlDataSource(String ds){
        return ds.contains("mysql");
    }


    public static boolean isSimbaxDataSource(String url) {
        return "http://simbax-da.int.yidian-inc.com/api".equals(url);
    }

    public static boolean isOakDataSource(String url) {
        return (null!=url) && url.contains("oak");
        //return "http://dataplatform.yidian-inc.com/oak/druid/query".equals(url);
    }

    public static String getClusterNameFromUrl(String url){
        if(url.contains("103-8-33")) {
            return "purple";
        }
        return "yellow";
    }


    /**
     * @param timestamp - 2018-12-19 09:00:00
     * @return - 2018-12-18 09:00:00
     */
    public static String getLastDay(String timestamp){
        return getDayAgo(timestamp, 1);
    }


    /**
     * @param timestamp - 2018-12-19 00:00:00
     * @param i - 2
     * @return - 2018-12-17 00:00:00
     */
    public static String getDayAgo(String timestamp, int i) {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime dt = dtf.parseDateTime(timestamp).minusDays(i);
        return dt.toString("yyyy-MM-dd HH:mm:ss");
    }

    public static String getNowString(){
        return DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
    }

    public static String getNowDateString(){
        return DateTime.now().toString("yyyy-MM-dd");
    }

    /**
     * @param timestamp - 2018-12-19 09:00:00
     * @return - 2018-12-19 08:00:00
     */
    public static String getLastHour(String timestamp){
        return getHourAgo(timestamp, 1);
    }


    /**
     * @param timestamp - 2018-12-19 09:00:00
     * @param i         - 2
     * @return - 2018-12-19 07:00:00
     */
    public static String getHourAgo(String timestamp, int i){
        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime dt = dtf.parseDateTime(timestamp).minusHours(i);
        return dt.toString("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * @param timestamp - 2018-12-19 09:58:00
     * @param i         - 2
     * @return - 2018-12-19 09:56:00
     */
    public static String getMinuteAgo(String timestamp, int i){
        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime dt = dtf.parseDateTime(timestamp).minusMinutes(i);
        return dt.toString("yyyy-MM-dd HH:mm:ss");
    }

    public static String toShortDatatime(String timestamp){
        return DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime(timestamp).toString("yy/M/d HH:mm");
    }

    public static String tab2Space(String input){
        Map<Integer,Integer> col2ColWidth = new HashMap<Integer, Integer>();
        for(String row:input.split("\n")){
            int i = 0;
            for(String cell:row.split("\t")){
                Integer tmp = col2ColWidth.get(i);
                if( null==tmp || cell.length()>tmp){
                    col2ColWidth.put(i,cell.length());
                }
                i++;
            }
        }
        StringBuffer res = new StringBuffer();
        for(String row:input.split("\n")){
            int i = 0;
            for(String cell:row.split("\t")){
                int colWidth = col2ColWidth.get(i)+2;
                res.append(String.format("%"+colWidth+"s",cell));
                i++;
            }
            res.append("\n");
        }
        return res.toString();
    }


    /**
     * 行分隔符\n
     * 单元格分隔符\t
     * 单元格中需要换行显示用逗号隔开
     * @param input
     * @return
     */
    public static String toHtmlTable(String input){
        StringBuffer res = new StringBuffer();
        res.append("<table class=\"gridtable\">");
        int i = 0;
        for(String row:input.split("\n")){
            res.append("<tr>");
            for(String cell:row.split("\t")){
                if(i==0){
                    res.append("<th>");
                    res.append(cell.replace(StringUtil.SEPERATOR,"<br>"));
                    res.append("</th>");
                }else{
                    String align = StringUtil.isDigitCell(cell)?"right":"left";
                    res.append(String.format("<td align=\"%s\">", align));
                    res.append(cell.replace(StringUtil.SEPERATOR,"<br>"));
                    res.append("</td>");
                }
            }
            res.append("</tr>");
            i++;
        }
        res.append("</table>");
        return res.toString();
    }

    /**
     * 行分隔符\n
     * 单元格分隔符\t
     * 单元格中需要换行显示用逗号隔开
     * @param input
     * @param highlightLines - 突出显示的行，从0开始
     * @return
     */
    public static String toHtmlTable(String input, List<Integer> highlightLines){
        StringBuffer res = new StringBuffer();
        res.append("<table class=\"gridtable\">");
        int i = 0;
        for(String row:input.split("\n")){
            res.append("<tr>");
            for(String cell:row.split("\t")){
                if(i==0){
                    res.append("<th>");
                    res.append(cell.replace(StringUtil.SEPERATOR,"<br>"));
                    res.append("</th>");
                }else{
                    String cssCell = "td";
                    String align = StringUtil.isDigitCell(cell)?"right":"left";
                    res.append(String.format("<%s align=\"%s\">", cssCell, align));
                    if(highlightLines.contains(i)){
                        res.append("<strong>");
                    }
                    res.append(cell.replace(StringUtil.SEPERATOR,"<br>"));
                    if(highlightLines.contains(i)){
                        res.append("</strong>");
                    }
                    res.append(String.format("</%s>", cssCell));
                }
            }
            res.append("</tr>");
            i++;
        }
        res.append("</table>");
        return res.toString();
    }

    public static boolean isDigitCell(String cell) {
        if(cell!=null ){
            if(cell.contains("%") || cell.contains(",")|| cell.contains("font")|| cell.trim().equals("-")) {
                return true;
            }
        }
        try{
            Double.parseDouble(cell);
            return true;
        }catch (Exception e ){
            return false;
        }
    }

    public static String decimal2String(BigDecimal input, int scale, boolean percent, BigDecimal lower, BigDecimal upper){
        if(percent){
            if(input.compareTo(upper)>0){
                return String.format("<font color=\"green\">%s%s</font>",input.multiply(new BigDecimal(100)).setScale(scale, RoundingMode.HALF_UP).toPlainString(),"%");
            }else if(input.compareTo(lower)<0){
                return String.format("<font color=\"red\">%s%s</font>",input.multiply(new BigDecimal(100)).setScale(scale, RoundingMode.HALF_UP).toPlainString(),"%");
            }
            return String.format("%s%s",input.multiply(new BigDecimal(100)).setScale(scale, RoundingMode.HALF_UP).toPlainString(),"%");
        }
        if(input.compareTo(upper)>0){
            return String.format("<font color=\"green\">%s</font>",input.setScale(scale, RoundingMode.HALF_UP).toPlainString());
        }else if(input.compareTo(lower)<0){
            return String.format("<font color=\"red\">%s</font>",input.setScale(scale, RoundingMode.HALF_UP).toPlainString());
        }
        return input.setScale(scale, RoundingMode.HALF_UP).toPlainString();
    }

    /**
     *
     *
     * @param input
     * @param scale
     * @param percent
     * @return
     */
    public static String decimal2String(BigDecimal input, int scale, boolean percent){
        if(percent){
            return String.format("%s%s",input.multiply(new BigDecimal(100)).setScale(scale, RoundingMode.HALF_UP).toPlainString(),"%");
        }
        return input.setScale(scale, RoundingMode.HALF_UP).toPlainString();
    }



    /**
     * 从sql中提取库名、表名
     * @param sql
     * @return
     */
    public static Triplet<Integer,String,String> getFreqDBAndTable(String sql){
        Matcher m_hour = REGEX_HIVE_QUERY_SQL_HOUR.matcher(sql);
        if(m_hour.find()){
            return Triplet.with(60,m_hour.group(1),m_hour.group(2));
        }else{
            Matcher m_day = REGEX_HIVE_QUERY_SQL_DAY.matcher(sql);
            if(m_day.find()){
                return Triplet.with(1440,m_day.group(1),m_day.group(2));
            }else{
                throw new RuntimeException(String.format("invalid sql : %s", sql));
            }
        }
    }

    public static boolean isEmpty(String s){
        return null == s || "".equals(s.trim());
    }

    public static JsonPrimitive getJsonPrimitiveFromJson(String json, String path){
        int indexLastDot = path.lastIndexOf(".");
        if(indexLastDot==-1){
            return new JsonParser().parse(json).getAsJsonObject().getAsJsonPrimitive(path);
        }else {
            String pathPrefix = path.substring(0, indexLastDot);
            String pathSuffix = path.substring(indexLastDot + 1);
            JsonObject obj = getJsonObjectFromJson(json, pathPrefix);
            return obj.getAsJsonPrimitive(pathSuffix);
        }
    }

    public static JsonArray getJsonArrayFromJson(String json, String path){
        int indexLastDot = path.lastIndexOf(".");
        String pathPrefix = path.substring(0,indexLastDot);
        String pathSuffix = path.substring(indexLastDot+1);
        JsonObject obj = getJsonObjectFromJson(json, pathPrefix);
        return obj.getAsJsonArray(pathSuffix);
    }

    public static JsonObject getJsonObjectFromJson(String json, String path){
        JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
        String[] paths = path.split("\\.");
        for(String tmp:paths){
            obj = obj.getAsJsonObject(tmp);
        }
        return obj;
    }

    public static List<String> getListFromJson(String json){
        return new Gson().fromJson(json,List.class);
    }

    public static void main(String[] args){
        double a = 10000.0/3;
        System.out.println(decimal2String(BigDecimal.valueOf(a), 0, false));
        String[] aaa = new String[]{"bfds","afds"};
        String aa = String.join(SEPERATOR,aaa);
        System.out.println(aa);
        System.out.println(aa.replace(SEPERATOR,""));
        for (String s : aa.split(SPLIT_SEPERATOR)) {
            System.out.println(s);
        }
    }


    public static String empty2dash(String newVersionDay) {
        if(isEmpty(newVersionDay)){
            return "-";
        }
        return newVersionDay;
    }

    public static String getShowRemark(String remark, String template){
        if(StringUtil.isEmpty(remark)) {
            return "";
        }else{
            String[] remarks = remark.split("\n");
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < remarks.length; i++) {
                buf.append(String.format("注%d,%s<br>", (i + 1), remarks[i]));
            }
            return String.format(template, buf.toString());
        }
    }


    public static String numberFormat(String display) {
        if(display!=null && !display.contains("%")){
            if(display.contains(".")){
                // double
                try {
                    return NumberFormat.getNumberInstance().format(Double.parseDouble(display));
                }catch(Exception e){

                }
            }else{
                // long
                try {
                    return NumberFormat.getNumberInstance().format(Long.parseLong(display));
                }catch(Exception e){

                }
            }
        }
        return display;
    }
}
