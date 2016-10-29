package org.guy.home.iot;

import org.apache.commons.configuration.PropertiesConfiguration;

public class IOTHelper {
	
	public static String getEcobeeSerialNo() {
		String serialNo = null;
		
		try
		{
			String path = IOTHelper.class.getClassLoader().getResource("iot/ecobee/info.properties").getPath();
			
			PropertiesConfiguration config = new PropertiesConfiguration(path);
			serialNo = config.getString("SERIAL_NO");
		} catch (Exception e) {
			System.err.println(e);
		}
		
		return serialNo;
	}
	
	public static String getEcobeeAPIKey() {
		String apiKey = null;
		
		try
		{
			String path = IOTHelper.class.getClassLoader().getResource("iot/ecobee/info.properties").getPath();
			
			PropertiesConfiguration config = new PropertiesConfiguration(path);
			apiKey = config.getString("API_KEY");
		} catch (Exception e) {
			System.err.println(e);
		}
		
		return apiKey;
	}
	
	public static String getEcobeePIN() {
		String pin = null;
		
		try
		{
			String path = IOTHelper.class.getClassLoader().getResource("iot/ecobee/info.properties").getPath();
			
			PropertiesConfiguration config = new PropertiesConfiguration(path);
			pin = config.getString("ECOBEE_PIN");
		} catch (Exception e) {
			System.err.println(e);
		}
		
		return pin;
	}
	
	public static String getEcobeeAuthCode() {
		String authCode = null;
		
		try
		{
			String path = IOTHelper.class.getClassLoader().getResource("iot/ecobee/info.properties").getPath();
			
			PropertiesConfiguration config = new PropertiesConfiguration(path);
		    authCode = config.getString("AUTH_CODE");
		} catch (Exception e) {
			System.err.println(e);
		}
		
		return authCode;
	}
	
}
