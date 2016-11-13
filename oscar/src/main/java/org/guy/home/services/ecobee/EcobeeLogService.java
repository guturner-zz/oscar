package org.guy.home.services.ecobee;

import java.util.List;

import org.guy.home.db.entities.EcobeeLog;
import org.guy.home.db.repositories.EcobeeLogRepository;
import org.guy.home.iot.ecobee.EcobeeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EcobeeLogService {

	@Autowired
	private EcobeeClient ecobeeClient;
	
	private EcobeeLogRepository ecobeeLogRepository;

	@Autowired
	public void setEcobeeLogRepository(EcobeeLogRepository ecobeeLogRepository) {
		this.ecobeeLogRepository = ecobeeLogRepository;
	}

	public void saveEcobeeLogObjects(List<EcobeeLog> ecobeeLogObjects) {
		for (EcobeeLog o : ecobeeLogObjects) {
			// Make sure not already in database:
			if (ecobeeLogRepository.findByTimestamp(o.getTimestamp()).isEmpty()) {
				// Make sure entry has meaningful data:
				if (o.getDesiredCool() != null && o.getDesiredHeat() != null) {
					ecobeeLogRepository.save(o);
					ecobeeClient.logRuntimeEvents(o);
				}
			}
		}
	}
}
