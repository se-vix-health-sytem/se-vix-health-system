/**
 * Public-facing site controllers — no authentication required.
 *
 * These pages are accessible to anyone and serve as the hospital's
 * public information portal.
 *
 * <ul>
 *   <li>{@code HomePageController}     — landing page ({@code /})</li>
 *   <li>{@code AboutController}        — about the hospital ({@code /about})</li>
 *   <li>{@code ContactController}      — contact form ({@code /contact})</li>
 *   <li>{@code DepartmentController}   — department list and detail pages</li>
 *   <li>{@code SpecialistController}   — specialist listings and profiles</li>
 *   <li>{@code MapController}          — hospital locations and directions</li>
 *   <li>{@code QuestionnaireController}— patient intake questionnaire</li>
 *   <li>{@code LegalController}        — privacy policy, GDPR, terms, accessibility</li>
 * </ul>
 *
 * All routes in this package are listed under {@code permitAll()} in
 * {@link com.nvivx.vixhealthsystem.config.SecurityConfig}.
 *
 * Main curator: Lorena Valentina Buitrón Zambrano
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.controllers.site;
