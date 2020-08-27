package Util;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DowloadZipPackage {
/*    *//**
     * @Des 将查询到的数据转化成zip文件并下载下来   另存为
     * @Date 2020/8/21 20:39
     *//*
    @RequestMapping(value = "/sql/downloadZip")
    @ResponseBody
    public void sqlDataToFile(
            @RequestParam("sql") String execute_sql,
            @RequestParam("select_server_zone") String zoneStr,  HttpServletResponse response) throws Exception {

        //过滤execute_sql
        execute_sql = execute_sql.trim().replace("\n", "");

        List<String> sqlList = Arrays.asList(execute_sql.split(";"));
        if (StringUtils.isEmpty(zoneStr)) {
            throw new Exception("sql 为null");
        }


        String[] ids = zoneStr.trim().replace("，", ",").split(",");
        if (ids == null || ids.length == 0) {
            throw new Exception("无 id");
        }

        StringJoiner sj = new StringJoiner(",");
        for (String zone_id : ids) {
            sj.add(zone_id + "");
        }


        //从数据库获取数据
        MySqlDB db = DBMgr.Instance.getWebDB();
        List<String> tmp_unique_db_list = new ArrayList<>();
        List<Integer> executeZoneIDList = new ArrayList<>();


        String sqll = "select * from " + TB_server_zone_db_info.NAME + " where id in (%s)";
        sqll = String.format(sqll, sj.toString());

        //确定需要执行的db 唯一Key ,去除重复的,确保只执行一次
        List<TB_server_zone_db_info> zoneList = db.beanQuery(sqll, TB_server_zone_db_info.class);

        try {
            if (zoneList == null || zoneList.isEmpty()) {
                throw new Exception("无 zone data");
            }
            for (TB_server_zone_db_info zone : zoneList) {
                String unique_db_config = zone.getDb_addr() + zone.getDb_port(); //唯一KEY addr + port
                if (!tmp_unique_db_list.contains(unique_db_config)) {
                    tmp_unique_db_list.add(unique_db_config);
                    executeZoneIDList.add(zone.getId());
                }
            }
        } catch (Exception e) {
            LoggerUtil.error(" download zip eee:"+e.getMessage(), e);
        }
        finally {
            if(db != null) {
                db.closeDB();
            }
        }

        String zipName = getZipName();

        response.reset();
        response.setContentType("application/zip;charset=utf-8");
        String fileNameStr = new String(zipName.getBytes(), "UTF-8");
        response.setHeader( "Content-Disposition", "attachment;filename="+fileNameStr );


        response.setContentType("application/ms-txt.numberformat:@");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Pragma", "public");
        response.setHeader("Cache-Control", "max-age=30");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileNameStr,"UTF-8"));


        ServletOutputStream out = response.getOutputStream();

        BufferedOutputStream bos = new BufferedOutputStream(out,64*1024);
        ZipOutputStream zos = new ZipOutputStream(bos);


        //执行sql
        try {
            for (Integer zone_id : executeZoneIDList) {
                MySqlDB zone_db = DBMgr.Instance.getGameDBByZone(zone_id);

                List<GMSqlQueryResult> queryResultList = zone_db.gmExecuteQuerySqlList(sqlList);
                int file_num = 1;
                try {
                    for (int j = 0; j < queryResultList.size(); j++) {
                        GMSqlQueryResult entry = queryResultList.get(j);


                        for (int ii = 1; ii <= 2; ii++) {

                            String filename = "";
                            if (ii == 1) {
                                filename = "zone_" + zone_id + "_sql_" + file_num + ".txt";
                            } else {
                                filename = "zone_" + zone_id + "_sql_" + file_num + "_result.csv";
                            }

                            //添加.txt文件
                            if (filename.contains(".txt")) {

                                String sql = entry.getSql();
                                zos.putNextEntry(new ZipEntry(filename));
                                Writer w = new OutputStreamWriter(zos, "GBK");
                                w.write(sql);
                                w.flush();

                            } else {
                                StringBuilder total = new StringBuilder();
                                ZipEntry zipEntry = new ZipEntry(filename);
                                zos.putNextEntry(zipEntry);

                                JSONArray queryResult = entry.getQueryResult();
                                //添加.csv文件
                                for (Object _one : queryResult) {

                                    StringBuilder content = new StringBuilder();
                                    JSONObject one = (JSONObject) _one;

                                    Set<String> keySet = one.keySet();
                                    //添加表头
                                    if (total.length() < 1) {
                                        for (String str : keySet) {
                                            if (total.length() < 1) {
                                                total.append(str);
                                            } else {
                                                total.append("," + str);
                                            }
                                        }
                                        Writer w = new OutputStreamWriter(zos, "GBK");
                                        w.write(total.toString());
                                        w.write("\n");
                                        w.flush();

                                    }

                                    String[] totals = total.toString().split(",");
                                    //添加内容
                                    for (int i = 0; i < totals.length; i++) {
                                        String s = "";
                                        if (one.getString(totals[ i ]) == null) {
                                            s = "NULL";
                                        } else {
                                            s = one.getString(totals[ i ]).contains(",") ? one.getString(totals[ i ]).replace(",", "，") : one.getString(totals[ i ]);
                                        }
                                        if (content.length() < 1) {
                                            content.append(s);
                                        } else {
                                            content.append("," + s);
                                        }
                                    }
                                    Writer w = new OutputStreamWriter(zos, "GBK");
                                    w.write(content.toString());
                                    w.write("\n");
                                    w.flush();
                                }
                            }
                        }
                        file_num++;
                    }
                } catch (IOException e) {
                    LoggerUtil.error("process gm down load sql err", e);
                } finally {
                    if (zone_db != null) {
                        zone_db.closeDB();
                    }
                }
            }
        }catch (Exception ex){
            LoggerUtil.error(" down load err", ex);
        }
        finally {
            if(zos != null) {
                zos.close();
            }

            if(bos != null) {
                bos.close();
            }
        }

    }*/
    /**
     * @Des 将文件压缩成zip格式
     * @Date 2020/8/22 16:23
     */
    public static void fileToZip(String dirname, List<File> srcFiles) {
        File file = new File(dirname);
        if (!file.exists()) {
            file.mkdirs();
        }
        String zipname = getZipName();

        ZipOutputStream zos = null;
        try {
            OutputStream out = new FileOutputStream(file + "/" + zipname + ".zip");
            zos = new ZipOutputStream(out);
            for (File srcFile : srcFiles) {
                byte[] buf = new byte[2 * 1024];
                zos.putNextEntry(new ZipEntry(srcFile.getName()));
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils", e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static String getZipName() {
        //创建压缩文件名
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);
        return year + "-" + (month) + "-" + day + "_" + hour + "_" + minute + "_" + second + "_query_result.zip";
    }
}
