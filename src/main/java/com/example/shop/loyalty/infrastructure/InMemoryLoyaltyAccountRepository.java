package com.example.shop.loyalty.infrastructure;

import com.example.shop.loyalty.domain.CustomerId;
import com.example.shop.loyalty.domain.LoyaltyAccount;
import com.example.shop.loyalty.domain.LoyaltyAccountId;
import com.example.shop.loyalty.domain.LoyaltyAccountRepository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryLoyaltyAccountRepository implements LoyaltyAccountRepository {

    private final Map<CustomerId, StoredLoyaltyAccount> store = new ConcurrentHashMap<>();

    @Override
    public void save(LoyaltyAccount loyaltyAccount) {
        // Capture the aggregate's state as a snapshot (decoupled from the live object).
        store.put(loyaltyAccount.customerId(), new StoredLoyaltyAccount(
            loyaltyAccount.id(),
            loyaltyAccount.customerId(),
            loyaltyAccount.points())
        );
    }

    @Override
    public Optional<LoyaltyAccount> findByCustomer(CustomerId id) {
        StoredLoyaltyAccount stored = store.get(id);
        if (stored == null) {
            return Optional.empty();
        }
        // Reconstitute a fresh aggregate from stored state on every read.
        return Optional.of(LoyaltyAccount.reconstitute(
            stored.id(),
            stored.customerId(),
            stored.points()
        ));
    }

    // The "persistence model": a plain snapshot of what we stored. In a JPA adapter
    // this would be a @Entity class; here it's just a record. The domain never sees it.
    private record StoredLoyaltyAccount(LoyaltyAccountId id,
                               CustomerId customerId,
                               int points) {
    }
}