NeoDatis
========

ODB (Object Database) ODBMS (Object Database manager system) for java

No oficial repository for NeoDatis http://neodatis.wikidot.com/ 1.9.x branch

NeoDatis ODB is a very simple Object Database that currently runs on the Java, .Net, Google Android, Groovy and Scala

To avoid Impedance mismatch overhead between Object and Relational worlds, give a try to Neodatis ODB. NeoDatis ODB is a new generation Object Database: a real native and transparent persistence layer for Java, .Net and Mono.

Object because the basic persistent unit is an object, not a table.
Native & Transparent because it directly persists objects the way they exist in the native programming language, without any conversion.
Using NeoDatis ODB as your persistence layer will let you focus on your business logic allowing storing and retrieving native objects in a single line of code. No more Relational to Object mapping is necessary, NeoDatis ODB just persists objects the way they are, no matter their complexity.

Sample code


//---------------------------------
// Create the instance be stored

Sport sport = new Sport("volley-ball");
 
// Open the database

ODB odb = ODBFactory.open("test.neodatis");
 
// Store the object

odb.store(sport);
 
// Close ODB

odb.close();

// Open the database

ODB odb = ODBFactory.open("test.neodatis");
 
// Retrieve objects using Generics

Objects<Player> players = odb.getObjects(Player.class);
 
// Close ODB
odb.close();

//---------------------------------

More samples on NeoDatis-Samples

Excelent project
