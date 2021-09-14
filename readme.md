# CWA Testcenter registration 
- Registration website
  - input necessary data to register a new ~~testcenter~~ as of 21.07. only partner registrations are required 
  - file upload (no executables) 
    - as of 28.07. virus scan on document upload?
- Exportservice
  - ~~as of 16.07. export has to be secured with Basic Auth~~
  - as of 28.07. keycloak authentication is mandatory
  - as of 22.07. created as ISO formatted timestamp
  - add UTF-8 with BOM to exported csv
  - download all uploaded files via http as one zip file
- Database access via HashiCorp Vault (provided)

## Requirements/Validation
- unique email address
- Required fields:
  - E-Mail für Kontakt
  - Name Teststelle/Betreiber 
  - Adresse: Straße, PLZ, Ort
  - Häkchen für RAT-Nachweis 
  - Software vorhanden (ja  Auswahl verfügbare Partner; nein  Portal)
- Optional fields
  - Ansprechpartner;Telefonnummer;
  - Teststellen-ID,
  - Häkchen für Arzt oder Apotheker; 
  - Betriebene Testcentren; 
  - Anzahl Tests pro Tag
- allowed documents types: pdf, jpg
  
# CWA Export function
- CSV Export per http(s)
- Basic Authentication for Export 

## Systemcontext

```plantuml
@startuml SystemContext
title System context

component ServiceNow #gray
actor partner #gray
actor "be-user" as user #gray

cloud otc {   
   rectangle context {
      [cwa-registration] as cwa
      () "testcenter" as reg
      () "export" as export
      () "documents" as docs          
   }
   database postgreSQL as db #lightGray
   component Vault #lightGray
   component KeyCloak as keycloak #lightGray
}

partner -> reg
reg - cwa

cwa -down-> db
cwa -down-> keycloak

cwa - export
cwa - docs
ServiceNow -[hidden]down-> user
export <- ServiceNow
docs <- user

[ServiceNow] -[hidden]down-> Vault
cwa -down-> Vault

@enduml
```
![](SystemContext.svg)

## Development
- run local vault instance
```shell
  set VAULT_ADDR=http://localhost:8200
  vault partner init
  vault secrets enable -path=secret/ kv
  vault kv put secret/cwa-registration \ 
    spring.datasource.username=testcenter \
    spring.datasource.password='#Telekom01' \
    spring.datasource.url=jdbc:postgresql://localhost:5432/testcenter
```
  
- run local postgresql instance:
<code>docker run -it --rm --network some-network postgres psql -h some-postgres -U postgres</code
 ```sql
  CREATE USER testcenter WITH PASSWORD '#Telekom01';
  CREATE DATABASE testcenter;
  GRANT ALL PRIVILEGES ON DATABASE testcenter TO testcenter;
 ```

- run local keycloak instance
```docker run -p 8080:8080 jboss/keycloak```
```docker exec <CONTAINER> /opt/jboss/keycloak/bin/add-user-keycloak.sh -u <USERNAME> -p <PASSWORD>```
```docker restart <CONTAINER>```

- create new realm `cwa-registration`
- create client `cwa-registration`
- create client roles `CWA_CSV_EXPORT_USER` and `CWA_ATTACHMENT_EXPORT_USER`
- create users and assigne roles

### Database
```plantuml source="src/main/doc/database.puml" 
```

### Configuration
|Key| Description|
---|---
|PostreSQL Datasource|
|```spring.datasource.url```| JDBC connection string |
|```spring.datasource.username```| database user |
|```spring.datasource.password```| database password |
|Registration Application|
|```config.downloadUrl```| URL for downloading attached documents.|
|```config.role.attachment_export```| "CWA_ATTACHMENT_EXPORT_USER"|
|```config.role.partner_export```| "CWA_CSV_EXPORT_USER"|
|```config.solution-file-location```| Path to file containing registered test software solutions |
|```config.role.partner_export```| Keycloak role for partner csv export |
|```config.role.attachment_export``` | Keycloak role for document download |
|KeyCloak|
|```keycloak.realm``` | cwa-registration |
|```keycloak.credentials.secret```| Client secret|
|```keycloak.auth-server-url``` | http://localhost:8180/auth|
|```keycloak.resource```| cwa-registration
|```keycloak.ssl-required```| external|
|```keycloak.use-resource-role-mappings```|true|

## Usage
1. Registration: https://<server>:<port>/partner/new 
2. Export: https://<server>:<port>/partner/export[?since=yyyy-MM-ddThh:mm]
```curl -i -u admin:top_secret http://localhost:8080/partner/export```
3. Document bundle download: https://<server>:<port>/partner/export/attachment?id=[UUID]
  ```curl -i -u admin:top_secret http://localhost:8080/partner/export/attachment```
  
### Todos
- Deliverable: Dockerimage -> @dire
- Deployment -> @dire
- Split deployment artefacts for registration and download
- virus detection during document upload (?)
-- ClamAV licence required (proxy?)
-- McAfee appliance 