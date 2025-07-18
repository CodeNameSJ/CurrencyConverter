# 💱 Currency Converter (Java 24 Edition)

A command-line Java 24 application to convert a base currency to multiple target currencies using live exchange rates
from [ExchangeRate-API](https://www.exchangerate-api.com).

## 🚀 Features

- Java 24 compatible with modern `record` and `pattern matching`
- Supports multiple target currencies in one go
- Applies optional transaction fee (default: 2%)
- Uses Gson for JSON parsing
- Error handling for invalid inputs and API failures

## 🪧 Demo

Enter amount: 100
Base currency (e.g., USD): USD
Target currencies (comma-separated, e.g., INR,EUR): INR,EUR
Transaction fee % (or blank for 2): 1
INR: 8295.70
EUR: 90.71

## 🛠 Tech Stack

| Layer        | Technology                                            |
|--------------|-------------------------------------------------------|
| Language     | Java 24                                               |
| JSON Parser  | [Gson 2.13.1](https://github.com/google/gson)         |
| Build Tool   | Maven                                                 |
| API Provider | [ExchangeRate-API](https://www.exchangerate-api.com/) |

## 📦 Setup Instructions

## 🔐 API Key

Replace `"YOUR-API-KEY"` in `CurrencyConverter.java` with your own
from [ExchangeRate-API](https://www.exchangerate-api.com).

### 1. Clone this repo

```bash
git clone https://github.com/yourusername/CurrencyConverterJava24.git
cd CurrencyConverterJava24
```

### 2. Compile and run (manual way)

```bash
javac -cp ".;lib/gson-2.10.1.jar" -d out src/main/java/org/currencyconverter/CurrencyConverter.java
java -cp ".;lib/gson-2.10.1.jar;out" org.currencyconverter.CurrencyConverter
```

### 3. Or use Maven

```bash
mvn clean compile exec:java -Dexec.mainClass="org.currencyconverter.CurrencyConverter"
```

## 📄 License

This project is licensed under the MIT License.
