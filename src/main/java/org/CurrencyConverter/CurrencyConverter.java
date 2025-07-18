package org.CurrencyConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class CurrencyConverter {
	final static String APIKEY = "YOUR-API-KEY";

	// Record representing the API response
	record ExchangeRateResponse(String result, String base_code, Map<String, Double> conversion_rates) {}

	static void main(String[] ignoredArgs) throws Exception {
		Scanner sc = new Scanner(System.in);

		System.out.print("Enter amount: ");
		double amount = sc.nextDouble();
		sc.nextLine();

		System.out.print("Base currency (e.g., USD): ");
		String base = sc.nextLine().trim().toUpperCase();

		if (base.length() != 3) {
			System.out.println("Invalid base currency. Use ISO 4217 format like USD.");
			return;
		}

		System.out.print("Target currencies (comma-separated, e.g., INR,EUR): ");
		List<String> targets = Arrays.stream(sc.nextLine().split(",")).map(String::trim).map(String::toUpperCase).toList();

		System.out.print("Transaction fee %% (or blank for 2): ");
		String feeIn = sc.nextLine().trim();
		final double feePercent = feeIn.isBlank() ? 2.0 : Double.parseDouble(feeIn);

		ExchangeRateResponse response = fetchRates(base);

		if (response == null || !"success".equalsIgnoreCase(response.result)) {
			System.err.println("Failed to get rates from API.");
			return;
		}

		// Conversion loop
		for (String target : targets) {
			Double rate = response.conversion_rates.get(target);
			if (rate != null) {
				double converted = amount * rate;
				double finalAmt = converted * (1 - feePercent / 100);
				System.out.printf("%s: %.2f%n", target, finalAmt);
			} else {
				System.out.printf("No rate available for %s%n", target);
			}
		}
	}

	private static ExchangeRateResponse fetchRates(String base) throws IOException {
		String urlStr = STR."https://v6.exchangerate-api.com/v6/{apiKey}/latest/{base}";
		URI uri = URI.create(urlStr);
		URL url = uri.toURL();

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.connect();

		if (conn.getResponseCode() != 200) {
			System.err.println("HTTP Error: " + conn.getResponseCode());
			return null;
		}

		StringBuilder json = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
			reader.lines().forEach(json::append);
		}

		Gson gson = new Gson();
		JsonObject root = gson.fromJson(json.toString(), JsonObject.class);
		String result = root.get("result").getAsString();
		JsonObject ratesObj = root.getAsJsonObject("conversion_rates");
		Map<String, Double> ratesMap = gson.fromJson(ratesObj, new TypeToken<Map<String, Double>>() {}.getType());

		return new ExchangeRateResponse(result, base, ratesMap);
	}
}
