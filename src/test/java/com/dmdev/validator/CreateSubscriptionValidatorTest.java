package com.dmdev.validator;

import com.dmdev.dto.CreateSubscriptionDto;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class CreateSubscriptionValidatorTest {

    private final CreateSubscriptionValidator validator = CreateSubscriptionValidator.getInstance();

    @Test
    void shouldPassValidation() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("Subscription")
                .provider("GOOGLE")
                .expirationDate(getInstant())
                .build();

        ValidationResult actualResult = validator.validate(dto);

        assertFalse(actualResult.hasErrors());
    }

    @Test
    void invalidUserID() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(null)
                .name("Subscription")
                .provider("GOOGLE")
                .expirationDate(getInstant())
                .build();

        ValidationResult actualResult = validator.validate(dto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(100);
    }

    @Test
    void invalidName() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("")
                .provider("GOOGLE")
                .expirationDate(getInstant())
                .build();

        ValidationResult actualResult = validator.validate(dto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(101);
    }

    @Test
    void invalidProvider() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("Subscription")
                .provider("GOGLE")
                .expirationDate(getInstant())
                .build();

        ValidationResult actualResult = validator.validate(dto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(102);
    }

    @Test
    void expirationDateBeforeNow() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("Subscription")
                .provider("GOOGLE")
                .expirationDate(Instant.now().minus(1, ChronoUnit.DAYS))
                .build();

        ValidationResult actualResult = validator.validate(dto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(103);
    }

    @Test
    void expirationDateIsNull() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("Subscription")
                .provider("GOOGLE")
                .expirationDate(null)
                .build();

        ValidationResult actualResult = validator.validate(dto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(103);
    }

    @Test
    void invalidUserIdNameProviderExpirationDate() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(null)
                .name("")
                .provider("GOGLE")
                .expirationDate(null)
                .build();

        ValidationResult actualResult = validator.validate(dto);

        assertThat(actualResult.getErrors()).hasSize(4);
        List<Integer> errorCodes = actualResult.getErrors().stream()
                .map(Error::getCode)
                .toList();
        assertThat(errorCodes).contains(100, 101, 102, 103);
    }

    private static Instant getInstant() {
        LocalDate localDate = LocalDate.parse("2023-06-30");
        LocalDateTime localDateTime = localDate.atStartOfDay();
        return localDateTime.toInstant(ZoneOffset.UTC);
    }
}