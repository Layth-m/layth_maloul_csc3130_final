import java.util.*;

class CurrencyConversion {
    // Inner class representing an edge in the graph
    static class Edge {
        int to; // Destination vertex index
        double modifiedCost; // Negative log of exchange rate

        // Constructor
        public Edge(int to, double modifiedCost) {
            this.to = to;
            this.modifiedCost = modifiedCost;
        }
    }

    // Method to maximize conversion rate between two currencies
    public static double maximizeConversionRate(List<String[]> exchangeRates, String fromCurrency, String toCurrency) {
        // Map to store currency to index mapping
        Map<String, Integer> currencyToIndex = new HashMap<>();
        // List to represent the graph
        List<List<Edge>> graph = new ArrayList<>();
        // Array to store predecessor vertices for path reconstruction
        int[] predecessor = new int[exchangeRates.size() * 2]; // Assuming worst case size

        // Initialize graph and currency indices
        int index = 0;
        for (String[] rate : exchangeRates) {
            String from = rate[0];
            String to = rate[1];
            double originalRate = Double.parseDouble(rate[2]);
            double modifiedCost = -Math.log(originalRate);

            // Add currency indices if not present
            currencyToIndex.putIfAbsent(from, index++);
            currencyToIndex.putIfAbsent(to, index++);

            // Expand graph as needed
            while (graph.size() < index) {
                graph.add(new ArrayList<>());
            }
            // Add edge to graph
            graph.get(currencyToIndex.get(from)).add(new Edge(currencyToIndex.get(to), modifiedCost));
        }

        // Dijkstra's algorithm for finding shortest path
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingDouble(e -> e.modifiedCost));
        double[] minModifiedCost = new double[index];
        Arrays.fill(minModifiedCost, Double.POSITIVE_INFINITY);
        minModifiedCost[currencyToIndex.get(fromCurrency)] = 0;
        pq.offer(new Edge(currencyToIndex.get(fromCurrency), 0));

        while (!pq.isEmpty()) {
            Edge curr = pq.poll(); // Remove and return top element in priority queue
            int currCurrency = curr.to;

            // Explore neighbors of current currency
            for (Edge next : graph.get(currCurrency)) {
                double newCost = curr.modifiedCost + next.modifiedCost;
                if (newCost < minModifiedCost[next.to]) {
                    minModifiedCost[next.to] = newCost;
                    predecessor[next.to] = currCurrency; // Store predecessor for path reconstruction
                    pq.offer(new Edge(next.to, newCost));
                }
            }
        }

        // Reconstruct path and calculate the overall conversion rate
        List<Integer> path = new ArrayList<>();
        int current = currencyToIndex.get(toCurrency);
        while (current != currencyToIndex.get(fromCurrency)) {
            path.add(current);
            current = predecessor[current];
        }
        path.add(currencyToIndex.get(fromCurrency));
        Collections.reverse(path); // Reverse the path to get it from source to destination

        // Print the path
        System.out.println("Path with the highest exchange rate from " + fromCurrency + " to " + toCurrency + ":");
        for (int vertex : path) {
            for (Map.Entry<String, Integer> entry : currencyToIndex.entrySet()) {
                if (entry.getValue() == vertex) {
                    System.out.print(entry.getKey() + " -> ");
                    break;
                }
            }
        }
        System.out.println(toCurrency);

        // Calculate the overall conversion rate
        double overallRate = Math.exp(-minModifiedCost[currencyToIndex.get(toCurrency)]);
        return overallRate;
    }

    // Main method
    public static void main(String[] args) {
        // Example exchange rates
        List<String[]> exchangeRates = new ArrayList<>();
        exchangeRates.add(new String[]{"USD", "EUR", "2"});
        exchangeRates.add(new String[]{"EUR", "USD", "0.5"});
        exchangeRates.add(new String[]{"USD", "CAD", "4"});
        exchangeRates.add(new String[]{"CAD", "USD", "0.25"});
        exchangeRates.add(new String[]{"USD", "JAP", "10"});
        exchangeRates.add(new String[]{"EUR", "JAP", "3"});
        exchangeRates.add(new String[]{"CAD", "JAP", "4"});


        // Currencies for conversion
        String fromCurrency = "USD";
        String toCurrency = "JAP";

        // Calculate and print result
        double result = maximizeConversionRate(exchangeRates, fromCurrency, toCurrency);
        System.out.println("Maximized conversion rate from " + fromCurrency + " to " + toCurrency + ": " + result);
    }
}
