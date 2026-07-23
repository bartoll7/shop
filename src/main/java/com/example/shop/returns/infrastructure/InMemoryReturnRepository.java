package com.example.shop.returns.infrastructure;

import com.example.shop.returns.domain.*;
import com.example.shop.shared.Money;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

/**
 * InMemoryReturnRepository — an ADAPTER implementing the ReturnRepository port.
 *
 * <p>Lives in infrastructure. The domain has no idea this exists. Later we can add
 * a JpaReturnRepository implementing the SAME port, and the domain won't change at
 * all — that's the whole point of the port/adapter split.
 *
 * A "collection of aggregates" hands back
 * independent aggregates, not shared references into its internal store.
 */
@Repository
public class InMemoryReturnRepository implements ReturnRepository {

    private final Map<ReturnId, StoredReturn> store = new ConcurrentHashMap<>();

    @Override
    public void save(Return aggregate) {
        // Capture the aggregate's state as a snapshot (decoupled from the live object).
        store.put(aggregate.id(), new StoredReturn(
            aggregate.id(),
            aggregate.orderId(),
            aggregate.status(),
            aggregate.refundAmount()
        ));
    }

    @Override
    public Optional<Return> findById(ReturnId id) {
        StoredReturn stored = store.get(id);
        if (stored == null) {
            return Optional.empty();
        }
        // Reconstitute a fresh aggregate from stored state on every read.
        return Optional.of(Return.reconstitute(
            stored.id(),
            stored.orderId(),
            stored.status(),
            stored.refundAmount()
        ));
    }

    // The "persistence model": a plain snapshot of what we stored. In a JPA adapter
    // this would be a @Entity class; here it's just a record. The domain never sees it.
    private record StoredReturn(ReturnId id,
                                OrderId orderId,
                                ReturnStatus status,
                                Money refundAmount) {
    }
}