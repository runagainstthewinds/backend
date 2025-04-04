package com.example.RunAgainstTheWind.enumeration;

public enum StandardDistance {
    ONE_KM(1000),
    FIVE_KM(5000),
    TEN_KM(10000),
    HALF_MARATHON(21097),
    MARATHON(42195);

    private final double meters;

    StandardDistance(double meters) {
        this.meters = meters;
    }

    public double getMeters() {
        return meters;
    }
}
