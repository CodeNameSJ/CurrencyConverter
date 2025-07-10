
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONObject;

public class CurrencyConverter {

    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/";
    private HashMap<String, Double> exchangeRates = new HashMap<>();

    public void fetchExchangeRates(String baseCurrency) {
        try {
            URL url = new URL(API_URL + baseCurrency);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            StringBuilder response;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject rates = jsonResponse.getJSONObject("rates");

            for (String key : rates.keySet()) {
                exchangeRates.put(key, rates.getDouble(key));
            }

            System.out.println("Exchange rates updated successfully!");
        } catch (IOException e) {
            System.out.println("Error fetching exchange rates: " + e.getMessage());
        }
    }

    public double convertCurrency(String from, String to, double amount) {
        if (!exchangeRates.containsKey(from) || !exchangeRates.containsKey(to)) {
            System.out.println("Invalid currency code");
            return -1;
        }
        double conversionRate = exchangeRates.get(to) / exchangeRates.get(from);
        return Math.round((amount * conversionRate) * 100.0) / 100.0;
    }

    public static void main(String[] args) {
        CurrencyConverter converter = new CurrencyConverter();
        String baseCurrency = "USD"; // Change as needed

        // Fetch live exchange rates
        converter.fetchExchangeRates(baseCurrency);

        // Convert currency
        double amount = 100;
        String from = "USD";
        String to = "EUR";
        double convertedAmount = converter.convertCurrency(from, to, amount);
        System.out.println(amount + " " + from + " = " + convertedAmount + " " + to);
    }

    public HashMap<String, Double> getExchangeRates() {
        return exchangeRates;
    }

    public void setExchangeRates(HashMap<String, Double> exchangeRates) {
        this.exchangeRates = exchangeRates;
    }
}
