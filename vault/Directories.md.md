[Return](professionals.md)
## Special Directories ##
G_String uses two special directories: 'work directory' and 'home directory'.

### Work Directory ###
The work directory is a special directory that needs to be established in the initial setup process, optimally in the operating system's 'user' directory. It provides the work environment for 'urGENOVA'. It contains the machine code of urGENOVA. The OS appropriate version is copied in automatically as part of the setup process.

During program execution the preprocessed data input file  '~data.txt', and a generated script file '~control.txt' are imported. G_String then calls urGENOVA, which provides its output as '~control.txt.lis'. The latter is then used by G_String for further processing.

### Home Directory ###
Any directory, from which the user selects a script or data input file automatically becomes the 'home directory'. G_String uses it for result-, as well as for log-files. But the home directory changes, whenever another directory is used for G_String input. The current home directory is remembered between G_String uses.