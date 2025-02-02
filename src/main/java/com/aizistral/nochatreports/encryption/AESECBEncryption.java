package com.aizistral.nochatreports.encryption;

import java.security.InvalidKeyException;

public class AESECBEncryption extends AESEncryption {

	protected AESECBEncryption() {
		super("ECB", "PKCS5Padding", false);
	}

	@Override
	public AESECBEncryptor getProcessor(String key) throws InvalidKeyException {
		return new AESECBEncryptor(key);
	}

	@Override
	public AESECBEncryptor getRandomProcessor() {
		try {
			return this.getProcessor(this.getRandomKey());
		} catch (InvalidKeyException ex) {
			throw new RuntimeException(ex);
		}
	}

}
