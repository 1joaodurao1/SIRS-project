#!/bin/bash

sudo apt install iptables-persistent

echo "Setting Firewall rules..."

# Flushes rules
sudo iptables -F

# Sets default rules
sudo iptables -P INPUT DROP
sudo iptables -P FORWARD DROP

# Allow incoming packets from client
sudo iptables -A INPUT -i eth0 -p tcp --dport 8443 -j ACCEPT

# Allow incoming responses from the database server (source port 5432)
sudo iptables -A INPUT -i eth1 -p tcp --sport 5432 -j ACCEPT


# Persist changes
sudo sh -c 'iptables-save > /etc/iptables/rules.v4'
sudo systemctl enable netfilter-persistent.service