package com.nursinghomegest.util.mail;

import javax.mail.Message.RecipientType;

public class RecipientBcc implements Recipient {

	private String address;
	
	public RecipientBcc(String address) {
		super();
		this.address = address;
	}

	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public RecipientType getRecipientType() {
		return RecipientType.BCC;
	}

}
