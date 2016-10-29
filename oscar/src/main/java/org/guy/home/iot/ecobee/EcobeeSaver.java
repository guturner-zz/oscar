package org.guy.home.iot.ecobee;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.guy.home.db.entities.EcobeeLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EcobeeSaver {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	public List<EcobeeLog> mapEcobeeLogObjects(Map<String, Map<String, String>> ecobeeLogMap) {
		List<EcobeeLog> ecobeeLogObjects = new ArrayList<EcobeeLog>();
		
		for (String timestamp : ecobeeLogMap.keySet()) {
			Map<String, String> vals = ecobeeLogMap.get(timestamp);
			EcobeeLog o = new EcobeeLog();
			
			o.setTimestamp(timestamp);
			o.setIot("ecobee");
			o.setAvgTemp(vals.get("zoneAvgTemp"));
			o.setAvgHumidity(vals.get("zoneHumidity"));
			o.setDesiredCool(vals.get("zoneCoolTemp"));
			o.setDesiredHeat(vals.get("zoneHeatTemp"));
			o.setEventName(vals.get("zoneCalendarEvent"));
			o.setHvacMode(vals.get("zoneHvacMode"));
			
			ecobeeLogObjects.add(o);
		}
		
		return ecobeeLogObjects;
	}

	public void logRuntimeEvents(EcobeeLog ecobeeLogObject) {
		String log = "";
		
		log += "iot=[ecobee] timestamp=[" + ecobeeLogObject.getTimestamp() + "] ";
		log += "avgTemp=[" + ecobeeLogObject.getAvgTemp() + "] ";
		log += "avgHumidity=[" + ecobeeLogObject.getAvgHumidity() + "] ";
		log += "desiredCool=[" + ecobeeLogObject.getDesiredCool() + "] ";
		log += "desiredHeat=[" + ecobeeLogObject.getDesiredHeat() + "] ";
		log += "eventName=[" + ecobeeLogObject.getEventName() + "] ";
		log += "hvacMode=[" + ecobeeLogObject.getHvacMode() + "] ";
		
		LOGGER.info(log);
	}
}
