package com.dmdev.dao;

import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.integration.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SubscriptionDaoIT extends IntegrationTestBase {

    private final SubscriptionDao subscriptionDao = SubscriptionDao.getInstance();

    @Test
    void findAll() {
        var subscription1 = subscriptionDao.insert(getSubscription(1));
        var subscription2 = subscriptionDao.insert(getSubscription(2));
        var subscription3 = subscriptionDao.insert(getSubscription(3));

        var actualResult = subscriptionDao.findAll();

        assertThat(actualResult).hasSize(3);
        List<Integer> subscriptionId = actualResult.stream()
                .map(Subscription::getId)
                .toList();
        assertThat(subscriptionId).contains(subscription1.getId(), subscription2.getId(), subscription3.getId());
    }

    @Test
    void findById() {
        var subscription = subscriptionDao.insert(getSubscription(1));

        var actualResult = subscriptionDao.findById(subscription.getId());

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(subscription);
    }

    @Test
    void findByIdIfUserDoesNotExist() {
        subscriptionDao.insert(getSubscription(1));

        var actualResult = subscriptionDao.findById(2);

        assertThat(actualResult).isEmpty();
    }

    @Test
    void deleteExistingEntity() {
        var subscription = subscriptionDao.insert(getSubscription(1));

        boolean actualResult = subscriptionDao.delete(subscription.getId());

        assertTrue(actualResult);
    }

    @Test
    void deleteNotExistingEntity() {
        subscriptionDao.insert(getSubscription(1));

        boolean actualResult = subscriptionDao.delete(2);

        assertFalse(actualResult);
    }

    @Test
    void update() {
        var subscription = subscriptionDao.insert(getSubscription(1));
        subscription.setStatus(Status.EXPIRED);
        subscription.setName("Subscription-new");

        var actualResult = subscriptionDao.update(subscription);

        assertThat(actualResult).isEqualTo(subscription);
    }

    @Test
    void insert() {
        var subscription = getSubscription(1);

        var actualResult = subscriptionDao.insert(subscription);

        assertNotNull(actualResult.getId());
    }

    @Test
    void findByUserId() {
        var subscription = subscriptionDao.insert(getSubscription(1));

        var actualResult = subscriptionDao.findByUserId(subscription.getUserId());

        assertThat(actualResult).hasSize(1);
    }

    private static Subscription getSubscription(Integer userId) {
        return Subscription.builder()
                .userId(userId)
                .name("Subscription")
                .provider(Provider.GOOGLE)
                .expirationDate(getExpDate())
                .status(Status.ACTIVE)
                .build();
    }

    private static Instant getExpDate() {
        LocalDate localDate = LocalDate.parse("2023-06-30");
        LocalDateTime localDateTime = localDate.atStartOfDay();
        return localDateTime.toInstant(ZoneOffset.UTC);
    }
}