import java.io.IOException;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Practice4 {
    public static class City
    {
        private String name;
        private String state;
        private int population;

        public City(String name, String state, int population)
        {
            this.name = name;
            this.state = state;
            this.population = population;
        }

        public String getName()
        {
            return name;
        }

        public String getState()
        {
            return state;
        }

        public int getPopulation()
        {
            return population;
        }

        @Override
        public String toString() {
            return "City{" +
                    "name='" + name + '\'' +
                    ", state='" + state + '\'' +
                    ", population=" + population +
                    '}';
        }
    }

    public static Stream<City> readCities(String filename) throws IOException
    {
        return Files.lines(Paths.get(filename))
                .map(l -> l.split(", "))
                .map(a -> new City(a[0], a[1], Integer.parseInt(a[2])));
    }

    public static void main(String[] args) throws IOException, URISyntaxException {

        Stream<City> cities = readCities("lab4\\src\\cities.txt");
        // Q1: count how many cities there are for each state
        // TODO: Map<String, Long> cityCountPerState = ...
//        String filename = "lab4\\src\\cities.txt";
//        Path pathToFile = Paths.get(filename);
//        System.out.println(pathToFile.toAbsolutePath());
        Map<String, Long> cityCountPerState = cities.collect(Collectors.groupingBy(City::getState, Collectors.counting()));


        cities = readCities("lab4\\src\\cities.txt");
        // Q2: count the total population for each state
        // TODO: Map<String, Integer> statePopulation = ...
        Map<String, Integer> statePopulation = cities.collect(Collectors.groupingBy(City::getState, Collectors.summingInt(City::getPopulation)));

        cities = readCities("lab4\\src\\cities.txt");
        // Q3: for each state, get the set of cities with >500,000 population
        // TODO: Map<String, Set<City>> largeCitiesByState = ...
        Map<String, Set<City>> largeCitiesByState =
                cities.collect(Collectors.groupingBy(City::getState, Collectors.filtering(c -> {
                    return c.getPopulation() > 500000;
                }, Collectors.toSet())));

//        out
        System.out.println("Q1 count cities for each state");
        System.out.println(cityCountPerState);
        System.out.println("Q2 count total population");
        System.out.println(statePopulation);
        System.out.println("Q3 count set of cities");
        Iterator<Map.Entry<String, Set<City>>> iterator = largeCitiesByState.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Set<City>> entry = iterator.next();
            System.out.print("key: " + entry.getKey());
            System.out.println("value: " + entry.getValue());
        }
    }
}
