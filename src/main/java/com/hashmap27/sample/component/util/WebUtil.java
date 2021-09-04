package com.hashmap27.sample.component.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Web 관련 헬퍼 객체
 */
@Slf4j
public class WebUtil {

    private WebUtil() { /* DO NOTHING */ }

    private static final String LOCAL_IP_V4 = "127.0.0.1";
    private static final String LOCAL_IP_V6 = "0:0:0:0:0:0:0:1";

    /** WEB Proxy 를 통한 접근인가? */
    public static boolean isProxyRequest(HttpServletRequest request) {
        String clientIp = request.getHeader("client-ip");       //L7 을 통할 경우 L7-WEB-WAS
        String proxyClientIp = request.getHeader("Proxy-Client-IP");
        String xForwardedFor = request.getHeader("X-Forwarded-For");

        return StringUtils.isNotBlank(clientIp) || StringUtils.isNotBlank(proxyClientIp) ||  StringUtils.isNotBlank(xForwardedFor);
    }

    /** LocalHost, LocalIP 를 통한 접근인가? */
    public static boolean isLocalHostRequest(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        String localAddr = request.getLocalAddr();
        //remoteAddr == localAddr : LocalIP 를 통한 접근
        //0:0:0:0:0:0:0:1 == remoteAddr : LocalHost를 통한 접근
        return StringUtils.equals(LOCAL_IP_V4, localAddr) || StringUtils.equals(LOCAL_IP_V6, remoteAddr);
    }

    public static String getClientIp(ServletRequest request) {
        // 원격 접속 IP 획득
        String ipAddress = null;
        if(request instanceof HttpServletRequest) {
            HttpServletRequest servletRequest = (HttpServletRequest) request;
            ipAddress = servletRequest.getHeader("x-forwarded-for");    // WEB 을 거쳐서 WAS 에 온 경우 Origin IP
            if(StringUtils.isBlank(ipAddress)) {
                ipAddress = servletRequest.getHeader("client-ip");
            }
            if(StringUtils.isBlank(ipAddress)) {
                ipAddress = servletRequest.getHeader("Proxy-Client-IP");
            }
            if(StringUtils.isBlank(ipAddress)) {
                ipAddress = request.getRemoteAddr();
            }
        }

        if(StringUtils.isNotBlank(ipAddress) && ipAddress.indexOf(',') >= 0) {
            ipAddress = ipAddress.substring(0, ipAddress.indexOf(','));
        }
        if(StringUtils.equals(LOCAL_IP_V6, ipAddress)) {
            ipAddress = LOCAL_IP_V4;
        }

        return ipAddress;
    }

    /** Request Header 를 Map 으로 획득 */
    public static Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValues = request.getHeader(headerName);
            headers.put(headerName, headerValues);
        }
        return headers;
    }

    /** Response Header 를 Map 으로 획득 */
    public static Map<String, String> getResponseHeaders(HttpServletResponse response) {
        Map<String, String> headers = new HashMap<>();
        Collection<String> headerNames = response.getHeaderNames();
        for(String headerName : headerNames) {
            String headerValues = response.getHeader(headerName);
            headers.put(headerName, headerValues);
        }
        return headers;
    }
}
