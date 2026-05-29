package com.bezkoder.spring.jpa.h2.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Navigation metadata controller.
 *
 * <p>Note: CORS is handled globally in {@code SecurityConfig}.
 * Do NOT add {@code @CrossOrigin} here.</p>
 */
@RestController
@RequestMapping("/api/navigation")
public class NavigationController {

    private static final Map<String, String> PAGE_TITLES = new HashMap<>();

    static {
        PAGE_TITLES.put("dashboard",          "Dashboard");
        PAGE_TITLES.put("funding-intelligence", "Funding Intelligence");
        PAGE_TITLES.put("reservations",        "Reservations");
        PAGE_TITLES.put("members",             "Members");
        PAGE_TITLES.put("disbursements",       "Disbursements");
        PAGE_TITLES.put("compliance",          "Compliance");
        PAGE_TITLES.put("reports-analytics",   "Reports & Analytics");
        PAGE_TITLES.put("communications",      "Communications");
        PAGE_TITLES.put("help-support",        "Help & Support");
        PAGE_TITLES.put("settings",            "Settings");
    }

    @GetMapping("/title/{pageId}")
    public Map<String, String> getPageTitle(@PathVariable String pageId) {
        String title = PAGE_TITLES.getOrDefault(pageId, "Unknown Page");
        return Map.of("title", title);
    }
}
