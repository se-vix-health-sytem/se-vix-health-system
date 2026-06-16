/**
 * Consumable resources and medical machinery.
 *
 * {@code Resource} is anything that gets stocked and consumed: gloves, syringes,
 * medication. {@code Storage} is the container that holds them, owned by a facility;
 * its domain methods enforce that quantity never drops below zero.
 * {@code Machinery} is tracked separately because it has a different lifecycle
 * (status transitions, maintenance history) rather than a quantity that goes up and down.
 *
 * Main curator: Viviana Fraccaroli
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.model.resource;