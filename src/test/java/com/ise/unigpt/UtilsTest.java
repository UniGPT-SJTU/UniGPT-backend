package com.ise.unigpt;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.ise.unigpt.utils.CookieUtils;
import com.ise.unigpt.utils.PaginationUtils;
import com.ise.unigpt.utils.StringTemplateParser;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UtilsTest {


    @Test
    void testPaginate() {
        List<Integer> data = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // Page 0, pageSize 3
        List<Integer> result = PaginationUtils.paginate(data, 0, 3);
        assertEquals(Arrays.asList(1, 2, 3), result);

        // Page 1, pageSize 3
        result = PaginationUtils.paginate(data, 1, 3);
        assertEquals(Arrays.asList(4, 5, 6), result);

        // Page 2, pageSize 3
        result = PaginationUtils.paginate(data, 2, 3);
        assertEquals(Arrays.asList(7, 8, 9), result);

        // Page 3, pageSize 3 (last page with less items)
        result = PaginationUtils.paginate(data, 3, 3);
        assertEquals(Collections.singletonList(10), result);

        // Page out of range
        result = PaginationUtils.paginate(data, 4, 3);
        assertTrue(result.isEmpty());
    }

    @Test
    void testInterpolate() {
        String input = "Hello, ++{name}! Welcome to ++{place}.";
        Map<String, String> keyValuePairs = Map.of(
                "name", "Alice",
                "place", "Wonderland"
        );

        String result = StringTemplateParser.interpolate(input, keyValuePairs);
        assertEquals("Hello, Alice! Welcome to Wonderland.", result);

        // Test with missing placeholders
        keyValuePairs = Map.of("name", "Bob");
        result = StringTemplateParser.interpolate(input, keyValuePairs);
        assertEquals("Hello, Bob! Welcome to .", result);

        // Test with no placeholders
        input = "Hello, World!";
        keyValuePairs = Map.of("name", "Charlie");
        result = StringTemplateParser.interpolate(input, keyValuePairs);
        assertEquals("Hello, World!", result);
    }

    @Test
    void testSetCookie() {
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        String name = "token";
        String value = "abc123";
        int maxAge = 3600;

        CookieUtils.set(response, name, value, maxAge);

        String expectedCookieValue = "token=abc123; HttpOnly; Secure; Max-Age=3600; Path=/; SameSite=None";
        verify(response, times(1)).setHeader("Set-Cookie", expectedCookieValue);
    }
}
