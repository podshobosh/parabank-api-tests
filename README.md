# ParaBank API Test Automation

![CI](https://github.com/podshobosh/parabank-api-tests/actions/workflows/ci.yml/badge.svg)

BDD-style REST API test automation framework built against [ParaBank](https://github.com/parasoft/parabank), Parasoft's open-source demo banking application.

Written as a working example of a production-shaped API automation stack: Cucumber for readable specifications, REST Assured for HTTP, TestNG as the engine, and ExtentReports plus Log4j2 for reporting and diagnostics.

---

## Stack

| Layer | Technology |
| --- | --- |
| Language | Java 17 |
| Build | Maven |
| API client | REST Assured 6.0.1 |
| BDD | Cucumber 7.33 (`cucumber-java`, `cucumber-testng`) |
| Test engine | TestNG 7.12 |
| Dependency injection | Cucumber PicoContainer |
| Reporting | ExtentReports 5.1.2 (Cucumber 7 adapter), Cucumber HTML/JSON/JUnit |
| Logging | Log4j2 |
| JSON | Jackson Databind |
| Boilerplate | Lombok |
| CI | GitHub Actions |

---

## Prerequisites

- **JDK 17+**
- **Maven 3.8+**
- A running **ParaBank** instance (the system under test)

### Start ParaBank

```bash
docker run -d --name parabank -p 8080:8080 -p 9001:9001 parasoft/parabank
```

Port 9001 exposes ParaBank's embedded HSQLDB so the tests can verify API results directly in the database. Without it the `the deposit is recorded in the database` and `the database balance has increased by` steps cannot connect.

Give it a minute to boot, then **seed the demo database**. ParaBank starts with no schema and no data, so every endpoint returns `400 Could not find customer #12212` until this runs:

```bash
curl -X POST http://localhost:8080/parabank/services/bank/initializeDB
```

Confirm it worked — this should return account XML rather than a 400:

```bash
curl http://localhost:8080/parabank/services/bank/customers/12212/accounts
```

The default `base.url` in `config.properties` points at `http://localhost:8080/parabank/services/bank`. The database settings (`db.url`, `db.user`, `db.password`) default to the HSQLDB on port 9001 and can be overridden the same way.

---

## Running the tests

Run everything (the default run skips scenarios tagged `@known-defect`, see below):

```bash
mvn clean test
```

### Filtering by tag

Tags are **not** hardcoded in the runner. The default filter lives in `src/test/resources/cucumber.properties` (`not @known-defect`), and any `-Dcucumber.filter.tags` on the command line overrides it:

```bash
mvn test -Dcucumber.filter.tags="@smoke"
```

```bash
mvn test -Dcucumber.filter.tags="@login or @lookUp"
```

```bash
mvn test -Dcucumber.filter.tags="@deposit and not @known-defect"
```

| Tag | Meaning |
| --- | --- |
| `@login`, `@lookUp`, `@createAccount`, `@deposit` | Feature domain |
| `@smoke` | Fast happy-path checks |
| `@regression` | Full behaviour coverage for the feature |
| `@known-defect` | Correct expected behaviour that ParaBank currently violates; excluded by default |

### Re-running failures

The runner writes failed scenarios to `target/failed_scenarios.txt`. Re-run just those (without `clean`, or the file is wiped first):

```bash
mvn test -Dtest=FailedTestRunner
```

### Parallel execution

Scenarios run in parallel on 4 threads by default. The thread count comes from `parallel.threads` in `pom.xml` and can be overridden per run:

```bash
mvn clean test                          # 4 threads
mvn clean test -Dparallel.threads=1     # sequential, handy when debugging
mvn clean test -Dparallel.threads=8
```

`TestRunner` makes Cucumber's scenario data provider parallel, and surefire passes the thread count to TestNG as `dataproviderthreadcount`. Threads form a pool: as soon as one finishes a scenario it takes the next waiting one.

Scenarios stay independent because each gets its own `ScenarioContext` from PicoContainer and its own `ThreadLocal` API log buffer.

**ParaBank is not safe under concurrent `createAccount` calls.** Firing six at once against a clean instance returned `400 Could not create new account` for some of them, while sequential calls always succeeded. `AccountService.createAccount` therefore holds a lock around that single call, so account creation is serialized while everything else (deposits, lookups, database checks) still runs in parallel. If a new intermittent failure ever shows up, check which other ParaBank endpoints are being called concurrently.

### Pointing at a different environment

`ConfigReader` resolves each property in precedence order — **system property → environment variable → `config.properties`** — so no file edit is needed to change environments:

```bash
mvn test -Dbase.url=http://qa-host:8080/parabank/services/bank
```

The environment-variable form uppercases the key and replaces dots with underscores, so `base.url` becomes `BASE_URL`.

---

## Test coverage

| Feature | Scenarios | Tags | Covers |
| --- | --- | --- | --- |
| `login.feature` | 1 scenario, 1 outline (2 examples) | `@login` | Valid login (200); invalid credentials (400 + error message assertion), with the bad credential pairs loaded from `testdata/invalidLogins.json` |
| `createAccount.feature` | 1 | `@createAccount` | Creating a CHECKING account from an existing funding account |
| `accountLookUp.feature` | 1 outline (2 examples), 1 scenario | `@lookUp` | Account lookup by ID — existing (200) and non-existent (400); the 200 response is validated against a JSON schema |
| `depositValidAmount.feature` | 2 scenarios, 1 outline (2 examples) | `@deposit`, `@smoke`, `@regression`, `@known-defect` | Valid deposit: confirmation message, balance increases by exactly the amount, and the transaction row exists in the database. Deposit into a non-existent account: 400 with the "could not find account" message. Invalid amounts (`0.00`, `-50.00`): expected to be rejected, balance unchanged (see known defects) |

The default run executes 8 scenarios plus a database connection smoke test (`DbConnectionTest`). The 2 `@known-defect` examples run only on request.

### Traceability

Every scenario carries one unique `@PB-nnn` tag, written in the style of a Jira key (`PB` is a placeholder project key; in a real project the tag holds the ticket id, and test-management tools such as Xray read it from there). Run a single requirement with `mvn test -Dcucumber.filter.tags="@PB-401"`.

| ID | Scenario | Endpoint | Other tags | Status |
| --- | --- | --- | --- | --- |
| `PB-101` | User logs in with valid credentials | `GET /login/{user}/{pass}` | `@login` | Active |
| `PB-102` | Login fails with invalid credentials (outline: wrong password, unknown user) | `GET /login/{user}/{pass}` | `@login`, `@negativeLogin` | Active |
| `PB-201` | Account lookup returns 200, JSON, matches schema | `GET /accounts/{id}` | `@lookUp` | Active |
| `PB-202` | Non-existent account is rejected | `GET /accounts/{id}` | `@negativelookUp` | Active |
| `PB-301` | User creates a new checking account | `POST /createAccount` | `@createAccount` | Active |
| `PB-401` | Customer deposits a valid amount (API and database checked) | `POST /deposit` | `@deposit`, `@smoke`, `@regression` | Active |
| `PB-402` | Customer cannot deposit an invalid amount (`0.00`, `-50.00`) | `POST /deposit` | `@deposit`, `@negative`, `@known-defect` | Known defect: ParaBank accepts these |
| `PB-403` | Customer cannot deposit into a non-existent account | `POST /deposit` | `@deposit`, `@negative` | Active |

The deposit feature creates its own checking account in a `Background` step, so it never depends on the state of a shared seed account, and it asserts the *change* in balance instead of an absolute value.

---

## Known defects found in ParaBank

These are findings about the system under test, not framework bugs.

| Finding | Evidence | Test |
| --- | --- | --- |
| `/deposit` accepts zero and negative amounts | HTTP 200 for `amount=0.0` and `amount=-50.0`; the `-50.0` call lowered the account balance by 50 (a deposit acting as a withdrawal) | `Customer cannot deposit an invalid amount`, tagged `@known-defect` |
| Login credentials travel in the URL | `GET /login/{username}/{password}` | Observed in `LoginSteps`; no dedicated test yet |

The deposit scenario is written for the **correct** behaviour and quarantined with a tag, so CI stays meaningful and the test starts guarding the behaviour as soon as the API is fixed. Run it on demand to see the failure:

```bash
mvn test -Dtest=TestRunner -Dcucumber.filter.tags="@known-defect"
```

---

## Project structure

```text
src/
├── main/java/com/podsho/parabank/
│   ├── client/
│   │   ├── ApiClient.java           # REST Assured wrapper — GET/POST/PUT/DELETE + query params
│   │   ├── Endpoints.java           # ParaBank endpoint paths in one place
│   │   └── AccountService.java      # ParaBank operations (login, accounts, deposit) with a shared status check
│   ├── models/
│   │   ├── Account.java             # Lombok POJO for deserialization
│   │   └── AccountType.java         # CHECKING / SAVINGS enum carrying ParaBank's numeric code
│   └── utils/
│       ├── ApiLogContext.java       # Per-thread request/response capture
│       ├── ConfigReader.java        # Layered config resolution
│       ├── Log.java                 # Log4j2 facade
│       ├── ScenarioContext.java     # State shared across step definitions (DI-scoped)
│       └── TestDataHelper.java      # Test data generation
│
└── test/
    ├── java/com/podsho/parabank/
    │   ├── db/
    │   │   ├── TransactionDao.java       # JDBC lookup of an account's latest transaction (ParaBank's HSQLDB)
    │   │   ├── AccountDao.java           # JDBC lookup of an account's stored balance
    │   │   ├── DbUtils.java              # Opens connections from the db.* properties
    │   │   └── Transaction.java          # Row model: type, amount, description
    │   ├── testdata/
    │   │   ├── TestData.java             # Loads named cases from JSON test data files
    │   │   └── LoginCase.java            # Model for one bad-login case
    │   ├── hooks/Hooks.java              # @Before / @After — log context lifecycle, failure attachment
    │   ├── runners/
    │   │   ├── TestRunner.java           # Cucumber + TestNG entry point
    │   │   └── FailedTestRunner.java     # Re-runs scenarios that failed last time
    │   └── stepdefinitions/              # Login, CreateAccount, AccountLookUp, Deposit, CommonSteps (shared steps)
    └── resources/
        ├── features/                     # Gherkin specifications
        ├── testdata/invalidLogins.json   # Bad credential pairs used by the negative login outline
        ├── schemas/account.json          # JSON schema for the account response (contract test)
        ├── cucumber.properties           # Default tag filter
        ├── extent.properties             # ExtentReports configuration and system info
        ├── extent-config.xml             # Extent theme and titles
        └── log4j2.xml                    # Logging configuration

.github/workflows/ci.yml                  # Starts ParaBank, seeds it, runs the suite, uploads reports
```

---

## Reports and diagnostics

After a run:

| Output | Location |
| --- | --- |
| ExtentReports HTML (primary) | `test-output/ExtentReport/extent-report.html` |
| Cucumber HTML (fallback) | `target/cucumber-reports.html` |
| Cucumber JSON / JUnit XML (for CI tooling) | `target/cucumber.json`, `target/cucumber.xml` |
| Execution logs | `logs/` |

### Failure diagnostics

`ApiLogContext` captures the full request and response for each scenario through REST Assured's logging filters. On failure, `Hooks` attaches that transcript directly to the report:

```java
if (scenario.isFailed()) {
    String apiLog = asHtml(ApiLogContext.getLog());
    scenario.attach(apiLog, "text/html", "API Log");
    ExtentCucumberAdapter.addTestStepLog(apiLog);
}
```

It is attached twice deliberately: `scenario.attach` is Cucumber's own API and feeds the Cucumber HTML report, while the Extent adapter ignores scenario attachments entirely and has to be fed through its own call.

A failed scenario therefore carries the exact HTTP exchange that caused it — no re-running with logging enabled to find out what happened. Passing scenarios stay clean, and the buffer is a `ThreadLocal` cleared per scenario, so it is safe for parallel execution.

---

## Continuous integration

`.github/workflows/ci.yml` runs on every push and pull request to `main`, and on manual dispatch:

1. Starts ParaBank in Docker (publishing ports 8080 and 9001) and waits for it to accept connections.
2. Seeds the demo database, retrying until the data endpoint returns 200.
3. Runs `mvn -B test`.
4. Uploads the reports (`test-output/`, Cucumber HTML/JSON/XML, `logs/`) as a build artifact, even when tests fail.

---

## Design notes

**Shared state via DI, not statics.** `ScenarioContext` is injected through PicoContainer, so step definition classes exchange data without static fields — keeping scenarios isolated and safe to run in parallel.

**Thin client layer.** `ApiClient` centralizes base URL resolution, content negotiation, and logging filters, so step definitions stay declarative and no `given()` chains leak into them.

**Service layer over the client.** `AccountService` owns the ParaBank operations (log in, find the funding account, create an account, deposit, read a balance), so endpoints and parameter names live in one place and steps read as intent. It is injected into step classes through PicoContainer. Methods that return a value (`login`, `getBalance`) fail fast through one shared `requireOk` check that names the operation, status and body; methods that return the raw `Response` (`deposit`) leave assertions to the steps.

**Typed domain values.** Account types are an `AccountType` enum, so a typo in a feature file fails loudly instead of silently becoming a checking account. Money is `BigDecimal` end to end and is compared with `compareTo`, never with a `double` and a tolerance.

**The database is checked directly, not assumed.** After a deposit, a step reads the newest row for that account through JDBC and compares type, amount and description with what the API reported. A second step reads the ACCOUNT table through `AccountDao` and checks the stored balance rose by the deposited amount, so an API that reports success without saving is caught. The query uses a `PreparedStatement`, and the connection comes from the `db.*` properties, so the same code can target another database by changing the driver and URL. The account under test is created per scenario, so its newest row is always the deposit and the shared funding account's transfers don't interfere.

**Contract tests alongside value assertions.** The account response is validated against `schemas/account.json` (field names, types, required keys, no extra fields), so an API change that keeps the values but breaks the shape is caught.

**Shared steps live in one place.** Steps reused across features (for example `status code should be {int}`) are in `CommonSteps`, so no feature depends on another feature's step class.

**Config precedence over config files.** Environment switching happens through system properties or environment variables, so the same artifact runs against local, CI, or a hosted instance unchanged.

**Tests own their data and assert deltas.** Scenarios create the account they need and assert how a value changes (balance after = balance before + amount) rather than comparing to a hardcoded number. An earlier version asserted a fixed balance and broke CI the moment another test changed it.

**Known product defects are quarantined, not hidden.** A scenario that documents correct behaviour the system violates is tagged `@known-defect` and excluded from the default run, instead of being deleted or rewritten to pass.

**Test data lives in files, not in steps.** The bad-login matrix is a JSON file (`src/test/resources/testdata/invalidLogins.json`) read by `TestData` with Jackson. The feature names a case (`"wrong password"`) in its `Examples` table and the step looks it up, so adding a case means adding one JSON entry and one table row, and a misspelled case name fails with a clear error. Valid credentials stay in `config.properties` and can be overridden by environment variables, because they are the part that would be secret in a real project.

---

## Roadmap

- [x] CI pipeline (GitHub Actions) publishing the Extent report as a build artifact
- [x] Machine-readable results (Cucumber JSON and JUnit XML)
- [x] Service layer (`AccountService`) injected via PicoContainer
- [x] Shared steps moved into `CommonSteps`
- [x] `AccountType` enum and `BigDecimal` for money
- [x] Base URI set in `ApiClient.baseRequest()`, which builds a fresh request per call so per-scenario logging stays intact
- [x] JSON schema validation for response contract testing
- [x] Database verification of API effects (JDBC against ParaBank's HSQLDB)
- [x] Data-driven scenarios loaded from JSON test data (invalid login cases)
- [ ] `Jenkinsfile` alongside the GitHub Actions workflow
- [x] Jira-style tags and a traceability table (requirement to scenario)
- [ ] Maven wrapper (`mvnw`) so the repo is clone-and-run without a local Maven install
- [x] Parallel execution via surefire thread configuration (account creation serialized)
- [ ] Expanded coverage: fund transfers, bill pay, transaction search
