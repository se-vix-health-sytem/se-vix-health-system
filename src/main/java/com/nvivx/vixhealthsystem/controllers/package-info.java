/**
 * MVC controllers — one sub-package per user role.
 *
 * All controllers return Thymeleaf template names or redirects; there are no
 * REST-only endpoints except inside {@code controllers.services} (payment status
 * polling) and {@code controllers.services.AppointmentController}.
 *
 * Sub-packages:
 * <ul>
 *   <li>{@code patient}  — patient authentication (SPID/CIE mock), appointment booking,
 *                          profile, and medical records</li>
 *   <li>{@code services} — appointment management API and payment processing</li>
 *   <li>{@code site}     — public-facing pages (home, about, departments, map, contact,
 *                          specialists, questionnaire, legal)</li>
 *   <li>{@code staff}    — all staff dashboards: specialist, secretary, buyer, technician,
 *                          staff manager; plus the shared auth and resource-take flow</li>
 * </ul>
 *
 * Main curator: Lorena Valentina Buitrón Zambrano
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.controllers;
