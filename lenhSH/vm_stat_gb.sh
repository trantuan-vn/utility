#!/bin/bash
pagesize=$(pagesize)
vm_stat | while read -r line; do
    key=$(echo "$line" | awk -F: '{print $1}')
    value=$(echo "$line" | awk -F: '{print $2}' | tr -dc '0-9')
    if [[ -n "$value" ]]; then
        gb=$(echo "$value * $pagesize / 1024 / 1024 / 1024" | bc -l)
        printf "%-30s %10.2f GB\n" "$key" "$gb"
    else
        echo "$line"
    fi
done
