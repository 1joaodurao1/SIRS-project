#!/bin/bash

config_file=manufacturer_configs

# Set up the network

echo "Configuring network for manufacturer"

sudo cp $config_file /etc/network/interfaces

sudo systemctl restart NetworkManager