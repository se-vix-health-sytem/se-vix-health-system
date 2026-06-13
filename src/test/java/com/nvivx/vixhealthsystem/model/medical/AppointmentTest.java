package com.nvivx.vixhealthsystem.model.medical;

import com.nvivx.vixhealthsystem.model.enums.AppointmentStatus;
import com.nvivx.vixhealthsystem.model.enums.PaymentStatus;
import com.nvivx.vixhealthsystem.model.person.Patient;
import com.nvivx.vixhealthsystem.model.person.employee.MedicalSpecialist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentTest {
    private Appointment appointment;
    private Patient patient;
    private MedicalSpecialist specialist;
    private LocalDateTime appointmentTime;

    @BeforeEach
    void setUp() {
        appointmentTime = LocalDateTime.now().plusDays(3).withHour(10).withMinute(0);
        patient = new Patient();
        patient.setId(1L);
        specialist = new MedicalSpecialist();
        specialist.setId(10L);

        appointment = new Appointment();
        appointment.setId(100);
        appointment.setDateTime(appointmentTime);
        appointment.setDuration(30);
        appointment.setNotes("Routine checkup");
        appointment.setPatient(patient);
        appointment.setMedicalSpecialist(specialist);
        appointment.setPaymentStatus(false);
        appointment.setStatus(AppointmentStatus.CONFIRMED);
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        assertEquals(100, appointment.getId());
        assertEquals(appointmentTime, appointment.getDateTime());
        assertEquals(30, appointment.getDuration());
        assertEquals("Routine checkup", appointment.getNotes());
        assertEquals(patient, appointment.getPatient());
        assertEquals(specialist, appointment.getMedicalSpecialist());
        assertFalse(appointment.isPaymentStatus());
        assertEquals(PaymentStatus.UNPAID, appointment.getPaymentStatus());
        assertEquals("CONFIRMED", appointment.getStatus());
        assertEquals(AppointmentStatus.CONFIRMED, appointment.getStatusEnum());
    }

    @Test
    void setPaymentStatus_WithBoolean_ShouldSetCorrectPaymentStatus() {
        appointment.setPaymentStatus(true);
        assertEquals(PaymentStatus.PAID, appointment.getPaymentStatus());
        assertTrue(appointment.isPaymentStatus());

        appointment.setPaymentStatus(false);
        assertEquals(PaymentStatus.UNPAID, appointment.getPaymentStatus());
        assertFalse(appointment.isPaymentStatus());
    }

    @Test
    void setPaymentStatus_WithEnum_ShouldSetCorrectPaymentStatus() {
        appointment.setPaymentStatus(PaymentStatus.PAID);
        assertEquals(PaymentStatus.PAID, appointment.getPaymentStatus());
        assertTrue(appointment.isPaymentStatus());
    }

    @Test
    void setStatus_WithString_ShouldSetCorrectStatus() {
        appointment.setStatus("PENDING");
        assertEquals("PENDING", appointment.getStatus());
        assertEquals(AppointmentStatus.PENDING, appointment.getStatusEnum());

        appointment.setStatus("CANCELLED");
        assertEquals("CANCELLED", appointment.getStatus());
        assertEquals(AppointmentStatus.CANCELLED, appointment.getStatusEnum());
    }

    @Test
    void setStatus_WithEnum_ShouldSetCorrectStatus() {
        appointment.setStatus(AppointmentStatus.COMPLETED);
        assertEquals("COMPLETED", appointment.getStatus());
        assertEquals(AppointmentStatus.COMPLETED, appointment.getStatusEnum());
    }

    @Test
    void setStatus_WithNull_ShouldHandleGracefully() {
        appointment.setStatus((String) null);
        assertNull(appointment.getStatus());
        assertNull(appointment.getStatusEnum());
    }

    @Test
    void isActive_ShouldReturnTrueForActiveAppointments() {
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        assertTrue(appointment.isActive());

        appointment.setStatus(AppointmentStatus.PENDING);
        assertTrue(appointment.isActive());

        appointment.setStatus(AppointmentStatus.RESCHEDULED);
        assertTrue(appointment.isActive());
    }

    @Test
    void isActive_ShouldReturnFalseForInactiveAppointments() {
        appointment.setStatus(AppointmentStatus.CANCELLED);
        assertFalse(appointment.isActive());

        appointment.setStatus(AppointmentStatus.COMPLETED);
        assertFalse(appointment.isActive());
    }

    @Test
    void isCancellable_ShouldReturnTrueForActiveAppointments() {
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        assertTrue(appointment.isCancellable());

        appointment.setStatus(AppointmentStatus.PENDING);
        assertTrue(appointment.isCancellable());
    }

    @Test
    void isCancellable_ShouldReturnFalseForInactiveAppointments() {
        appointment.setStatus(AppointmentStatus.CANCELLED);
        assertFalse(appointment.isCancellable());

        appointment.setStatus(AppointmentStatus.COMPLETED);
        assertFalse(appointment.isCancellable());
    }

    @Test
    void cancel_ShouldSetStatusToCancelled() {
        appointment.cancel();
        assertEquals(AppointmentStatus.CANCELLED, appointment.getStatusEnum());
    }

    @Test
    void cancel_ShouldThrowExceptionWhenAlreadyCancelled() {
        appointment.setStatus(AppointmentStatus.CANCELLED);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            appointment.cancel();
        });

        assertTrue(exception.getMessage().contains("cannot be cancelled"));
    }

    @Test
    void cancel_ShouldThrowExceptionWhenAlreadyCompleted() {
        appointment.setStatus(AppointmentStatus.COMPLETED);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            appointment.cancel();
        });

        assertTrue(exception.getMessage().contains("cannot be cancelled"));
    }

    @Test
    void reschedule_ShouldUpdateDateTimeAndSetStatusToRescheduled() {
        LocalDateTime newTime = LocalDateTime.now().plusDays(5);

        appointment.reschedule(newTime);

        assertEquals(newTime, appointment.getDateTime());
        assertEquals(AppointmentStatus.RESCHEDULED, appointment.getStatusEnum());
    }

    @Test
    void reschedule_ShouldThrowExceptionWhenAppointmentIsCancelled() {
        appointment.setStatus(AppointmentStatus.CANCELLED);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            appointment.reschedule(LocalDateTime.now().plusDays(1));
        });

        assertTrue(exception.getMessage().contains("cannot be rescheduled"));
    }

    @Test
    void reschedule_ShouldThrowExceptionWhenAppointmentIsCompleted() {
        appointment.setStatus(AppointmentStatus.COMPLETED);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            appointment.reschedule(LocalDateTime.now().plusDays(1));
        });

        assertTrue(exception.getMessage().contains("cannot be rescheduled"));
    }

    @Test
    void parameterizedConstructor_ShouldCreateAppointmentWithStatusConfirmed() {
        Appointment newAppointment = new Appointment(
                LocalDateTime.now(), 45, "Test notes", patient, specialist
        );

        assertEquals("CONFIRMED", newAppointment.getStatus());
        assertEquals(45, newAppointment.getDuration());
        assertEquals("Test notes", newAppointment.getNotes());
        assertEquals(patient, newAppointment.getPatient());
        assertEquals(specialist, newAppointment.getMedicalSpecialist());
    }
}