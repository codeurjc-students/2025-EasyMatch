package es.codeurjc.dto;

import java.util.List;

public record ClubDTO (
    Long id,
    String name,
    String city,
    String address,
    String phone,
    String email,
    String web,
    ScheduleDTO schedule,
    PriceRangeDTO priceRange,
    List<SportDTO> sports,
    List<Integer> numberOfCourts
) {
    
}