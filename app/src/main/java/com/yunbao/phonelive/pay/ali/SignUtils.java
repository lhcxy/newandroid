package com.yunbao.phonelive.pay.ali;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * 支付宝给订单加签名工具
 */
public class SignUtils {

	private static final String ALGORITHM = "RSA";

	private static final String SIGN_ALGORITHMS = "SHA1WithRSA";

	private static final String SIGN_SHA256RSA_ALGORITHMS = "SHA256WithRSA";

	private static final String DEFAULT_CHARSET = "UTF-8";

	private static String getAlgorithms(boolean rsa2) {
		return rsa2 ? SIGN_SHA256RSA_ALGORITHMS : SIGN_ALGORITHMS;
	}

	public static String sign(String content, String privateKey) {
		try {

			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
					Base64.decode(privateKey));
			KeyFactory keyf = KeyFactory.getInstance(ALGORITHM,"BC");
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);



			java.security.Signature signature = java.security.Signature
					.getInstance(SIGN_ALGORITHMS);

			signature.initSign(priKey);
			signature.update(content.getBytes(DEFAULT_CHARSET));

			byte[] signed = signature.sign();

			return Base64.encode(signed);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
