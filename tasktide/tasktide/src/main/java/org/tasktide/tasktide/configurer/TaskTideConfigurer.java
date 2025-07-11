/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.tasktide.tasktide.configurer;

import org.eclipse.microprofile.config.Config;
import org.tasktide.tasktide.parser.ArgumentTree;
import org.tasktide.tasktide.parser.model.ArgumentBuilder;
import org.tasktide.tasktide.parser.model.ArgumentMap;


/**
 * Interface for configuring and population programs {@link ArgumentTree}
 * 
 * @author bkenna
 */
public interface TaskTideConfigurer {
 
    public void addToTree(ArgumentTree argTree);
    public ArgumentMap getArgumentMap();
    public ArgumentBuilder getArgumentBuilder();
    public String getPath();
    public Config getConfig();
    public boolean parseCommandLineArguments(String[] argsIn, ArgumentTree argTree);
    public void initConfig(ArgumentTree argTree); // Separate methods used for config, this just runs them
}