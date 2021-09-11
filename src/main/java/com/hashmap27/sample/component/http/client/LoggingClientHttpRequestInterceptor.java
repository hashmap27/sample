package com.hashmap27.sample.component.http.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.http.client.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Spring RestTemplate Logging 을 위한 ClientHttpRequestInterceptor 구현
 *
 * how to use: LoggingClientHttpRequestInterceptor.applyToTemplate(restTemplate) 으로 적용
 * Use1: applyToBufferingClientHttpRequestFactory 로 사전에 request, response 에 버퍼링 factory 설정
 * Use2: applyToRestTemplate 으로 적용 시 useBufferedResponseWhenOutput 을 true 로 설정하여 출력하는 순간에 버퍼링 적용처리
 *
 * @see https://gist.github.com/jkuipers/24ffbf8a5ba26c0177629e9aba492bfa
 */
@Slf4j
public class LoggingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    private static final String RESPONSE_WRAPPER_CLASS = "org.springframework.http.client.BufferingClientHttpResponseWrapper";

    /**
     * restTemplate에 LoggingClientHttpRequestInterceptor를 적용
     * @param useBufferedResponseWhenOutput 출력할때 Response버퍼링이 적용되지 않은 경우 적용하여 출력할 것인가?
     */
    public static void applyToRestTemplate(RestTemplate restTemplate, boolean useBufferedResponseWhenOutput) {
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        interceptors.add(new LoggingClientHttpRequestInterceptor(useBufferedResponseWhenOutput));
        log.info("## {} apply to restTemplate", LoggingClientHttpRequestInterceptor.class.getSimpleName());
        restTemplate.setInterceptors(interceptors);
    }

    /**
     * restTemplate에 BufferingClientHttpRequestFactory를 적용
     * @param restTemplate 적용대상 {@link RestTemplate}
     * @param optionalRequestFactory {@link BufferingClientHttpRequestFactory}를 적용하면서 내부적으로 사용할 {@link ClientHttpRequestFactory} (값이 없으면 {@link SimpleClientHttpRequestFactory}를 사용)
     */
    public static void applyToBufferingClientHttpRequestFactory(RestTemplate restTemplate, ClientHttpRequestFactory optionalRequestFactory) {
        ClientHttpRequestFactory requestFactory = (optionalRequestFactory == null) ? new SimpleClientHttpRequestFactory(): optionalRequestFactory;
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(requestFactory));
    }

    ///////////////////////////////////////////////////////////////////////////
    /** Response버퍼링을 설정하지 않은 경우에 대해 경고처리 했는지 여부 */
    private volatile boolean loggedMissingResponseBuffering = false;

    /** 출력할때 Response버퍼링이 적용되지 않은 경우 적용하여 출력할 것인가 */
    private boolean useBufferedResponseWhenOutput;

    private Class<?> responseWrapperClass;
    private Constructor<?> responseWrapperConstructor;

    /**
     * 생성자
     * @param useBufferedResponseWhenOutput 출력할때 Response버퍼링이 적용되지 않은 경우 적용하여 출력할 것인가?
     */
    public LoggingClientHttpRequestInterceptor(boolean useBufferedResponseWhenOutput) {
        this.useBufferedResponseWhenOutput = useBufferedResponseWhenOutput;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] requestBody, ClientHttpRequestExecution execution) throws IOException {
        logRequest(request, requestBody);
        ClientHttpResponse response = execution.execute(request, requestBody);
        return logResponse(request, requestBody, response);
    }

    /**
     * 로깅 메타데이터를 기록 (하위클래스에서 정보를 받아서 쓰려는 경우)
     * @param key 메타데이터 키
     * @param value 메타데이터 값
     */
    public void putMetadata(HttpRequest request, String namespace, String key, Object value) { /**/ }

    /**
     * 요청로그 출력
     * @param request 요청객체
     * @param requestBody 요청Body
     */
    private void logRequest(HttpRequest request, byte[] requestBody) {
        try {
            if(log.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder();
                //URL
                logRequestUrl(request, sb);
                //HEADER
                logRequestHeaders(request, sb);
                //BODY
                logRequestBody(request, requestBody, sb);
                log.debug(sb.toString());
            }
        } catch (Exception e) {
            log.warn("logRequest FAILED. {} - {}", e.getClass().getName(), e.getMessage(), e);
        }
    }

    /**
     * 요청로그 URL 출력
     * @param request 요청객체
     * @param sb 출력대상 StringBuilder
     */
    private void logRequestUrl(HttpRequest request, StringBuilder sb) {
        HttpMethod method = request.getMethod();
        URI uri = request.getURI();
        sb.append("\r\n>> ").append(method).append(" ").append(uri);
        putMetadata(request, "request", "method", method);
        putMetadata(request, "request", "url", uri);
    }
    /**
     * 요청로그 Header 출력
     * @param request 요청객체
     * @param sb 출력대상 StringBuilder
     */
    private void logRequestHeaders(HttpRequest request, StringBuilder sb) {
        Map<String, String> headers = request.getHeaders().toSingleValueMap();
        Set<Map.Entry<String, String>> entrySet = headers.entrySet();
        if(!CollectionUtils.isEmpty(entrySet)) {
            sb.append("\r\n>> request.headers: ").append(headers);
            putMetadata(request, "request", "headers", headers);
        }
    }
    /**
     * 요청로그 Body 출력
     * @param request 요청객체
     * @param requestBody 요청Body
     * @param sb 출력대상 StringBuilder
     */
    private void logRequestBody(HttpRequest request, byte[] requestBody, StringBuilder sb) {
        if (requestBody.length > 0) {
            if(hasTextBody(request.getHeaders())) {
                String requestBodyText = new String(requestBody, determineCharset(request.getHeaders()));
                sb.append("\r\n>> request.body: ").append(requestBodyText);
                putMetadata(request, "request", "body", requestBodyText);
            } else {
                sb.append("\r\n>> request.body: [BINARY]");
                putMetadata(request, "request", "body", "[BINARY]");
            }
        }
    }

    /**
     * 요청 로그 출력
     * @param request 요청객체
     * @param requestBody 요청Body
     * @param responseParameter 응답객체
     * @return 반환할 응답객체
     */
    private ClientHttpResponse logResponse(HttpRequest request, byte[] requestBody, ClientHttpResponse responseParameter) {
        ClientHttpResponse response = responseParameter;
        try {
            if(log.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder();
                //Request URL
                logRequestUrl(request, sb);
                //Request HEADER
                logRequestHeaders(request, sb);
                //Request BODY
                logRequestBody(request, requestBody, sb);
                //Response STATUS
                logResponseStatus(request, response, sb);
                //Response HEADER
                logResponseHeaders(request, response, sb);
                //Response BODY
                response = logResponseBody(request, response, sb);
                log.debug(sb.toString());
            }
        } catch (Exception e) {
            log.warn("logResponse FAILED. {} - {}", e.getClass().getName(), e.getMessage(), e);
        }
        return response;
    }

    /**
     * 응답객체 상태 출력
     * @param response 응답객체
     * @param sb 출력대상 StringBuilder
     */
    private void logResponseStatus(HttpRequest request, ClientHttpResponse response, StringBuilder sb) {
        try {
            int rawStatusCode = response.getRawStatusCode();
            String statusText = StringUtils.defaultString(response.getStatusText(), "");
            sb.append("\r\n<< response.status: ").append(rawStatusCode).append(" ").append(statusText);
            putMetadata(request, "response", "status", rawStatusCode);
            putMetadata(request, "response", "statusText", statusText);
        } catch (IOException e) {
            log.warn("logResponseStatus FAILED. {} - {}", e.getClass().getName(), e.getMessage(), e);
        }
    }

    /**
     * 응답객체 헤더 출력
     * @param response 응답객체
     * @param sb 출력대상 StringBuilder
     */
    private void logResponseHeaders(HttpRequest request, ClientHttpResponse response, StringBuilder sb) {
        Map<String, String> headers = response.getHeaders().toSingleValueMap();
        Set<Map.Entry<String, String>> entrySet = headers.entrySet();
        if(!CollectionUtils.isEmpty(entrySet)) {
            sb.append("\r\n<< response.headers: ").append(headers);
            putMetadata(request, "response", "headers", headers);
        }
    }

    /**
     * 응답객체 Body 출력
     * @param responseParameter 응답객체
     * @param sb 출력대상 StringBuilder
     * @return 출력후 ClientHttpResponse를 반환 (경우에 따라 버퍼링 Wrapper를 반환)
     */
    private ClientHttpResponse logResponseBody(HttpRequest request, ClientHttpResponse responseParameter, StringBuilder sb) {
        ClientHttpResponse response = responseParameter;
        try {
            HttpHeaders responseHeaders = response.getHeaders();
            long contentLength = responseHeaders.getContentLength();
            if (contentLength != 0) {
                //응답BODY출력에 성공했는가?
                boolean outputSuccess = false;
                if (hasTextBody(responseHeaders)) {
                    //응답BODY을 출력할 것인가?
                    boolean outputResponseBody = false;
                    //응답을 출력해야 하는 순간에 버퍼링 처리
                    if(useBufferedResponseWhenOutput) {
                        ClientHttpResponse responseBuffered = ensureBuffered(response);
                        if(responseBuffered != null) {
                            response = responseBuffered;
                            outputResponseBody = true; //출력시점 버퍼링 성공
                        }
                    } else if(isResponseBuffered(response)) {
                        outputResponseBody = true; //버퍼링된 응답객체
                    }
                    if(outputResponseBody) {
                        String bodyText;
                        try {
                            bodyText = StreamUtils.copyToString(response.getBody(), determineCharset(responseHeaders));
                            sb.append("\r\n<< response.body: ").append(bodyText);
                            putMetadata(request, "response", "body", bodyText);
                            outputSuccess = true;
                        } catch (IOException e) {
                            log.warn("logResponseBody FAILED. {} - {}", e.getClass().getName(), e.getMessage(), e);
                            putMetadata(request, "response", "body", "logResponseBody FAILED. " + e.getClass().getName() + " - " + e.getMessage());
                        }
                    }
                }
                if(!outputSuccess) {
                    //메타정보만 출력
                    if (contentLength == -1) {
                        sb.append("\r\n<< Content-Length: UNKNOWN");
                    } else {
                        sb.append("\r\n<< Content-Length: ").append(contentLength);
                    }
                    putMetadata(request, "response", "contentLength", contentLength);

                    MediaType contentType = responseHeaders.getContentType();
                    if (contentType != null) {
                        sb.append("\r\n<< Content-Type: ").append(contentType);
                    } else {
                        sb.append("\r\n<< Content-Type: [null]");
                    }
                    putMetadata(request, "response", "contentType", contentType);
                }
            }
        } catch (Exception e) {
            log.warn("logResponseBody FAILED. {} - {}", e.getClass().getName(), e.getMessage(), e);
            putMetadata(request, "response", "EXCEPTION", "logResponseBody FAILED. " + e.getClass().getName() + " - " + e.getMessage());
        }
        return response;
    }

    /**
     * ContentType이 text&#47;*, *&#47;json, *&#47;xml 이면 true
     * @param headers contentType을 검사할 HttpHeaders 객체
     */
    protected boolean hasTextBody(HttpHeaders headers) {
        MediaType contentType = headers.getContentType();
        if (contentType != null) {
            String subtype = contentType.getSubtype();
            return "text".equals(contentType.getType()) || "xml".equals(subtype) || "json".equals(subtype);
        }
        return false;
    }

    /**
     * ContentType의 charset 획득 (기본은 UTF-8)
     * @param headers contentType을 검사할 HttpHeaders 객체
     */
    protected Charset determineCharset(HttpHeaders headers) {
        MediaType contentType = headers.getContentType();
        if (contentType != null) {
            try {
                Charset charSet = contentType.getCharset();
                if (charSet != null) {
                    return charSet;
                }
            } catch (UnsupportedCharsetException e) {
                log.warn("determineCharset FAILED. contentType:{} - {} - {}", contentType, e.getClass().getName(), e.getMessage(), e);
            }
        }
        return StandardCharsets.UTF_8;
    }

    /**
     * 응답객체가 BufferingClientHttpResponseWrapper로 버퍼링 되었는지 확인. 버퍼링 한 경우에만 Response Body 출력가능
     * @param response
     * @return
     */
    protected boolean isResponseBuffered(ClientHttpResponse response) {
        // class is non-public, so we check by name
        boolean buffered = RESPONSE_WRAPPER_CLASS.equals(response.getClass().getName());
        if (!buffered && !loggedMissingResponseBuffering) {
            log.warn("!!! CANNOT LOG HTTP RESPONSE BODY!!! Configuration the RestTemplate with a BufferingClientHttpRequestFactory required!!!");
            loggedMissingResponseBuffering = true;
        }
        return buffered;
    }

    /**
     * 주어진 응답객체가
     * @param responseParameter
     * @return
     */
    private ClientHttpResponse ensureBuffered(ClientHttpResponse responseParameter) {
        ClientHttpResponse response = responseParameter;
        try {
            if (this.responseWrapperClass == null) {
                this.responseWrapperClass = Class.forName(RESPONSE_WRAPPER_CLASS, false, ClientHttpResponse.class.getClassLoader());
            }
            if (!this.responseWrapperClass.isInstance(response)) {
                if (this.responseWrapperConstructor == null) {
                    this.responseWrapperConstructor = this.responseWrapperClass.getDeclaredConstructor(ClientHttpResponse.class);
                    this.responseWrapperConstructor.setAccessible(true);
                }
                if(!this.responseWrapperClass.equals(response.getClass())) {
                    response = (ClientHttpResponse)this.responseWrapperConstructor.newInstance(response);
                }
            }
            return response;
        } catch (Exception e) {
            log.warn("ensureBuffered FAILED. creating {} instance. {} - {}", RESPONSE_WRAPPER_CLASS, e.getClass().getName(), e.getMessage(), e);
            return null;
        }
    }
}
