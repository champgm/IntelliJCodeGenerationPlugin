# Mac's Code Generation And Action Plugin
name subject to change



So far this plugin contains these actions:
------------------------------------------
 * [Generate Preconditions](https://github.com/champgm/IntelliJCodeGenerationPlugin/blob/wip/src/com/champgm/intellij/plugin/preconditions/GeneratePreconditionsChecks.java) - looks at the parameters of the method in which the caret is placed, and generates preconditions for any non-primitve parameters. It will also use StringUtils to check to see if String parameters are null or empty. 
 * [Make Parameters Final](https://github.com/champgm/IntelliJCodeGenerationPlugin/blob/wip/src/com/champgm/intellij/plugin/parameters/MakeParametersFinal.java) - Iterates through all methods in a class and all paramters in those methods and marks them as "final" if they are not already final. 
