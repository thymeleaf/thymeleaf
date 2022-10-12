# Releasing Thymeleaf

## 1. Configuration

Configure access to the snapshots and staging servers in `settings.xml`:

```xml
  <servers>
    <server>
      <id>sonatype-nexus-snapshots</id>
      <username>[USER IN SONATYPE NEXUS]</username>
      <password>[ENCODED PASSWORD IN SONATYPE NEXUS]</password>
    </server>
    <server>
      <id>sonatype-nexus-staging</id>
      <username>[USER IN SONATYPE NEXUS]</username>
      <password>[ENCODED PASSWORD IN SONATYPE NEXUS]</password>
    </server>
  </servers>
```

## 2. Check dependency snapshots

If this is an GA or RC version of Thymeleaf being published, check that no
dependencies are in SNAPSHOT version (thymeleaf-parent's pom.xml).

(Make sure the Spring Boot parent dependencies in Spring Boot-based example
application pom.xml's are also checked for SNAPSHOTs).

If any SNAPSHOTs exist, set to a fixed version and commit.

## 3. Set the new version

```shell
mvn versions:set -DprocessAllModules=true -DnewVersion=X.Y.Z
mvn versions:commit
git add .
git commit -m "Prepare release thymeleaf-X.Y.Z"
git push
```

## 4. Create and deploy the release into the staging repositories

```shell
mvn clean compile deploy
```

## 5. Create tag and set to new development version

```shell
mvn scm:tag -Dtag=thymeleaf-X.Y.Z
mvn versions:set -DprocessAllModules=true -DnewVersion=X.Y.[Z+1]-SNAPSHOT
mvn versions:commit
git add .;
git commit -m "Prepare for next development iteration"
git push
```

## 6. Create tag and set to new development version

If any SNAPSHOT dependencies had to have their versions fixed before building, go
back to SNAPSHOT versions wherever needed and commit.

## 7. Manage staging repository in Central

Follow instructions at https://central.sonatype.org/publish/publish-guide/


