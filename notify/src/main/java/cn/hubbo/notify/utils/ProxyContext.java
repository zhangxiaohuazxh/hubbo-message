package cn.hubbo.notify.utils;


import org.apache.http.HttpHost;

public class ProxyContext {

	private static final ThreadLocal<HttpHost> PROXY_HOLDER = new ThreadLocal<>();

	public static void setProxy(String host, int port) {
		PROXY_HOLDER.set(new HttpHost(host, port));
	}

	public static HttpHost getProxy() {
		return PROXY_HOLDER.get();
	}

	public static void clear() {
		PROXY_HOLDER.remove();
	}

}
