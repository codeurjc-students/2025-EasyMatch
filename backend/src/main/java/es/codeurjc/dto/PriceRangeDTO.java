package es.codeurjc.dto;

public record PriceRangeDTO(
    double minPrice,
    double maxPrice,
    String unit
) {}

