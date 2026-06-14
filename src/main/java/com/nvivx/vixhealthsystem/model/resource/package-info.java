/**
 * Hospital resources — consumables and medical machinery.
 *
 * <ul>
 *   <li>{@code Resource} — a trackable consumable item (gloves, syringes, medication, etc.)
 *                          with a name, description, and unit price.</li>
 *   <li>{@code Storage}  — holds a map of {@code Resource → quantity}; owned by a
 *                          {@code MedicalFacility}. Domain methods enforce that quantity
 *                          never goes negative.</li>
 *   <li>{@code Machinery}— a piece of medical equipment with a serial number and
 *                          operational status ({@code MachineStatus}).</li>
 * </ul>
 *
 * Main curator: Viviana Fraccarolli
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.model.resource;
