package vn.edu.hoasen.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import com.vaadin.flow.server.VaadinSession;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageServiceTest {

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getMessage_WithKey_ShouldReturnMessage() {
        String key = "test.key";
        String expectedMessage = "Test Message";
        when(messageSource.getMessage(eq(key), eq(null), eq(key), any(Locale.class)))
            .thenReturn(expectedMessage);

        String result = messageService.getMessage(key);

        assertEquals(expectedMessage, result);
        verify(messageSource).getMessage(eq(key), eq(null), eq(key), any(Locale.class));
    }

    @Test
    void getMessage_WithKeyAndArgs_ShouldReturnFormattedMessage() {
        String key = "test.key";
        Object[] args = {"arg1", "arg2"};
        String expectedMessage = "Test Message with arg1 and arg2";
        when(messageSource.getMessage(eq(key), eq(args), eq(key), any(Locale.class)))
            .thenReturn(expectedMessage);

        String result = messageService.getMessage(key, args);

        assertEquals(expectedMessage, result);
        verify(messageSource).getMessage(eq(key), eq(args), eq(key), any(Locale.class));
    }

    @Test
    void getCurrentLanguage_WithoutSession_ShouldReturnDefault() {
        String result = messageService.getCurrentLanguage();
        assertEquals("en", result);
    }
}