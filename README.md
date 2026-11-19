\# Rush Hour



Programozás II. – Egyetemi beadandó  

Nyíregyházi Egyetem



\## A játék bemutatása



A Rush Hour egy Java nyelven készült, konzolos, kétszemélyes logikai játék, amelyben a játékos a számítógép ellen játszik.



A játék célja a saját jármű kijuttatása a pályáról.



\## Játékszabályok



\- Az alapértelmezett pálya mérete 6 × 12.

\- A játékos céljárműve az A.

\- A számítógép céljárműve a B.

\- A játékos kezdi a játékot.

\- A járművek csak a saját irányukban mozoghatnak.

\- A járművek nem ütközhetnek egymással.

\- A számítógép véletlenszerű szabályos lépéseket végez.



\## Parancsok



| Parancs | Művelet |

|---|---|

| X 2 | Jármű mozgatása |

| X -1 | Mozgatás ellenkező irányba |

| EXIT A | Céljármű kijuttatása |

| SAVE TXT | Pálya mentése |

| SAVE XML | Játék mentése |

| LOAD XML | Játék visszatöltése |

| QUIT | Kilépés |



\## Felhasznált technológiák



\- Java 21

\- Maven

\- JUnit 5

\- Mockito

\- JaCoCo

\- Checkstyle

\- SQLite

\- Logback

\- Git



\## A program felépítése



\- model – Járművek, pálya és mozgások.

\- game – A játék működése.

\- player – A játékosok kezelése.

\- database – Eredmények és ranglista.

\- io – Fájlok mentése és betöltése.



\## Tesztelési eredmények



\- Automatikus tesztek száma: 96

\- Sikeres tesztek: 96

\- Utasításlefedettség: 94%

\- Elágazáslefedettség: 82%

\- Checkstyle szabálysértések: 0



\## Fordítás



`mvn clean install`



\## Program indítása



`java -jar target/rush-hour-1.0-SNAPSHOT-jar-with-dependencies.jar`



\## Kódminőség ellenőrzése



`mvn checkstyle:check`



\## Extra funkciók



A játék támogatja a játékállás XML-fájlba történő mentését és visszatöltését, valamint az eredmények SQLite-adatbázisban történő tárolását.

