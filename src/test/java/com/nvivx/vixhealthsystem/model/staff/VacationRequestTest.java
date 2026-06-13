package com.nvivx.vixhealthsystem.model.staff;

import com.nvivx.vixhealthsystem.model.enums.VacationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class VacationRequestTest {
    private VacationRequest request;

    @BeforeEach
    void setUp() {
        request = new VacationRequest();
        request.setId(1);
        request.setEmployeeId(100);
        request.setEmployeeName("Mario Rossi");
        request.setStartDate(LocalDate.of(2024, 7, 10));
        request.setEndDate(LocalDate.of(2024, 7, 20));
        request.setReason("Annual vacation");
        request.setStatus(VacationStatus.PENDING);
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        assertEquals(1, request.getId());
        assertEquals(100, request.getEmployeeId());
        assertEquals("Mario Rossi", request.getEmployeeName());
        assertEquals(LocalDate.of(2024, 7, 10), request.getStartDate());
        assertEquals(LocalDate.of(2024, 7, 20), request.getEndDate());
        assertEquals("Annual vacation", request.getReason());
        assertEquals(VacationStatus.PENDING, request.getStatus());
        assertEquals("PENDING", request.getStatusName());
    }

    @Test
    void parameterizedConstructor_ShouldInitializeRequest() {
        VacationRequest newRequest = new VacationRequest(
                2, 200, "Laura Bianchi",
                LocalDate.of(2024, 8, 1), LocalDate.of(2024, 8, 10),
                "Family leave", VacationStatus.APPROVED
        );

        assertEquals(2, newRequest.getId());
        assertEquals(200, newRequest.getEmployeeId());
        assertEquals("Laura Bianchi", newRequest.getEmployeeName());
        assertEquals(LocalDate.of(2024, 8, 1), newRequest.getStartDate());
        assertEquals(LocalDate.of(2024, 8, 10), newRequest.getEndDate());
        assertEquals("Family leave", newRequest.getReason());
        assertEquals(VacationStatus.APPROVED, newRequest.getStatus());
    }

    @Test
    void getDaysRequested_ShouldCalculateCorrectDays() {
        assertEquals(11, request.getDaysRequested()); // 7/10 to 7/20 inclusive = 11 days
    }

    @Test
    void getDaysRequested_ShouldReturnZeroWhenDatesNull() {
        request.setStartDate(null);
        request.setEndDate(null);
        assertEquals(0, request.getDaysRequested());
    }

    @Test
    void getDaysRequested_ShouldHandleSingleDay() {
        request.setStartDate(LocalDate.of(2024, 7, 15));
        request.setEndDate(LocalDate.of(2024, 7, 15));
        assertEquals(1, request.getDaysRequested());
    }

    @Test
    void setStatus_WithString_ShouldConvertToEnum() {
        request.setStatus("APPROVED");
        assertEquals(VacationStatus.APPROVED, request.getStatus());
        assertEquals("APPROVED", request.getStatusName());
    }

    @Test
    void setStatus_WithNullString_ShouldSetNullEnum() {
        request.setStatus((String) null);
        assertNull(request.getStatus());
        assertNull(request.getStatusName());
    }

    @Test
    void setStatus_WithInvalidString_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            request.setStatus("INVALID");
        });
    }
}