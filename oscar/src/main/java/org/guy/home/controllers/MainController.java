package org.guy.home.controllers;

import org.guy.home.iot.ecobee.EcobeeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

	@Autowired
	private EcobeeClient ecobeeClient;

	@RequestMapping("/")
	public String index() {
		ecobeeClient.getLogs();

		return "main/main";
	}

}
