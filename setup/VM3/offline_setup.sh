#!/bin/bash

config_file = VM3_configs

# Set up the network

sudo cp $config_file /etc/network/interfaces

sudo systemctl restart NetworkManager