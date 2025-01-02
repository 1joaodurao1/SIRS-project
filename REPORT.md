# A051 MotorIST Project Report

## 1. Introduction

### MotorIST

IST is now selling electric cars with modern management systems. The system allows users to configure the car remotely, such as close/open the car; configure the AC; check the battery level. This is done by a user application installed on the user's computer or mobile device. Additionally, to maintain the car up to date, the car also allows for firmware updates from the manufacturer.

### Project Overview

The MotorIST project aims to develop a secure and efficient communication system for managing an  electric car. The system facilitates remote configuration of car settings and firmware updates, ensuring that the car remains up-to-date and operates optimally. The primary components of the system include the application server, client application, and manufacturer server and a database server, all of which communicate using secure protocols.

In order to do this we implemented a library that is also a CLI application with 3 main functionalities:

- Protect -> Receives a JSON file ciphers it and generates an encrypted JSON output-file.
- Unprotect -> Receives an encrypted JSON input-file, decrypts it and generates the original JSON file.
- Check -> Receives an encrypted JSON input-file and verifies if the integrity of the encryption has been compromised or not.

In this report , I will further explain this secure-document module and how it works.

The client application can do a multitude of commands and depending on the command and the role choose it acts as that type of user.

Commands available :

```sh 
There is 1 owner, 1 mechanic and 1 normal user created
    - Normal User commands:
        - user read_config
        - user change_config [config:value]
        - user update_firmware 
        - user view_logs
        - user set_maintenance on/off password
    - Owner: 
        - owner read_config
        - owner change_config [config:value]
        - owner update_firmware 
        - owner view_logs
        - owner set_maintenance on/off password
    - Mechanic: 
        - mechanic read_config no (reads as mechanic)
        - mechanic read_config yes (reads as owner)
        - mechanic change_config [config:value]
        - mechanic update_firmware 
        - mechanic view_logs
        - mechanic set_maintenance on/off password
    - Exit : Exists the program
    - Help : Displays the possible commands

```

**Network UML Diagramm** :

![network_uml](/img/network.png)


## 2. Project Development

### 2.1. Secure Document Format

#### 2.1.1. Design

The Library we made to secure our documents , is consisted of ,basically, 3 functionalities , **Protect, Check and Unprotect** , which are responsible , respectively , for ciphering a document, checking the integrity of one and deciphering.

Because this module is designed to be a versatile library and a CLI application , its architecture is modular, ensuring that each component handles a specific aspect of the secure document processing.

- **Core Components** :
  - **CryptographicOperations** : This class provides the main API for the secure document operations, including `protect`, `unprotect`, and `check`. It orchestrates the use of encryption and digital signature functionalities.
  - **SymmetricCipherImpl** : This class implements the `CipherMethod` interface and handles the encryption and decryption of document content using AES.
  - **DigitalSignatureImpl** : This class implements the `IntegrityMethod` interface and handles the creation and verification of digital signatures using SHA-256 with RSA.

- **Interfaces** :
  - **CipherMethod** : Defines the methods for encryption and decryption operations.
  - **IntegrityMethod** : Defines the methods for creating and verifying digital signatures.

- **Validation** :
  - **ValidationHandler** : This class handles the validation of user input commands in the CLI application.
  - **ProtectValidation** : This class provides specific validation logic for the `protect` command.
  - **UnprotectValidation** : This class provides specific validation logic for the `unprotect` command.
  - **CheckValidation** : This class provides specific validation logic for the `check` command.

- **CLI Application** :
  - **CommandLineInterface** : This class provides a command-line interface for interacting with the `secure-documents` module. It supports commands for protecting, unprotecting, and checking documents.



The usage of this library through it's CLI is as followed:
```sh
protect <input-file> <output-file> <sender> <reciever>
unprotect <input-file> <output-file> <reciever>
check <input-file> <sender> <reciever>
```

A **sender** and a **reciever** can be either a user, owner or mechanic.
When using this interface be carefull how you use it because , the way unprotect and check are called is dependent on how protect was called

An example:

```sh 
protect example.json out.json owner mechanic
```

In here the sender of the message is the owner and the reciever is the mechanic, this means that we have to do the following unportect and check calls.

```sh 
unprotect out.json  deciphered.json mechanic
check out.json owner mechanic
```

