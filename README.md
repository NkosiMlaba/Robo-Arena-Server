### Setup and Installation:

1. On a linux machine, connect to the internet and run:
        
        sudo apt update

2. To install maven run:
        
        sudo apt install maven -y

3. To verify the installation, run:
        
        mvn -version

4. Install java using:
        
        sudo apt install default-jdk -y

5. To verify the installation, run:
        
        java -version

6. Install make using:
        
        sudo apt-get install build-essential

7. Verify make install using:
        
        make --v

<br>

### Running instructions:
##### Building the project
1. Run this command to build the project:
        
        make all

##### Running the server
1. Run the server:
        
        make server


##### Running the client:
1. Run the client:
        
        make client

##### Running the webapi:
1. Run the webapi:
        
        make webapi
            

<br>

### Test running instructions:
##### Run unittests tests only:
1. Run the tests with the following command:
        
        make tests

##### Run all acceptance tests:
1. Run the tests with the following command:
        
        make acceptance


##### Run acceptance tests against reference server:
1. Run the tests with the following command:
        
        make acceptance-reference

##### Run acceptance tests against our server:
1. Run the tests with the following command:
        
        make acceptance-ourserver
<br>


# Docker running instructions
#### (find all docker images in your pc)
    sudo docker images

#### (builds the image)
    sudo docker build -t java-server .

#### (runs the server inside the container, using port binding)
    sudo docker run -t -p 5050:5050 java-server

#### next step is to allow own port terminal to interact with container terminal
    sudo docker run -t -p 5050:5050 --rm -it java-server

#### Pulling the image
    sudo docker pull nmlaba023/2023-brownfields-server-deployment:latest
    sudo docker images
    sudo docker run -t -p 5050:5050 --rm -it java-server

<br>

# Usage:
##### Client commands:
Here are some basic commands you can use in the client to control your robot:

    forward <steps>: Move the robot forward by the specified number of steps
    back <steps>: Move the robot backward by the specified number of steps
    left: Turn the robot to the left
    right: Turn the robot to the right
    look: Show the current state of the world around the robot
    state: Show the current state of the robot
    fire: Shoot a bullet in the current direction of the robot
    repair: Repair the robot's shields
    reload: Reload the robot's ammo
    off: Quit the client and disconnect from the server

##### Server commands:
Here are some basic commands you can use to control your server:

    robots: lists all robots in server
    dump: lists the state of the world
    quit: quit the server
    save: saves the current world to a database
    restore: restores the world from a database
    clear: clears the terminal interface

<br>


### Documentation:

For detailed documentation and additional resources, please visit our [Wiki](https://gitlab.wethinkco.de/nmlaba023/dbn03_brownfields_2024/-/wikis/pages).

<br>
