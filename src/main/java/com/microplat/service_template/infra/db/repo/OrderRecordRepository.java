package com.microplat.service_template.infra.db.repo;

import com.microplat.service_template.domain.Order;
import com.microplat.service_template.domain.OrderRepository;
import com.microplat.service_template.infra.db.entity.OrderRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * JPA repository for OrderRecord that also fulfills the domain OrderRepository contract.
 */
public interface OrderRecordRepository extends JpaRepository<OrderRecord, Long>, OrderRepository {

    // Use explicit JPQL to avoid name collision with domain's findByOrderId(String)
    @Query("select o from OrderRecord o where o.orderId = :orderId")
    Optional<OrderRecord> findRecordByOrderId(@Param("orderId") String orderId);

    // Domain adapter methods
    @Override
    default Optional<Order> findByOrderId(String orderId) {
        return findRecordByOrderId(orderId).map(this::toDomain);
    }

    @Override
    default Order save(Order order) {
        OrderRecord saved = save(toRecord(order));
        return toDomain(saved);
    }

    // Mapping helpers
    private Order toDomain(OrderRecord r) {
        return new Order(r.getOrderId(), r.getAmount(), r.getCreatedAt());
    }

    private OrderRecord toRecord(Order o) {
        return new OrderRecord(o.orderId(), o.amount());
    }
}
