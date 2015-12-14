# CS726 HW3: Message passing in undirected graphical models

Pararth Shah 09005009


## Executing the code:

This repo contains a Netbeans project which implements MPA on a UGM. Please import it in netbeans, and you can execute it easily from the IDE. If you are compiling from commandline, please compile all java classes under the package mpa in src/. There are no external dependencies. The class mpa.MPA contains the main class which should be executed. 

Please set the string "dir" in the main function to point to the folder containing the input files graph.log and potentials.log



## Comments on the status of the code:

The code is divided into Java classes:

- UGM.java: Models an undirected graphical model
- CPT2.java: Models a conditional probability table (slight change from implementation of CPT.java, but this new version is used by all classes)
- JTree.java: Models a junction tree on the underlying UGM
- MPA.java: Main class which executes the message passing algorithm

In MPA.java, the graph and potentials are first read, then the graph is triangulated, and then the Junction tree is computed.

After that, I have written code for computing MAP, but I was unable to debug it, so it is commented out.

For computing marginals, I have written and compiled the code, it works on small cases.
