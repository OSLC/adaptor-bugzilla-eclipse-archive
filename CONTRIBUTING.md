# Contributing

Use compatible Lyo SNAPSHOT dependencies for development and test against Lyo HEAD
with the manual acceptance workflow when changing integration behavior.

Before submitting, follow [DEVELOPMENT.md](DEVELOPMENT.md) for Maven, formatting,
static analysis and workflow checks. Keep the README startup commands working,
and update human-facing docs when build, CI, configuration or deployment changes.

## Generated code

Many sources and the POM contain Lyo Designer `Start of user code` / `End of user
code` guards. Keep handwritten Java changes within these regions; prefer model
or generator changes for generated boilerplate. This checkout does not include
the Designer model. The compiler properties, Jetty plugin and logging dependency in the POM
must retain Java 25, Jetty EE9 12.1 and the Logback backend when regenerating.
New Maven plugins and handwritten dependencies belong in POM user-code regions.
