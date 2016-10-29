package org.guy.home.controllers;

import java.util.List;
import java.util.Map;

import org.guy.home.db.entities.EcobeeLog;
import org.guy.home.iot.ecobee.EcobeeReader;
import org.guy.home.iot.ecobee.EcobeeSaver;
import org.guy.home.services.ecobee.EcobeeLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

	@Autowired
	private EcobeeLogService ecobeeLogService;
	
	@RequestMapping("/")
	public String index() {
		
		try {
			EcobeeReader ecobeeReader = new EcobeeReader();
			Map<String, Map<String, String>> detailsMap = ecobeeReader.getEcobeeLogDetailsMap();
			
			EcobeeSaver ecobeeSaver = new EcobeeSaver();
			List<EcobeeLog> ecobeeLogObjects = ecobeeSaver.mapEcobeeLogObjects(detailsMap);
			
			ecobeeLogService.saveEcobeeLogObjects(ecobeeLogObjects);
		} catch (Exception e) {
			System.err.println(e);
		}
		
		return "main/main";
	}

}
