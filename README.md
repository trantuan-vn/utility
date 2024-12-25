# utility
1. exceltodb
    delete from system_parameters.business_fields;
    delete from system_parameters.business_tables;    
    mvn clean package
    java -jar ./target/exceltodb-1.0-SNAPSHOT.jar --path /Users/cunkem/Downloads/data_v3.xlsx  --version v1 --dbUrl jdbc:postgresql://localhost:5432/smartconsultor --dbUser smartconsultor
    SELECT system_parameters.generate_table();





100. Linh tinh
SELECT CURRENT_TIMESTAMP;
SELECT CURRENT_TIME;
SELECT CURRENT_TIMESTAMP AT TIME ZONE 'UTC';
select EXTRACT(DOY FROM (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'))
delete from system_parameters.business_fields;
delete from system_parameters.business_tables;    
SHOW timezone;
SELECT current_setting('timezone');
select * from system_parameters.business_fields;
select * from system_parameters.business_tables;    
SELECT system_parameters.generate_table();
SELECT * FROM system_parameters.temp_execution_log ORDER BY executed_at DESC;