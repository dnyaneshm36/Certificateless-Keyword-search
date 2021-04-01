# Certificateless Public Key Encryption with Keyword search.

Welcome to the Certificateless Public Key Encryption with Keyword search implemented using Java. 

Here is a guideline to help you understand code.

# Folder Structure

The workspace contains two folders, where:

- `src`: the folder to maintain java sources.
- `jpbc Library`: the folder contains jpbc library jar files. [Download jpbc. ](http://gas.dia.unisa.it/projects/jpbc/download.html#.YGVrEXUzZEY)


# Requirements
### Install JRE.
        sudo apt install default-jre 
### Verify the installation.
        java -version
### Install JDK.
        sudo apt install default-jdk
### Verify the installation.
        javac -version
### Setting the JAVA_HOME Environment Variable.
#### Open editer by cmd.
        sudo gedit /etc/environment
#### Copy paste the path.       
        JAVA_HOME="/usr/lib/jvm/java-11-openjdk-amd64"
#### Reloading changes.
        source /etc/environment
#### Verify that the environment variable is set.
        echo $JAVA_HOME
for infromation check. [more](https://www.digitalocean.com/community/tutorials/how-to-install-java-with-apt-on-ubuntu-18-04)

# Performance
### Machine configurations.
        OS                              : Ubuntu 20.04.2 LTS
        OS TYPE                         : 64-bit
        Architecture:                   : x86_64
        CPU                             : Intel Core i5-8250U 8 x 1.60GHz	
        CPU                             : 826.581 MHz
        CPU max                         : 3400.0000 MHz
        CPU min                         : 400.0000 MHz
        Memory                          : 8 GB 2.133GHz DDR4
        Hard Disk                       : 2TB
        Programming                     : JAVA Programming language.


### Avarage time to run algorithm. (SHA-256) used in Hash.

        Setup                           : 31.492 (milli) second.
        Sender keygen                   : 155.749 (milli) second.
        Receiver keygen                 : 153.459 (milli) second.
        Cipher                          : 247.070 (milli) second.
        Trapdoor                        : 108.658 (milli) second.
        Test                            : 146.516 (milli) second.


## Java Vscode

The `JAVA DEPENDENCIES` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-pack/blob/master/release-notes/v0.9.0.md#work-with-jar-files-directly).