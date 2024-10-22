# Oracle with EAN
Barcodes in ORACLE using JAVA

```
    c:\java
    c:\java\exp 
```

```
    CREATE TABLE "TEST_TABLICY" 
     (  
        "KOD" VARCHAR2(20 BYTE), 
        "IMG" BFILE
     );
```

```
    CREATE DIRECTORY EAN13 AS 'C:\java\exp\';
```

```
    create or replace PROCEDURE proc_gen_EAN13 (kodEAN VARCHAR2, sciezka VARCHAR2) AS language java `name 'ean13.generuj(java.lang.String,java.lang.String)';`
```

```
    call PROC_GEN_EAN13 ('4008110296364','C:\java\exp\');
```

```
EXEC DBMS_JAVA.grant_permission('GRZEGORZ', 'java.io.FilePermission', '<<ALL FILES>>', 'read ,write, execute, delete');
EXEC DBMS_JAVA.grant_permission('GRZEGORZ', 'SYS:java.lang.RuntimePermission', 'writeFileDescriptor', '');
EXEC DBMS_JAVA.grant_permission('GRZEGORZ', 'SYS:java.lang.RuntimePermission', 'readFileDescriptor', '');
GRANT JAVAUSERPRIV TO GRZEGORZ;
```