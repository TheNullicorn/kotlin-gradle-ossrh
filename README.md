## Features
- Documentation
  - Includes package-level documentation support (`src/commonMain/resources/package_docs`)
  - Includes code snippet support in documentation (`src/commonMain/resources/samples`)
- GitHub Actions
  - Regenerates `gh-pages` with fresh documentation when `main` branch changes
  - Publishes to an OSSRH staging repository whan `main` branch changes

## Setup
1. Assign the following values in `gradle.properties`, according to the comments above them:
   1. `project.name`
   2. `project.group_id`
   3. `author.username`
   4. `author.email`
2. Go to `src/commonMain/resources/package_docs/Example.md`, and either...
   1. delete the file
   2. replace `<PROJECT>` with the same value you used for `project.name` in step 1.i.
3. Go to your GitHub repository's `Settings > Secrets > Actions > New`
   1. Create `OSSRH_USERNAME` and `OSSRH_PASSWORD` with either...
      1. your actual username and password
      2. the username and password value of a "user token" for your account (recommended)
4. Open `~/.gradle/gradle.properties` (create the file if it's missing)
   1. Add the following to the end of the file:
   ```groovy
   ossrhUsername = '...' // Same value as OSSRH_USERNAME from step 3.i
   ossrhPassword = '...' // Same value as OSSRH_PASSWORD from step 3.i
   ```