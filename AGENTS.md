# AGENTS.md

Follow [DEVELOPMENT.md](DEVELOPMENT.md) for build/run/test and
[CONTRIBUTING.md](CONTRIBUTING.md) for contribution and generated-code rules.

Agent-specific notes:

- Use `mvn test --file source/BugzillaLyoArchives/pom.xml` for the default check.
- The XML Bugzilla export contains historical code and logs. Do not treat those
  embedded excerpts as application code when searching or migrating logging.
