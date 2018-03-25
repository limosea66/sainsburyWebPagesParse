package com.manchester.sainsbury.wei.sainsburyTest;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Ms Wei Hu
 * @since 17th March 2018 This class is used to get the information from
 *        Sainsbury website according to JSON data requirement. Can only do it
 *        during weekend due to my current heavy work load
 *
 */
public class FruitsInformation {
	private double sum = 0.0;
	public double getSum() {
		return sum;
	}

	public void setSum(double sum) {
		this.sum = sum;
	}

	public JSONObject getObjMainList() {
		return objMainList;
	}

	public void setObjMainList(JSONObject objMainList) {
		this.objMainList = objMainList;
	}

	// used to format the unit price - total up to 2 decimal places
	NumberFormat formatter = new DecimalFormat("#0.00");
	// Main list object with a json array for products
	private JSONObject objMainList = new JSONObject();
	Document doc = null;
	final String url = "https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/webapp/wcs/stores/servlet/gb/groceries/berries-cherries-currants6039.html";
	final static Logger LOGGER = Logger.getLogger(FruitsInformation.class.getName());

	public static void main(String[] args) throws Exception {
		FruitsInformation http = new FruitsInformation();
		LOGGER.info("Call sendGet() method");
		http.sendGet();
	}

	public Document getDocument(String url) {
		// using Jsoup to connect to url to get web page content as Document type

		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * This method is to parse web content according to JSON data requirement
	 * 
	 * @return void
	 * @throws Exception
	 */
	public void sendGet() throws Exception {
		doc = getDocument(url);
		LOGGER.info("Jsoup connect method used");
		
		JSONObject json = null;
		JSONArray jArray = new JSONArray();

		// select Document item "li" with class name "gridItem"
		Elements products = doc.select("li[class=gridItem]");
		LOGGER.info("\nNumber of Products: " + products.size());

		for (Element product : products) {
			json = new JSONObject();
			generateJson(product, json);
			LOGGER.info("generateJson method called");
			// Add json object to jArray
			jArray.put(json);

		}
		// add the jArray result and total price into json object objMainList
		objMainList.put("results", jArray);
		objMainList.put("total", formatter.format(sum / 2.0));

		// using Jackson Object Mapper to get pretty print
		ObjectMapper mapper = new ObjectMapper();
		Object jsonObj = mapper.readValue(objMainList.toString(2), Object.class);
		System.out.println(mapper.defaultPrettyPrintingWriter().writeValueAsString(jsonObj));

	}

	/**
	 * this method is used to produce json object from element
	 * 
	 * @param element
	 * @param json
	 */
	private void generateJson(Element element, JSONObject json) {
		Elements children = element.children();
		for (Element child : children) {
			// get product title from element div with class name productNameAndPromotions
			for (Element e : child.select("div.productNameAndPromotions")) {
				json.put("title: ", e.text());
			}
			// get Price Per unit for the product according to the requirement format
			for (Element e : child.select("p.pricePerUnit")) {
				// summing up price
				sum += Float.parseFloat((String) e.text().toString().subSequence(1, 5));
				json.put("unit_price: ",
						formatter.format(Float.parseFloat((String) e.text().toString().subSequence(1, 5))));
			}
			// only get first-line description for a particular product
			Element eDescription = child.select("div.productText").first();
			if (eDescription != null) {
				Element eDes = eDescription.select("p:not([class])").first();

				if (eDes.text() != "") {
					json.put("Description: ", eDes.text());
				}

			}
			// get Nutrition Level in Kcal Per 100g for a particular product
			Element eNutritionLevel = child.select("tr.tableRow0").first();
			if (eNutritionLevel != null && eNutritionLevel.text() != "") {
				Element eTd = eNutritionLevel.select("td").first();
				// nutrition value is not empty, then add json field "kcal_per_100g"
				if (eTd != null && eTd.text() != "") {
					json.put("kcal_per_100g: ",
							Integer.parseInt(eNutritionLevel.text().substring(0, eTd.text().length() - 4)));
				}
			}

			// Only follow the link for the first page
			if (child.hasClass("product")) {
				// follow the link to get the content
				Document linkDoc = null;
				Elements link = child.getElementsByTag("a");
				// change relative links to absolute links
				String linkUrl = (link.attr("href").toString().replaceAll("../../../../../../",
						"https://jsainsburyplc.github.io/serverside-test/site/www.sainsburys.co.uk/"));

				try {
					linkDoc = Jsoup.connect(linkUrl).get();
				} catch (IOException e) {
					e.printStackTrace();
				}
				// call generateJson method again
				generateJson(linkDoc, json);
			}
		}

	}
}
