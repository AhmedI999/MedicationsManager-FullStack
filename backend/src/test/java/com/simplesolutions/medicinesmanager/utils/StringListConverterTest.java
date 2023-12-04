package com.simplesolutions.medicinesmanager.utils;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@FieldDefaults(level = AccessLevel.PRIVATE)
class StringListConverterTest {
    StringListConverter converterTest;
    List<String> inputListTest;
    List<String> expectedOutput;

    @BeforeEach
    void setUp() {
        converterTest = new StringListConverter();
        inputListTest = Arrays.asList("apple", "banana", "orange");
        expectedOutput = Arrays.asList("apple", "banana", "orange");
    }

    @Test
    @DisplayName("Ensure that convertToDatabaseColumn can join attributes with ,")
    void convertToDatabaseColumn() {
        // Given
        String expected = "apple,banana,orange";
        //When
        String actual = converterTest.convertToDatabaseColumn(inputListTest);
        //Then
        assertThat(actual).isEqualTo(expected);
    }

    @Nested
    @DisplayName("convertToEntityAttribute test units")
    class StringListConverter_convertToEntityAttribute{}
    @Test
    @DisplayName("Ensure that convertToEntityAttribute return data as a list")
    void convertToEntityAttribute_returnList() {
        // Given
        String inputString = "apple,banana,orange";
        //When
        List<String> actual = converterTest.convertToEntityAttribute(inputString);
        //Then
        assertThat(actual).isEqualTo(expectedOutput);
    }
    @Test
    @DisplayName("Ensure that convertToEntityAttribute return null when data is empty")
    void convertToEntityAttribute_returnNull() {
        // Given
        String inputString = null;
        //When
        List<String> actual = converterTest.convertToEntityAttribute(inputString);
        //Then
        assertThat(actual).isNull();
    }

}