package com.tsystems.mms.cwa.registration.repository;

import com.tsystems.mms.cwa.registration.model.Partner;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default repository implementation
 *
 * @author Robin Lutter (Robin.Lutter@T-Systems.com)
 */
@Repository
@Transactional
public interface PartnerRepository extends CrudRepository<Partner, Long> {

    @Query("SELECT o FROM Partner o where o.created > :createdSince")
    List<Partner> findNewSince(@Param("createdSince") LocalDateTime createdSince);
}
