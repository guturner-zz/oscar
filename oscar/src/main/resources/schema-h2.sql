CREATE SCHEMA IF NOT EXISTS oscar;
SET SCHEMA oscar;
CREATE TABLE IF NOT EXISTS ecobeelog (
	id INTEGER NOT NULL AUTO_INCREMENT,
	iot VARCHAR(64) NOT NULL,
	ts VARCHAR(64) NOT NULL,
	avg_temp VARCHAR(64),
	avg_humidity VARCHAR(64),
	desired_cool VARCHAR(64),
	desired_heat VARCHAR(64),
	event_name VARCHAR(64),
	hvac_mode VARCHAR(64),
	PRIMARY KEY(ts)
);