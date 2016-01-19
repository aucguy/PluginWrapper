package com.aucguy.wrapperPlugin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import org.w3c.dom.Node;

/**
 * These are various tools for DOM. Most of them aren't used.
 * They're just here in case their ever needed.
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
	
	private static final DomUtil instance = new DomUtil(); //the instance
	private static DocumentBuilder builder; //the thing that creates documents
	private static Transformer transformer; //the thing that saves documentss
	
	//create builder and transformer
	static {
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			transformer = TransformerFactory.newInstance().newTransformer();
		} catch (ParserConfigurationException | TransformerConfigurationException 
				| TransformerFactoryConfigurationError e) {
			throw(new RuntimeException(e));
		}
	}
	
	public static Document parseDocument(File f) throws SAXException, IOException {
		InputStream stream = new FileInputStream(f);
		Document doc = builder.parse(stream);
		stream.close();
		return doc;
	}
	
	public static void saveDocument(Document doc, File f) throws TransformerException {
		transformer.transform(new DOMSource(doc), new StreamResult(f));
	}
	
	/**
	 * iterates over the children of an element
	 * @param node the element to iterate over
	 * @return the iterator
	 */
	public static ElementIter iter(Node node) {
		return instance.new ElementIter(node);
	}
}
