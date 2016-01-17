package com.aucguy.wrapperPlugin.test;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class DomTest {
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new File(args[0]));
		
		Node node = document.getDocumentElement().getFirstChild();
		int i = 0;
		while(node != null) {
			System.out.println("node #" + i++ + ": " + node.getNodeName());
			node = node.getNextSibling();
		}
		System.out.println(document.getDocumentElement().getElementsByTagName("dependencies").item(0));
	}
}
