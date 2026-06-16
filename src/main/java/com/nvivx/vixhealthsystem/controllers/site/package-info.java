/**
 * Public site controllers, no authentication required.
 *
 * These cover the hospital's outward-facing web presence. All routes here are
 * listed under {@code permitAll()} in
 * {@link com.nvivx.vixhealthsystem.config.SecurityConfig}
 * and should never store session state or reference protected resources.
 *
 * Controllers:
 * <ul>
 *   <li>{@code HomePageController}      - landing page ({@code /})</li>
 *   <li>{@code AboutController}         - hospital information ({@code /about})</li>
 *   <li>{@code ContactController}       - contact form ({@code /contact})</li>
 *   <li>{@code DepartmentController}    - department list and detail pages</li>
 *   <li>{@code SpecialistController}    - specialist listings and profiles</li>
 *   <li>{@code MapController}           - hospital locations and directions</li>
 *   <li>{@code QuestionnaireController} - symptom triage questionnaire</li>
 *   <li>{@code LegalController}         - privacy policy, GDPR, terms, accessibility</li>
 * </ul>
 *
 * Main curator: Lorena Valentina Buitrón Zambrano
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.controllers.site;