/**
 * Physical structure of the hospital system.
 *
 * Models the real-world hierarchy: a {@code MedicalFacility} (hospital or clinic)
 * contains {@code Department}s, which own {@code Room}s, an {@code Office}, and
 * optionally a {@code Storage} unit. Rooms can be specialised
 * ({@code SpecializedRoom}, {@code InternationRoom}).
 *
 * {@code Location} holds the address used on public-facing pages (maps, contact info).
 * {@code Hospital} extends {@code MedicalFacility} and is the concrete type persisted
 * in the database.
 *
 * Main curator: Viviana Fraccaroli
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.model.facility;
