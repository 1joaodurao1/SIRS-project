#!/bin/bash

# Variables
PASSWORD="changeme"

#---------------------------Initial setup------------------------------------

mkdir certificates
cd certificates


# ---------------------------------- CA--------------------------------------

# Generate private key
openssl genrsa -out ca.key

# Generate CSR
openssl req -new -key ca.key -out ca.csr -subj "/C=PT/ST=Lisbon/L=Lisbon/O=MotorIST/OU=ca/CN=ca"

# Generate signed certificate
openssl x509 -req -days 365 -in ca.csr -signkey ca.key -out ca.crt

echo 01 > ca.srl

echo "Certificates and keys for CA have been successfully generated!"


# ---------------------------------- DB--------------------------------------

# Generate private key
openssl genrsa -out db.key

# Create a configuration file for SAN
SAN_CONFIG=db_san.cnf
cat > $SAN_CONFIG <<EOL
[req]
distinguished_name = req_distinguished_name
req_extensions = v3_req
[req_distinguished_name]
[v3_req]
subjectAltName = @alt_names
[alt_names]
IP.1 = 192.168.2.254
EOL

# Generate CSR
openssl req -new -key db.key -out db.csr -subj "/C=PT/ST=Lisbon/L=Lisbon/O=MotorIST/OU=db/CN=db" -config $SAN_CONFIG

# Generate signed certificate
openssl x509 -req -days 365 -in db.csr -CA ca.crt -CAkey ca.key -out db.crt -extfile $SAN_CONFIG

# Cleanup temporary SAN config
rm $SAN_CONFIG

echo "Certificates and keys for user have been successfully generated!"


# ---------------------------------- USER--------------------------------------

# Generate private key
openssl genrsa -out user.key

# Transform private key
openssl pkcs8 -topk8 -inform PEM -outform PEM -in user.key -out newuser.key -nocrypt

# Generate public key
openssl rsa -in user.key -pubout -out user.pubkey

# Create a configuration file for SAN
SAN_CONFIG=user_san.cnf
cat > $SAN_CONFIG <<EOL
[req]
distinguished_name = req_distinguished_name
req_extensions = v3_req
[req_distinguished_name]
[v3_req]
subjectAltName = @alt_names
[alt_names]
IP.1 = 192.168.0.1
IP.2 = 192.168.1.1
EOL

# Generate CSR
openssl req -new -key user.key -out user.csr -subj "/C=PT/ST=Lisbon/L=Lisbon/O=MotorIST/OU=user/CN=user" -config $SAN_CONFIG

# Generate signed certificate
openssl x509 -req -days 365 -in user.csr -CA ca.crt -CAkey ca.key -out user.crt -extfile $SAN_CONFIG

# Cleanup temporary SAN config
rm $SAN_CONFIG

# Convert the certificate to PEM format
openssl x509 -in user.crt -out user.pem

# Create PKCS12 file
openssl pkcs12 -export -in user.crt -inkey user.key -out user.p12 -passout pass:$PASSWORD

echo "Certificates and keys for user have been successfully generated!"

# ---------------------------------- Owner--------------------------------------

# Generate private key
openssl genrsa -out owner.key

# Transform private key
openssl pkcs8 -topk8 -inform PEM -outform PEM -in owner.key -out newowner.key -nocrypt

# Generate public key
openssl rsa -in owner.key -pubout -out owner.pubkey

# Create a configuration file for SAN
SAN_CONFIG=owner_san.cnf
cat > $SAN_CONFIG <<EOL
[req]
distinguished_name = req_distinguished_name
req_extensions = v3_req
[req_distinguished_name]
[v3_req]
subjectAltName = @alt_names
[alt_names]
IP.1 = 192.168.0.1
IP.2 = 192.168.1.1
EOL

# Generate CSR
openssl req -new -key owner.key -out owner.csr -subj "/C=PT/ST=Lisbon/L=Lisbon/O=MotorIST/OU=owner/CN=owner" -config $SAN_CONFIG

# Generate signed certificate
openssl x509 -req -days 365 -in owner.csr -CA ca.crt -CAkey ca.key -out owner.crt -extfile $SAN_CONFIG

# Cleanup temporary SAN config
rm $SAN_CONFIG

# Convert the certificate to PEM format
openssl x509 -in owner.crt -out owner.pem

# Create PKCS12 file
openssl pkcs12 -export -in owner.crt -inkey owner.key -out owner.p12 -passout pass:$PASSWORD

echo "Certificates and keys for owner have been successfully generated!"

# ---------------------------------- Mechanic--------------------------------------

# Generate private key
openssl genrsa -out mechanic.key

