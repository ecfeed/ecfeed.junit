# ecfeed.junit

The project was generated using [IntelliJ](https://www.jetbrains.com/idea/download/#section=linux), and therefore it is recommended to use the same IDE for future development. Note, that the free (community) version is enough to run the defined gradle tasks.

## Creating a local repository

To download the source code and create a local repository, you have to:
1) Connect to the remote repository, i.e. File - New - Project from Version Control, provide a valid GitHub URL and press clone. Note, that you might be asked for your credentials during the process. You can set the download directory according to your personal preference.
2) Switch to the correct branch (develop). Go to the project directory (it contains the .git folder), open the terminal and type <b>git fetch</b>, then <b>git checkout develop</b>.
3) Go to the project directory (it contains the .git folder), open the terminal and type <b>git submodule update --recursive --remote</b>. This command updates the submodules in the project path.
4) Go to the submodule directory, i.e. reference/submodule/ecfeec.core. Open the terminal and switch to the correct branch <b>git fetch</b>, then <b>git checkout develop</b>.
  
 ## Creating .jar files
 
 1) To generate the jar file without dependencies, click on the gradle menu (on the right side of the IDE) and select: Tasks - build - jar.
 2) to generate the jar file with all dependencies included, click on the gradle menu (on the right side of the IDE) and select: Tasks - other - jarWithDependencies.
