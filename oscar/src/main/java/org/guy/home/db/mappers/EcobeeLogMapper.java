package org.guy.home.db.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.guy.home.constants.IOTDevices;
import org.guy.home.db.entities.EcobeeLog;
import org.springframework.stereotype.Component;

@Component
public class EcobeeLogMapper {
	
	public static final String AVG_TEMP = "zoneAvgTemp";
	public static final String AVG_HUMIDITY = "zoneHumidity";
	public static final String DESIRED_COOL = "zoneCoolTemp";
	public static final String DESIRED_HEAT = "zoneHeatTemp";
	public static final String EVENT_NAME = "zoneCalendarEvent";
	public static final String HVAC_MODE = "zoneHvacMode";
	
	public EcobeeLog mapEcobeeLogObject(String timeStamp, Map<String, String> propertyValueMap) {
		EcobeeLog ecobeeLogObject = new EcobeeLog();
		
		ecobeeLogObject.setTimestamp(timeStamp);
		ecobeeLogObject.setIot(IOTDevices.ECOBEE.getName());
		ecobeeLogObject.setAvgTemp(propertyValueMap.get(AVG_TEMP));
		ecobeeLogObject.setAvgHumidity(propertyValueMap.get(AVG_HUMIDITY));
		ecobeeLogObject.setDesiredCool(propertyValueMap.get(DESIRED_COOL));
		ecobeeLogObject.setDesiredHeat(propertyValueMap.get(DESIRED_HEAT));
		ecobeeLogObject.setEventName(propertyValueMap.get(EVENT_NAME));
		ecobeeLogObject.setHvacMode(propertyValueMap.get(HVAC_MODE));
		
		return ecobeeLogObject;
	}
	
	public List<EcobeeLog> mapEcobeeLogObjects(Map<String, Map<String, String>> ecobeeLogMap) {
		List<EcobeeLog> ecobeeLogObjects = new ArrayList<EcobeeLog>();
		
		for (String timestamp : ecobeeLogMap.keySet()) {
			ecobeeLogObjects.add(mapEcobeeLogObject(timestamp, ecobeeLogMap.get(timestamp)));
		}
		
		return ecobeeLogObjects;
	}
}
