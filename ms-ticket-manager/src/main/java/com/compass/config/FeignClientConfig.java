package com.compass.config;

import com.compass.dto.EventDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-event-manager", url = "http://localhost:8081")
public interface FeignClientConfig {
    @GetMapping("/events/get-event/{eventId}")
    EventDTO getEventById(@PathVariable("eventId") String eventId);
}
