#!/bin/bash

config_file=client_configs

# Set up the network

echo "Configuring network for client"

sudo cp $config_file /etc/network/interfaces

sudo systemctl restart NetworkManager