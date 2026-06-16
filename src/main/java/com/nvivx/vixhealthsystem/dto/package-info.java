/**
 * Data Transfer Objects for crossing layer boundaries.
 *
 * These are intentionally thin. They carry only what a controller or service
 * needs to send or receive, without leaking JPA entity internals to the outside
 * world. If you find yourself adding JPA relationships or business methods to a
 * DTO, that logic belongs in the model or service layer instead.
 *
 * The payment DTOs ({@code PaymentRequest}, {@code PaymentResponse},
 * {@code PaymentStatus}) support the simulated payment flow and should never
 * reference real card-processing APIs. {@code CreateAppointmentRequest} and
 * {@code AppointmentResponse} handle the booking lifecycle on the patient side.
 *
 * Main curator: Navjot Kaur
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.dto;