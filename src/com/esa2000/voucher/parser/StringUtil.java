package com.esa2000.voucher.parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class StringUtil {

	
	public static String toString(Document document) {
		String xml = "";

		ByteArrayOutputStream baos = null;
		XMLWriter writer = null;
		try {
			baos = new ByteArrayOutputStream();

			OutputFormat outputFormat = OutputFormat.createPrettyPrint();
			outputFormat.setEncoding("UTF-8");
			writer = new XMLWriter(new OutputStreamWriter(baos), outputFormat);
			writer.write(document);
			writer.close();

			xml = baos.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return xml;
	}
}
