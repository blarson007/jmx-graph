package com.jmxgraph.domain.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.jmxgraph.businessaction.TextEncryptor;

public class PasswordEncryptionAdapter extends XmlAdapter<String, String> {
	
	@Override
	public String marshal(String plainText) throws Exception {
		return TextEncryptor.getInstance().encrypt(plainText);
	}

	@Override
	public String unmarshal(String encrypted) throws Exception {
		return TextEncryptor.getInstance().decrypt(encrypted);
	}
}
