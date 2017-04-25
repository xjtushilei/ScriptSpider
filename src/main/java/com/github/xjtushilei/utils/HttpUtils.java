package com.github.xjtushilei.utils;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    private static String USER_AGENT[] = {
            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; AcooBrowser; .NET CLR 1.1.4322; .NET CLR 2.0.50727)",
            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; Acoo Browser; SLCC1; .NET CLR 2.0.50727; Media Center PC 5.0; .NET CLR 3.0.04506)",
            "Mozilla/4.0 (compatible; MSIE 7.0; AOL 9.5; AOLBuild 4337.35; Windows NT 5.1; .NET CLR 1.1.4322; .NET CLR 2.0.50727)",
            "Mozilla/5.0 (Windows; U; MSIE 9.0; Windows NT 9.0; en-US)",
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET CLR 2.0.50727; Media Center PC 6.0)",
            "Mozilla/5.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET CLR 1.0.3705; .NET CLR 1.1.4322)",
            "Mozilla/4.0 (compatible; MSIE 7.0b; Windows NT 5.2; .NET CLR 1.1.4322; .NET CLR 2.0.50727; InfoPath.2; .NET CLR 3.0.04506.30)",
            "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN) AppleWebKit/523.15 (KHTML, like Gecko, Safari/419.3) Arora/0.3 (Change: 287 c9dfb30)",
            "Mozilla/5.0 (X11; U; Linux; en-US) AppleWebKit/527+ (KHTML, like Gecko, Safari/419.3) Arora/0.6",
            "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.2pre) Gecko/20070215 K-Ninja/2.1.1",
            "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9) Gecko/20080705 Firefox/3.0 Kapiko/3.0",
            "Mozilla/5.0 (X11; Linux i686; U;) Gecko/20070322 Kazehakase/0.4.5",
            "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.8) Gecko Fedora/1.9.0.8-1.fc10 Kazehakase/0.5.6",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/535.20 (KHTML, like Gecko) Chrome/19.0.1036.7 Safari/535.20",
            "Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; fr) Presto/2.9.168 Version/11.52",
    };

    //类初始化时，自动实例化，饿汉单例模式
    private static final HttpUtils httpUtils = new HttpUtils();

    /**
     * 单例
     *
     * @return 单例
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
     * @return httpclient实例
     */
    public CloseableHttpClient getHttpClient() {
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(connectionRequestTimeout)
                .setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(httpClientConnectionManager).setDefaultRequestConfig(requestConfig).build();
        if (httpClientConnectionManager != null && httpClientConnectionManager.getTotalStats() != null) {
            //            logger.info("httpclient连接池: " + httpClientConnectionManager.getTotalStats().toString());
        }
        return httpClient;
    }


    /**
     * get请求页面
     *
     * @param urlString
     * @return 获得的页面
     */
    public String get(String urlString) {
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

            httpGet.addHeader("User-Agent", USER_AGENT[randomInt]);
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
                    String charset = null;
                    ContentType contentType = null;
                    contentType = ContentType.getOrDefault(entity);
                    Charset charsets = contentType.getCharset();
                    src = new String(buffer.toByteArray());
                    if (null != charsets) {
                        charset = charsets.toString();
                    } else {
                        //发现httpclient带的功能有问题，这里自己又写了一下。
                        Pattern pattern = Pattern.compile("<head>([\\s\\S]*?)<meta([\\s\\S]*?)charset\\s*=(\")?(.*?)\"");
                        Matcher matcher = pattern.matcher(src.toLowerCase());
                        if (matcher.find()) {
                            charset = matcher.group(4);
                        } else {
                            charset = "utf-8";
                        }
                    }
                    src = new String(buffer.toByteArray(), charset);
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
        }

        return src;
    }


    public static void main(String[] args) throws IOException {
        String url = "https://www.jd.com/";
        System.out.println(getInstance().get(url));
        //            System.out.println(Jsoup.connect(url).get().html());
    }
}
