package org.guy.home.db.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name="ecobeelog")
public class EcobeeLog implements Serializable {

	private static final long serialVersionUID = 3345347593487146859L;

	@Id
	@GeneratedValue
	private Long id;
	
	@Column
	String iot;
	
	@Column(name = "ts")
	String timestamp;
	
	@Column
	String avgTemp;
	
	@Column
	String avgHumidity;
	
	@Column
	String desiredCool;
	
	@Column
	String desiredHeat;
	
	@Column
	String eventName;
	
	@Column
	String hvacMode;
	
	public EcobeeLog() {
		
	}
	
	public EcobeeLog(String iot, String timestamp, String avgTemp, String avgHumidity, String desiredCool, String desiredHeat, String eventName, String hvacMode) {
		this.iot         = iot;
		this.timestamp   = timestamp;
		this.avgTemp     = avgTemp;
		this.avgHumidity = avgHumidity;
		this.desiredCool = desiredCool;
		this.desiredHeat = desiredHeat;
		this.eventName   = eventName;
		this.hvacMode    = hvacMode;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIot() {
		return iot;
	}

	public void setIot(String iot) {
		this.iot = iot;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getAvgTemp() {
		return avgTemp;
	}

	public void setAvgTemp(String avgTemp) {
		this.avgTemp = avgTemp;
	}

	public String getAvgHumidity() {
		return avgHumidity;
	}

	public void setAvgHumidity(String avgHumidity) {
		this.avgHumidity = avgHumidity;
	}

	public String getDesiredCool() {
		return desiredCool;
	}

	public void setDesiredCool(String desiredCool) {
		this.desiredCool = desiredCool;
	}

	public String getDesiredHeat() {
		return desiredHeat;
	}

	public void setDesiredHeat(String desiredHeat) {
		this.desiredHeat = desiredHeat;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getHvacMode() {
		return hvacMode;
	}

	public void setHvacMode(String hvacMode) {
		this.hvacMode = hvacMode;
	}
}
