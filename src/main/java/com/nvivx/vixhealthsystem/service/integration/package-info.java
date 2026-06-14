/**
 * External integration services — Firebase, payments, notifications, questionnaire.
 *
 * Everything here talks to something outside the core domain:
 * <ul>
 *   <li>{@code FirebaseAuthService}  — creates, deletes, and authenticates Firebase
 *                                      Authentication accounts for employees and patients</li>
 *   <li>{@code PaymentService}       — simulates a payment gateway (demo/prototype only;
 *                                      no real charges are made)</li>
 *   <li>{@code NotificationService}  — logs simulated email notifications (demo mode;
 *                                      no actual mail is sent)</li>
 *   <li>{@code QuestionnaireService} — processes patient intake questionnaires and
 *                                      returns a triage recommendation</li>
 * </ul>
 *
 * Main curator: Lorena Valentina Buitrón Zambrano
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.service.integration;
