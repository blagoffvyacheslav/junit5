package com.dmdev.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class PropertiesUtilTest {

    @ParameterizedTest
    @MethodSource("com.dmdev.util.PropertiesUtilTest#getPropertyArguments")
    void checkProperties(String key, String expectedValue) {
        String actualResult = PropertiesUtil.get(key);

        assertThat(actualResult).isEqualTo(expectedValue);
    }

    static Stream<Arguments> getPropertyArguments() {
        return Stream.of(
                Arguments.of("db.url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"),
                Arguments.of("db.user", "sa"),
                Arguments.of("db.password", ""),
                Arguments.of("db.driver", "org.h2.Driver")
        );
    }
}