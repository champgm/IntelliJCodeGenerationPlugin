# Mac's Code Generation And Action Plugin
name subject to change

Instructions for use:
------------------------------------------
 Arguably, the best way to use this plugin's finalization features is to add them to some keyboard shortcut that you are used to pressing frequently without thinking about it. For some people this is Ctrl+S for saving, or one for reformatting code. Here is what I personally like to do :
 * Install the plugin
 * Open any class file with some stuff written in it already
 * Click somewhere within the class body
 * From the top menu, select Edit -> Macros -> "Start Macro Recording"
 * From the top menu (or the right-click menu) select "Refactor"
 * Click each of these items, "Make Variables Final", "Make Fields Final", and "Make Parameters Final".
 * From the top menu, select "Code"
 * Click each of these items, "Reformat Code", "Rearrange Code", and optionally (it can cause problems with Scala coding) "Optimize Imports" 
 * If you're editing this into a save-actions-type macro, from the top menu, select File -> "Save All"
 * At this point, you can optionally add any other actions that you think are appropriate to apply
 * From the top menu, select Edit -> Macros -> "Stop Macro Recording"
 * A dialog box will pop up. Name this something recognizable, like "Refactor, Reformat, and Rearrange"
 * Open the keymap in the preferences dialog (easisest way is to open the preferences dialog and type "keymap" into the search box"
 * Search for your macro with the box at the top right, type whatever name you assigned it in that box.
 * Double click on the macro and pick "Add Keyboard Shortcut"
 * In the first textbox, press the key combination you want to use to trigger these actions
 * Double check the "Conflicts" section at the bottom of this dialog to make sure you're not about to un-bind something impormtant, then click "OK"
 * If you get a prompt about the shortcut already being assigned, click "Remove"
 * You should now have a keyboard shortcut that will neaten up your code automatically. Don't forget to press it often. 
 

So far this plugin contains these actions:
------------------------------------------
 * [Generate Preconditions](https://github.com/champgm/IntelliJCodeGenerationPlugin/blob/wip/src/com/champgm/intellij/plugin/preconditions/GeneratePreconditionsChecks.java) - looks at the parameters of the method in which the caret is placed, and generates preconditions for any non-primitve parameters. It will also use StringUtils to check to see if String parameters are null or empty. To trigger this action, right click within a method or constructor, select "Generate" and then pick "Add Preconditions Checks" from the list.
 * [Make Parameters Final](https://github.com/champgm/IntelliJCodeGenerationPlugin/blob/wip/src/com/champgm/intellij/plugin/parameters/MakeParametersFinal.java) - Iterates through all methods in a class and all paramters in those methods and marks them as "final" if they are not already final. 
 * [Make Fields Final](https://github.com/champgm/IntelliJCodeGenerationPlugin/blob/wip/src/com/champgm/intellij/plugin/variables/MakeFieldsFinal.java) - Iterates through all fields in a class and marks them as "final" if they are not modified. 
 * [Make Variables Final](https://github.com/champgm/IntelliJCodeGenerationPlugin/blob/wip/src/com/champgm/intellij/plugin/variables/MakeVariablesFinal.java) - Iterates through all variables in a class and marks them as "final" if they are not modified.
