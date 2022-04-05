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

## 2. Set the new version

```shell
mvn versions:set -DnewVersion=X.Y.Z
mvn versions:commit
git add .
git commit -m "Prepare release thymeleaf-X.Y.Z"
git push
```

## 3. Create and deploy the release into the staging repositories

```shell
mvn clean compile deploy
```

## 4. Create tag and set to new development version

```shell
mvn scm:tag -Dtag thymeleaf-X.Y.Z
mvn versions:set -DnewVersion=X.Y.[Z+1]-SNAPSHOT
mvn versions:commit
git add .;
git commit -m "Prepare for next development iteration"
git git push
```

## 5. Manage staging repository in Central

Follow instructions at https://central.sonatype.org/publish/publish-guide/


