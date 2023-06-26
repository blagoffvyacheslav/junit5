package com.dmdev.mapper;

import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

class CreateSubscriptionMapperTest {

    private final CreateSubscriptionMapper mapper = CreateSubscriptionMapper.getInstance();

    @Test
    void map() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("Subscription")
                .provider(Provider.GOOGLE.name())
                .expirationDate(getExpDate())
                .build();

        Subscription actualResult = mapper.map(dto);

        Subscription expectedResult = Subscription.builder()
                .userId(1)
                .name("Subscription")
                .provider(Provider.GOOGLE)
                .expirationDate(getExpDate())
                .status(Status.ACTIVE)
                .build();
        Assertions.assertThat(actualResult).isEqualTo(expectedResult);
    }

    private static Instant getExpDate() {
        LocalDate localDate = LocalDate.parse("2023-06-30");
        LocalDateTime localDateTime = localDate.atStartOfDay();
        return localDateTime.toInstant(ZoneOffset.UTC);
    }
}