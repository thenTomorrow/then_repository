package com.nursinghomegest.util.mail;

import javax.mail.Message.RecipientType;

public interface Recipient {
	public String getAddress();
	public RecipientType getRecipientType();
}
