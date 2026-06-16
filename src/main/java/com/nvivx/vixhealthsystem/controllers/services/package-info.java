/**
 * Shared service controllers: appointment CRUD and payment processing.
 *
 * Unlike the role-specific sub-packages, these two controllers serve multiple
 * user types. {@code AppointmentController} is used by both secretaries and
 * specialists; {@code PaymentController} is accessible to patients and secretaries.
 *
 * The payment flow is simulated (no real charges) and exists only to demonstrate
 * the end-to-end booking lifecycle for the demo.
 *
 * Main curator: Lorena Valentina Buitrón Zambrano
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.controllers.services;