package com.paypilot.service;

import com.itextpdf.text.DocumentException;
import com.paypilot.model.Bill;
import com.paypilot.model.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PdfGeneratorServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private PdfGeneratorService pdfGeneratorService;

    @Test
    void generatePdf_ReturnsBytes_AndUsesUserService() throws DocumentException, IOException {
        Bill bill = new Bill();
        bill.setId(1L);
        bill.setUserId(10L);
        bill.setTitle("Internet Bill");
        bill.setCategory(Category.OTHER);
        bill.setAmount(999.99);
        bill.setDueDate(LocalDate.now());

        com.paypilot.model.User u = new com.paypilot.model.User();
        try {
            // setName may exist on User; set if available to avoid nulls in PDF
            java.lang.reflect.Method m = u.getClass().getMethod("setName", String.class);
            m.invoke(u, "Test User");
        } catch (NoSuchMethodException ignored) {
            // ignore if method not present
        } catch (Exception e) {
            // best-effort; test should still pass
        }
        when(userService.getUserById(10L)).thenReturn(u);

        ByteArrayOutputStream os = pdfGeneratorService.generatePdf(bill);
        assertNotEquals(0, os.toByteArray().length);
        verify(userService).getUserById(10L);
    }
}
