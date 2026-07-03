package com.example.shop.loyalty.application;

import com.example.shop.loyalty.domain.CustomerId;
import com.example.shop.loyalty.domain.LoyaltyAccount;
import com.example.shop.loyalty.domain.LoyaltyAccountId;
import com.example.shop.loyalty.domain.LoyaltyAccountRepository;
import com.example.shop.shared.DomainEvent;
import com.example.shop.shared.DomainEventPublisher;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AwardPointsApplicationService {

    private final LoyaltyAccountRepository loyaltyAccounts;
    private final DomainEventPublisher events;

    public AwardPointsApplicationService(LoyaltyAccountRepository loyaltyAccounts, DomainEventPublisher events) {
        this.loyaltyAccounts = loyaltyAccounts;
        this.events = events;
    }

    @Transactional
    public LoyaltyAccountId awardCustomer(UUID customerUuid, BigDecimal amount) {
        CustomerId customerId = CustomerId.of(customerUuid);
        LoyaltyAccount account = loyaltyAccounts.findByCustomer(customerId)
            .orElseGet(() -> LoyaltyAccount.create(customerId));

        account.award(amount.intValue());

        loyaltyAccounts.save(account);
        publishEvents(account.pullDomainEvents());
        return account.id();
    }

    private void publishEvents(List<DomainEvent> domainEvents) {
        for (DomainEvent event : domainEvents) {
            events.publish(event);
        }
    }
}