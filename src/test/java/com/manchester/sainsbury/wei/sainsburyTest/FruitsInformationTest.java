package com.manchester.sainsbury.wei.sainsburyTest;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.helper.StringUtil;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.FormElement;
import org.jsoup.parser.HtmlTreeBuilder;
import org.jsoup.parser.Parser;
import org.jsoup.parser.XmlTreeBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import static org.mockito.Mockito.*;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Tests the URL connection.
 * 
 * @author Wei Hu
 */

@RunWith(PowerMockRunner.class)
public class FruitsInformationTest {
	FruitsInformation classUnderTest = new FruitsInformation();
	Connection mockConnection = null;
	Connection mockConnection1 = null;
	Connection mockConnection2 = null;
	Connection mockConnection3 = null;
	String url = "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/webapp/wcs/stores/servlet/gb/groceries/berries-cherries-currants6039.html";
	String urlStrawbury = "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/shop/gb/groceries/berries-cherries-currants/sainsburys-british-strawberries-400g.html";
	String urlBlueburry = "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/shop/gb/groceries/berries-cherries-currants/sainsburys-blueberries-200g.html";
	String urlBC = "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/shop/gb/groceries/berries-cherries-currants/sainsburys-blackcurrants-150g.html";
	
	Document doc = null;
	Document docStrawbury = null;
	Document docBlueburry = null;
	Document docBC = null;

	@Before
	public void executedBeforeEachTestMethod() {
		// get html document content from the resources folder using Google Guava
		String fruits = "";
		String strawbury = "";
		String blueburry = "";
		String blackcurrent="";
		try {
			fruits = Resources.toString(Resources.getResource("testHtml"), Charsets.UTF_8);
			strawbury = Resources.toString(Resources.getResource("testHtmlStrawbury"), Charsets.UTF_8);
			blueburry = Resources.toString(Resources.getResource("testHtmlBlueburry"), Charsets.UTF_8);
			blackcurrent = Resources.toString(Resources.getResource("testHtmlBC"), Charsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Using Jsoup parse methond to convert the html content into Document Type
		doc = Jsoup.parse(fruits);
		docStrawbury = Jsoup.parse(strawbury);
		docBlueburry = Jsoup.parse(blueburry);
		docBC = Jsoup.parse(blackcurrent);
		// Mock Connection class
		mockConnection = Mockito.mock(Connection.class);
		mockConnection1 = Mockito.mock(Connection.class);
		mockConnection2 = Mockito.mock(Connection.class);
		mockConnection3 = Mockito.mock(Connection.class);
		// Call mockStatic Jsoup.class to enable static mocking
		PowerMockito.mockStatic(Jsoup.class);

		// Stub static method Jsoup.connect;
		PowerMockito.when(Jsoup.connect(url)).thenReturn(mockConnection);
		PowerMockito.when(Jsoup.connect(urlStrawbury)).thenReturn(mockConnection1);
		PowerMockito.when(Jsoup.connect(urlBlueburry)).thenReturn(mockConnection2);
		PowerMockito.when(Jsoup.connect(urlBC)).thenReturn(mockConnection3);

		try {
			when(mockConnection.get()).thenReturn(doc);
			when(mockConnection1.get()).thenReturn(docStrawbury);
			when(mockConnection2.get()).thenReturn(docBlueburry);
			when(mockConnection3.get()).thenReturn(docBC);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	@PrepareForTest({ Jsoup.class })
	public void getDocumentTest() {
		doc = classUnderTest.getDocument(url);
		try {
			verify(mockConnection, times(1)).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(doc.title().toString().contains("Berries, cherries & currants"));
	}

	@SuppressWarnings("unchecked")
	@Test
	@PrepareForTest({ Jsoup.class })
	// Test sendGet() method
	public void sendGetTest() {
		try {
			classUnderTest.sendGet();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			verify(mockConnection, times(1)).get();
			verify(mockConnection1, times(1)).get();
			verify(mockConnection2, times(1)).get();
			verify(mockConnection3, times(1)).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertTrue(doc.title().toString().contains("Berries, cherries & currants"));
		assertEquals(5.25,classUnderTest.getSum()/2.0,0.01);
		assertEquals("5.25",classUnderTest.getObjMainList().get("total"));
		assertTrue(classUnderTest.getObjMainList().get("results").toString().contains("by Sainsbury's strawberries"));
		assertTrue(classUnderTest.getObjMainList().get("results").toString().contains("Union Flag"));
		assertTrue(classUnderTest.getObjMainList().get("results").toString().contains("Sainsbury's Blackcurrants 150g"));
		
				
	}
}
