package org.guy.home.iot.ecobee;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.guy.home.iot.IOTHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

public class EcobeeReader {
	
	/**
	 * Calls the Ecobee Authorize service to retrieve PIN and Authorization Code.
	 * @deprecated Should only ever be called once.
	 */
	public String getAuthCode() {
		HttpResponse<JsonNode> jsonResponse = null;
		
		try {
			jsonResponse = Unirest.get("https://api.ecobee.com/authorize")
					.queryString("response_type", "ecobeePin")
					.queryString("client_id", IOTHelper.getEcobeeAPIKey())
					.queryString("scope", "smartWrite")
					.asJson();
		} catch (Exception e) {
			System.err.println(e);
		}
		
		return jsonResponse.getBody().getObject().getString("code");
	}
	
	/**
	 * Calls the Ecobee Token service to retrieve Access Token.
	 * 
	 * @param authCode Retrieved from getAuthCode
	 * @deprecated Should only ever be called once.
	 */
	public String getAccessToken(String authCode) {
		HttpResponse<JsonNode> jsonResponse = null;
		
		try {
			jsonResponse = Unirest.get("https://api.ecobee.com/token")
					.queryString("grant_type", "ecobeePin")
					.queryString("code", authCode)
					.queryString("client_id", IOTHelper.getEcobeeAPIKey())
					.asJson();
		} catch (Exception e) {
			System.err.println(e);
		}
		
		return jsonResponse.getBody().getObject().getString("access_token");
	}
	
	/**
	 * Calls the Ecobee Token service to retrieve a refreshed Access Token.
	 */
	public String getNewAccessToken() {
		HttpResponse<JsonNode> jsonResponse = null;
		
		String refreshToken = getRefreshToken();
		
		try {
			jsonResponse = Unirest.post("https://api.ecobee.com/token")
					.queryString("grant_type", "refresh_token")
					.queryString("refresh_token", refreshToken)
					.queryString("client_id", IOTHelper.getEcobeeAPIKey())
					.asJson();
		} catch (Exception e) {
			System.err.println(e);
		}
		
		JSONObject jsonObject = jsonResponse.getBody().getObject();
		
		saveRefreshToken(jsonObject.getString("refresh_token"));
		saveAccessToken(jsonObject.getString("access_token"));
		
		return jsonObject.getString("access_token");
	}
	
	/**
	 * Calls the Ecobee Thermostat service to retrieve thermostat runtime details.
	 * 
	 * @param accessToken Retrieved from getNewAccessToken.
	 */
	public Map<String, Map<String, String>> getThermostatRuntime(String accessToken) throws Exception {
		Map<String, Map<String, String>> detailsMap = new HashMap<String, Map<String, String>>();
		HttpResponse<JsonNode> jsonResponse = null;
		
		Date today = new Date();
		String todayDateString = new SimpleDateFormat("yyyy-MM-dd").format(today);
		String body = "{\"startDate\":\"" + todayDateString + 
				      "\",\"endDate\":\"" + todayDateString +
				      "\",\"columns\":\"zoneAveTemp,zoneCalendarEvent,zoneCoolTemp,zoneHeatTemp,zoneHumidity,zoneHvacMode\",\"selection\":{\"selectionType\":\"thermostats\",\"selectionMatch\":\"" + IOTHelper.getEcobeeSerialNo() + "\"}}";
		
		try {
			jsonResponse = Unirest.get("https://api.ecobee.com/1/runtimeReport")
					.header("Authorization", "Bearer " + accessToken)
					.queryString("format", "json")
					.queryString("body", body)
					.asJson();
		} catch (Exception e) {
			System.err.println(e);
		}
		
		JSONObject jsonObject = jsonResponse.getBody().getObject();
		
		if (jsonObject.has("status")) {
			JSONObject statusObject = jsonObject.getJSONObject("status");
			if (statusObject.has("message") && statusObject.getString("message").contains("Authentication token has expired. Refresh your tokens.")) {
				throw new Exception(statusObject.getString("message"));
			}
		}
		
		JSONArray reportList  = jsonObject.getJSONArray("reportList");
		JSONArray rowList     = reportList.getJSONObject(0).getJSONArray("rowList");
		
		for (int i = 0; i < rowList.length(); i++) {
			String eventString = rowList.getString(i);
			String[] eventLs = eventString.split(",");
			
			Map<String, String> eventMap = new HashMap<String, String>();
			try {
				eventMap.put("zoneAvgTemp", eventLs[2]);
				eventMap.put("zoneCalendarEvent", eventLs[3]);
				eventMap.put("zoneCoolTemp", eventLs[4]);
				eventMap.put("zoneHeatTemp", eventLs[5]);
				eventMap.put("zoneHumidity", eventLs[6]);
				eventMap.put("zoneHvacMode", eventLs[7]);
			} catch (Exception e) {
				// Skipped
			}
			detailsMap.put(eventLs[0] + " " + eventLs[1], eventMap);
		}
		
		return detailsMap;
	}
	
	public Map<String, Map<String, String>> getEcobeeLogDetailsMap() {
		return getEcobeeLogDetailsMap(false);
	}
	
	public Map<String, Map<String, String>> getEcobeeLogDetailsMap(Boolean lastChance) {
		Map<String, Map<String, String>> detailsMap = null;
		
		try {
			String accessToken = getAccessToken();
			detailsMap = getThermostatRuntime(accessToken);
		} catch (Exception e) {
			System.err.println(e);
			if (!lastChance) {
				getNewAccessToken();
				getEcobeeLogDetailsMap(true);
			}
		}
		
		return detailsMap;
	}
	
	private void saveRefreshToken(String token) {
		try
		{
			String oldRefreshToken = getRefreshToken();
			String timestamp = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss").format(new Date());
			
			String path = this.getClass().getClassLoader().getResource("iot/ecobee/refresh_token.properties").getPath();
			
			PropertiesConfiguration config = new PropertiesConfiguration(path);
			config.setProperty("refresh_token", token);
			config.setProperty(timestamp + "_refresh_token", oldRefreshToken);
		    config.save();
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	private String getRefreshToken() {
		String refreshToken = null;
		
		try
		{
			String path = this.getClass().getClassLoader().getResource("iot/ecobee/refresh_token.properties").getPath();
			
			PropertiesConfiguration config = new PropertiesConfiguration(path);
		    refreshToken = config.getString("refresh_token");
		} catch (Exception e) {
			System.err.println(e);
		}
		
		return refreshToken;
	}
	
	private void saveAccessToken(String token) {
		try
		{
			String oldAccessToken = getRefreshToken();
			String timestamp = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss").format(new Date());
			
			String path = this.getClass().getClassLoader().getResource("iot/ecobee/access_token.properties").getPath();
			
			PropertiesConfiguration config = new PropertiesConfiguration(path);
			config.setProperty("access_token", token);
			config.setProperty(timestamp + "_access_token", oldAccessToken);
		    config.save();
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	public String getAccessToken() {
		String refreshToken = null;
		
		try
		{
			String path = this.getClass().getClassLoader().getResource("iot/ecobee/access_token.properties").getPath();
			
			PropertiesConfiguration config = new PropertiesConfiguration(path);
		    refreshToken = config.getString("access_token");
		} catch (Exception e) {
			System.err.println(e);
		}
		
		return refreshToken;
	}
}
