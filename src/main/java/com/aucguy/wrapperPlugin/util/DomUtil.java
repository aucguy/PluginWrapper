package com.aucguy.wrapperPlugin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import org.w3c.dom.Node;

/**
 * various tools for DOM
 */
public final class DomUtil {
	public class ElementIter implements Iterable<Element>, Iterator<Element> {
		protected Element elem;
		
		private ElementIter(Node x) {
			x = x.getFirstChild();
			elem = x instanceof Element ? (Element) x : getNextElement(x);
		}
		
		@Override
		public boolean hasNext() {
			return elem != null;
		}

		@Override
		public Element next() {
			Element ret = elem;
			elem = getNextElement(elem);
			return ret;
		}
		
		private Element getNextElement(Node node) {
			do node = node.getNextSibling(); while (!(node instanceof Element || node == null));
			return (Element) node;
		}

		@Override
		public Iterator<Element> iterator() {
			return this;
		}
	}
	private static final DomUtil instance = new DomUtil();
	private static DocumentBuilder builder;
	static {
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw(new RuntimeException(e));
		}
	}
		
	public static Document parseDocument(File f) throws SAXException, IOException {
		InputStream stream = new FileInputStream(f);
		Document doc = builder.parse(stream);
		stream.close();
		return doc;
	}
	
	public static ElementIter iter(Node node) {
		return instance.new ElementIter(node);
	}
}
