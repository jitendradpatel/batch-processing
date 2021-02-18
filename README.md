# batch-processing
## Watch https://youtu.be/J6IPlfm7N6w
## Setup Instruction
#### 1. Create Example Database - `CREATE DATABASE example` 
#### 2. Generate data (1 million) - `pgbench -i -s 10 example`
#### 3. Create empty table (pgbench_accounts2) - `create table pgbench_accounts2 as select * from pgbench_accounts where 1=0;`
#### 4. Update batch-processing/src/resources/application.yml to reflect Postgres db username/password
#### 5. CD to batch-processing
#### 6. Run the app - `./mvnw spring-boot:run`

