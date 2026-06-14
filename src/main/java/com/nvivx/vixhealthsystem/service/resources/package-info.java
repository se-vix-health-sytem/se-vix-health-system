/**
 * Hospital resource services — inventory, machinery, and rooms.
 *
 * <ul>
 *   <li>{@code InventoryService}      — manages consumable stock levels; buyers add
 *                                       resources, employees take them; both actions
 *                                       are audit-logged (UC25, UC27)</li>
 *   <li>{@code MachineryService}      — tracks the status and maintenance history of
 *                                       medical machinery</li>
 *   <li>{@code RoomService}           — queries room availability across departments</li>
 *   <li>{@code ResourceTakeLogStore}  — in-memory log of every resource taken by an employee,
 *                                       displayed on the staff manager's dashboard</li>
 * </ul>
 *
 * Main curator: Lorena Valentina Buitrón Zambrano
 *
 * Note: All team members may contribute to files in this package.
 */
package com.nvivx.vixhealthsystem.service.resources;
