# CurrencyConverter

Simple Java CLI tool to convert currencies using [ExchangeRate-API](https://www.exchangerate-api.com/).

## Features

- Uses Java 21+ features (records, pattern matching, HttpClient)
- Fetches live exchange rates via ExchangeRate-API
- Applies transaction fee percentage
- Targets multiple currencies in one run

## Setup

1. Get an API key from https://www.exchangerate-api.com
2. Set environment variable `EXCHANGE_API_KEY`

   ```bash
   export EXCHANGE_API_KEY=your_api_key_here
   ```

## Build with Maven:

   ```bash
   mvn clean compile
   ```

## Run:

   ```bash
   mvn exec:java -Dexec.mainClass="org.CurrencyConverter.CurrencyConverter"
   ```