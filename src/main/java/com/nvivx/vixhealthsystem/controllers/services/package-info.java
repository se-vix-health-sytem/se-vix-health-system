/**
 * Service-layer controllers — appointment management and payment.
 *
 * Unlike the role-specific sub-packages, these controllers handle shared
 * functionality accessed by multiple user types:
 *
 * <ul>
 *   <li>{@code AppointmentController} — CRUD operations on appointments; used by both
 *                                        secretaries and medical specialists</li>
 *   <li>{@code PaymentController}      — shows the payment form, processes (simulated)
 *                                        charges, and serves the confirmation page</li>
 * </ul>
 *
 * Main curator: Lorena Valentina Buitrón Zambrano
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.controllers.services;
