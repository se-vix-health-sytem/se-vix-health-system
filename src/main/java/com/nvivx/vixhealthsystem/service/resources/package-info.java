/**
 * Resource management services: inventory, machinery, and room availability.
 *
 * The split between {@code InventoryService} (consumables) and
 * {@code MachineryService} (equipment) reflects a real operational distinction;
 * consumables are taken and replenished while machinery is tracked by status
 * transitions and maintenance history.
 *
 * <ul>
 *   <li>{@code InventoryService}     - stock levels; buyers add, employees take;
 *                                      both actions are audit-logged (UC25, UC27)</li>
 *   <li>{@code MachineryService}     - machine status tracking and maintenance history</li>
 *   <li>{@code RoomService}          - bed availability queries across departments</li>
 *   <li>{@code ResourceTakeLogStore} - in-memory take log for the staff manager's view</li>
 * </ul>
 *
 * Main curator: Lorena Valentina Buitrón Zambrano
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.service.resources;