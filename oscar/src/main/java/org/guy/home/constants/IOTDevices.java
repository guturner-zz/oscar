package org.guy.home.constants;

public enum IOTDevices {
	ECOBEE("ecobee");
	
	private String name;
	
	IOTDevices(String name) {
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
