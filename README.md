# RKimer

## Description

RKimer is an open-source speedcube timing program.
Right now, it's in the early stages of development and any contributors are welcome
to submit a pull request. (See [Contributing](CONTRIBUTING.md))

## Building

- Create a new file in the root of the project called `gradle.properties` 
with your github username (lowercase) and personal authentication token (see the
[GitHub Documentation](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token)).
Example `gradle.properties` file:
```
gpr.user=<userame>
gpr.token=<token>
```
- Run the Gradle wrapper with the artifact you want to produce. 
(e.g. `./gradlew shadowJar` produces the full executable jar file)

## Copyright

All code in this project is licensed under the GNU GPL v3.
The fonts used for the timer are licensed under the 
[SIL OPEN FONT LICENSE Version 1.1](http://scripts.sil.org/OFL)
The DSEG7 Modern font was created by keshikan and the source code is available on 
[GitHub](https://github.com/keshikan/DSEG)