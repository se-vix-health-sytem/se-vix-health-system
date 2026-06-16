/**
 * Adapters for external systems: Firebase, payments, notifications, and triage.
 *
 * All four services here interact with something outside the core domain.
 * In the current demo setup none of the side-effecting ones (payment,
 * notifications) do anything real; they log to console and return simulated
 * results. That boundary is intentional so the system runs without live credentials.
 *
 * <ul>
 *   <li>{@code FirebaseAuthService}  - creates, deletes, and authenticates
 *                                      Firebase accounts for staff and patients</li>
 *   <li>{@code PaymentService}       - simulates a payment gateway; no charges made</li>
 *   <li>{@code NotificationService}  - logs simulated email notifications; no mail sent</li>
 *   <li>{@code QuestionnaireService} - processes symptom input and returns a
 *                                      department and urgency recommendation</li>
 * </ul>
 *
 * Main curator: Lorena Valentina Buitrón Zambrano
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.service.integration;