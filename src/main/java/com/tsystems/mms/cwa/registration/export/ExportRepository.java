package com.tsystems.mms.cwa.registration.export;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tsystems.mms.cwa.registration.model.Export;

/**
 * Repository for storing all export requests..
 *
 * @author Robin Lutter (Robin.Lutter@T-Systems.com)
 */
@Repository
@Transactional
public interface  ExportRepository extends CrudRepository<Export, Long> {

    @Query("select MAX(exportTime) from Export")
    LocalDateTime findLastExport();
}
