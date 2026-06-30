# Security Vulnerability Fixes

## Overview

This repository is a multi-module Maven project. Snyk and Dependabot alerts should be fixed from the root Maven parent, `food-ordering-system/pom.xml`, so all service/module POMs inherit the same patched versions.

The June 2026 alerts are dependency alerts, not application-code findings. The affected module paths are mostly reporting where vulnerable transitive dependencies are visible in the graph.

## Fixes Applied

The root dependency management now pins these security-sensitive dependencies:

| Dependency | Version | Why it is pinned |
|------------|---------|------------------|
| `org.apache.kafka:kafka-clients` | `3.9.1` | Fixes the Kafka 3.x Dependabot alerts for producer buffer-pool race conditions and sensitive DEBUG logging without jumping to Kafka 4.x. |
| `org.postgresql:postgresql` | `42.7.12` | Updates pgjdbc beyond the SCRAM/PBKDF2 CPU exhaustion advisory line. |
| `org.apache.avro:avro` | `1.12.1` | Keeps Avro on the latest 1.12.x line used by the Kafka model/plugin modules. |
| `org.apache.commons:commons-compress` | `1.28.0` | Reduces archive-processing advisories such as resource exhaustion, symlink, and temporary-file findings. |
| `org.yaml:snakeyaml` | `2.2` | Overrides older transitive SnakeYAML versions pulled by framework tooling. |

PostgreSQL, Avro, Commons Compress, and SnakeYAML are centrally managed in the root parent. Do not add competing versions in individual service modules unless a module has a proven compatibility exception.

## Kafka Compatibility Note

Kafka `4.x` and Confluent serializer `8.x` are available, but they should be tested as a coordinated upgrade with Spring Kafka and the Kafka cluster/container images. For the current Spring Boot 3.x codebase, `kafka-clients` `3.9.1` is the safer remediation target for the Dependabot alerts shown in the screenshot.

## Required Scanner Setup

Snyk needs Maven available to build an accurate dependency graph. The checked-in `food-ordering-system/snyk-report.json` shows Snyk failed because `mvn` was not on `PATH`.

Install Maven locally or in CI, then run scans from `food-ordering-system`:

```bash
mvn -version
mvn -B -DskipTests dependency:tree
snyk test --all-projects
```

For GitHub Actions, use `actions/setup-java` with Maven caching before invoking Snyk:

```yaml
- uses: actions/setup-java@v4
  with:
    distribution: temurin
    java-version: '17'
    cache: maven
- run: mvn -B -DskipTests dependency:tree
  working-directory: food-ordering-system
- run: snyk test --all-projects
  working-directory: food-ordering-system
```

## Verification Checklist

1. Run `mvn -B clean verify` from `food-ordering-system`.
2. Run `mvn -B dependency:tree -Dincludes=org.apache.kafka:kafka-clients,org.postgresql:postgresql,org.apache.avro:avro,org.apache.commons:commons-compress,org.yaml:snakeyaml`.
3. Confirm the resolved versions match the table above.
4. Run `snyk test --all-projects` after Maven is on `PATH`.
5. Re-open or re-check Dependabot alerts after GitHub rescans the default branch.

## Operational Hardening

Dependency updates remove known vulnerable library code, but several Snyk categories also need runtime controls:

1. Keep HTTP request/body size limits configured on gateways and services.
2. Avoid enabling Kafka client DEBUG logging in production.
3. Validate external URLs and host allowlists before making outbound calls.
4. Keep database credentials and Kafka secrets outside committed YAML files.
5. Add CI dependency scanning so stale reports do not hide newly introduced vulnerable versions.