Since the scope of this library was very specific to our buiseness scenario , there was no need to accomodate to various styles of inputs and outputs as we knew that it would only be used in a very specific way , so the code is only made to recieve JSON of a specified format.

**Example of input:**

````json 
{
  "content": {
    "user": "user",
    "command" : "update",
    "configuration": {
       "ac": [
          { "out1": "123"},
          { "out2": "456"}
       ],
       "seat": [
          { "pos1": "1"},
          { "pos3": "2"}
       ]
    }
   },
   "metadata" : {
      
   }
}
````

**Example of output:**

````json 
{
    "content":"ImzovFpTdZ0Utcfma1S3YD2CUdpYiYY4NNv4qyO1J/1mBytuZGEtd3lHomlGf1JEZAgG4QLZwqipkRYGdU7KI5E4f7fiAxeyn07YJEOVUsLmkHZHbXoKRwzt2coORX2C9rFRuNvZtDnhxBEXmeT7RiJbMNRvJA0RXR5AFrZCvTs\u003d",
    "metadata":
    {
        "signature":"EX4X/EvjYe44SK9j9ELwMkEhTCbxnjf7ffHasgQSOx4Hma8wYHRpv/xfEJbAo8zL1ObrDQ8bg8uE/ZEGnvJsmdORuokFku1gjhs8upo3Qq0aQ9xjt8pjogMLe6CF0yLaeU+Dh71BCPWwTu00Yag/SiFFzhRYJvFt6PKUKDfXSR8Rd3CHozOpCZ2TCeNcbolXRP8EuxNv3Lfbh0IwEgbL4M6OTL9TwQc7vQKWKOdBU30TBVx5eZwcXJhg1oc6k4Y64bIX3WRNIhayiiG/1mVLlnM+016iepPFiJp235IhwGChbh5sezzeZxGBnxqYS8YOXbvDVL3Ttu/Jq1VkxjNKcA\u003d\u003d","key":"RTSCnGhJ7duTjGlKCHYnR1zeDTzPOBGP5NI9o8mC9e67uEOTXIlgEcbKRbJ+mSPHT2rNxIQWmVpjoDrM9UghKdUGqn+ijlD7cUc+l2qsxwTPeIwrPvZGkUEwblajKyZJMbYvnKv94MJ7JLx+c4f90amTv28gNxZeYvb8OiHYZbVsPEfzVtZe6eiq+M/BLVYjEYPK2yEXcPROqrVQy7jtYHx5DwgSpehIkKFGYm9Pf/Yi6JI/C8QFEriMA/STSxCygL8cgKMGyasvq9Q06KFyrYIJvqRdRVs5pM1KmvciIFXr4z9Y76OBQjGaa5R0bn3cu4vY+3COO6LhHTfBIPx8rA\u003d\u003d",
        "iv":"CJe1VU3tpK4DAaJNiPhiAymqjdOiVBWIsjm59OifPCfpb4NeGbphgCCIJaNrsybfHg67Qsux5eZADnOG9lD4Pk3Nnyz1BOTAorHIFcKPson+tQs26f+X3gK87AhOjrMAWBAOtYxUfG/Dctb4nZ9JOffjkXGNZ+E5kvTicP/WaM3zMyB7hnfstxatDGDcb9qt48f59MR27tSA0mMeQCJKvdl/m8D/r+ATDo70tdCFYv4AtKmoqYomqjVLrkjkxpTXnbo6M7bzRixiK3d2crFgMxtSEG/+Mk67E8i9Gw+wC5j0M5+5my6fFKhgnCQS1X9FPtDyuEsBgfGxd+hpEuzXKw\u003d\u003d"
    }
}
````

Basically , the format of the input JSON needs to have two built in sections, **content** and **metadata**, the content one is for whatever content the json wants to hold and the metadata is to store information about the encryption.

The names provided for the sender and reciever fields are very important because we use information about those users ( their private and public keys ) in order to encrypt and decipher the document. 
These keys are stored inside the secure documents module in the resources folder, so based on the names provided we can use the correct keys.
Hybrid ciphering is used in order to protect the document, inside the Protect function a symmetric cipher and an Initialization Value array are generated and used to encode the **content** section that is, later, encoded to a string and stored again inside that same section.
The created symmetric cipher and IV are stored inside the **metadata** block , but before that, they are both encrypted with the public key of the reciever that has been provided so that only the excepted reciever can decipher the whole document.
Additionally , we use a **Digital Signature** aswell, so when protecting it is generated a value that corresponds to a Digitak Signature that is stored, once again, in the **metadata** section of the JSON.

