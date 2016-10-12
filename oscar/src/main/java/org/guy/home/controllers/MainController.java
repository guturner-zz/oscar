package org.guy.home.controllers;

import org.guy.home.iot.ecobee.EcobeeReader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

	@RequestMapping("/")
	public String index() {
		
		EcobeeReader ecobeeReader = new EcobeeReader();
		ecobeeReader.logRuntimeEvents();
		
		return "main/main";
	}

}
