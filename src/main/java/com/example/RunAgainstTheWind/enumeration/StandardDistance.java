package com.example.RunAgainstTheWind.enumeration;

public enum StandardDistance {
    ONE_KM(1),
    FIVE_KM(5),
    TEN_KM(10),
    HALF_MARATHON(21.097),
    MARATHON(42.195);

    private final double meters;

    StandardDistance(double meters) {
        this.meters = meters;
    }

    public double getMeters() {
        return meters;
    }
}
