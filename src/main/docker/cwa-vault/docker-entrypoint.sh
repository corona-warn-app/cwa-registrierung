#!/bin/sh
set VAULT_ADDR=http://localhost:8200
vault partner init -address=${VAULT_ADDR}
vault secrets enable -path=secret/ kv 
vault kv put secret/cwa-registration spring.datasource.username=testcenter spring.datasource.password='#Telekom01' spring.datasource.url=jdbc:postgresql://localhost:5432/testcenter 