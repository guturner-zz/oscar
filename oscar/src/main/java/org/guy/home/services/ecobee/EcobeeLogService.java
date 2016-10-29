package org.guy.home.services.ecobee;

import java.util.List;

import org.guy.home.db.entities.EcobeeLog;
import org.guy.home.db.repositories.EcobeeLogRepository;
import org.guy.home.iot.ecobee.EcobeeSaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EcobeeLogService {

	private EcobeeLogRepository ecobeeLogRepository;

	@Autowired
	public void setEcobeeLogRepository(EcobeeLogRepository ecobeeLogRepository) {
		this.ecobeeLogRepository = ecobeeLogRepository;
	}

	public void saveEcobeeLogObjects(List<EcobeeLog> ecobeeLogObjects) {
		EcobeeSaver ecobeeSaver = new EcobeeSaver();
		
		for (EcobeeLog o : ecobeeLogObjects) {
			// Make sure not already in database:
			if (ecobeeLogRepository.findByTimestamp(o.getTimestamp()).isEmpty()) {
				// Make sure entry has meaningful data:
				if (o.getDesiredCool() != null && o.getDesiredHeat() != null) {
					ecobeeLogRepository.save(o);
					ecobeeSaver.logRuntimeEvents(o);
				}
			}
		}
	}
}
