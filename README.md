# 051 MotorIST Project Read Me

<!-- this is an instruction line; after you follow the instruction, delete the corresponding line. Do the same for all instruction lines! -->

## Team

| Number | Name        | User                             | E-mail                                   |
|--------|-------------|----------------------------------|------------------------------------------|
| 103668 | David Palma |  https://github.com/DavidMLPalma | david.palma@tecnico.ulisboa.pt           |
| 103698 | João Durão  |   https://github.com/1joaodurao1 | joaopedrocostacorreia@tecnico.ulisboa.pt | 


![João](/img/João.png) ![David](/img/David.png)

## Contents

This repository contains documentation and source code for the *Network and Computer Security (SIRS)* project.

The [REPORT](/REPORT.md) document provides a detailed overview of the key technical decisions and various components of the implemented project.
It offers insights into the rationale behind these choices, the project's architecture, and the impact of these decisions on the overall functionality and performance of the system.

This document presents installation and demonstration instructions.

## Installation

To see the project in action, it is necessary to setup a virtual environment, with 3 networks and 4 machines.

The following diagram shows the networks and machines:

![Diagram](/img/network.png)


### Prerequisites

All the virtual machines are based on: Linux 64-bit, Kali 2023.2a

[Download](https://turbina.gsd.inesc-id.pt/csf2324/resources/kali-linux-2023.2a-installer-amd64.iso) and [install](INITIALIZE_KALI.md) a virtual machine of Kali Linux 2023.2a.

### Machine configurations

In the Virtual Box, we will now set our VMs according to the network image provided above.

Rename the VM to Client

Before starting our demonstration, we must change the network settings.
- Select the VM Settings/Network/Adapter1
- Replace NAT and attach to Internal Network. Call it **"sw-1"**. We will use this switch to connect to the API.
- Promiscuous Mode: Allow VMs 
- Do the same for Adapter2 and call it **"sw-2"**. We will use this switch to connect to the manufacturer.
- Enable Adapter 3 and attach NAT


Now clone the Client machine to create the other machines:
- MAC Adress Policy: Generate new MAC addresses for all network adapters
- Select Linked Clone
- Name the machines for practicality purposes: Manufacturer, Server, Database


For the Manufacturer:
- Select the VM Settings/Network/Adapter1
- Rename it **"sw-2"**. We will use this switch to connect to the client.
- Replace Internal Network on Adapter2 and attach to NAT.
- Disable Adapter3

For the Server:
- Select the VM Settings/Network/Adapter1
- Don't change anything here. We will use this switch to connect to the client.
- Select the VM Settings/Network/Adapter2
- Rename it **"sw-3"**. We will use this switch to connect to the database.
- Don't change Adapter3

For the Database:
- Select the VM Settings/Network/Adapter1
- Rename it **"sw-3"**. We will use this switch to connect to the API.
- Replace Internal Network on Adapter2 and attach to NAT.
- Disable Adapter3

Now let's access the machines !

#### Client

This machine runs the **client application**, which consists of a CLI.

Initialize the machine with the appropriate script

```sh
$ cd setup/VMclient
$ bash init-client.sh
```

Reboot the system to apply changes !

To verify the network setup:
```sh
$ ifconfig
```

The output should be something like this:
```
eth0: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 1500
        inet 192.168.0.1  netmask 255.255.255.0  broadcast 192.168.0.255
        inet6 fe80::a00:27ff:fea0:47d9  prefixlen 64  scopeid 0x20<link>
        ether 08:00:27:a0:47:d9  txqueuelen 1000  (Ethernet)
        RX packets 0  bytes 0 (0.0 B)
        RX errors 0  dropped 0  overruns 0  frame 0
        TX packets 16  bytes 2424 (2.3 KiB)
        TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0

eth1: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 1500
        inet 192.168.1.1  netmask 255.255.255.0  broadcast 192.168.1.255
        inet6 fe80::a00:27ff:fec8:79e7  prefixlen 64  scopeid 0x20<link>
        ether 08:00:27:c8:79:e7  txqueuelen 1000  (Ethernet)
        RX packets 0  bytes 0 (0.0 B)
        RX errors 0  dropped 0  overruns 0  frame 0
        TX packets 16  bytes 2424 (2.3 KiB)
        TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0
...
```

Install the project dependencies and targets:
```sh
$ mvn clean install
```

To run the client:
```sh
$ mvn -pl client exec:java
```

After all is setup, you can disable the Adapter with NAT, as we don't need it anymore !


#### Manufacturer

This machine runs the **manufacturer**, which acts as a server(Spring Boot 3.2.0).

Initialize the machine with the appropriate script

```sh
$ cd setup/VMmanufacturer
$ bash init-manufacturer.sh
```

Reboot the system to apply changes !

To verify the network setup:
```sh
$ ifconfig
```

The output should be something like this:
```
eth0: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 1500
        inet 192.168.1.254  netmask 255.255.255.0  broadcast 192.168.1.255
        inet6 fe80::a00:27ff:fe84:63e2  prefixlen 64  scopeid 0x20<link>
        ether 08:00:27:84:63:e2  txqueuelen 1000  (Ethernet)
        RX packets 0  bytes 0 (0.0 B)
        RX errors 0  dropped 0  overruns 0  frame 0
        TX packets 17  bytes 2494 (2.4 KiB)
        TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0
...
```

Install the project dependencies and targets:
```sh
$ mvn clean install
```

To run the manufacturer:
```sh
$ mvn -pl manufacturer spring-boot:run
```

After all is setup, you can disable the Adapter with NAT, as we don't need it anymore !


#### Application Server

This machine runs the **API server** (Spring Boot 3.2.0).

Initialize the machine with the appropriate script

```sh
$ cd setup/VMserver
$ bash init-server.sh
```

Reboot the system to apply changes !

To verify the network setup:
```sh
$ ifconfig
```

The output should be something like this:
```
eth0: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 1500
        inet 192.168.0.254  netmask 255.255.255.0  broadcast 192.168.0.255
        inet6 fe80::a00:27ff:fe56:738d  prefixlen 64  scopeid 0x20<link>
        ether 08:00:27:56:73:8d  txqueuelen 1000  (Ethernet)
        RX packets 0  bytes 0 (0.0 B)
        RX errors 0  dropped 0  overruns 0  frame 0
        TX packets 20  bytes 2694 (2.6 KiB)
        TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0

eth1: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 1500
        inet 192.168.2.1  netmask 255.255.255.0  broadcast 192.168.2.255
        inet6 fe80::a00:27ff:fed4:6f7a  prefixlen 64  scopeid 0x20<link>
        ether 08:00:27:d4:6f:7a  txqueuelen 1000  (Ethernet)
        RX packets 0  bytes 0 (0.0 B)
        RX errors 0  dropped 0  overruns 0  frame 0
        TX packets 19  bytes 2634 (2.5 KiB)
        TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0
...
```

Finally, the application-server/src/main/resources/application.properties file must be changed !

```
spring.datasource.url=jdbc:postgresql://192.168.2.254:5432/cardb?ssl=true&sslmode=verify-ca&sslrootcert=pathtoconfig/ca.crt
```

```sh
$ mvn clean install
```

To run the server:
```sh
$ mvn -pl application-server spring-boot:run #Exit server after running; This is just to install dependencies
```

Lets setup the firewall now:
```sh
$ cd setup/VMserver
$ bash firewall_setup.sh
```

To make the iptables rules persistent, in server we install iptables-persistent. Select "yes" to save the current rules when prompted.

To verify the firewall setup:
```sh
$ sudo iptables -L
```

The output should be something like this:
```
Chain INPUT (policy DROP)
target     prot opt source               destination         
ACCEPT     tcp  --  anywhere             anywhere             tcp dpt:8443
ACCEPT     tcp  --  anywhere             anywhere             tcp spt:postgresql

Chain FORWARD (policy DROP)
target     prot opt source               destination         

Chain OUTPUT (policy ACCEPT)
target     prot opt source               destination
```

According to this:
- We DROP all incoming packets, except those that come to port 8443 and those that come from the database, port 5432.
- We DROP all packets to be forwarded
- We ACCEPT all packets sent from this machine on any port, since the communication with the database is from a random port.

After all is setup, you can disable the Adapter with NAT, as we don't need it anymore !


#### Database

This machine runs the **database server** (PostgreSQL 15).

Initialize the machine with the appropriate script

```sh
$ cd setup/VMdatabase
$ bash init-db.sh
```

Reboot the system to apply changes !

To verify the network setup:
```sh
$ ifconfig
```

This should be the output:
```
eth0: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 1500
        inet 192.168.2.254  netmask 255.255.255.0  broadcast 192.168.2.255
        inet6 fe80::a00:27ff:fedb:a244  prefixlen 64  scopeid 0x20<link>
        ether 08:00:27:db:a2:44  txqueuelen 1000  (Ethernet)
        RX packets 0  bytes 0 (0.0 B)
        RX errors 0  dropped 0  overruns 0  frame 0
        TX packets 15  bytes 1805 (1.7 KiB)
        TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0
...
```


Now, we must create our database :
```sh
$ sudo service postgresql start
$ sudo -u postgres psql
$ CREATE DATABASE cardb; #\l to see databases
$ CREATE USER sirs WITH ENCRYPTED PASSWORD 'sirs'; #\du to see users
$ GRANT ALL PRIVILEGES ON DATABASE cardb TO sirs;
$ GRANT ALL PRIVILEGES ON SCHEMA public TO sirs;
$ ALTER ROLE sirs WITH SUPERUSER;
```

It is done ! We can now exit :

```sh
$ \q
```

To see that everything is going well:
```sh
$ psql -U sirs -d cardb # Connect to the cardb database with new user
$ sirs # Password
$ \dt # List the tables in the cardb database. See if our tables are there !
```

Lets setup the firewall now:
```sh
$ cd setup/VMdatabase
$ bash firewall_setup.sh
```
To make the iptables rules persistent, in server we install iptables-persistent. Select "yes" to save the current rules when prompted.

To verify the firewall setup:
```sh
$ sudo iptables -L
```

This should be the output:
```
[sudo] password for david: 
Chain INPUT (policy DROP)
target     prot opt source               destination         
ACCEPT     tcp  --  anywhere             anywhere             tcp dpt:postgresql

Chain FORWARD (policy DROP)
target     prot opt source               destination         

Chain OUTPUT (policy DROP)
target     prot opt source               destination         
ACCEPT     tcp  --  anywhere             anywhere             tcp spt:postgresql
```

According to this:
- We DROP all incoming packets, except those that come to port 5432
- We DROP all packets to be forwarded
- We DROP all packets sent from this machine except those sent in port 5432

After all is setup, you can disable the Adapter with NAT, as we don't need it anymore !


## Demonstration

Now that all the networks and machines are up and running, lets see its security functionalities !
```sh
$ user read_config # for example
```
- When a user exchanges messages with server through commands, TLS ensures encryption.

![packet_list](/img/packet_list_server_client.png)
![packet_tls](/img/packet_tls.png)

- Furthermore, we double the messages with secure-document library, so when they arrive on the server and on client they are encrypted
![client_response](/img/client_response.png)


- Regarding the communication, TLS is also used to protect communication
![database_wireshark](/img/database_wireshark.png)

  
That sums up the security implementations on our project.

Additionally, if any of the user/mechanic/owner tries to access one resource that is unauthorized, they will receive an appropriate response:
![user_view_logs](/img/user_view_logs.png)

This concludes the demonstration.



### Troubleshooting

Here are the most common mistakes while trying to configure the machines:

- When trying to run or install the project, you are not able to download to dependencies.
- This may have to do with the /etc/resolv.conf , as the nameserver may not be the one expected.
  - Replace it with 8.8.8.8
- If file is okay, is NAT configured ?
- If yes, the firewall may be interfering
  - Apply ACCEPT to both INPUT and OUTPUT chains
```
No plugin found for prefix 'spring-boot' in the current project and in the plugin groups [org.apache.maven.plugins, org.codehaus.mojo] available from the repositories [local (/home/david/.m2/repository), central (https://repo.maven.apache.org/maven2)]
```

- When trying to setup database, PostgreSQL server not initializing.
- This may have to do with the version PostgreSQL
  - If using the Kali image provided, version is 15.
  - Check if files being updated in the scripts are of that version
- Checking for postgresql logs in /var/log/postgresql/main/logs_main... may be useful

```sh
psql: error: connection to server on socket "/var/run/postgresql/.s.PGSQL.5432" failed: No such file or directory
        Is the server running locally and accepting connections on that socket?
```


### License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) for details.

----
END OF README