package com.nvivx.vixhealthsystem.controllers.site;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @brief Controller for the public hospital-location map pages — base URL {@code /map}.
 *
 * Provides the two location-related views: an interactive map that pins all
 * VIX Health System facilities, and a directions page that helps visitors plan
 * their journey.  Facility coordinates are hard-coded to match the seeded
 * {@code MedicalFacilities} database rows; the data is serialised into plain
 * {@code Map} objects so Thymeleaf's inline JavaScript engine produces valid
 * JS literals without any extra serialisation step.
 */
@Controller
@RequestMapping("/map")
public class MapController {

    // =========================================================
    // GET HANDLERS
    // =========================================================

    /**
     * GET /map/hospitals — render the interactive hospital-location map.
     *
     * Each facility is projected into a plain {@link Map} ({@code name}, {@code lat},
     * {@code lng}, {@code address}, {@code phone}, {@code type}) so that Thymeleaf's
     * {@code th:inline="javascript"} block can serialise the list directly as a JS
     * array without a custom converter.
     *
     * @param model  Receives {@code locations} (list of plain maps) and {@code pageTitle}.
     * @return       Thymeleaf template {@code site/map/hospitals}.
     */
    @GetMapping("/hospitals")
    public String showHospitalMap(Model model) {
        // Convert to List<Map> so Thymeleaf's JS inline serializer produces valid JS objects
        List<Map<String, Object>> locations = getHospitalLocations().stream()
                .map(loc -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("name",    loc.getName());
                    m.put("lat",     loc.getLat());
                    m.put("lng",     loc.getLng());
                    m.put("address", loc.getAddress());
                    m.put("phone",   loc.getPhone());
                    m.put("type",    loc.getType());
                    return m;
                })
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("locations", locations);
        model.addAttribute("pageTitle", "Find Our Locations");
        return "site/map/hospitals";
    }

    /**
     * GET /map/directions — render the directions and transport information page.
     *
     * @param model  Receives {@code pageTitle}.
     * @return       Thymeleaf template {@code site/map/directions}.
     */
    @GetMapping("/directions")
    public String getDirections(Model model) {
        model.addAttribute("pageTitle", "Get Directions");
        return "site/map/directions";
    }

    // =========================================================
    // HELPERS
    // =========================================================

    /**
     * Build the static list of VIX Health System facility locations.
     *
     * Coordinates and contact details are hard-coded here to mirror the seeded
     * {@code MedicalFacilities} rows in the database.  If new facilities are
     * added to the database, this list must be kept in sync manually until a
     * proper repository-backed approach is implemented.
     *
     * @return List of {@link HospitalLocation} objects for the three current facilities.
     */
    private List<HospitalLocation> getHospitalLocations() {
        List<HospitalLocation> locations = new ArrayList<>();

        // Hardcoded locations matching database's MedicalFacilities
        locations.add(new HospitalLocation(
                "VIX Central Hospital",
                46.066422, 11.119928,
                "Via San Camillo 1, 38122 Trento TN",
                "+39 0461 000 001",
                "HOSPITAL"));

        locations.add(new HospitalLocation(
                "VIX Tecnomed Nord",
                46.082944, 11.119928,
                "Via Brennero 45, 38100 Trento TN",
                "+39 0461 000 002",
                "CLINIC"));

        locations.add(new HospitalLocation(
                "VIX Tecnomed Sud",
                45.890584, 11.039744,
                "Via Malpensa 12, 38068 Rovereto TN",
                "+39 0461 000 003",
                "CLINIC"));

        return locations;
    }

    // =========================================================
    // INNER CLASS
    // =========================================================

    /**
     * Lightweight value object for a single hospital/clinic location.
     *
     * Holds the data needed to drop a pin on the front-end map and populate
     * the info-window popup (name, coordinates, address, phone, type).
     */
    public static class HospitalLocation {
        private final String name;
        private final double lat;
        private final double lng;
        private final String address;
        private final String phone;
        private final String type;

        public HospitalLocation(String name, double lat, double lng, String address, String phone, String type) {
            this.name = name;
            this.lat = lat;
            this.lng = lng;
            this.address = address;
            this.phone = phone;
            this.type = type;
        }

        public String getName() { return name; }
        public double getLat() { return lat; }
        public double getLng() { return lng; }
        public String getAddress() { return address; }
        public String getPhone() { return phone; }
        public String getType() { return type; }
    }
}
