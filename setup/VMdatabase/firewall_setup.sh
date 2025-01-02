#!/bin/bash

sudo apt install iptables-persistent

echo "Setting Firewall rules..."

# Flushes rules
sudo iptables -F

# Sets default rules
sudo iptables -P INPUT DROP
sudo iptables -P OUTPUT DROP
sudo iptables -P FORWARD DROP

# Sets rules for incoming and outgoing packets
sudo iptables -A INPUT -i eth0 -p tcp --dport 5432 -j ACCEPT
sudo iptables -A OUTPUT -o eth0 -p tcp --sport 5432 -j ACCEPT

# Persist changes
sudo sh -c 'iptables-save > /etc/iptables/rules.v4'
sudo systemctl enable netfilter-persistent.service