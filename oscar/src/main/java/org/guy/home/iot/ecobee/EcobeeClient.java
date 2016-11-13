package org.guy.home.iot.ecobee;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.guy.home.constants.IOTDevices;
import org.guy.home.db.entities.EcobeeLog;
import org.guy.home.db.mappers.EcobeeLogMapper;
import org.guy.home.iot.IOTClient;
import org.guy.home.iot.IOTDataHelper;
import org.guy.home.services.ecobee.EcobeeLogService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

@Component
public class EcobeeClient extends IOTDataHelper implements IOTClient {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	private static final String ACCESS_TOKEN = "access_token";
	private static final String REFRESH_TOKEN = "refresh_token";

	@Autowired
	private EcobeeLogService logService;

	@Autowired
	private EcobeeLogMapper mapper;

	public Boolean getLogs() {
		Boolean success = true;

		// Refresh Access Token:
		String refreshToken = getRefreshToken(IOTDevices.ECOBEE.getName());
		getNewAccessToken(refreshToken);

		try {
			String accessToken = getAccessToken(IOTDevices.ECOBEE.getName());

			// Call Ecobee Thermostate service:
			Map<String, Map<String, String>> detailsMap = getThermostatRuntime(accessToken);

			// Map JSON return to EcobeeLog objects:
			List<EcobeeLog> ecobeeLogObjects = mapper.mapEcobeeLogObjects(detailsMap);

			logService.saveEcobeeLogObjects(ecobeeLogObjects);
		} catch (Exception e) {
			success = false;
			LOGGER.error("Exception in EcobeeClient.getLogs: " + e);
		}

		return success;
	}

	/**
	 * Calls the Ecobee Token service to retrieve a refreshed Access Token.
	 */
	public String getNewAccessToken(String refreshToken) {
		HttpResponse<JsonNode> jsonResponse = null;

		try {
			jsonResponse = Unirest.post("https://api.ecobee.com/token").queryString("grant_type", REFRESH_TOKEN)
					.queryString("refresh_token", refreshToken)
					.queryString("client_id", getAPIKey(IOTDevices.ECOBEE.getName())).asJson();
		} catch (Exception e) {
			LOGGER.error("Exception in EcobeeClient.getNewAccessToken: " + e);
		}

		JSONObject jsonObject = jsonResponse.getBody().getObject();
		LOGGER.debug("EcobeeClient.getNewAccessToken response: " + jsonResponse.getBody());

		saveRefreshToken(IOTDevices.ECOBEE.getName(), jsonObject.getString(REFRESH_TOKEN));
		saveAccessToken(IOTDevices.ECOBEE.getName(), jsonObject.getString(ACCESS_TOKEN));

		return jsonObject.getString(ACCESS_TOKEN);
	}

	/**
	 * Calls the Ecobee Thermostat service to retrieve thermostat runtime
	 * details.
	 * 
	 * @param accessToken
	 *            Retrieved from getNewAccessToken.
	 */
	public Map<String, Map<String, String>> getThermostatRuntime(String accessToken) throws Exception {
		Map<String, Map<String, String>> detailsMap = new HashMap<String, Map<String, String>>();
		HttpResponse<JsonNode> jsonResponse = null;

		Date today = new Date();
		String todayDateString = new SimpleDateFormat("yyyy-MM-dd").format(today);
		String body = "{\"startDate\":\"" + todayDateString + "\",\"endDate\":\"" + todayDateString
				+ "\",\"columns\":\"zoneAveTemp,zoneCalendarEvent,zoneCoolTemp,zoneHeatTemp,zoneHumidity,zoneHvacMode\",\"selection\":{\"selectionType\":\"thermostats\",\"selectionMatch\":\""
				+ getSerialNo() + "\"}}";

		try {
			jsonResponse = Unirest.get("https://api.ecobee.com/1/runtimeReport")
					.header("Authorization", "Bearer " + accessToken).queryString("format", "json")
					.queryString("body", body).asJson();
		} catch (Exception e) {
			LOGGER.error("Exception in EcobeeClient.getThermostatRuntime: " + e);
		}

		JSONObject jsonObject = jsonResponse.getBody().getObject();
		LOGGER.debug("EcobeeClient.getThermostatRuntime response: " + jsonResponse.getBody());

		JSONArray reportList = jsonObject.getJSONArray("reportList");
		JSONArray rowList = reportList.getJSONObject(0).getJSONArray("rowList");

		for (int i = 0; i < rowList.length(); i++) {
			String eventString = rowList.getString(i);
			String[] eventLs = eventString.split(",");

			Map<String, String> eventMap = new HashMap<String, String>();
			try {
				eventMap.put(EcobeeLogMapper.AVG_TEMP, eventLs[2]);
				eventMap.put(EcobeeLogMapper.EVENT_NAME, eventLs[3]);
				eventMap.put(EcobeeLogMapper.DESIRED_COOL, eventLs[4]);
				eventMap.put(EcobeeLogMapper.DESIRED_HEAT, eventLs[5]);
				eventMap.put(EcobeeLogMapper.AVG_HUMIDITY, eventLs[6]);
				eventMap.put(EcobeeLogMapper.HVAC_MODE, eventLs[7]);
			} catch (Exception e) {
				// Skipped
			}
			detailsMap.put(eventLs[0] + " " + eventLs[1], eventMap);
		}

		return detailsMap;
	}

	public void logRuntimeEvents(EcobeeLog ecobeeLogObject) {
		StringBuilder log = new StringBuilder();

		log.append("iot=[" + IOTDevices.ECOBEE.getName() + "] timestamp=[" + ecobeeLogObject.getTimestamp() + "] ");
		log.append(EcobeeLogMapper.AVG_TEMP + "=[" + ecobeeLogObject.getAvgTemp() + "] ");
		log.append(EcobeeLogMapper.AVG_HUMIDITY + "=[" + ecobeeLogObject.getAvgHumidity() + "] ");
		log.append(EcobeeLogMapper.DESIRED_COOL + "=[" + ecobeeLogObject.getDesiredCool() + "] ");
		log.append(EcobeeLogMapper.DESIRED_HEAT + "=[" + ecobeeLogObject.getDesiredHeat() + "] ");
		log.append(EcobeeLogMapper.EVENT_NAME + "=[" + ecobeeLogObject.getEventName() + "] ");
		log.append(EcobeeLogMapper.HVAC_MODE + "=[" + ecobeeLogObject.getHvacMode() + "] ");

		LOGGER.info(log.toString());
	}

	private String getSerialNo() {
		String relativePath = "iot/" + IOTDevices.ECOBEE.getName() + "/info.properties";
		return getPropertyValue(relativePath, "SERIAL_NO");
	}

	public String getPIN() {
		String relativePath = "iot/" + IOTDevices.ECOBEE.getName() + "/info.properties";
		return getPropertyValue(relativePath, "ECOBEE_PIN");
	}
}
