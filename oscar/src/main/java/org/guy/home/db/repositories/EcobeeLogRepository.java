package org.guy.home.db.repositories;

import java.util.List;

import org.guy.home.db.entities.EcobeeLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface EcobeeLogRepository extends CrudRepository<EcobeeLog, Long> {

		Page<EcobeeLog> findAll(Pageable pageable);
		List<EcobeeLog> findByTimestamp(String timeStamp);
	
}
