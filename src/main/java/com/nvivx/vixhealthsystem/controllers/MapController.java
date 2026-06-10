// src/main/java/com/nvivx/vixhealthsystem/controllers/MapController.java
package com.nvivx.vixhealthsystem.controllers;

import com.nvivx.vixhealthsystem.model.facility.Location;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/map")
public class MapController {

    @GetMapping("/hospitals")
    public String showHospitalMap(Model model) {
        List<HospitalLocation> locations = getHospitalLocations();
        model.addAttribute("locations", locations);
        model.addAttribute("apiKey", "${GOOGLE_MAPS_API_KEY}"); // Replace with env variable
        model.addAttribute("pageTitle", "Find Our Locations");
        return "map/hospitals";
    }

    @GetMapping("/directions")
    public String getDirections(Model model) {
        model.addAttribute("pageTitle", "Get Directions");
        return "map/directions";
    }

    private List<HospitalLocation> getHospitalLocations() {
        List<HospitalLocation> locations = new ArrayList<>();
        locations.add(new HospitalLocation("VIX Central Hospital",
                46.066422, 11.119928,
                "Via San Camillo 1, 38122 Trento TN",
                "+39 0461 000 001",
                "HOSPITAL"));
        locations.add(new HospitalLocation("VIX Tecnomed Nord",
                46.082944, 11.119928,
                "Via Brennero 45, 38100 Trento TN",
                "+39 0461 000 002",
                "CLINIC"));
        locations.add(new HospitalLocation("VIX Tecnomed Sud",
                45.890584, 11.039744,
                "Via Malpensa 12, 38068 Rovereto TN",
                "+39 0461 000 003",
                "CLINIC"));
        return locations;
    }

    class HospitalLocation {
        String name;
        double lat;
        double lng;
        String address;
        String phone;
        String type;

        public HospitalLocation(String name, double lat, double lng, String address, String phone, String type) {
            this.name = name;
            this.lat = lat;
            this.lng = lng;
            this.address = address;
            this.phone = phone;
            this.type = type;
        }
        // Getters...
        public String getName() { return name; }
        public double getLat() { return lat; }
        public double getLng() { return lng; }
        public String getAddress() { return address; }
        public String getPhone() { return phone; }
        public String getType() { return type; }
    }
}