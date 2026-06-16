/**
 * MVC controllers, organised by user role.
 *
 * There are no REST-only endpoints outside {@code controllers.services};
 * everything else returns a Thymeleaf template name or a redirect.
 * Role-specific routes are protected by Spring Security before the controller
 * is ever reached, so controllers don't re-check permissions themselves.
 *
 * Sub-packages:
 * <ul>
 *   <li>{@code patient}  - patient auth (SPID/CIE mock), appointment booking,
 *                          profile, and medical records</li>
 *   <li>{@code services} - appointment management API and payment</li>
 *   <li>{@code site}     - public-facing pages, no authentication required</li>
 *   <li>{@code staff}    - one controller per staff role, plus shared auth
 *                          and the resource-take flow</li>
 * </ul>
 *
 * Main curator: Lorena Valentina Buitrón Zambrano
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.controllers;