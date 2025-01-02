#!/bin/bash

config_file=server_configs

# Set up the network

echo "Configuring network for server"

sudo cp $config_file /etc/network/interfaces

sudo systemctl restart NetworkManager