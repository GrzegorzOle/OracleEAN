# Oracle with EAN
Barcody EAN13 w ORACLE z użyciem JAVA

Podstawą utworzenia kodu EAN13 (źródłem zgodności) był system UPC, który jest stosowany w Kanadzie i USA.
Sam kod składa się wyłącznie z liczb, zawierając cyfrę kontrolną, która pozwala sprawdzić prawidłowość odczytu.
Skupimy się na najważniejszych aspektach tworzenia grafiki kodu tak, by mógł być odczytany za pomocą czytników.
Kod kreskowy składa się z 95 białych i czarnych pasków znajdujących się jeden przy drugim bez przerwy
(aby zaprezentować właściwy sposób, pasek czarny przedstawiany jest jako 1, a pasek biały jako 0).
Kod kreskowy składa  się z sekwencji:
* 3 bitowej reprezentacji startu w postaci 101
* liczb 2-7 kodowanych lewostronnie reprezentowanych przez 7 bitowe sekwencje.
wewnętrznych pasków rozdzielających sekwencję w postaci 01010
* liczb 8-13 kodowanych prawostronnie reprezentowanych przez 7 bitowe sekwencje
* 3 bitowej sekwencji końca kodu w postaci 101.

Kodowanie pierwszych siedmiu znaków w systemie

| Cyfra | Kodowanie lewostronne nieparzyste | Kodowanie lewostronne parzyste | Kodowanie prawostronne |
|-------|-----------------------|-----------------------|------------------------|
| 0     |0001101                | 0100111               | 1110010                |
| 1     |0011001                | 0110011               | 1100110                |
| 2     |0010011                | 0011011               | 1101100                |
| 3     |0111101                | 0100001               | 1000010                |
| 4     |0100011                | 0011101               | 1011100                |
| 5     |0110001                | 0111001               | 1001110                |
| 6     |0101111                | 0000101               | 1010000                |
| 7     |0111011                | 0010001               | 1000100                |
| 8     |0110111                | 0001001               | 1001000                |
| 9     |0001011                | 0010111               | 1110100                |

Kolejne liczby kod EAN 13

| Wartość Pierwszej Cyfry | 2 | 3 | 4 | 5 | 6 | 7 |
|--------------|---|---|---|---|---|---|
| 0            | N | N | N | N | N | N |
| 1            | N | N | P | N | P | N |
| 2            | N | N | P | N | P | P |
| 3            | N | N | P | P | P | N |
| 4            | N | P | N | N | P | P |
| 5            | N | P | P | N | N | P |
| 6            | N | P | P | P | N | N |
| 7            | N | P | N | P | N | P |
| 8            | N | P | N | P | P | N |
| 9            | N | P | P | N | P | N |

Klasa tworzy plik graficzny dla wprowadzonego w linii parametrów kodu numerycznego oraz ścieżki podanej jako parametr klasy.
Zaproponowałem typ PNG, który z racji swojego rozmiaru będzie szybko transmitowany i otwierany, choć nie każda aplikacja klienta ma obsługę tego rodzaju grafiki.

Następnym etapem będzie umieszczenie naszej klasy wewnątrz bazy danych.
Na serwerze bazy danych tworzymy katalog, z którego będziemy wczytywać źródło, oraz katalog przeznaczony na pliki z grafikami kodów, np.:

```
    c:\java
    c:\java\exp 
```
Katalogi i ścieżki należy traktować jako przykładowe głównie w celu zaprezentowania poszczególnych etapów czynności, jest to element bardzo dowolny i generalnie zależy od własności i planów działania administratora.
Aby rozwiązanie było wydajne, katalog z bazą kodów najlepiej zlokalizować na tym samym serwerze co baza danych.
W naszym przykładzie wykorzystuję środowisko Oracle 11g (choć funkcjonalność taką można uzyskać na poprzednich wersjach systemu).
Celem głównym klasy jest tworzenie pliku kodu kreskowego w przypadku wystąpienia wartości, a wyzwalaczem w naszym przypadku ma być funkcja SQL w bazie danych ORACLE, więc należy przygotować środowisko bazy do dalszych działań.
W naszej przestrzeni tabe tworzymy tabele niezbędne do wykonania dalszych czynności.

