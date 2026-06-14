/**
 * Data Transfer Objects that move data across layer boundaries.
 *
 * DTOs in this package are kept intentionally thin — they carry only what
 * the controller or service needs to send or receive, without leaking JPA
 * entity details to the outside world.
 *
 * Classes:
 * <ul>
 *   <li>{@code AppointmentResponse}     — appointment data returned to the patient portal</li>
 *   <li>{@code CreateAppointmentRequest}— fields submitted when booking a new appointment</li>
 *   <li>{@code PaymentRequest}          — card details and amount for the payment flow</li>
 *   <li>{@code PaymentResponse}         — outcome of a payment attempt (transaction ID, status)</li>
 *   <li>{@code PaymentStatus}           — point-in-time status snapshot for polling</li>
 * </ul>
 *
 * Main curator: Navjot Kaur
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.dto;
