package org.CurrencyConverter;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.*;

public class CurrencyConverter {
	public static void main(String[] ignoredArgs) {
		Scanner sc = new Scanner(System.in);

		System.out.print("Enter amount: ");
		double amount = sc.nextDouble();
		sc.nextLine();

		System.out.print("Base currency: ");
		String base = sc.nextLine().trim().toUpperCase();
		if (!base.matches("[A-Z]{3}")) {
			System.err.println("Invalid base currency code.");
			return;
		}

		System.out.print("Target currencies (comma-separated): ");
		List<String> targets = Arrays.stream(sc.nextLine().split(",")).map(String::trim).map(String::toUpperCase).toList();

		System.out.print("Transaction fee % (or blank for 2): ");
		String feeIn = sc.nextLine().trim();
		double feePercent = feeIn.isBlank() ? 2.0 : Double.parseDouble(feeIn);

		if (feePercent < 0 || feePercent > 100) {
			System.err.println("Invalid fee percentage. Must be between 0 and 100.");
			return;
		}

		String apiKey = System.getenv("EXCHANGE_API_KEY");
		if (apiKey == null) {
			System.err.println("Missing API key. Set EXCHANGE_API_KEY env variable.");
			return;
		}

		String urlStr = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/" + base;

		try {
			URI uri = URI.create(urlStr);
			URL url = uri.toURL();
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");

			if (conn.getResponseCode() != 200) {
				System.err.println("API call failed with status: " + conn.getResponseCode());
				return;
			}

			StringBuilder json = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
				reader.lines().forEach(json::append);
			}

			JSONObject root = new JSONObject(json.toString());

			String result = root.getString("result");
			String baseCode = root.getString("base_code");

			Map<String, Double> rates = root.getJSONObject("conversion_rates").toMap().entrySet().stream().filter(e -> e.getValue() instanceof Number).collect(HashMap::new, (m, e) -> m.put(e.getKey(), ((Number) e.getValue()).doubleValue()), HashMap::putAll);

			ExchangeRateResponse response = new ExchangeRateResponse(result, baseCode, rates);

			if (response.result().equals("success")) {
				for (String target : targets) {
					Double rate = rates.get(target);
					if (rate != null) {
						double raw = amount * rate;
						double finalAmt = raw * (1 - feePercent / 100);
						System.out.printf("→ %s: Rate %.4f | Before fee: %.2f | After %.1f%% fee: %.2f%n", target, rate, raw, feePercent, finalAmt);
					} else {
						System.out.printf("⚠️  No rate available for target currency: %s%n", target);
					}
				}
			} else {
				System.err.println("API returned failure status: " + response.result());
			}

		} catch (Exception e) {
			System.err.println("❌ An error occurred: " + e.getMessage());
			e.printStackTrace(new PrintWriter(System.err, true)); // ✅ modern, avoids warning
		}
	}

	record ExchangeRateResponse(String result, String base_code, Map<String, Double> conversion_rates) {}
}
