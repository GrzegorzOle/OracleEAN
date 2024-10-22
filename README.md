# Oracle with EAN
Barcodes w ORACLE z użyciem JAVA

Podstawą utworzenia kodu EAN13 (źródłem zgodności) był system UPC, który jest stosowany w Kanadzie i USA.
Sam kod składa się wyłącznie z liczb, zawierając cyfrę kontrolną, która pozwala sprawdzić prawidłowość odczytu.
Skupimy się na najważniejszych aspektach tworzenia grafiki kodu tak, by mógł być odczytany za pomocą czytników.
Kod kreskowy składa się z 95 białych i czarnych pasków znajdujących się jeden przy drugim bez przerwy
(aby zaprezentować właściwy sposób, pasek czarny przedstawiany jest jako 1, a pasek biały jako 0).
Kod kreskowy składa  się z sekwencji:
3 bitowej reprezentacji startu w postaci 101
liczb 2-7 kodowanych lewostronnie reprezentowanych przez 7 bitowe sekwencje.
wewnętrznych pasków rozdzielających sekwencję w postaci 01010
liczb 8-13 kodowanych prawostronnie reprezentowanych przez 7 bitowe sekwencje
3 bitowej sekwencji końca kodu w postaci 101.

|-------|-----------------------|--------------|
| Cyfra |Kodowanie lewostronne  |Kodowanie     |
|-------|------------|----------|              |
|       |nieparzyste |parzyste  |prawostronne  |
|-------|------------|----------|--------------|
| 0     |0001101     | 0100111  | 1110010      |
| 1     |0011001     | 0110011  | 1100110      |
| 2     |0010011     | 0011011  | 1101100      |
| 3     |0111101     | 0100001  | 1000010      |
| 4     |0100011     | 0011101  | 1011100      |
| 5     |0110001     | 0111001  | 1001110      |
| 6     |0101111     | 0000101  | 1010000      |
| 7     |0111011     | 0010001  | 1000100      |
| 8     |0110111     | 0001001  | 1001000      |
| 9     |0001011     | 0010111  | 1110100      |

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