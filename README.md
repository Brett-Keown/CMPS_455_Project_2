# CMPS 455 Project 2

This project demonstrates three access-control schemes. Select the scheme by
passing `-S` followed by its number:

| Argument | Task |
| --- | --- |
| `-S 1` | Access Matrix |
| `-S 2` | Access List for Objects |
| `-S 3` | Capability List for Domains |

Only these three options are accepted. For example, `-S 2` runs the Access
List task. Missing arguments, extra arguments, or an unsupported option display
the usage message.

## Run from IntelliJ IDEA

### Shared configurations 

After getting the latest project files, open the project in IntelliJ and choose
`Access Matrix`, `Access List`, or `Capability List` from the run-configuration
dropdown in the toolbar, then click **Run**. You should not need to create the
configurations again. If they do not appear, check that the shared run
configuration files were committed and included with the project; marking a
configuration as shared saves it in the project, but does not by itself send it
to other contributors. IntelliJ stores shared configurations as project files,
commonly under `.run` or `.idea/runConfigurations`.



## Run from a command line

Recompile the project and run the `Main` class with the desired scheme.

```text
javac --release 11 -d out src\*.java
java -cp out Main -S 1
```

Replace `1` with `2` or `3` to run another scheme.

