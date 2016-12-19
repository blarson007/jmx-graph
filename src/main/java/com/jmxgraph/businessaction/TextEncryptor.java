package com.jmxgraph.businessaction;

import org.jasypt.util.text.BasicTextEncryptor;

public class TextEncryptor {
	
	private BasicTextEncryptor basicTextEncryptor;
	
	private TextEncryptor() {
		basicTextEncryptor = new BasicTextEncryptor();
		basicTextEncryptor.setPassword("changeme");
	}

	private static class InstanceHolder {
		private static final TextEncryptor instance = new TextEncryptor();
	}
	
	public static TextEncryptor getInstance() {
		return InstanceHolder.instance;
	}
	
	public String encrypt(String plainText) {
		return basicTextEncryptor.encrypt(plainText);
	}
	
	public String decrypt(String encrypted) {
		return basicTextEncryptor.decrypt(encrypted);
	}
}
