package com.piggy.bank.piggybankgroovy;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.lang.JoseException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProvisionService {
	public static final String KEY_ENC_ALGO = "RSA1_5";
	public static final String CONTENT_ENC_ALGO = "A128CBC-HS256";
	public static final String COF_TOKEN_ID = "STIDDNfecf9382d19244dfaef2dd2d48c9da50";
	public static final String fileName = "cof.txt";

	public static void main(String a[]) throws IOException, CertificateException, JoseException {
		ProvisionService service = new ProvisionService();
		String requestId = "eyJhbGciOiJSU0ExXzUiLCJlbmMiOiJBMTI4Q0JDLUhTMjU2Iiwia2lkIjoiQ3Q2ck9aLXJkSEtkSFM1Ylg1Q3dUYW9rU2w1d0dfcXh2VHdwQ1dQYWhXQSJ9.ev6bVDaf-LgvHFe2Q9M3dttgauwbtkKaBvHr3eFhBrMGdNifJdGEL5Qa2yrcpdXjWG28oKeM5hlRNioEOxR8n2MZgs3GNV2JBs0c_aHmuDVQh12y5aO4p_ZtO-065ELIchDHqkrRmhchVvSX9VFCbJ9sIJ8PWIF6B_4UwYTf3xLV0iHEgmiq3xexSKD8w4GKxLyFpyWzCpL6Q1pjbEeG0shckm168QnclBMCiE-zVut-1Q1qjt1hLjC6oe95Qg0DQ74bzJeKUUr2tqodwOU-rXl7a4wvpdgO5YLqw9CWwlBjmV-K0CStArNpdYN34FPQ66ujG6F8k8mPtmtgOgrBPg.Me7Ix1NtubzbC2Od0YEX8Q.oSPw9_Frkw9LDxlDT_bcuMtb6z2tcoP-J84ihAbo4f-WDhPK0OsGd2SijZ1G8EG71bA68A1NdvohaNVvm5BpCYrZ_wKuTYEMIc-tiYJ-jAK4EyMs7R3vKg0zyBI3iHMFmlZ8dRgReTYQ6NieJ6SzXDwj7YcTj_vXVbfz6XMZrz5BntpUavERkc6GGIVLUpZAZz8aveDu-KMFegeSULAHc7URbuapSJVRzxEQQUjhLWUYmRKz5fPcswYUQ5IoPzer.cGGSbX_hU5P5NzQuVhxAmg";
		service.getCOFResource();
		service.getCOFStatus();
	}

	public Map<String, String> getToken() throws IOException {
		StringBuilder urlBuilder = new StringBuilder(
				"https://apis.discover.com/auth/oauth/v2/token?grant_type=client_credentials&client_id=l7xx755d65f582c84dd98db0cc1bd77cc631&client_secret=815ea410500b4c8aaf441683a727c3d7&scope=COF");
		URL url = new URL(urlBuilder.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setDoOutput(true);

		System.out.println("Response code: " + conn.getResponseCode());
		BufferedReader rd;
		if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} else {
			rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		}
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();
		conn.disconnect();
		System.out.println(sb.toString());
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> jsonMap = mapper.readValue(sb.toString(), new TypeReference<Map<String, String>>() {
		});
		System.out.println(jsonMap.get("access_token"));
		return jsonMap;
	}

	public String getCOFResource() throws IOException, CertificateException, JoseException {
		String accessToken = getAccessToken();
		System.out.println(accessToken);
		StringBuilder urlBuilder = new StringBuilder("https://api.discover.com/nws/nwp/cof/v2/account/provision");
		URL url = new URL(urlBuilder.toString());
		//Submit Provision
		HttpURLConnection conn = (HttpURLConnection) url.openConnection(); 
		conn.setRequestMethod("POST");
		conn.setRequestProperty("x-dfs-c-app-cert", "dfsexxRX7h7gba1dCtZzA-oSZUt9VN3lFysmJkX5FPobX4Jbg");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Cache-Control", "no-store");
		conn.setRequestProperty("x-dfs-api-plan", "NWS-COF-Sandbox");
		conn.setRequestProperty("Authorization", "Bearer " + accessToken);
		conn.setRequestProperty("Content-Type", "application/json");
		byte[] body = ("{       \"requestHeader\": {           \"requestId\": \"6ffe2e0f-7934-442c-a229-601ba1dd2a2c\",               \"sessionId\": \"a60977f6-6f4f-4880-acd0-5f5801e92580\",               \"programId\": \"8020\",                 \"userContext\": {                         \"walletId\": \"036c8ed3-2b3a-487e-943a-6eebd5b380ab\"                 }         },          \"accountProvisionRequest\": {           \"secureContext\": {                     \"encryptedContent\":"
				+ " \"" + getEncryptedContext()
				+ "\"                },                  \"deviceContext\": {                     \"deviceType\": \"6\"                    },                  \"userProvisionContext\": {                        \"emailAddress\": \"sometbody@acme.com\",                      \"emailAddressAge\": \"30\",                       \"hashedEmailAddress\": \"dffdgdfr43fr4o4o4fo4fo4\"            },                  \"riskContext\": {                       \"accountRisk\": \"4\"                   }         }}")
						.getBytes();
		conn.setFixedLengthStreamingMode(body.length);
		conn.setDoOutput(true);

		OutputStream out = conn.getOutputStream();
		out.write(body);
		System.out.println("Response code: " + conn.getResponseCode());
		BufferedReader rd;
		if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} else {
			rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		}
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();
		conn.disconnect();
		String responseToken = sb.toString();
		System.out.println(responseToken);
		// BufferedReader br = new BufferedReader(new FileReader("/cof.txt"));
		// try {
		// StringBuilder buffer = new StringBuilder();
		// String cof = br.readLine();
		//
		// while (cof != null) {
		// buffer.append(line);
		// buffer.append(System.lineSeparator());
		// line = br.readLine();
		// }
		// String cofToken = buffer.toString();
		// System.out.println(cofToken);
		// } finally {
		// br.close();
		// }
		return responseToken;
	}

	private String getAccessToken() throws IOException {
		Map<String, String> tokenMap = getToken();
		String accessToken = tokenMap.get("access_token");
		return accessToken;
	}

	public String getEncryptedContext() throws CertificateException, JoseException {
		CertificateFactory fac = CertificateFactory.getInstance("X509");
		String certObject = "MIIFYTCCBEmgAwIBAgIQBbe8/4b30zncItJIyNBsSjANBgkqhkiG9w0BAQsFADBNMQswCQYDVQQGEwJVUzEVMBMGA1UEChMMRGlnaUNlcnQgSW5jMScwJQYDVQQDEx5EaWdpQ2VydCBTSEEyIFNlY3VyZSBTZXJ2ZXIgQ0EwHhcNMTcwOTIwMDAwMDAwWhcNMTkwOTI1MTIwMDAwWjCBmzELMAkGA1UEBhMCVVMxETAPBgNVBAgTCElsbGlub2lzMRMwEQYDVQQHEwpSaXZlcndvb2RzMRkwFwYDVQQKExBERlMgU2VydmljZXMgTExDMRkwFwYDVQQLExBQYXltZW50IFNlcnZpY2VzMS4wLAYDVQQDDCVERFhfSldFX1BSRVBST0QuZGlzY292ZXJmaW5hbmNpYWwuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwYY1cHydBfrGWHQwr0WUK2SC+nKTm2RKDV5lz5W/oMQYsSuu34rnY+e1+i//qpnshL98brjrcbnsVIaQBZH6P6OmlP5w50Ata9aWbUDtoMomZ23dhK5bxwSqYl17rwiESY9ADWFAW0WZVMc02hUR9GOIpr2wbzzZeQfUaoN5rctT4NfZXcQeBSWzfPp4Bg7BttBd4L9AGaVFQ3/lGQRAtcK4gXztfHXUr0ADUAnEpnfilBZ4AO6PeWOeXx7jF8m0SpNsPsxhtdzWW+UZKU2+bEggAQncTZkIBZ/g05E/QdsAErjkHW0Qx4TOMw9GKT4CAti8q9i5jQXLIRIanXMCpQIDAQABo4IB7DCCAegwHwYDVR0jBBgwFoAUD4BhHIIxYdUvKOeNRji0LOHG2eIwHQYDVR0OBBYEFPS3340cGmbE5gFNLRfyGp3a0BEEMDAGA1UdEQQpMCeCJUREWF9KV0VfUFJFUFJPRC5kaXNjb3ZlcmZpbmFuY2lhbC5jb20wDgYDVR0PAQH/BAQDAgWgMB0GA1UdJQQWMBQGCCsGAQUFBwMBBggrBgEFBQcDAjBrBgNVHR8EZDBiMC+gLaArhilodHRwOi8vY3JsMy5kaWdpY2VydC5jb20vc3NjYS1zaGEyLWcxLmNybDAvoC2gK4YpaHR0cDovL2NybDQuZGlnaWNlcnQuY29tL3NzY2Etc2hhMi1nMS5jcmwwTAYDVR0gBEUwQzA3BglghkgBhv1sAQEwKjAoBggrBgEFBQcCARYcaHR0cHM6Ly93d3cuZGlnaWNlcnQuY29tL0NQUzAIBgZngQwBAgIwfAYIKwYBBQUHAQEEcDBuMCQGCCsGAQUFBzABhhhodHRwOi8vb2NzcC5kaWdpY2VydC5jb20wRgYIKwYBBQUHMAKGOmh0dHA6Ly9jYWNlcnRzLmRpZ2ljZXJ0LmNvbS9EaWdpQ2VydFNIQTJTZWN1cmVTZXJ2ZXJDQS5jcnQwDAYDVR0TAQH/BAIwADANBgkqhkiG9w0BAQsFAAOCAQEAtpF6tWM3ntr4zL5LEk3dBz2Rrzv0Lru+QmDfETA7HFAStV7xhXwncWmDqdS17zU4bm4AWmGHMXvPP77ANOfb8ohOM8aZ1i6zmWp7g19+p2aSo1JInVaTFfY/gD/HgDYo4lZKuSRsXPC7pQM2BOV2/G5tARFEhhAzPgVCIJHSzvA85boaXxjn1HB7U2FarMYbgaxDIlWYbKFSMFp3jNqfM3C+6tuVinq7hEeFPbRpDNy99q3aDFOLAWY8NvqQmdnDK1NsKi+IvEO6POscDDvlrLAN+igx+kES0LdJ/Pdhevx2TkmeDwgdxp3K9uP62hIRxu3y3viqGCB+P+RRAJmbQA==";

		ByteArrayInputStream in = new ByteArrayInputStream(Base64.decodeBase64(certObject));
		System.out.println(in.toString());
		X509Certificate cert = (X509Certificate) fac.generateCertificate(in);
		RSAPublicKey publicKey = (RSAPublicKey) cert.getPublicKey();
		System.out.println("publicKey - " + publicKey);
		String message = "{ \"pan\": \"6011000010048738\", \"expDate\": \"1218\",  \"cardHolderName\": \"DareDevils\", \"billingAddr\": \"1310 Velley Lake Dr, APT 123\", \"billingZip\": \"60195\" ,\"cid\": \"123\", \"source\": \"on-file\"}";
		JsonWebEncryption jweForEncrypt = new JsonWebEncryption();
		jweForEncrypt.setPlaintext(message);
		jweForEncrypt.setAlgorithmHeaderValue(KEY_ENC_ALGO);
		jweForEncrypt.setEncryptionMethodHeaderParameter(CONTENT_ENC_ALGO);
		jweForEncrypt.setKey(publicKey);
		jweForEncrypt.setHeader("kid", RSAUtil.getPublicKeyKid(publicKey));
		String jweString = jweForEncrypt.getCompactSerialization();
		System.out.println("encrypted jweString " + jweString);
		return jweString;
	}

	public String getCOFStatus() throws IOException {
		StringBuilder urlBuilder = new StringBuilder("https://api.discover.com/nws/nwp/cof/v2/wallet/account/status");
		URL url = new URL(urlBuilder.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("x-dfs-c-app-cert", "dfsexxRX7h7gba1dCtZzA-oSZUt9VN3lFysmJkX5FPobX4Jbg");
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Cache-Control", "no-store");
		conn.setRequestProperty("x-dfs-api-plan", "NWS-COF-Sandbox");
		conn.setRequestProperty("Authorization", "Bearer " + getAccessToken());
		conn.setRequestProperty("Content-Type", "application/json");
		byte[] body = ("{       \"requestHeader\": {           \"requestId\": \"c010c5f9-a64c-4073-ada5-71271f2fd5d3\",               \"programId\": \"8020\"        },        \"accountStatusRequest\": {              \"tokens\": [{                           \"tokenId\": \""
				+ COF_TOKEN_ID + "\"                }]        }}").getBytes();
		conn.setFixedLengthStreamingMode(body.length);
		conn.setDoOutput(true);

		OutputStream out = conn.getOutputStream();
		out.write(body);
		System.out.println("Response code: " + conn.getResponseCode());
		BufferedReader rd;
		if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} else {
			rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		}
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();
		conn.disconnect();
		String cofStatusResponse = sb.toString();
		ObjectMapper objectMapper = new ObjectMapper();
		System.out.println(cofStatusResponse);
		Map<String, Object> jsonMap = objectMapper.readValue(cofStatusResponse,
				new TypeReference<Map<String, Object>>() {
				});

		Map<String, Object> tokenStatus = (Map<String, Object>) jsonMap.get("accountStatusResponse");
		List array = (java.util.List) tokenStatus.get("tokenStatus");
		String status = (String) ((Map) array.get(0)).get("tokenStatus");
		System.out.println(status);
		return status;
	}
}
