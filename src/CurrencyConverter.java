import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;

public class CurrencyConverter {
	// Record matching the JSON response fields
	record ExchangeRateResponse(String result, String base_code, Map<String, Double> conversion_rates) {}

	public static void main(String[] args) throws Exception {
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter amount: ");
		double amount = sc.nextDouble(); sc.nextLine();
		System.out.print("Base currency: ");
		String base = sc.nextLine().toUpperCase();
		System.out.print("Target currencies (comma-separated): ");
		List<String> targets = Arrays.stream(sc.nextLine().split(","))
				                       .map(String::trim).map(String::toUpperCase)
				                       .toList();
		System.out.print("Transaction fee % (or blank for 2): ");
		String feeIn = sc.nextLine().trim();
		double feePercent = feeIn.isBlank() ? 2.0 : Double.parseDouble(feeIn);

		// Fetch live exchange rates from API
		String apiKey = "YOUR-API-KEY";
		String urlStr = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/" + base;
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.connect();

		// Read JSON response
		StringBuilder json = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(conn.getInputStream()))) {
			reader.lines().forEach(json::append);
		}

		// Parse JSON into objects (using org.json here for simplicity)
		JSONObject root = new JSONObject(json.toString());
		String result = root.getString("result");
		// Build record from JSON fields
		Map<String, Double> ratesMap = new HashMap<>();
		JSONObject ratesObj = root.getJSONObject("conversion_rates");
		for (String curr : ratesObj.keySet()) {
			ratesMap.put(curr, ratesObj.getDouble(curr));
		}
		ExchangeRateResponse resp = new ExchangeRateResponse(
				result, base, ratesMap);

		// Handle response with pattern matching
		switch (resp) {
			case ExchangeRateResponse("success", String baseCode, Map<String, Double> rates) -> {
				// Perform conversions for each target currency
				for (String target : targets) {
					Double rate = rates.get(target);
					if (rate != null) {
						double converted = amount * rate;
						double finalAmt = converted * (1 - feePercent/100);
						System.out.printf("%s: %.2f%n", target, finalAmt);
					} else {
						System.out.printf("No rate for %s%n", target);
					}
				}
			}
			default -> {
				System.err.println("Failed to get rates from API (result=" + resp.result() + ")");
			}
		}
	}
}
