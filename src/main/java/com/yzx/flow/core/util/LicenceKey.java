package com.yzx.flow.core.util;

import com.alibaba.fastjson.JSONObject;
import com.xiaoleilu.hutool.crypto.asymmetric.KeyType;
import com.xiaoleilu.hutool.crypto.asymmetric.RSA;
import com.xiaoleilu.hutool.date.DateTime;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.lang.Base64;
import com.xiaoleilu.hutool.util.CharsetUtil;
import com.xiaoleilu.hutool.util.NetUtil;
import com.xiaoleilu.hutool.util.StrUtil;
import com.yzx.flow.core.util.licence.LicenceVo;

public class LicenceKey {
	/**
	 * 获取密文
	 * @return
	 * @throws Exception 
	 */
	private static String getSecretLicence() throws Exception{
		String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAK8pupK7em4lWqx7KGD01vLrBDoy5oW4ocfUywJKXqOjuzriEHM48sfp1Kx04K3KwMYWFFP8Oal3yi700MmxpH/PAMQt5NvOo9uAn0dV7jMpslxV+wFT5YodNOAP/7cDA7QOjOwMKWVXj1R7TWu/ZDH2UGK1JGzTHVT7d9dO8uCdAgMBAAECgYA//DpPR3Tdr3+D6ilB4a5zZi5RJC7ZQiy21qCh4ZDYrgLC67menXryVBSAOuGoGpx4v7AFTemyYjPVG1D5nI6+FodD8K+CQVsdiGPAL0/D99OoygRR2xPHPSohKS8VU7dGcYoJJs9XsvWV1WtkOGPMFjk52QiZYNFsCchTLZR1AQJBANxUNBk8njBeZjT//syujE6n+aw3hMcU3du5dMXk4ULlFsN/ArNcR+nWes6Sz+DW9N2P3AvpHQ8HkdNjpxMtCPECQQDLhZHVvjzWzalFRyngorkf0xKSsmDhye7CN0x1AQ7znND0ebcJPK84HO6aBhY0RRMSR3nY+89NOuFy4tFBdzJtAkEAqju09TrMznphZdnVOLg0WCGY2uMah+crc7Va7/vjwBfY+ruLjqFiPt4kjK8KRqJPF1Erp1g5x38FwNpiIILPQQJBAJYYevZE57+PS3AhTJwzxg3Xb9IqpdK/R0uK1uNPlYRKubwja77vN1ZN2BZVuNhxvN/1QMKq6zbM3uoIp0T9sH0CQBAF/9RNpBWqj+xshaWbvaVT4hI1OsMwW6peaHphsJKN7h/PaQdNptcexVCV9MkI1cJWbuZgKein75NSjZcbft4=";
//		RSA rsa = new RSA(privateKey,null);
		//私钥加密，公钥解密
		String mac = "00-21-0f-c9-2e-02"+"~"+"00-21-0f-c9-ad-02"+"~"+"52-54-00-AA-DF-7E"+"~"+"A4-1F-72-93-E2-CE"+"~"+"F8-BC-12-66-55-03"+
				"~"+"2E-F0-BD-BB-8C-4F"+"~"+"b0-83-fe-5a-3a-c6"+"~"+"2e-77-72-db-3a-a1"+"~"+"50-9a-4c-0a-1f-41"+"~"+"de-07-d7-06-1d-a8"+"~"+"9e-83-57-f0-92-18"+"~"+"00-0C-29-DE-4B-46"+"~"+"00-0C-29-DE-4B-46"+"~"+"96-13-57-85-5c-34"+"~"+"c8-1f-66-31-b4-51";
		System.out.println(mac);
		LicenceVo licence = new LicenceVo();
		String validity = "300";
		String startTime = DateUtil.now();
		DateTime parse = DateUtil.parse(startTime);
		String endTime = DateUtil.offsetDay(parse, Integer.parseInt(validity)).toString("yyyy-MM-dd HH:mm:ss");
		licence.setStartTime(DateUtil.now());
		licence.setValidity(validity);
		licence.setCustomerName("测试");
		licence.setEndTime(endTime);
		String password = "11111";
		byte[] encrypt2 = RSAUtils.encryptByPrivateKey(StrUtil.bytes(mac+","+endTime+","+password,CharsetUtil.CHARSET_UTF_8), privateKey);
//		byte[] encrypt2 = rsa.encrypt(StrUtil.bytes(mac+","+endTime+","+password,CharsetUtil.CHARSET_UTF_8), KeyType.PrivateKey);
		String result = Base64.encode(encrypt2) + "~" +Base64.encode(JSONObject.toJSONString(licence))+","+DateUtil.today();
		return result;
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(getSecretLicence());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
