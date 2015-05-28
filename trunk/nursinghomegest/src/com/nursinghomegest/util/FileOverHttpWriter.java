package com.nursinghomegest.util;

import java.io.IOException;
import java.io.OutputStream;

public interface FileOverHttpWriter {
	public void put(OutputStream os) throws IOException;
}
