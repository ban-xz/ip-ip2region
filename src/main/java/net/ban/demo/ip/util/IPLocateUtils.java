package net.ban.demo.ip.util;

import org.apache.commons.io.FileUtils;
import org.apache.http.util.TextUtils;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.lionsoul.ip2region.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.reflect.Method;

public class IPLocateUtils {

    public static Logger logger = LoggerFactory.getLogger(IPLocateUtils.class);

    /**
     * @param ip
     * @return 格式 中国|0|四川省|宜宾市|移动
     */
    public static String getCityInfo(String ip) {
        String info = "";
        String path = IPLocateUtils.class.getResource("/ip2region.db").getPath();
        File file = new File(path);
        try {
            if (file.exists() == false) {
                String tmpDir = System.getProperties().getProperty("java.io.tmpdir");
                path = tmpDir + "ip.db";
                file = new File(path);
                FileUtils.copyInputStreamToFile(IPLocateUtils.class.getClassLoader().getResourceAsStream("classpath:ip2region.db"), file);
            }
            if (Util.isIpAddress(ip) == false) {
                logger.error(" Invalid ip address");
            }
            DbConfig config = new DbConfig();
            DbSearcher searcher = new DbSearcher(config, path);
            Method method = searcher.getClass().getMethod("btreeSearch", String.class);//查询算法B-tree
            DataBlock dataBlock = (DataBlock) method.invoke(searcher, ip);
            if (dataBlock == null) {
                logger.error(" Invalid ip address");
            }
            info = dataBlock.getRegion();
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        if (!TextUtils.isBlank(info)) {
            return info;
        }
        return null;
    }


    public static boolean isComefromCDN(HttpServletRequest request) {
        String host = request.getHeader("host");
        return host.contains("www.189.cn") || host.contains("shouji.189.cn") || host.contains(
                "image2.chinatelecom-ec.com") || host.contains(
                "image1.chinatelecom-ec.com");
    }

    public static String getIpByRequest(HttpServletRequest request){
        String clientIp = request.getHeader("x-forwarded-for");
        if (clientIp == null || clientIp.length() == 0 || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }
        String[] clientIps = clientIp.split(",");
        if (clientIps.length <= 1){
            clientIp.trim();
        }
        // 判断是否来自CDN
        if (IPLocateUtils.isComefromCDN(request)) {
            if (clientIps.length >= 2){
                clientIps[clientIps.length - 2].trim();
            }
        }
        clientIp = clientIps[clientIps.length - 1].trim();
        return clientIp;
    }

}
