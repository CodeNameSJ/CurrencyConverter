# 💡 Currency Converter Project – Q&A
---

### What is the main functionality of this project?
- It converts a given amount from a base currency to one or more target currencies
using live exchange rates, and applies an optional transaction fee.
---

### Why was Gson chosen over org.json?
- Gson is more modern and flexible
- It allows easy mapping between JSON and Java classes
- It avoids manual parsing code
---

### What is a `record` in Java 24?
- A record is a concise way to create immutable data classes
- Used here for `ExchangeRateResponse` to hold API data cleanly
---

### What is `URI.toURL()` used for?
- Avoids the deprecated `new URL(String)` constructor
- Ensures valid URL format using `URI.create(...)`
---

### What would happen if the API key is wrong?
- The app checks if the `result` in the JSON is `"success"` and handles failure gracefully with an error message
---

### Can this be extended to a web app?
- Yes. The logic can be reused in a Spring Boot REST API or a JavaFX GUI.
---

### What does this project do?
- It converts an amount from a base currency to multiple target currencies using real-time exchange rates via a REST API.
---

###  Why did I use Gson instead of org.json?
- Gson is lightweight, widely adopted by Java developers, and integrates well with record-based parsing and newer Java features.
---

### How is Java 24 used here?
- `record` is used to define an immutable API response class.
- Pattern matching is used for type-safe JSON response handling.
---

### How is error handling done?
- Input validation (for ISO codes)
- API HTTP response check
- Graceful fallback if no rate is found
---

### 5. What would I improve?
- Add a GUI version (Swing/JavaFX)
- Add caching or offline mode
- Add unit tests for parsing and fee logic
---