# Transform private key
openssl pkcs8 -topk8 -inform PEM -outform PEM -in mechanic.key -out newmechanic.key -nocrypt

# Generate public key
openssl rsa -in mechanic.key -pubout -out mechanic.pubkey

# Create a configuration file for SAN
SAN_CONFIG=mechanic_san.cnf
cat > $SAN_CONFIG <<EOL
[req]
distinguished_name = req_distinguished_name
req_extensions = v3_req
[req_distinguished_name]
[v3_req]
subjectAltName = @alt_names
[alt_names]
IP.1 = 192.168.0.1
IP.2 = 192.168.1.1
EOL

# Generate CSR
openssl req -new -key mechanic.key -out mechanic.csr -subj "/C=PT/ST=Lisbon/L=Lisbon/O=MotorIST/OU=mechanic/CN=mechanic" -config $SAN_CONFIG

# Generate signed certificate
openssl x509 -req -days 365 -in mechanic.csr -CA ca.crt -CAkey ca.key -out mechanic.crt -extfile $SAN_CONFIG

# Cleanup temporary SAN config
rm $SAN_CONFIG

# Convert the certificate to PEM format
openssl x509 -in mechanic.crt -out mechanic.pem

# Create PKCS12 file
openssl pkcs12 -export -in mechanic.crt -inkey mechanic.key -out mechanic.p12 -passout pass:$PASSWORD

echo "Certificates and keys for mechanic have been successfully generated!"

# ---------------------------------- Manufacturer--------------------------------------

# Generate private key
openssl genrsa -out manufacturer.key

# Transform private key
openssl pkcs8 -topk8 -inform PEM -outform PEM -in manufacturer.key -out newmanufacturer.key -nocrypt

# Generate public key
openssl rsa -in manufacturer.key -pubout -out manufacturer.pubkey

# Create a configuration file for SAN
SAN_CONFIG=manufacturer_san.cnf
cat > $SAN_CONFIG <<EOL
[req]
distinguished_name = req_distinguished_name
req_extensions = v3_req
[req_distinguished_name]
[v3_req]
subjectAltName = @alt_names
[alt_names]
IP.1 = 192.168.1.254
EOL

# Generate CSR
openssl req -new -key manufacturer.key -out manufacturer.csr -subj "/C=PT/ST=Lisbon/L=Lisbon/O=MotorIST/OU=manufacturer/CN=manufacturer" -config $SAN_CONFIG

# Generate signed certificate
openssl x509 -req -days 365 -in manufacturer.csr -CA ca.crt -CAkey ca.key -out manufacturer.crt -extfile $SAN_CONFIG

# Cleanup temporary SAN config
rm $SAN_CONFIG

# Convert the certificate to PEM format
openssl x509 -in manufacturer.crt -out manufacturer.pem

# Create PKCS12 file
openssl pkcs12 -export -in manufacturer.crt -inkey manufacturer.key -out manufacturer.p12 -passout pass:$PASSWORD

echo "Certificates and keys for manufacturer have been successfully generated!"

# ---------------------------------- Server--------------------------------------

# Generate private key
openssl genrsa -out server.key

# Transform private key
openssl pkcs8 -topk8 -inform PEM -outform PEM -in server.key -out newserver.key -nocrypt

# Generate public key
openssl rsa -in server.key -pubout -out server.pubkey

# Create a configuration file for SAN
SAN_CONFIG=manufacturer_san.cnf
cat > $SAN_CONFIG <<EOL
[req]
distinguished_name = req_distinguished_name
req_extensions = v3_req
[req_distinguished_name]
[v3_req]
subjectAltName = @alt_names
[alt_names]
IP.1 = 192.168.0.254
EOL

# Generate CSR
openssl req -new -key server.key -out server.csr -subj "/C=PT/ST=Lisbon/L=Lisbon/O=MotorIST/OU=server/CN=server" -config $SAN_CONFIG

# Generate signed certificate
openssl x509 -req -days 365 -in server.csr -CA ca.crt -CAkey ca.key -out server.crt -extensions v3_req -extfile $SAN_CONFIG

# Cleanup temporary SAN config
rm $SAN_CONFIG

# Convert the certificate to PEM format
openssl x509 -in server.crt -out server.pem

# Create PKCS12 file
openssl pkcs12 -export -in server.crt -inkey server.key -out server.p12 -passout pass:"changeme"

echo "Certificates and keys for server have been successfully generated!"

#----------------------------------TRUSTSTORES-------------------------------------------

