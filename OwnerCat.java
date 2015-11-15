import java.util.*;

/**
 * Copyright (C) 2015 Alexey Shevchenko
 *
 * These components are Cat and CatOwner which store information about
 * particular cat and particular owner (their location, vistsed station for a human etc).
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

class Cat {
    private int id;
    private int curStation;
    private HashMap<Integer, Station> tflMap = StationManager.getStationManager().getStationList();

    public Cat(int id){
        this.id = id;
        Random random = new Random();
        Integer[] array = new Integer[tflMap.keySet().size()];
        tflMap.keySet().toArray(array);
        curStation = array[random.nextInt(array.length)];
        tflMap.get(curStation).addCat(id);
    }

    public int getId() {
        return id;
    }

    public boolean move(){
        Station st = tflMap.get(curStation);
        List<Integer> conList = st.getConList();
        if (conList.size() == 0)
            return  false; // trapped, nowehere to go
        // index search is not that straightforward as in order to choose a direction
        // to move we need a randomly generated index of some station first,
        // which we access afterwards
        int index = StationManager.getRandomIndex(conList.size());
        int newStId = conList.get(index);
        Station newSt = tflMap.get(newStId);
        st.removeCat(id);
        newSt.addCat(id);
        curStation = newStId;
        tflMap.get(newStId).incrementVisited();
        return true;
    }
}

class Owner{
    private int id;
    private int curStation;
    private HashSet<Integer> track = new HashSet<Integer>();
    private int counter = 0;
    private HashMap<Integer, Station> tflMap = StationManager.getStationManager().getStationList();

    public Owner(int id){
        this.id = id;
        Random random = new Random();
        Integer[] array = new Integer[tflMap.keySet().size()];
        tflMap.keySet().toArray(array);
        curStation = array[random.nextInt(array.length)];
        track.add(curStation);
    }

    public int getId() {
        return id;
    }

    public HashSet<Integer> getTrack() {
        return track;
    }

    public boolean move(){
        counter++;
        Station st = tflMap.get(curStation);
        List<Integer> conList = st.getConList();
        if (conList.size() == 0)
            return  false; // trapped, nowehere to go
        List<Integer> tmplist = st.getConList();
        int newStIndex;
        int newStId;
        for (Integer item : conList) {
            if (track.contains(item))
                tmplist.remove(item);
        }
        // links list of the stations is the connections of this station
        // it's relatively cheap to iterate through it and remove all the visited
        // stations
        if (tmplist.size() == 0) {
            newStIndex = StationManager.getRandomIndex(conList.size());
            newStId = conList.get(newStIndex);
        }
        else if (tmplist.size() == 1) {
            newStId = tmplist.get(0);
        }
        else {
            newStIndex = StationManager.getRandomIndex(tmplist.size());
            newStId = tmplist.get(newStIndex);
        }
        curStation = newStId;
        track.add(newStId);
        tflMap.get(newStId).incrementVisited();
        return true;
    }

    public boolean searchCat(){
        Station st = tflMap.get(curStation);
        HashSet<Integer> catSet = st.getCatSet();
        if (catSet.contains(id)) {
            System.out.println("Owner " + id + " found cat " + id + " station. " + st.getStName() + " is now closed.");
            StationManager.getStationManager().closeStation(st);
            // update statistics related to the happy event through metrics controller component
            MetricsController mtc = MetricsController.getMetricsController();
            mtc.incrementNumCatsFound();
            mtc.calcTotalMovesToFind(counter);
            st.removeCat(id);
            return true;
        }
        return false;
    }
}
