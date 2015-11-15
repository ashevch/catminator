import java.util.HashMap;

/**
 * Copyright (C) 2015 Alexey Shevchenko
 *
 * This component stores and calculates information about the search process
 * such as total number of cats, found cats, total moves made while searching.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

public class MetricsController {
    private int numCats = 0;
    private int numCatsFound = 0;
    private int totalMovesToFind = 0;
    private static MetricsController instance = null;

    private MetricsController() {}

    public static MetricsController getMetricsController() {
        if (instance == null){
            instance = new MetricsController();
        }
        return instance;
    }

    public int getNumCats() {
        return numCats;
    }

    public void setNumCats(int num) {
        numCats = num;
    }

    public void incrementNumCatsFound() {
        numCatsFound++;
    }

    public void calcTotalMovesToFind(int steps) {
        totalMovesToFind += steps;
    }

    public int getNumCatsFound() {
        return numCatsFound;
    }

    public int getAvgMovesToFind() {
        if (numCatsFound == 0)
            return 100000; // no cat found, return maximum
        return totalMovesToFind / numCatsFound;
    }

    public String getMostVisitedStation(){
        HashMap<Integer, Station> tflMap = StationManager.getStationManager().getStationList();
        int mostVisited = 0;
        String stName = null;
        for (Integer x : tflMap.keySet()){
            Station tmp = tflMap.get(x);
            if (tmp.getVisited() > mostVisited){
                mostVisited = tmp.getVisited();
                stName = tmp.getStName();
            }
        }
        return stName;
    }

    public void printStats() {
        System.out.println("Total number of cats: " + getNumCats());
        System.out.println("Found number of cats: " + getNumCatsFound());
        System.out.println("Average moves number to find a cat: " + getAvgMovesToFind());
        System.out.println("Most visited station: " + getMostVisitedStation());
    }
}