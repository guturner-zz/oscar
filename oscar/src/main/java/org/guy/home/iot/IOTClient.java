package org.guy.home.iot;

public interface IOTClient {
	String getNewAccessToken(String refreshToken);
	Boolean getLogs();
}
