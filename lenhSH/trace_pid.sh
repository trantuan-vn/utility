#!/bin/bash

if [ -z "$1" ]; then
  echo "Usage: $0 <PID>"
  exit 1
fi

pid=$1

echo "Tracing process tree for PID: $pid"
echo

while [ "$pid" != "1" ]; do
  # In thông tin tiến trình hiện tại
  ps -p "$pid" -o pid,ppid,user,command,lstart

  echo "  Child processes of PID $pid:"
  # Liệt kê con trực tiếp của pid hiện tại
  children=$(pgrep -P "$pid")

  if [ -z "$children" ]; then
    echo "    (none)"
  else
    for cpid in $children; do
      ps -p "$cpid" -o pid,ppid,user,command,lstart | sed 's/^/    /'
    done
  fi

  echo "-----------------------------------"

  # Lấy ppid của pid hiện tại để lặp lên cha
  ppid=$(ps -o ppid= -p "$pid" | tr -d ' ')

  if [ -z "$ppid" ]; then
    echo "No parent process found, stopping."
    break
  fi

  pid=$ppid
done

# In tiến trình root (PPID = 1)
if [ "$pid" == "1" ]; then
  echo "Reached root process (PID 1):"
  ps -p 1 -o pid,ppid,user,command,lstart
fi
