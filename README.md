# APIM Test Framework

This repository contains a testing framework for WSO2 API Manager (APIM) using Docker, Testcontainers, and Cucumber.

## Getting Started

### Prerequisites

- Docker
- Java 11+
- Maven

### Setup

1. Clone the repository:
```bash
git clone https://github.com/SivakumarAbisherk/apim-test-framework.git
cd apim-test-framework
```

2. Add your preferred version of the APIM pack to the folder at:
```
tests-common/testcontainers/src/main/resources/apim
```

3. Update the relative directory of the APIM pack in the file:
```
tests-common/testcontainers/src/main/java/org/wso2/am/testcontainers/BaseAPIMContainer.java
```

For example:
```java
private static final String API_MANAGER_DIR = "/src/main/resources/apim/wso2am-4.5.0"; //if the pack was of version 4.5.0
```

4. Build the project:
```bash
mvn clean install
```

### Running Tests

To run all tests:
```bash
mvn test
```

To run a specific test:

1. Specify the relative path of the feature file you want to test in:
```
tests-integration/cucumber-tests/src/test/java/org/wso2/am/integration/cucumbertests/runners/groupedTestRunner.java
```

2. Modify the `@CucumberOptions` annotation:
```java
@CucumberOptions(
    features = "src/test/resources/features/customHeaderTest", // modify this line with your feature path
    glue = "org.wso2.am.integration.cucumbertests.stepdefinitions",
    plugin = {"pretty", "html:target/cucumber-report/groupedtestrunner.html"}
)
```

3. Run the specific test with:
```bash
mvn test -DrunGroupTest=true
```

## Project Structure

- `tests-common`: Common testing utilities and Testcontainers configuration
- `tests-integration`: Integration tests written with Cucumber

[//]: # (## License)

[//]: # ()
[//]: # (This project is licensed under [LICENSE NAME] - see the LICENSE file for details.)

[//]: # ()
[//]: # (## Contributing)

[//]: # ()
[//]: # (Please read CONTRIBUTING.md for details on our code of conduct and the process for submitting pull requests.)
