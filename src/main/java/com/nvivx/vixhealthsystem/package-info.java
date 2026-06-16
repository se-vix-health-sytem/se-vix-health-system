/**
 * Root package of the VIX Health System.
 *
 * VIX is a hospital management platform built for a university software
 * engineering project. It handles patient authentication via a mock SPID/CIE flow,
 * appointment booking, staff management across six role types, medical records,
 * inventory and machinery tracking, and a simulated payment flow.
 *
 * The code is split into eight sub-packages: {@code config} (security and Firebase
 * bootstrap), {@code controllers} (MVC, one sub-package per role), {@code database}
 * (low-level JDBC utilities), {@code dto} (cross-layer data carriers), {@code exception}
 * (typed business exceptions), {@code model} (JPA entities and enums), {@code repository}
 * (Spring Data and JSON-backed stores), and {@code service} (all business logic).
 *
 * Entry point: {@link com.nvivx.vixhealthsystem.VixHealthSystemApplication}
 */
package com.nvivx.vixhealthsystem;