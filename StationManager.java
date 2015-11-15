import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Copyright (C) 2015 Alexey Shevchenko
 *
 * These components are Station and StationManager which store information
 * about stations' graph ans Station itself.
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

 class Station {
    private int id;
    private String stName;
    // list of stations connected to this station is rather short and can be stored in list
    // This is easier to access the neighbor stations ids and the perfomance is still fine.
    private List<Integer> conList = new LinkedList<Integer>();
    private HashSet<Integer> catSet = new HashSet<Integer>();
    private int visited = 0;

    public Station(int id, String stName) {
        this.id = id;
        this.stName = stName;
    }

    public int getId() {
        return id;
    }

    public String getStName() {
        return stName;
    }

    public List<Integer> getConList() {
        return new LinkedList<Integer>(conList);
    }

    public HashSet<Integer> getCatSet() {
        return new HashSet<Integer>(catSet);
    }

    public int getVisited() { return visited; }

    public void addConnection(Integer conn) {
        this.conList.add(conn);
    }

    public void addCat(Integer catId) {
        this.catSet.add(catId);
    }

    public void removeConn(Integer conn) {
        this.conList.remove(conn);
    }

    public void removeCat(Integer catId) {
        this.catSet.remove(catId);
    }

    public void incrementVisited(){
        this.visited++;
    }
}

public class StationManager {
    private static HashMap<Integer, Station> stationMap;
    private static StationManager instance;

    private StationManager (){
        stationMap = new HashMap<Integer, Station>();
    }

    public static StationManager getStationManager(){
        // singleton prevents of creation of multiple stations' graphs
        if (instance == null)
            instance = new StationManager();
        return instance;
    }

    public HashMap<Integer, Station> getStationList() {
        return stationMap;
    }

    public void initializeStationsAndRoutes() {
        try {
            insertStationNames();
            insertStationConnections();
        }catch (Exception e){
            System.out.println("Initialization failed. Please ensure tfl_stations.csv and tfl_connections.csv");
            e.printStackTrace();
            // exit with code 1 if reading file problem occurred
            System.exit(1);
        }
    }

    public void closeStation(Station station){
        // when cat owner finds his/her cat the station is closed
        for(Integer x : station.getConList()){
            stationMap.get(x).removeConn(station.getId());
        }
    }
    public static int getRandomIndex(int Number) {
        Random random = new Random();
        return random.nextInt(Number);
    }

    private void insertStationNames() throws IOException {
        // the maps containing stations and connections are initialized
        // only once.
        InputStream stream = StationManager.class.getResourceAsStream("tfl_stations.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String current;
        while ((current = reader.readLine()) != null) {
            String[] result = current.split(",");
            if (result.length == 2) {
                stationMap.put(Integer.parseInt(result[0]), new Station(Integer.parseInt(result[0]), result[1]));
            }
            else
                System.out.println("WARN: Found malformed string " + current + " in tfl_stations.csv");
        }
    }

    private void insertStationConnections() throws Exception {
        // the maps containing stations and connections are initialized
        // only once.
        InputStream stream = StationManager.class.getResourceAsStream("tfl_connections.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String current;
        while ((current = reader.readLine()) != null){
            String[] result = current.split(",");
            if (result.length == 0) {
                System.out.println("WARN: Found malformed string " + current + " in tfl_connections.csv");
                continue;
            }
            Integer key1 = Integer.parseInt(result[0]);
            Integer key2 = Integer.parseInt(result[1]);
            readConnections(stationMap, key1, key2);
            readConnections(stationMap, key2, key1);
        }
    }

    private void readConnections(HashMap<Integer, Station> stationList, Integer key1, Integer key2) throws Exception{
        Station station = stationList.get(key1);
        if ( station != null ){
            station.addConnection(key2);
        }
        else {
            throw new Exception("Unknown station identifier in tfl_connections.csv");
        }
    }
}
