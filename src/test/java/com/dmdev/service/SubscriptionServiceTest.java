package com.dmdev.service;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.exception.SubscriptionException;
import com.dmdev.exception.ValidationException;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import com.dmdev.validator.Error;
import com.dmdev.validator.ValidationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionDao subscriptionDao;
    @Mock
    private CreateSubscriptionMapper createSubscriptionMapper;
    @Mock
    private CreateSubscriptionValidator createSubscriptionValidator;
    @Mock
    private Clock clock;
    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    void upsertSuccess() {
        Subscription subscription = getSubscription(Status.ACTIVE);
        CreateSubscriptionDto subscriptionDto = getSubscriptionDto();
        doReturn(new ValidationResult()).when(createSubscriptionValidator).validate(subscriptionDto);
        doReturn(List.of(subscription)).when(subscriptionDao).findByUserId(subscriptionDto.getUserId());
        doReturn(subscription).when(subscriptionDao).upsert(subscription);

        Subscription actualResult = subscriptionService.upsert(subscriptionDto);

        assertThat(actualResult).isEqualTo(subscription);
        verify(subscriptionDao).upsert(subscription);
    }

    @Test
    void upsertFailed() {
        CreateSubscriptionDto subscriptionDto = getSubscriptionDto();
        ValidationResult validationResult = new ValidationResult();
        validationResult.add(Error.of(101, "message"));
        doReturn(validationResult).when(createSubscriptionValidator).validate(subscriptionDto);

        assertThrows(ValidationException.class, () -> subscriptionService.upsert(subscriptionDto));
        verifyNoInteractions(subscriptionDao, createSubscriptionMapper);
    }

    @Test
    void cancelSuccess() {
        Subscription subscription = getSubscription(Status.ACTIVE);
        doReturn(Optional.of(subscription)).when(subscriptionDao).findById(subscription.getId());

        subscriptionService.cancel(subscription.getId());

        assertThat(subscription.getStatus()).isEqualTo(Status.CANCELED);
        verify(subscriptionDao).update(subscription);
    }

    @Test
    void shouldThrowExceptionIfStatusNotActive() {
        Subscription subscription = getSubscription(Status.EXPIRED);
        doReturn(Optional.of(subscription)).when(subscriptionDao).findById(subscription.getId());

        assertThrows(SubscriptionException.class, () -> subscriptionService.cancel(subscription.getId()));
    }

    @Test
    void expireSuccess() {
        Subscription subscription = getSubscription(Status.ACTIVE);
        Clock fixedClock = Clock.fixed(Instant.now(), ZoneOffset.UTC);
        doReturn(Optional.of(subscription)).when(subscriptionDao).findById(subscription.getId());
        doReturn(fixedClock.instant()).when(clock).instant();

        subscriptionService.expire(subscription.getId());

        assertThat(subscription.getStatus()).isEqualTo(Status.EXPIRED);
        assertThat(subscription.getExpirationDate()).isEqualTo(Instant.now(fixedClock));
        verify(subscriptionDao).update(subscription);
    }

    @Test
    void shouldThrowExceptionIfStatusExpired() {
        Subscription subscription = getSubscription(Status.EXPIRED);
        doReturn(Optional.of(subscription)).when(subscriptionDao).findById(subscription.getId());

        assertThrows(SubscriptionException.class, () -> subscriptionService.expire(subscription.getId()));
    }

    private static Subscription getSubscription(Status status) {
        return Subscription.builder()
                .id(1)
                .userId(1)
                .name("Subscription")
                .provider(Provider.GOOGLE)
                .expirationDate(getExpDate())
                .status(status)
                .build();
    }

    private static CreateSubscriptionDto getSubscriptionDto() {
        return CreateSubscriptionDto.builder()
                .userId(1)
                .name("Subscription")
                .provider("GOOGLE")
                .expirationDate(getExpDate())
                .build();
    }

    private static Instant getExpDate() {
        LocalDate localDate = LocalDate.parse("2023-06-30");
        LocalDateTime localDateTime = localDate.atStartOfDay();
        return localDateTime.toInstant(ZoneOffset.UTC);
    }
}