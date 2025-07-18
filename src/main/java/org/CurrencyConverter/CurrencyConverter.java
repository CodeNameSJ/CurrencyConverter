package org.CurrencyConverter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.fusesource.jansi.Ansi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CurrencyConverter {
	final static String APIKEY = "YOUR-API-KEY";

	// Record representing the API response
	record ExchangeRateResponse(String result, String base_code, Map<String, Double> conversion_rates) {}

	static void main(String[] ignoredArgs) throws Exception {
		Scanner sc = new Scanner(System.in);

		// Title
		System.out.println(Ansi.ansi().fg(Ansi.Color.YELLOW).a("\n\n\t════════════ Currency Converter ════════════").reset());

		// Prompt for amount
		System.out.print(Ansi.ansi().fg(Ansi.Color.WHITE).a("\nEnter amount: ").reset());
		double amount = sc.nextDouble();
		sc.nextLine();  // Consume the newline character

		// Prompt for base currency
		System.out.print(Ansi.ansi().fg(Ansi.Color.WHITE).a("Base currency (INR/USD): ").reset());
		String base = sc.nextLine().trim().toUpperCase();

		// Validate base currency
		if (base.length() != 3) {
			System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("\nInvalid base currency format. Please use 3-letter codes like USD.").reset());
			return;
		}

		// Prompt for target currencies
		System.out.print(Ansi.ansi().fg(Ansi.Color.WHITE).a("Target currencies (INR, EUR): ").reset());
		List<String> targets = Arrays.stream(sc.nextLine().split(",")).map(String::trim).map(String::toUpperCase).toList();

		// Prompt for transaction fee
		System.out.print(Ansi.ansi().fg(Ansi.Color.WHITE).a("Transaction fee % (Default 0): ").reset());
		String feeIn = sc.nextLine().trim();
		final double feePercent = feeIn.isBlank() ? 2.0 : Double.parseDouble(feeIn);

		// Fetch exchange rates from API
		ExchangeRateResponse response = fetchRates(base);

		// Check for API response errors
		if (response == null || !"success".equalsIgnoreCase(response.result)) {
			System.err.println(Ansi.ansi().fg(Ansi.Color.RED).a("\nError: Failed to fetch exchange rates.").reset());
			return;
		}

		System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).bold().a("\n\t\t╔═══════ Conversion Results ════════╗").reset());
		// Conversion loop
		for (String target : targets) {
			Double rate = response.conversion_rates.get(target);
			if (rate != null) {
				double converted = amount * rate;
				double finalAmt = converted * (1 - feePercent / 100);
				String result = String.format("%.2f", finalAmt);
				String baseAmount = String.format("%.2f", amount);

				// Displaying each conversion in the desired format
				System.out.printf(String.valueOf(Ansi.ansi().fg(Ansi.Color.CYAN).a("\t\t║ %-1s : %-6s   ->   %-1s : %-6s ║\n").reset()), base, baseAmount, target, result);
			} else {
				System.out.printf(String.valueOf(Ansi.ansi().fg(Ansi.Color.RED).a("\t\t ║          No rate available         ║\n").reset()));
			}
		}
		System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("\t\t╚═══════════════════════════════════╝").reset());
	}

	// Fetch exchange rates from API
	private static ExchangeRateResponse fetchRates(String base) throws IOException {
		String urlStr = "https://v6.exchangerate-api.com/v6/" + APIKEY + "latest/" + base;
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
