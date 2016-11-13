package org.guy.home.iot;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class IOTDataHelper {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	protected String getAbsolutePath(String relativePath) {
		return IOTDataHelper.class.getClassLoader().getResource(relativePath).getPath();
	}

	protected String getPropertyValue(String relativePath, String property) {
		String value = null;

		try {
			String path = getAbsolutePath(relativePath);
			PropertiesConfiguration config = new PropertiesConfiguration(path);
			value = config.getString(property);
		} catch (Exception e) {
			LOGGER.error("Exception in IOTDataHelper.getPropertyValue: " + e);
		}

		return value;
	}

	protected String getAPIKey(String iotDevice) {
		String relativePath = "iot/" + iotDevice + "/info.properties";
		return getPropertyValue(relativePath, "API_KEY");
	}

	protected String getAuthCode(String iotDevice) {
		String relativePath = "iot/" + iotDevice + "/info.properties";
		return getPropertyValue(relativePath, "AUTH_CODE");
	}
	
	protected String getRefreshToken(String iotDevice) {
		String relativePath = "iot/" + iotDevice + "/refresh_token.properties";
		return getPropertyValue(relativePath, "refresh_token");
	}
	
	protected String getAccessToken(String iotDevice) {
		String relativePath = "iot/" + iotDevice + "/access_token.properties";
		return getPropertyValue(relativePath, "access_token");
	}
	
	protected void saveRefreshToken(String iotDevice, String token) {
		try
		{
			String oldRefreshToken = getRefreshToken(iotDevice);
			String timestamp = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss").format(new Date());
			
			String path = getAbsolutePath("iot/" + iotDevice + "/refresh_token.properties");
			
			PropertiesConfiguration config = new PropertiesConfiguration(path);
			config.setProperty("refresh_token", token);
			config.addProperty(timestamp + "_refresh_token", oldRefreshToken);
		    config.save();
		} catch (Exception e) {
			LOGGER.error("Exception in IOTDataHelper.saveRefreshToken: " + e);
		}
	}
	
	protected void saveAccessToken(String iotDevice, String token) {
		try
		{
			String oldAccessToken = getAccessToken(iotDevice);
			String timestamp = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss").format(new Date());
			
			String path = getAbsolutePath("iot/" + iotDevice + "/access_token.properties");
			
			PropertiesConfiguration config = new PropertiesConfiguration(path);
			config.setProperty("access_token", token);
			config.addProperty(timestamp + "_access_token", oldAccessToken);
		    config.save();
		} catch (Exception e) {
			LOGGER.error("Exception in IOTDataHelper.saveAccessToken: " + e);
		}
	}
}
