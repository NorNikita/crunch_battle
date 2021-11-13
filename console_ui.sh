#!/usr/bin/env bash

GREEN="\e[32m"
RED="\e[31m"

ENDCOLOR="\e[0m"

cat /dev/null > ./src/main/resources/bid_ask.txt

while true
 do
  clear
  COUNT=$((COUNT+1))
  echo -e "${RED}Ask  price  amount${ENDCOLOR} ${GREEN} Bid  price  amount ${ENDCOLOR}"
  tail -n10 ./src/main/resources/bid_ask.txt
  sleep 1
 done