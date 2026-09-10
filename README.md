![CI](https://github.com/podshobosh/parabank-api-tests/actions/workflows/ci.yml/badge.svg)
# ParaBank API Test Automation

BDD-style REST API test automation framework built against [ParaBank](https://github.com/parasoft/parabank), Parasoft's open-source demo banking application.

Written as a working example of a production-shaped API automation stack: Cucumber for readable specifications, REST Assured for HTTP, TestNG as the engine, and ExtentReports plus Log4j2 for reporting and diagnostics.

---

## Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Build | Maven |
| API client | REST Assured 6.0.1 |
| BDD | Cucumber 7.33 (`cucumber-java`, `cucumber-testng`) |
| Test engine | TestNG 7.12 |
| Dependency injection | Cucumber PicoContainer |
| Reporting | ExtentReports 5.1.2 (Cucumber 7 adapter) |
| Logging | Log4j2 |
| JSON | Jackson Databind |
| Boilerplate | Lombok |

---

## Prerequisites

- **JDK 21+**
- **Maven 3.8+**
- A running **ParaBank** instance (the system under test)

### Start ParaBank

```bash
docker run -d -p 8080:8080 parasoft/parabank
```

Give it a minute to boot, then confirm it's up:

```bash
curl http://localhost:8080/parabank/services/bank/customers/12212/accounts
```

The default `base.url` in `config.properties` points at `http://localhost:8080/parabank/services/bank`.

---

## Running the tests

Run everything:

```bash
mvn clean test
```

### Filtering by tag

Tags are **not** hardcoded in the runner — filter at runtime so the full suite stays the default:

```bash
mvn test -Dcucumber.filter.tags="@login"
```

```bash
mvn test -Dcucumber.filter.tags="@login or @lookUp"
```

Features are tagged by domain — `@login`, `@lookUp`, `@createAccount` — so any subset can be run in isolation.

### Pointing at a different environment

`ConfigReader` resolves each property in precedence order — **system property → environment variable → `config.properties`** — so no file edit is needed to change environments:

```bash
mvn test -Dbase.url=http://qa-host:8080/parabank/services/bank
```

The environment-variable form uppercases the key and replaces dots with underscores, so `base.url` becomes `BASE_URL`.

---

## Test coverage

| Feature | Scenarios | Tag | Covers |
|---|---|---|---|
| `login.feature` | 2 | `@login` | Valid login (200); invalid credentials (400 + error message assertion) |
| `createAccount.feature` | 1 | `@createAccount` | Creating a CHECKING account from an existing funding account |
| `accountLookUp.feature` | 1 outline, 2 examples | `@lookUp` | Account lookup by ID — existing (200) and non-existent (400) |

Four scenarios, five executions. Both positive and negative paths are covered; the lookup feature is data-driven via `Scenario Outline`.

---

## Project structure

```
src/
├── main/java/com/podsho/parabank/
│   ├── client/ApiClient.java        # REST Assured wrapper — GET/POST/PUT/DELETE + query params
│   ├── hooks/Hooks.java             # @Before / @After — log context lifecycle, failure attachment
│   ├── models/Account.java          # Lombok POJO for serialization
│   └── utils/
│       ├── ApiLogContext.java       # Per-scenario request/response capture
│       ├── ConfigReader.java        # Layered config resolution
│       ├── Log.java                 # Log4j2 facade
│       ├── ScenarioContext.java     # State shared across step definitions (DI-scoped)
│       └── TestDataHelper.java      # Test data generation
│
└── test/
    ├── java/com/podsho/parabank/
    │   ├── runners/TestRunner.java   # Cucumber + TestNG entry point
    │   └── stepdefinitions/          # Login, CreateAccount, AccountLookUp
    └── resources/
        ├── features/                 # Gherkin specifications
        ├── extent.properties         # ExtentReports configuration
        └── log4j2.xml                # Logging configuration
```

---

## Reports and diagnostics

After a run:

| Output | Location |
|---|---|
| ExtentReports HTML | `test-output/` |
| Cucumber HTML | `target/cucumber-reports.html` |
| Execution logs | `logs/` |

### Failure diagnostics

`ApiLogContext` captures the full request and response for each scenario through REST Assured's logging filters. On failure, `Hooks` attaches that transcript directly to the report:

```java
if (scenario.isFailed()) {
    scenario.attach(ApiLogContext.getLog(), "text/plain", "API Log");
}
```

A failed scenario therefore carries the exact HTTP exchange that caused it — no re-running with logging enabled to find out what happened. Passing scenarios stay clean, and the context is cleared per scenario so parallel-safe state is preserved.

---

## Design notes

**Shared state via DI, not statics.** `ScenarioContext` is injected through PicoContainer, so step definition classes exchange data without static fields — keeping scenarios isolated and safe to parallelize later.

**Thin client layer.** `ApiClient` centralizes base URL resolution, content negotiation, and logging filters, so step definitions stay declarative and no `given()` chains leak into them.

**Config precedence over config files.** Environment switching happens through system properties or environment variables, so the same artifact runs against local, CI, or a hosted instance unchanged.

---

## Roadmap

- [ ] CI pipeline (GitHub Actions) publishing the Extent report as a build artifact
- [ ] Maven wrapper (`mvnw`) so the repo is clone-and-run without a local Maven install
- [ ] JSON schema validation for response contract testing
- [ ] Parallel execution via surefire thread configuration
- [ ] Expanded coverage: fund transfers, bill pay, transaction search
