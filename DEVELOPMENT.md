# Development

The single Maven module is `source/BugzillaLyoArchives`, packaged as a WAR.
Bugzilla data comes from the bundled XML export; no live Bugzilla service is needed.
Normal builds use the Lyo `7.0.0.Beta4` BOM.

## Prerequisites

- JDK 25 and Maven 3.9 or newer.
- Docker or Podman with a Compose provider for container deployment.

## Build and tests

From the repository root:

```sh
mvn -B --no-transfer-progress test --file source/BugzillaLyoArchives/pom.xml
mvn -B --no-transfer-progress package --file source/BugzillaLyoArchives/pom.xml
```

JUnit 6 tests verify the bundled archive and Logback provider. Docker is not
needed for these tests. CI runs Maven `verify`, including the formatting check.
SpotBugs is an explicit additional check; review its findings before submitting:

SpotBugs currently reports the generated `servlet.Application` sharing the simple
name of its Jakarta superclass (`NM_SAME_SIMPLE_NAME_AS_SUPERCLASS`). The generator
model is absent from this checkout; the finding remains visible instead of being
suppressed. Spotless checks handwritten tests and preserves generated main sources.

```sh
cd source/BugzillaLyoArchives
mvn spotless:check
mvn -Pspotbugs verify
mvn -Prewrite rewrite:dryRun
```

The Rewrite dry run writes `target/rewrite/rewrite.patch` inside the module.
Review its changes before applying them, particularly changes outside generated
user-code guards.

Deployment and configuration are documented in the
[README quickstart](README.md#getting-started).

## Logging

SLF4J logs use Logback, configured in `src/main/resources/logback.xml` inside the
module. Application and Lyo logs default to DEBUG; other logs default to INFO.

## CI maintenance

CI runs on pull requests and `merge_group` so queued changes are tested. GHCR
publishing uses trusted main/release/scheduled/manual events. Workflow changes
should pass zizmor and actionlint:

```sh
uvx zizmor --offline .github/workflows
actionlint
```

The manually dispatched Lyo HEAD build defaults to `main`, accepts an upstream
branch or fork, and has read-only permissions. It installs that checkout, detects
its Maven project version, and passes `-Dversion.lyo=<detected version>` to the
project build so the imported Lyo BOM matches the locally built artifacts.
The override applies only to that CI run; it does not modify the project POM.
The upstream install skips tests, Javadocs, source JARs, and CycloneDX output,
and disables Lyo's SpotBugs, Spotless, and Enforcer profiles. The archive project's
`verify` step retains its own checks.
