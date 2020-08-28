package net.ban.demo.ip.ip2region;

import net.ban.demo.ip.util.IPLocateUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


/**
 * @author ban_xz
 * @date 2020/08/28
 */
@RestController
public class IpDemo {

    @GetMapping("/ip")
    public String getIpInfo(HttpServletRequest request){
        String ipAddress = IPLocateUtils.getIpByRequest(request);
        System.out.println(ipAddress);
        return IPLocateUtils.getCityInfo(ipAddress);
    }


}
