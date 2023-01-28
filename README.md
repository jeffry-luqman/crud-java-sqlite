# **Aplikasi CRUD Menu Restoran Sederhana**
Ini adalah contoh aplikasi Create, Read, Update dan Delete (CRUD) menu restoran sederhana menggunakan Java dan SQLite.

## Memulai
1. Pastikan komputer kamu sudah terinstall [Git](https://git-scm.com/) dan [Java Development Kit](http://jdk.java.net/).
2. Kloning repositori ini ke komputer kamu dan masuk ke folder crud-java-sqlite dari terminal atau cmd
	```bash
	git clone https://github.com/jeffry-luqman/crud-java-sqlite.git && cd crud-java-sqlite
	```
3. Lakukan kompilasi aplikasi
	```bash
	javac CRUD.java
	```
4. Jalankan aplikasi
  * Windows
	```bash
	java -cp .;sqlite-jdbc-3.40.0.0.jar CRUD
	```
  * Linux atau Mac
	```bash
	java -cp .:sqlite-jdbc-3.40.0.0.jar CRUD
	```

## Catatan
Aplikasi ini menggunakan [Driver SQLite JDBC](https://github.com/xerial/sqlite-jdbc), seharusnya driver tersebut diunduh langsung dari sana, namun untuk memudahkan proses belajar terutama bagi pemula driver tersebut juga disertakan pada repositori ini.