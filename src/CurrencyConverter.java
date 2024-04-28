import java.util.*;

class CurrencyConversion {
    static class Edge {
        int to;
        double modifiedCost; // Negative log of exchange rate

        public Edge(int to, double modifiedCost) {
            this.to = to;
            this.modifiedCost = modifiedCost;
        }
    }

    public static double maximizeConversionRate(List<String[]> exchangeRates, String fromCurrency, String toCurrency) {
        Map<String, Integer> currencyToIndex = new HashMap<>();
        List<List<Edge>> graph = new ArrayList<>();

        // Initialize graph and currency indices
        int index = 0;
        for (String[] rate : exchangeRates) {
            String from = rate[0];
            String to = rate[1];
            double originalRate = Double.parseDouble(rate[2]);
            double modifiedCost = -Math.log(originalRate);

            currencyToIndex.putIfAbsent(from, index++);
            currencyToIndex.putIfAbsent(to, index++);

            while (graph.size() < index) {
                graph.add(new ArrayList<>());
            }
            graph.get(currencyToIndex.get(from)).add(new Edge(currencyToIndex.get(to), modifiedCost));
        }

        // Dijkstra's algorithm
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingDouble(e -> e.modifiedCost));
        double[] minModifiedCost = new double[index];
        Arrays.fill(minModifiedCost, Double.POSITIVE_INFINITY);
        minModifiedCost[currencyToIndex.get(fromCurrency)] = 0;
        pq.offer(new Edge(currencyToIndex.get(fromCurrency), 0));

        while (!pq.isEmpty()) {
            Edge curr = pq.poll();
            int currCurrency = curr.to;

            for (Edge next : graph.get(currCurrency)) {
                double newCost = curr.modifiedCost + next.modifiedCost;
                if (newCost < minModifiedCost[next.to]) {
                    minModifiedCost[next.to] = newCost;
                    pq.offer(new Edge(next.to, newCost));
                }
            }
        }

        // Calculate the overall conversion rate
        double overallRate = Math.exp(-minModifiedCost[currencyToIndex.get(toCurrency)]);
        return overallRate;
    }

    public static void main(String[] args) {
        List<String[]> exchangeRates = new ArrayList<>();
        exchangeRates.add(new String[]{"USD", "EUR", "1.5"}); // Example exchange rate
        exchangeRates.add(new String[]{"USD", "CAD", "0.5"}); // Example exchange rate
        exchangeRates.add(new String[]{"EUR", "JAP", "1"}); // Example exchange rate
        exchangeRates.add(new String[]{"CAD", "JAP", "5"}); // Example exchange rate
        exchangeRates.add(new String[]{"USD", "JAP", "1.5"}); // Example exchange rate

        // Add more exchange rates as needed

        String fromCurrency = "USD";
        String toCurrency = "JAP";

        double result = maximizeConversionRate(exchangeRates, fromCurrency, toCurrency);
        System.out.println("Maximized conversion rate from " + fromCurrency + " to " + toCurrency + ": " + result);
    }
}