keytool -import -trustcacerts -file server.pem -keypass changeme -storepass changeme -alias server -keystore usertruststore.jks -noprompt
keytool -import -trustcacerts -file manufacturer.pem -keypass changeme -storepass changeme -alias manufacturer -keystore usertruststore.jks -noprompt
keytool -import -trustcacerts -file server.pem -keypass changeme -storepass changeme -alias server -keystore ownertruststore.jks -noprompt
keytool -import -trustcacerts -file manufacturer.pem -keypass changeme -storepass changeme -alias manufacturer -keystore ownertruststore.jks -noprompt
keytool -import -trustcacerts -file server.pem -keypass changeme -storepass changeme -alias server -keystore mechanictruststore.jks -noprompt
keytool -import -trustcacerts -file manufacturer.pem -keypass changeme -storepass changeme -alias manufacturer -keystore mechanictruststore.jks -noprompt
keytool -import -trustcacerts -file mechanic.pem -keypass changeme -storepass changeme -alias mechanic -keystore manufacturertruststore.jks -noprompt
keytool -import -trustcacerts -file user.pem -keypass changeme -storepass changeme -alias user -keystore servertruststore.jks -noprompt
keytool -import -trustcacerts -file owner.pem -keypass changeme -storepass changeme -alias owner -keystore servertruststore.jks -noprompt
keytool -import -trustcacerts -file mechanic.pem -keypass changeme -storepass changeme -alias mechanic -keystore servertruststore.jks -noprompt


#--------------------------------MOVING FILES----------------------------------------------

# Define base directories
APP_SERVER_PRIVATE="../../application-server/src/main/resources/private"
APP_SERVER_PUBLIC="../../application-server/src/main/resources/public"
APP_SERVER_TLS="../../application-server/src/main/resources/tls"

CLIENT_PRIVATE="../../client/src/main/resources/private"
CLIENT_PUBLIC="../../client/src/main/resources/public"
CLIENT_TLS="../../client/src/main/resources/tls"

MANUFACTURER_PRIVATE="../../manufacturer/src/main/resources/private"
MANUFACTURER_PUBLIC="../../manufacturer/src/main/resources/public"
MANUFACTURER_TLS="../../manufacturer/src/main/resources/tls"

CONFIG="../../config"

# Delete existing files in target directories
echo "Cleaning up target directories..."
rm -f $APP_SERVER_PRIVATE/*
rm -f $APP_SERVER_PUBLIC/*
rm -f $APP_SERVER_TLS/*
rm -f $CLIENT_PRIVATE/*
rm -f $CLIENT_PUBLIC/*
rm -f $CLIENT_TLS/*
rm -f $MANUFACTURER_PRIVATE/*
rm -f $MANUFACTURER_PUBLIC/*
rm -f $MANUFACTURER_TLS/*
rm -f $CONFIG/*

# Copy files to Application Server directories
echo "Copying files to Application Server directories..."
cp newserver.key $APP_SERVER_PRIVATE
mv $APP_SERVER_PRIVATE/newserver.key $APP_SERVER_PRIVATE/server.key
cp manufacturer.pubkey mechanic.pubkey owner.pubkey server.pubkey user.pubkey $APP_SERVER_PUBLIC
cp server.p12 servertruststore.jks $APP_SERVER_TLS

# Copy files to Client directories
echo "Copying files to Client directories..."
cp newmanufacturer.key newmechanic.key newowner.key newuser.key $CLIENT_PRIVATE
mv $CLIENT_PRIVATE/newuser.key $CLIENT_PRIVATE/user.key
mv $CLIENT_PRIVATE/newmanufacturer.key $CLIENT_PRIVATE/manufacturer.key
mv $CLIENT_PRIVATE/newmechanic.key $CLIENT_PRIVATE/mechanic.key
mv $CLIENT_PRIVATE/newowner.key $CLIENT_PRIVATE/owner.key
cp manufacturer.pubkey mechanic.pubkey owner.pubkey server.pubkey user.pubkey $CLIENT_PUBLIC
cp mechanic.p12 mechanictruststore.jks owner.p12 ownertruststore.jks user.p12 usertruststore.jks $CLIENT_TLS

# Copy files to Manufacturer directories
echo "Copying files to Manufacturer directories..."
cp newmanufacturer.key $MANUFACTURER_PRIVATE
mv $MANUFACTURER_PRIVATE/newmanufacturer.key $MANUFACTURER_PRIVATE/manufacturer.key
cp manufacturer.pubkey mechanic.pubkey owner.pubkey user.pubkey $MANUFACTURER_PUBLIC
cp manufacturer.p12 manufacturertruststore.jks $MANUFACTURER_TLS

# Copy files to config
cp ca.crt db.key db.crt $CONFIG

cd ..
rm -r certificates

echo "File operations completed successfully!"