- **Protect and Unprotect** : They both generate output Jsons with opposite content of one another, meaning the output JSON of Protect has the content ciphered while the one from Unprotect has not. The Protect action can deal with empty an **metadata** section has it does need to read anything from it. On the contrary , the Unportect needs to read the symmetric key and the IV from that section so it must have those properties inside otherwise it will not work.

- **Check** : It does not generate an output Json , it merely checks whether or not the JSON has been tampered with , so it generates a positive or negative response based on the quality of the integrity of the encrypted content.

This way we can ensure the confidentiality, integrity, and authenticity of the data exchanged between the application server, client, and manufacturer because this library is used to cipher and decipher all data in traffic between these entities.

- **Confidentiality:** 
  - **Algorithm:** AES (Advanced Encryption Standard) in CBC (Cipher Block Chaining) mode with PKCS5Padding.
  - **Rationale:** AES is chosen for its efficiency and strong security properties, making it suitable for encrypting large amounts of data. CBC mode ensures that identical plaintext blocks are encrypted differently, enhancing security. PKCS5Padding ensures that the plaintext is padded to a multiple of the block size, preventing padding oracle attacks. AES is a NIST-approved encryption standard widely used in various applications.
  - **Implementation:** The `SymmetricCipherImpl` class handles the encryption and decryption of the document content using AES. The `encrypt` method generates a symmetric key and IV, encrypts the content, and then encrypts the key and IV using RSA. The `decrypt` method reverses this process.

- **Integrity:**
  - **Algorithm:** SHA-256 with RSA for digital signatures.
  - **Rationale:** SHA-256 provides a strong hash function, ensuring that any modification to the document can be detected. RSA is used to sign the hash, providing a way to verify the authenticity of the document. This combination ensures that the document has not been tampered with and that it originates from a trusted source. Both SHA-256 and RSA are NIST-approved algorithms, ensuring their reliability and security.
  - **Implementation:** The `DigitalSignatureImpl` class handles the creation and verification of digital signatures using SHA-256 with RSA. The `signature` method signs the document content, and the `checkDigest` method verifies the signature.

- **Authenticity:**
  - **Algorithm:** RSA for digital signatures.
  - **Rationale:** RSA is a widely used public-key cryptosystem that provides strong security for digital signatures, ensuring that the document was indeed sent by the claimed sender. The use of public and private keys allows for secure verification of the sender's identity. RSA is a NIST-approved algorithm, ensuring its reliability and security.
  - **Implementation:** Each document includes a digital signature generated using the sender's private key. The recipient can verify the signature using the sender's public key.


#### 2.1.2. Implementation

The implementation of the secure document format is done using Java, leveraging the `javax.crypto` and `java.security` packages for cryptographic operations. The Gson library is used for JSON parsing and serialization.

- **Encryption**
  - **Class** : `SymmetricCipherImpl` 
  - **Methods** :
    - `encrypt(JsonObject inputJson, String userType, Integer moduleId)`: Encrypts the content of the document. Generates a symmetric key and IV based on AES, encrypts the content, and then encrypts the key and IV using RSA.
    - `decrypt(JsonObject inputJson, String userType, Integer moduleId)`: Decrypts the content of the document. Decrypts the key and IV using RSA, and then decrypts the content using the symmetric key and IV.
    - `encryptDB(JsonObject json, byte[] keyBytes, byte[] iv)`: Encrypts the database configuration using AES. This method is used to securely store configurations in the database. So it is not used by the CLI application of secure-documents, however it is used in the applicatioon server , so the library must have this function.
    - `decryptDB(String encryptedData, byte[] keyBytes, byte[] iv)`: ecrypts the database configuration using AES. This method is used to retrieve and decrypt configurations from the database. Once again , it is not used by the CLI but it is used in the library.