```sql
    CREATE TABLE "TEST_TABLICY" 
     (  
        "KOD" VARCHAR2(20 BYTE), 
        "IMG" BFILE
     );
```
W polu KOD będziemy wprowadzać wartość numeryczną kodu EAN, natomiast w polu IMG automatycznie funkcje utworzą właściwy plik z kodem oraz udo-
stępnią go aplikacjom klienta. Typ BFILE w polu stanowi wskaźnik do zewnętrznego pliku binarnego.
Pole zawiera więc „alias” katalogu i nazwę pliku w katalogu.
Dane tego rodzaju pobierane są automatycznie podczas wywoływania zawartości pola i wysyłane do aplikacji klienta na „żądanie”.
Dla właściwego wykorzystania tego mechanizmu należy zdefiniować alias wskazujący katalog przechowywania plików (w naszym przypadku będzie to c:\java\exp).
```
    CREATE DIRECTORY EAN13 AS 'C:\java\exp\';
```
(podczas wywoływania należy zwrócić uwagę szczególną na prawidłowość wprowadzania nazwy katalogu).
Aby baza danych mogła obsługiwać klasy Jav-y, potrzebuje mieć zainstalowane i skonfigurowane środowisko JAVA SDK.
Sprawdzenie możemy w katalogu źródła (w katalogu, gdzie znajduje się plik źródła) wywołać polecenie:

```
C:\java\javac
```
Wynikiem polecenia powinno być wyświetlenie informacji pomocy dla ustawień kompilacji klasy.
Jeżeli nie mamy odpowiedzi, a środowisko SDK jest zainstalowane, dodajemy właściwe wpisy do ścieżki PATH systemu.
Prawidłowe ustawienie środowiska „globalnie dla serwera” jest o tyle ważne, iż baza danych w każdym przy padku będzie „oczekiwać” wsparcia systemu w tym zakresie.
Za załadowanie klasy do bazy danych odpowiada narzędzie Oracle „loadjava” (gdzie w naszym przykładzie
użytkownikiem jest np. Grzegorz, a hasłem password baza danych lokalna na porcie 1521, a SID bazy danych orcl):
```
C:\java>loadjava -u Grzegorz/password@localhost:1521:orcl -v ean13.java
```
Wewnątrz bazy danych powinien pojawić się obiekt JAVA.
Nasza klasa zawiera polecenia i funkcje realizujące
dostęp do plików, katalogów, należy zwłaszcza w środowisku Linux sprawdzić prawa dostępu do katalogu składowanych grafik do zapisu i kasowania dla użytkownika Oracle.
Poza uprawnieniami systemowymi ważne jest również uzyskanie właściwych uprawnień do wykorzystania nowej funkcjonalności wewnątrz bazy danych.
Jednak z uprawnieniami to nie wszystko, trzeba jeszcze nadać dla „schematu” użytkownika prawa dla uruchamiania klas JAVY z dostępem do zasobów plików
serwera.
Za pomocą SQLPlus z uprawnieniami SYSTEM ustawiamy uprawnienia.
```sql
EXEC DBMS_JAVA.grant_permission('GRZEGORZ', 'java.io.FilePermission', '<<ALL FILES>>', 'read ,write, execute, delete');
EXEC DBMS_JAVA.grant_permission('GRZEGORZ', 'SYS:java.lang.RuntimePermission', 'writeFileDescriptor', '');
EXEC DBMS_JAVA.grant_permission('GRZEGORZ', 'SYS:java.lang.RuntimePermission', 'readFileDescriptor', '');
GRANT JAVAUSERPRIV TO GRZEGORZ;
```
Po tych poleceniach w „schemacie” można uruchamiać „programy” JAVA zapisujące dane na dysk (oczywiście w zakresie uprawnień i zasięgu bazy serwera bazy danych). Serwery Oracle umożliwiają two rzenie procedur, funkcji, triggerów, które będą odwoły wać się do klas Javy (W najnowszych wersjach serwerów można odwoływać się do nich bezpośrednio).

```
    create or replace PROCEDURE proc_gen_EAN13 (kodEAN VARCHAR2, sciezka VARCHAR2) AS language java `name 'ean13.generuj(java.lang.String,java.lang.String)';`
```
Teraz za pomocą składni polecenia SQL możemy sprawdzić działanie naszej klasy:
```sql
    call PROC_GEN_EAN13 ('4008110296364','C:\java\exp\');
```
Wywołanie procedury powinno zakończyć się utworzeniem pliku w przykładowym katalogu składowania:
```
C:\java\exp\ 4008110296364.png
```
Naszym celem jest automatyczne uzupełnienie pola z grafiką właściwą zawartością w sposób automatyczny.
Tworzymy trigger:

```sql
CREATE OR REPLACE TRIGGER TGR_BIU_TEST_TABLICY
BEFORE INSERT OR UPDATE
ON TEST_TABLICY
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
BEGIN
  DECLARE PLIK# VARCHAR2(255);
  BEGIN
 IF :NEW.KOD IS NOT NULL THEN
 BEGIN
 BEGIN
        PROC_GEN_EAN13(:NEW.KOD,'C:\\java\\exp\\');
      END;
      PLIK# := :NEW.KOD||'.png';
 :NEW.IMG := BFILENAME('EAN13', PLIK#);
 END;
 END IF;
  END;
END;
```

