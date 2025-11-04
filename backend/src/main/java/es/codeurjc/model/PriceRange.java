package es.codeurjc.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class PriceRange {

    private double minPrice;
    private double maxPrice;
    private String unit; 


    public PriceRange(double minPrice, double maxPrice, String unit) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.unit = unit;
    }

    public PriceRange() {
    }

    public double getMinPrice() {
        return minPrice;
    }
    
    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
        
    @Override
    public String toString() {
        return minPrice + " - " + maxPrice + " " + unit;
    }
    
}