- **Digital Signature** 
  - **Class** : `DigitalSignatureImpl`
  - **Methods** : 
    - `signature(JsonObject inputJson, String senderUser, Integer moduleId)`: Signs the document content using SHA-256 with RSA. Generates a hash of the content and signs it with the sender's private key.
    - `checkDigest(JsonObject inputJson, String senderUser, Integer moduleId, String signatureStr)`: Verifies the digital signature using SHA-256 with RSA. Generates a hash of the content and verifies it with the sender's public key.
    - `signGetRequest(String command, String senderUser, Integer moduleId)`: Signs a GET request using SHA-256 with RSA. This method is used to generate a Digital Signature of the string that represents the command to insure integrity and authentication.Once again , it is not used by the CLI but it is used in the library and it will later be further explained on how it is used.
    - `checkGetRequest(String command, String senderUser, String signatureStr, Integer moduleId)`: Verifies the signature of a GET request using SHA-256 with RSA. Once again , it is not used by the CLI but it is used in the library.

The `moduleID` that appears in almost every function argument list is used to identify from where the call of the function came from , meaning from which of the 4 modules it came ( **client, application-server, manufacturer or secure-documents**) , so that the path from where we read the keys from is correct, given that every module has the keys it needs stored inside it in a folder named `resources`.

The Protect , Unprotect and Check functionalities of the CLI application use a combination of these functions , meaning :
    1. **Protect** uses the signature function and the encrypt funtion, in this order.
    2. **Unprotect** uses only the decrypt functionalities has it does not perform any type of checks.
    3. **Check** uses the decrypt function and the checkDigest funtion, in this order.


**Challenges and Solutions**

- **Multi-Purpose Module** : 
  - Because the `secure-documents` module is supposed to be able to be , at the same time, a CLI application and a library, it was hard to deal with the requirements that both of these applications needed, in terms of what functions were needed and how they needed to be implemented , sometimes there were different versions of the same function with slight changes in what came as inputs and other some minor differences. Additionally , because the library could be imported in any of the 4 modules available , dealing with the path to the keys was a challenge at first as well.

- **Key Management** :
  - Managing and securely storing provate keys can be problematic and could leas to safety concerns if done incorrectly, the solution we came to was using Java KeyStore (JKS) and PKCS12 formats to store keys securely, the `Common` class has some methods to load keys from files.


### 2.2. Infrastructure

#### 2.2.1. Network and Machine Setup

The infrastructure consists of four main components: the application server, the client, and the manufacturer server and the database server. Each component is configured to communicate securely using TLS (Transport Layer Security).

- **Application Server and Database Server:**
  - **Port:** 8443
  - **TLS Configuration:** TLS authentication enabled. 
  - **Database:** PostgreSQL with SSL enabled for secure communication between the server and the database. The database stores encrypted configurations.
  - **Initialization:** The `DatabaseInitializer` class initializes the database with encrypted configurations. This ensures that sensitive data is protected at rest.

- **Client:**
  - **Communication:** Uses HTTPS to communicate with both the application server and the manufacturer server. This ensures that data is encrypted in transit.
  - **TLS Configuration:** Client certificates for TLS authentication. The client presents a certificate to authenticate itself to the server.
  - **Key Management:** The `HTTPHandler` class sets the appropriate key store and trust store properties for each role. This ensures that the correct certificates are used for authentication.

- **Manufacturer Server:**
  - **Port:** 8444
  - **TLS Configuration:** TLS authentication enabled. 
  - **Endpoints:** Provides an endpoints for firmware updates and configuration management. These endpoints are secured with TLS to ensure that data is encrypted in transit.

- **Database Tables** :
  - **Car Configuration** : Table with two rows.
    - **id** : Unique identifier for each row , it is the primary key and auto-generated.
    - **car_configuration** : String that stores the encrypted configuration data.
    - **maintenance_mode** : Boolean , which is a flag indicating whether we are in maintenance mode or not , it came has a necessity to implement our **Security Challenge**.
  - **Car Audit** : Stores logs of action that change the car state, also came has a necessity to implement our **Security Challenge**.
    - **id** : Unique identifier for each row , it is the primary key and auto-generated.
    - **action-log** : String of the action that took place.
    - **type_user** : String that represents the user who performed the action.
    - **digital_signature** String that represents the digital sgnature that came in the respective HTTP request.
    - **configuration** : The configuration changes as result of the action.

Besides using TLS level encryption to secure our communications , on top of using our library, we also added two firewalls so that the communication between the Client and the Application-Server and between the Application-Server and the Database is further protected.

In terms of technology used , we already has some experience with Spring Boot and PostgreSQL from previous courses so we decided to continue using them.

#### 2.2.2. Server Communication Security

Has you can expect there is communication between various different machines:

1. **Client <-> Application-Server**
2. **Client <-> Manufacturer**
3. **Application-Server <-> Database**

And all of them need protecting , and some need some extra steps to fully ensure secure communication.

Before explaining how each one of this communication protocols are done, we must first detail how we shared the keys and certificates that are used in all of them.

The appropriate keystores and truststores are stored in each one of the modules inside the resources/tls folder.
Likewise, the private and public keys needed for communication and ciphering are stored, inside each module, in the resources folder. 

**Key and Certificate Generation**

We designed and generated a self-signed certificate for an entity that we call CA, that we later use to sign every single one of the certificates for the remaining entities.

After that we generated for the application server, the manufacturer , mechanic, normal user and owner, a pair of asymmetric keys and a certificate that is signed by our CA.

Sequencially , for each one of them we created a keystore where we stored their certificate signed by their respective private keys , the .p12 files.

At last , again , for the user, owner and mechanic we created a truststore (.jks files )were we inserted the entities that each one trusts, the list of trusted entities by each one is :

1. **User** : It trusts the application-server and the manufacturer server.
2. **Owner** : It trusts the application-server and the manufacturer server.
3. **Mechanic** : It trusts the application-server and the manufacturer server.

Once generated, these keystores, key pairs and truststores , were copied to the modules that needed them to communicate.

For developing purposes we directly import the certificates from our file-system to Java , so we do not use a real Certificate Authority. In a real-life production environment, these certificates would not be stored locally, rather we would get them in the TLS handshake from a truted third party authority.
In the same manner the existance of all private keys in a system would be deprecated in a production environment has it is not safe.


**Client <-> Application-Server**

Communication between these two entities is done via HTTPS , so HTTP with SSL , using the certificates generated as explained before.

Because we use RestAPI which are by default stateless, meaning each request from the client to the server must contain all the information needed to process the request , the TLS protection does not handle user authentication or authorization so we decided to use our `secure-document` library to protect the communication further. So before sending the JSON through the HTTPS channel we call the `addSecurity` function from our library that adds the protection we mention on the previous point (**2.1**), so with the Digital Signature that we send we can , on the server-side do access-control , meaning we try to check the file for integrity with the public key of every single user that is authorized to have access to that type of command , if we cannot verify the integrity with any of the allowed user's private key, then the one trying to do the command does not have access to it.
If we can do it , it means the user is authorized to do so and we can  remove the security and dicipher the command with the `removeSecurity`function of our library.

Some end-points are of type **GET** so they dont have a body, so in those cases instead of using the normal `addSecurity` and `removeSecurity`, the library provides a `signGetRequest` , that the client uses , that creates a Digital Signature that we send as an header in the GET request. The content that is being used to create the hash is just a string representation of the type of command being used so for example:

For a read_configuration command as owner:

```sh 
signGetRequest("read","owner", moduleID);
```

In the server-side , it uses `checkGetRequest` to check the validity of the Signature and to see if the user has permissions to do such command.


**Client <-> Manufacturer**

The same ideia is applied to the communication between these two entities, because all types of users trust the manufacturers server, we use HTTPS , but it does not do authenticathion of which tyoe of user is trying to communicate with the server.
So, in a similar way, we do access control in the same way that we did in the Client <-> Application-Server , therefor ensuring that only the mechanic can get access to the endpoint.

