package com.example.jasim.tour.weather.Helpers;


public class TemperatureFormatter {
    public static String format(double temperature) {
        return String.format("%.2f°", temperature - 273.16);
    }
}
