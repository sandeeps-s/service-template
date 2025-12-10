package com.microplat.service_template.infra.db.repo;

import com.microplat.service_template.infra.db.entity.OutboxEventRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEventRecord, String> {

    List<OutboxEventRecord> findByProcessedAtIsNullOrderByCreatedAtAsc(Pageable pageable);
}
