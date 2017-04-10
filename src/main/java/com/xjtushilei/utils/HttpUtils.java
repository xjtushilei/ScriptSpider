package com.xjtushilei.utils;

import com.xjtushilei.utils.httpclient.Constant;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.ByteArrayBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.zip.GZIPInputStream;

public class HttpUtils {
    private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    // 创建httpclient连接池
    private PoolingHttpClientConnectionManager httpClientConnectionManager = null;

    private final int maxTotalPool = 200;
    private final int maxConPerRoute = 20;
    private final int socketTimeout = 30000;
    private final int connectionRequestTimeout = 20000;
    private final int connectTimeout = 10000;


    //类初始化时，自动实例化，饿汉单例模式
    private static final HttpUtils httpUtils = new HttpUtils();

    /**
     * 单例
     *
     * @return
     */
    public static HttpUtils getInstance() {
        return httpUtils;
    }

    private HttpUtils() {
        init();
    }

    /**
     * 创建httpclient连接池，并初始化httpclient
     */
    public void init() {
        try {
            SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(null,
                    new TrustSelfSignedStrategy())
                    .build();
            HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.getDefaultHostnameVerifier();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslcontext, hostnameVerifier);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslsf)
                    .build();
            httpClientConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            // Increase max total connection to 200
            httpClientConnectionManager.setMaxTotal(maxTotalPool);
            // Increase default max connection per route to 20
            httpClientConnectionManager.setDefaultMaxPerRoute(maxConPerRoute);
            SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(socketTimeout).build();
            httpClientConnectionManager.setDefaultSocketConfig(socketConfig);
        } catch (Exception e) {

        }
    }


    /**
     * 多线程调用时，获取httpclient
     *
     * @return
     */
    public CloseableHttpClient getHttpClient() {
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(connectionRequestTimeout)
                .setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(httpClientConnectionManager).setDefaultRequestConfig(requestConfig).build();
        if (httpClientConnectionManager != null && httpClientConnectionManager.getTotalStats() != null) {
            logger.info("httpclient连接池: " + httpClientConnectionManager.getTotalStats().toString());
        }
        System.out.println(HttpClients.custom());
        return httpClient;
    }


    /**
     * get请求页面
     *
     * @param urlString
     * @return
     */
    public static String get(String urlString) {
        String src = "";
        if (null == urlString || !urlString.startsWith("http")) {//如果urlString为null或者urlString为空，或urlString非http开头，返回src空值
            return src;
        }
        //创建response
        CloseableHttpResponse response = null;
        HttpGet httpGet = null;
        urlString = urlString.trim();//防止传入的urlString首尾有空格
        //转化String url为URI,解决url中包含特殊字符的情况
        try {
            URL url = new URL(urlString);
            URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
            httpGet = new HttpGet(uri);
            //设置请求头
            httpGet.addHeader("Accept", "*/*");
            //			httpGet.addHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
            httpGet.addHeader("Connection", "keep-alive");
            httpGet.addHeader("Accept-Encoding", "gzip, deflate");

            //设置USER_AGENT
            Random random = new Random();
            int randomInt = random.nextInt(4);
            System.err.println(randomInt);

            httpGet.addHeader("User-Agent", Constant.USER_AGENT[randomInt]);
            //此处的代理暂时注释
            //			String[] proxys = Constant.HTTPCLIENT_PROXY[randomInt].split("\\s+");
            //			//添加代理
            //			HttpHost proxy = new HttpHost(proxys[0].trim(), Integer.parseInt(proxys[1].trim()), "http");
            //			RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
            //			httpGet.setConfig(config);
            //执行请求
            try {
                if (urlString.startsWith("https")) {
                    System.setProperty("jsse.enableSNIExtension", "false");
                    response = httpUtils.getHttpClient().execute(httpGet);
                } else {
                    response = httpUtils.getHttpClient().execute(httpGet);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //得到响应状态码
            int statuCode = response.getStatusLine().getStatusCode();
            //根据状态码进行逻辑处理
            switch (statuCode) {
                case 200:
                    //获得响应实体
                    HttpEntity entity = response.getEntity();
                    /**
                     * 仿浏览器获取网页编码
                     * 浏览器是先从content-type的charset（响应头信息）中获取编码，
                     * 如果获取不了，则会从meta（HTML里的代码）中获取charset的编码值
                     */
                    //第一步-->处理网页字符编码
                    String charset = null;
                    ContentType contentType = null;
                    contentType = ContentType.getOrDefault(entity);
                    Charset charsets = contentType.getCharset();
                    if (null != charsets) {
                        charset = charsets.toString();
                    } else {
                        charset = "utf-8";
                    }
                    //判断返回的数据流是否采用了gzip压缩
                    Header header = entity.getContentEncoding();
                    boolean isGzip = false;
                    if (null != header) {
                        for (HeaderElement headerElement : header.getElements()) {
                            if (headerElement.getName().equalsIgnoreCase("gzip")) {
                                isGzip = true;
                            }
                        }
                    }
                    //获得响应流
                    InputStream inputStream = entity.getContent();
                    ByteArrayBuffer buffer = new ByteArrayBuffer(4096);
                    byte[] tmp = new byte[4096];
                    int count;
                    if (isGzip) {//如果采用了Gzip压缩，则进行gizp压缩处理
                        GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
                        while ((count = gzipInputStream.read(tmp)) != -1) {
                            buffer.append(tmp, 0, count);
                        }
                    } else {//处理非gzip格式的数据
                        while ((count = inputStream.read(tmp)) != -1) {
                            buffer.append(tmp, 0, count);
                        }
                    }
                    //根据获取的字符编码转为string类型
                    src = new String(buffer.toByteArray(), charset);

                    //转化Unicode编码格式]
                    //src = Common.decodeUnicode(src);
                    //                    logger.info(src);
                    break;
                case 400:
                    logger.info("下载400错误代码，请求出现语法错误" + urlString);
                    break;
                case 403:
                    logger.info("下载403错误代码，资源不可用" + urlString);
                    break;
                case 404:
                    logger.info("下载404错误代码，无法找到指定资源地址" + urlString);
                    break;
                case 503:
                    logger.info("下载503错误代码，服务不可用" + urlString);
                    break;
                case 504:
                    logger.info("下载504错误代码，网关超时" + urlString);
                    break;
            }

        } catch (MalformedURLException e) {
            //执行URL url = new URL()的异常
            logger.error("执行URL url = new URL()的异常", e);
        } catch (URISyntaxException e) {
            //执行URI uri = new URI()的异常
            logger.error("执行URL url = new URL()的异常", e);
        } catch (ClientProtocolException e) {
            // 执行httpClient.execute(httpGet)的异常
            logger.error("执行httpClient.execute(httpGet)的异常", e);
        } catch (IOException e) {
            // 执行httpClient.execute(httpGet)的异常
            logger.error("执行httpClient.execute(httpGet)的异常", e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error("response.close()的异常", e);
                }
            }
            httpGet.abort();    //结束后关闭httpGet请求
            /**
             * httpclient的链接有线程池管理，这里不用直接关闭
             */
            //			try {//关闭连接
            //				httpClient.close();
            //			} catch (IOException e) {
            //				e.printStackTrace();
            //			}
        }

        return src;
    }


    public static void main(String[] args) throws IOException {
        String url = "https://github.com/glshi/testlive/blob/685be0f1334493ece609f6d112c5d9841242cd64/src/main/java/com/li/tools/httpclient/test/SSLClient.java";
        get(url);
        //            System.out.println(Jsoup.connect(url).get().html());
    }
}
