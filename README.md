# oscar
Spring Boot app. A hub to view logs for IoT devices.

# Why 'Oscar'?
The mascot for this application is an octopus with tentacles reaching throughout a smart home. Each tentacle represents access to a specific smart device. Eventually, api.ai support will give our octopus intelligence.

# Intro
Demonstrates knowledge of:
* Spring Boot
* Web APIs (Ecobee, SmartThings, etc.)
* api.ai

# Setup
Certain files have been hidden from this repository.

For example, under src/main/resources you will need to create an 'iot' folder, and within that a new folder for each device (e.g. 'ecobee').

You will need to populate a info.properties and refresh_token.properties for each smart device under src/main/resources/iot/\<device\>/

info.properties
```
SERIAL_NO=1234567890

API_KEY=abcdefg

ECOBEE_PIN=12a3

AUTH_CODE=abcdefg
```

You should create a folder named 'oscar' in your C:\\Users\\\<User\>\\ folder. Under that, create a 'db' and 'logs' folder.

You can monitor your H2 database anytime the Web server is running by visiting http://localhost:8080/console/. You'll want to change the DB name to ~/oscar/db/oscar
