package tlsversiontest;

/**
 * Sample client to test the behavior of iConsole http client for event posting functionality.
 * Uses the same libraries as iConsole: com.zycus.integration.api.v1.module.event.service.impl.EventPostingServiceImpl
 * 
 * Source code based on:
 * http://192.168.1.251/public/svn/zygrate/branches/iConsole/R17.10.1.0_P8/ZygrateAPI/rest/module/com/zycus/integration/api/v1/module/event/service/impl/EventPostingServiceImpl.java  
 */
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

public class TLSChecker {

	public static void main(String[] args) throws Throwable, NoSuchAlgorithmException {
		//System.setProperty("javax.net.debug", "all");
		
		String URL = "https://";
		URL = "https://192.168.3.16:8443";//Supports ONLY TLS1.2
		URL = "https://192.168.3.16:8444";//Supports TLS1 and TLS1.2
		URL = "https://192.168.3.16:8445";//Supports ONLY TLS1
		
		HttpClient v1Client = createCertByPassClient("TLS");
		HttpClient v11Client = createCertByPassClient("TLSv1.1");
		HttpClient v2Client = createCertByPassClient("TLSv1.2");
		HttpPost httpPost = new HttpPost(URL);
		HttpResponse httpResponse = null;
		
		httpResponse = execute(v1Client,httpPost);
		System.out.println("TLS/TLS1.0 Client for URL:"+httpPost.getURI().toString() + " " + (null==httpResponse ? "Handshake failed" : httpResponse.getStatusLine().toString()+" "+EntityUtils.toString(httpResponse.getEntity())));

		httpResponse = execute(v11Client,httpPost);
		System.out.println("TLSv1.1 Client for URL:"+httpPost.getURI().toString() + " " + (null==httpResponse ? "Handshake failed" : httpResponse.getStatusLine().toString()+" "+EntityUtils.toString(httpResponse.getEntity())));
		httpResponse = execute(v2Client,httpPost);
		System.out.println("TLSv1.2 Client for URL:"+httpPost.getURI().toString() + " " + (null==httpResponse ? "Handshake failed" : httpResponse.getStatusLine().toString()+" "+EntityUtils.toString(httpResponse.getEntity())));
	}
	
	private static HttpResponse execute(HttpClient client, HttpPost httpPost) {
		HttpResponse httpResponse = null;
		try {
			httpResponse = client.execute(httpPost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		//System.out.println("URL:"+httpPost.getURI().toString() + " " + (null==httpResponse ? "Handshake failed" : httpResponse.getStatusLine().toString()));
		return httpResponse;
	}
	
	private static HttpClient createCertByPassClient(String tlsVersion) throws NoSuchAlgorithmException, KeyManagementException
	{
		SSLContext sslContext = SSLContext.getInstance(System.getProperty("TLS_VERSION", tlsVersion));
		// set up a TrustManager that trusts everything

		sslContext.init(null, new TrustManager[] { new X509TrustManager()
		{
			public X509Certificate[] getAcceptedIssuers()
			{
				// Allow all issuers.
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType)
			{
				// Allow all clients to connect. Not Product Ready.
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType)
			{
				// Allow all servers to connect. Not Product Ready.
			}
		} }, new SecureRandom());
		HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
		SSLSocketFactory sf = new SSLSocketFactory(sslContext);
		sf.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
		Scheme httpsScheme = new Scheme("https", 443, sf);
		Scheme httpScheme = new Scheme("http", 80, sf);
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(httpsScheme);
		schemeRegistry.register(httpScheme);
		
		/**
		 * Added for local apache testing
		 */
		schemeRegistry.register(new Scheme("https", 8443, sf));
		schemeRegistry.register(new Scheme("https", 8444, sf));
		schemeRegistry.register(new Scheme("https", 8445, sf));
		

		ClientConnectionManager cm = new SingleClientConnManager(schemeRegistry);
		HttpClient httpClient = new DefaultHttpClient(cm);
		//HttpConnectionParams.setSoTimeout(httpClient.getParams(), 20);//Releasing threads so that ActiveMQ doesn't pause processing messages.
		return httpClient;
	}
	
	private static HttpPost getChangiPost() throws Exception {
		String httpURL = "https://";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("cache-control", "no-cache");
		headers.put("content-type", "application/json");
		
		Map<String, String> params = null;
		String postData = "{ \"tenantId\": \"dde505a0-1111-4b9a-ac6a-73e51533cd4d\", \"eventId\": \"IDocument_7339_EVENT_PUBLISH_1521038874592\", \"entityId\": \"7339\", \"entityType\": \"SOURCING_EVENT\", \"eventType\": \"EVENT_PUBLISH\", \"version\": -1, \"createdOn\": \"Mar 14, 2018 8:17:54 PM\", \"deliveryStatus\": -1, \"sourceSystem\": \"ISOURCE\", \"modifiedOn\": \"Mar 14, 2018 8:17:54 PM\" }";
		return newPost(httpURL, headers, params, postData);
	}
	
	public static HttpPost newPost(String httpURL, Map<String, String> headers, Map<String, String> params, String postData) throws Exception
			{
			  if (params != null)
			  {
			    URIBuilder builder = new URIBuilder();
			    builder.setPath(httpURL);
			    
			    for (Map.Entry<String, String> entry : params.entrySet())
			    {
			      builder.addParameter((String)entry.getKey(), (String)entry.getValue());
			    }
			    
			    try
			    {
			      httpURL = builder.build().toString();
			    }
			    catch (Exception e)
			    {
			      throw new Exception("Invalid URL or HTTP parameters", e);
			    }
			  }
			  
			  HttpPost httpPost = new HttpPost(httpURL);
			  
			  if (postData != null)
			  {
			    try
			    {
			      httpPost.setEntity(new StringEntity(postData));
			    }
			    catch (UnsupportedEncodingException e)
			    {
			      throw new Exception("Invalid encoding in post data", e);
			    }
			  }
			  
			  if (headers != null)
			  {
			    for (Map.Entry<String, String> entry : headers.entrySet())
			    {
			      httpPost.addHeader((String)entry.getKey(), (String)entry.getValue());
			    }
			  }
			  
			  return httpPost;
			}
	
}