Additionally and to guarantee that firmware only comes from the manufacturer, when an authorized user ( mechanic ) accesses the endpoint in manufacturer server, it encrypts the firmware with its private key and then creates a digital signature of that same file. So the content of the JSON sent from the manufacturer to the client besides being protected as explained earlier , it also has the firmware encrypted by the the manufacturer private key and a digital sognature of it. The function that does this is provided by the `secure-document` and it is called `protectFirmware`.
The client then takes those two fields ( the firmware and it's digital signature ) and copies them to the JSON it is going to send to the application-server.
So the application-server can detect who is sending the command of updating the firmware and can also confirm if the firmware was in fact sent by the manufacturer and not other entity.


**Application-Server <-> Database**

Here we do not use HTTPS, because Spring-Boot has a mechanism to encrypt the interaction with a PostgreSQL database, so we decided to use that instead. It uses SSL 1.3 to encrypt the whole connection,furthermore, adding on to it , before sending the data to the database, we cipher sensitive information with a symmetric key that is only kept in the application-server, for that we use a function of our library called `encryptDB` that uses the key to encrypt the configuration JSON before sending it through to the database.
So all sensitive data has one more layer of encryption in traffic to the database, and it is stored encrypted.
To decipher it we use the `decryptDB` funtion that uses the symmetric key to decipher the content.


### 2.3. Security Challenge

#### 2.3.1. Challenge Overview

We choose **Security Challenge B** so , we had to implement the ideia of maintenance mode which could only be set by the owner of the car, in that mode we had a few requirements to fullfil:

- [SRB1: data privacy] The mechanic cannot see the user configurations, even when he has the car key.
- [SRB2: authorization] The mechanic (when authenticated) can change any parameter of the car, for testing purposes. 
- [SRB3: data authenticity] The user can verify that the mechanic performed all the tests to the car. 

This would mean that we had to implement a new state, the maintenance state, where , if in that mode a mechanic could :

1. Change the configurations of the car , however those configurations changes would not be permenent because when maintenance mode is turned off , the car would go back to the configurations of the owner. So we decided that we need a second row in the Car configuration table to represent the ephemoral configuration file that the mechanic would change while in this mode.
   
2. Read Car Configurations of that ephemoral state without the need of using the owner key, while at the same time not being able to read the owner configurations even while using the owner's key.

When that mode is turned off, and back on again the state of that configuration file should be the default again, this would mean that we had to reset that file every time the mode is switched off.


#### 2.3.2. Attacker Model

In this section, we define the attacker model, which outlines the potential threats, the capabilities and limitations of the attacker, and the trust levels of various entities involved in the system. This helps in understanding the security measures needed to protect the system against potential attacks.

**Attacker Model:**

1. **Fully Trusted Entities:**
   - **Owner:** The owner of the car is fully trusted. They have full access to the car's configurations and can enable or disable maintenance mode using a password.
   - **Application Server:** The server is fully trusted to store and manage the car configurations securely. It ensures that only authorized users can access or modify the configurations.

2. **Partially Trusted Entities:**
   - **Mechanic:** The mechanic is partially trusted. They are allowed to modify the car configurations for testing purposes when maintenance mode is enabled. However, they should not have access to the owner's configurations.

3. **Untrusted Entities:**
   - **Potential Attackers:** Any external entity or unauthorized user attempting to gain access to the car configurations or modify them without proper authorization.

**Capabilities and Limitations of the Attacker:**

1. **Capabilities:**
   - **Physical Access:** The attacker may have physical access to the car and its systems, including the car key.
   - **Network Access:** The attacker may attempt to intercept or tamper with the communication between the client and the server.
   - **Credential Theft:** The attacker may try to steal the credentials of the owner or mechanic to gain unauthorized access.

2. **Limitations:**
   - **No Password Access:** The attacker does not have access to the owner's password, which is required to enable or disable maintenance mode and access the owner's configurations.
   - **No Private Key Access:** The attacker does not have access to the private keys used for digital signatures, making it difficult to forge valid requests or responses.
   - **Limited Configuration Access:** Even if the attacker gains access to the mechanic's credentials, they can only modify the temporary configurations and not the owner's configurations.

**Threat Scenarios:**

1. **Unauthorized Configuration Changes:**
   - **Threat:** An attacker attempts to change the car configurations without proper authorization.
   - **Mitigation:** The system uses digital signatures to verify the authenticity of requests. Only authorized users with valid signatures can modify the configurations.

2. **Eavesdropping and Tampering:**
   - **Threat:** An attacker intercepts and tampers with the communication between the client and the server.
   - **Mitigation:** The system uses TLS to encrypt all data transmitted between the client and the server, preventing eavesdropping and tampering.

3. **Direct Database Access:**
   - **Threat:** An attacker gains direct access to the database machine and attempts to read the car configurations.
   - **Mitigation:** The database stores encrypted configurations, ensuring that even with direct access to the database machine, the attacker cannot see the configurations.


The combination of TLS, digital signatures, and access control ensures that the system remains secure and resilient against a range of attacks.

#### 2.3.3. Solution Design and Implementation

**Solution Design:**

1. **Maintenance Mode:**

   - A new column `maintenance_mode` (boolean) is added to the `car_configurations` table to indicate whether the system is in maintenance mode.
  
   - When maintenance mode is enabled, the mechanic can modify the car configurations for testing purposes. These changes are temporary and do not affect the owner's configurations.

2. **Password Protection:**

   - A password is required to enable or disable maintenance mode. This ensures that only authorized users (the owner) can change the maintenance mode status.

   - The password is hashed using SHA-256 and stored securely and sent to the application-server when setting on and off the mode.

   - If the owner activates the maintenance mode , the server creates a new symmetric cipher from the normal symmetric key stored and used to cipher the database and from the provided hash password, this is done in the `deriveKeyFromPassword` , this new key is now used to cipher the first row ( owner configurations ) of the `car_configurations` table, and the normal symmetric key encrypts the second row ( ephemoral mechanic configurations ).

   - If the owner deactivates the maintenance mode , we decipher both rows , the first with the joint key ( symmetric key + password ) and the second with symmetric key. After that we cipher the first row with the normal symmetric key , and to the second row we take the default JSON that represent the default configurations and cipher it with the default symmetric , resetting the ephemoral configuration file.

3. **Data Privacy:**
   
   - When the system is in maintenance mode, the owner always sends the hashed password ( note: in the available commands in does not show the need of doing that because it is done in backhand of the client application to make it easier to run commands) so that he can access the right row , and therefor the right configuration file.
  
   - When the system is in maintenance mode, the mechanic can only access and modify the temporary configurations because he does not have access to the password so he does not send it, therefor, he can only decipher the second row of the table leaving the owner's configurations inaccessible to the him.
  
   - Still with the mode on, if the mechanic tries to read configurations as the owner , he cannot do it because , even though the server identifies him as the owner but the request does not have the password so the diciphering of the first row cannot happen.
  
   - The `getConfiguration` method checks the `maintenance_mode` status and returns the appropriate configuration based on the user's role and the mode.

4. **Logging activity:**

   - The `car_audit` table logs all actions performed on the car configurations, including the digital signature of the action. This allows the owner to verify that the mechanic performed all the tests.

**Implementation Details:**

Beside the collumn in the `car_configurations` , we added methods to deal with password both server-side and client side, added a new end-point to the application-server so that the client could now try and set the maintenance mode on and off.
Additionally , also added a new functionality that led to a new end-point for the user to be able to view the audit logs that store events of configuration chnages of the car.

This way we have 5 different endpoints , 2 of them are GET requests and the other 3 are POST, for the POST requests, the client sends the application-server the following JSON:

Client -> Application : https://ip:port/car/api/command 

```json 
{
    "content": "This a string that represents the content encripted",
   "metadata" : {
        "signature" : "This is a String representation the DS of the content section signed with the private key of the sender",
        "iv" : " This is a String representation of the IV bytes ciphered with the applications public key",
        "key" : " This is a String representation of the symmetric key used to cipher the content, ciphered with the applications public key"
   }
}
```

The application server to every type of request responds with the following JSON: 

```json 
{
    "content": "This a string that represents the content encripted",
    "metadata" : {
        "signature" : "This is a String representation the DS of the content section signed with the private key of the application-server",
        "iv" : " This is a String representation of the IV bytes ciphered with the sender's public key",
        "key" : " This is a String representation of the symmetric key used to cipher the content, ciphered with the sender's public key"
   }
}
```
The `content` section after deciphered contains:

```json 
{
    "content" :{
        "sucess" : "true/false",
        "data" : "Response to the command",
    }
}
```
The **success** field indicates wheter or not the command had success.

## 3. Conclusion

Throughout this project, we gained valuable experience in:

- **System Design and Implementation:** Building a secure and efficient communication system from the ground up.
- **Security Practices:** Applying firewalls, encryption, and other security measures to protect data and communication channels.
- **Collaboration:** Coordinating between multiple VMs and ensuring seamless integration and communication.


### Future Work

While the MotorIST project has achieved its primary goals, there are opportunities for further enhancements:

1. **Mutual TLS for Development Environment:** Implementing mutual TLS for the development environment on the VMs. We had mutual TLS working in the localhost environment when all of our code was running locally. We tried to implement it i the VMs but encounter many errors we did not know how to fix, we have actually the truststores and keystores configured for that , we just do not use it in the properties files of the application-server and manufacturer.
2. **Firmware Verification:** Finding a better way for the application server to verify that the firmware comes from the manufacturer. Currently, the firmware file must be very small because it is encrypted with a private key.


----
END OF REPORT
