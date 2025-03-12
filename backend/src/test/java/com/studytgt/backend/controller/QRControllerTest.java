package com.studytgt.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows; // Correct import
import static org.mockito.Mockito.*;

class QRControllerTest {

    @InjectMocks
    private QRController qrController;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServletOutputStream outputStream;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); 
    }

    /**
     * Test the `generateQRCode` method of the QRController.
     * This test checks the behavior of the `generateQRCode` method when the response's output stream is successfully returned.
     * It ensures that the content type is set to IMAGE_PNG.
     *
     * @throws Exception if any unexpected error occurs during the test
     */
    @Test
    void testGenerateQRCode() throws Exception {
        // Create QR Code
        when(response.getOutputStream()).thenReturn(outputStream);
        qrController.generateQRCode(response);

        // Assert it's an image and that data is sent 
        verify(response).setContentType(MediaType.IMAGE_PNG_VALUE);
        verify(outputStream, atLeastOnce()).flush(); 
    }

    /**
     * Test the `generateQRCode` method of the QRController to handle exceptions.
     * This test checks the behavior of the `generateQRCode` method when the output stream is not retrievable, 
     * simulating an IOException being thrown.
     * The test verifies that the exception is properly thrown by the controller.
     *
     * @throws Exception if any unexpected error occurs during the test
     */
    @Test
    void testGenerateQRCode_Exception() throws Exception {
        // Create QR Code (Failed)
        when(response.getOutputStream()).thenThrow(new IOException("Failed to get output stream"));

        // Assert Exception is Thrown
        assertThrows(Exception.class, () -> qrController.generateQRCode(response));
    }
}