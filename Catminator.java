import java.util.*;

/**
 * Copyright (C) 2015 Alexey Shevchenko
 *
 * This is the main component of the search cat application.
 * It creates number of cats and owners with the corresponding ids,
 * runs the initialization and search process.
 *
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

public class Catminator {
    final static int iterNumber = 100000;
    static HashMap<Integer, Owner> owners = new HashMap<Integer, Owner>();
    static HashMap<Integer, Cat> cats = new HashMap<Integer, Cat>();

    public static int consoleRead(){
        System.out.println("Enter the number of cats and humans");
        Scanner in = new Scanner(System.in);
        int number = 0;
        boolean readFlag = false;
        while(!readFlag) {
            try {
                number = Integer.parseInt(in.nextLine());
                if (number < 0)
                    System.out.println("You entered the below 0, try again");
                else if (number == 0)
                    System.out.println("You entered 0 number of cats/owners, there is nothing to search");
                else {
                    readFlag = true;
                    in.close();
                    System.out.println(number + " of beloved cats got lost in the London and " + number + " of sad owners went searching them");
                }
            } catch (NumberFormatException e) {
                System.out.println("wrong input format try again");

            }
        }
        return number;
    }

    public static void initialize(){
        StationManager stm = StationManager.getStationManager();
        stm.initializeStationsAndRoutes();

        MetricsController metricsController = MetricsController.getMetricsController();
        int N = Catminator.consoleRead();
        metricsController.setNumCats(N);

        for(int i=0; i<N; i++){
            Cat cat = new Cat(i);
            cats.put(i, cat);
            Owner owner = new Owner(i);
            owners.put(i, owner);
        }
    }

    public static void searchProcess(){
        for (int i=0; i < iterNumber; i++){
            for (Iterator<Map.Entry<Integer, Owner>> it = owners.entrySet().iterator(); it.hasNext();) {
                // ids of owner and his/her cat are identical so we can run inly one loop
                // and use id as key in the hash map
                Map.Entry<Integer, Owner> ownerEntry = it.next();
                Owner owner = ownerEntry.getValue();

                if (owner.searchCat()) {
                    // if owner finds the cat both are removed from the collections
                    // to decrease number of iterations and cat search options
                    it.remove();
                    cats.remove(owner.getId());
                }
                else {
                    if (owner.move()) {
                        // if owner was able to move to another station
                        // we move the corresponding cat to its other station too
                        Cat cat = cats.get(owner.getId());
                        if(!cat.move()) {
                            // if cat fails to move, it happens in the only one case when all stations are closed.
                            // In this case we remove cat and the corresponding owner from the collections.
                            System.out.println("Cat " + owner.getId() + " trapped. and removed from search list with owner");
                            cats.remove(cat.getId());
                            it.remove();
                        }
                    } else {
                        // if owner fails to move it happens in the only one case when all stations are closed.
                        // In this case we remove cat and the corresponding owner from the collections.
                        System.out.println("Owner " + owner.getId() + " trapped. and removed from search list with cat");
                        it.remove();
                        cats.remove(owner.getId());
                    }
                }
            }
        }
    }

    public static void main(String[] args){
        initialize();
        searchProcess();
        MetricsController metricsController = MetricsController.getMetricsController();
        metricsController.printStats();
    }
}

