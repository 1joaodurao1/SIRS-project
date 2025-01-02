#!/bin/bash

config_file=database_configs

# Set up the network

echo "Configuring network for database"

sudo cp $config_file /etc/network/interfaces

sudo systemctl restart NetworkManager