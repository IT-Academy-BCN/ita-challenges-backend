# Com crear un JRE personalitzat des de Windows

Aquest document descriu els passos necessaris des de Windows per crear un JRE personalitzat utilitzant `jlink`.
Tot el procés es realitza dins d’un contenidor Docker, cosa que et permetrà generar un JRE optimitzat i compatible amb el sistema operatiu **Alpine Linux**, que és el que s’utilitza al **Sandbox** on s’executarà i provarà el codi de l’usuari.

## **1. Aixeca un contenidor Docker amb OpenJDK**

El primer pas és crear un nou contenidor utilitzant la imatge oficial d’OpenJDK per configurar un entorn on puguis personalitzar el JRE.
Per fer-ho, obre el terminal i executa l'ordre següent:

```bash
docker run -it --rm bellsoft/liberica-openjdk-alpine:21 sh
```

Aquesta instrucció aixecarà un contenidor basat en BellSoft Liberica OpenJDK 21 sobre Alpine Linux i amb una shell interactiva.
Si tot ha anat correctament, ja estaràs dins del contenidor. Gràcies al flag ```--rm```, aquest contenidor s’eliminarà automàticament quan surtis d’ell.

A continuació, necessitaràs afegir alguns mòduls o eines bàsiques que no estan disponibles per defecte en Alpine Linux. Executa les següents instruccions dins del contenidor:

1.  Instal·la el shell bash, les utilitats binutils i els coreutils necessaris per a fer tasques d'administració i manipulació d'arxius amb aquesta ordre:
```bash
apk add --no-cache bash binutils coreutils
```

2. Afegeix l'OpenJDK 21 al contenidor amb la següent instrucció, incloent-hi mòduls addicionals que no existeixen per defecte en la imatge d'Alpine, però que són necessaris per a construir el JRE:
```bash
apk add openjdk21-jdk
```

3. Finalment, configura les variables d'entorn necessàries perquè el JDK estigui disponible en el contenidor utilitzant el següent comando:
```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
export PATH=$JAVA_HOME/bin:$PATH
```

## **2. Crea el JRE personalitzat amb jlink**

Dins del contenidor, genera el JRE personalitzat seguint aquests passos:

#### 2.1. Identifica els mòduls necessaris

Selecciona els mòduls que vols incloure al JRE personalitzat.
Per exemple, per a crear el primer JRE personalitzat del projecte, només s'ha inclòs el següent mòdul, encara que pot ser que necessitis afegir algun més:

- java.base

Nota: altres mòduls no especificats durant el procés també poden incloure's automàticament. Això pot deure's a mòduls predeterminats i essencials o a mòduls inclosos que tinguin dependències internes que requereixin d'altres mòduls.

#### 2.2. Executa l'ordre jlink

Fes servir ```jlink``` per generar el JRE personalitzat executant aquesta instrucció dins del contenidor:

```bash
jlink --module-path $JAVA_HOME/jmods \
--add-modules java.base \
--strip-debug --compress=2 --no-header-files --no-man-pages \
--output /custom-jre
```

Això crearà un JRE personalitzat amb els mòduls especificats al directori ```/custom-jre``` (dins del contenidor). Pots modificar l'ordre per afegir o excloure altres mòduls, així com canviar la ruta del directori de sortida si fos necessari.

## **3. Copia el JRE generat al teu ordinador**

Assegura’t d’obtenir una còpia del JRE personalitzat abans de destruir el contenidor, ja que el JRE també es destruirà amb ell.
Per copiar el JRE personalitzat al teu sistema, segueix aquests passos:

#### 3.1. Comprimeix el JRE dins del contenidor

Executa l'ordre següent per crear un fitxer comprimit del JRE i així facilitar-ne la transferència:

```bash
tar -czvf /custom-jre.tar.gz /custom-jre
```

#### 3.2. Identifica l’ID del contenidor

Obre un **nou terminal** per treballar fora del contenidor (sense tancar l’anterior) i verifica l’ID del teu contenidor en execució amb la següent instrucció:

```bash
docker ps
```

Aquesta ordre mostra un llistat detallat dels contenidors en execució. Assegura’t de copiar l’ID del contenidor on has creat el JRE personalitzat.

#### 3.3. Copia el fitxer comprimit del contenidor al teu ordinador

**Des del nou terminal**, executa la següent ordre per copiar el fitxer comprimit del contenidor al teu ordinador (substitueix ```<container_id>``` per l’ID real que has copiat al pas anterior i ```<path>``` per la ruta on vols que es copiï el fitxer):

```bash
docker cp <container_id>:/custom-jre.tar.gz <path>
```

Exemple:

```bash
docker cp 800a361bc05c:/custom-jre.tar.gz C:\Users\Michel\Desktop\custom-jre
```

#### 3.4. Descomprimeix el fitxer ```custom-jre.tar.gz``` al teu ordinador

Utilitza una de les següents opcions per descomprimir el fitxer:

1. Amb el terminal:

**Des del nou terminal**, executa la següent ordre (modificant les rutes segons correspongui):

```bash
tar -xvzf "compressed_file_path" -C "output_path"
```

Exemple:

```bash
tar -xvzf "C:\Users\Michel\Desktop\custom-jre\custom-jre.tar.gz" -C "C:\Users\Michel\Desktop\custom-jre"
```

2. Amb 7-Zip:

- Fes clic dret sobre el fitxer comprimit anomenat ```custom-jre.tar.gz```.
- Selecciona "7-Zip > Extreu aquí". Això descomprimirà el fitxer ```custom-jre.tar```.
- Repeteix el procés sobre el fitxer ```custom-jre.tar``` per obtenir finalment la carpeta descomprimida ```custom-jre```.

#### 3.5.  Verifica els mòduls del teu JRE personalitzat

Accedeix al subdirectori ```custom-jre\legal``` i verifica que s’hi hagin inclòs els mòduls necessaris.

## **4. Finalitza i neteja**

Si tot està correcte i no necessites refer el JRE, torna al terminal anterior (és a dir, dins del contenidor) i executa l'ordre ```exit```.
En sortir del contenidor, aquest s’eliminarà automàticament gràcies al flag ```--rm``` que vam utilitzar en la creació del contenidor.


## **5. Testa el JRE personalitzat**

Una vegada que hagis creat i descomprimit el JRE personalitzat, és important verificar que funciona correctament.
Per a això, tens en aquest mateix projecte diverses classes creades per a testar el seu funcionament (les trobaràs en ```itachallenge-score/custom-jre/test```).

#### 5.1. Configura el Dockerfile

Crea aquest Dockerfile per a poder provar el JRE personalitzat:

```bash
# Fes servir Alpine com a base (pots ajustar la versió)
FROM alpine:latest

# Còpia el JRE personalitzat al contenidor
COPY custom-jre /custom-jre

# Estableix la variable d'entorn JAVA_HOME al JRE personalitzat
ENV JAVA_HOME=/custom-jre
ENV PATH=$JAVA_HOME/bin:$PATH

# Estableix el directori de treball
WORKDIR /app

# Còpia la classe prèviament compilada dins del contenidor (en aquest cas, HelloWorld.class)
COPY HelloWorld.class /app/

#  Instrucció per defecte per a executar el programa (recorda indicar la classe correcta)
CMD ["java", "HelloWorld"]
```

#### 5.2. Prepara el teu entorn de proves

Crea una carpeta de proves en la teva màquina local. És a dir, dins del teu sistema, crea una carpeta anomenada ```test``` (o qualsevol altre nom que prefereixis) i col·loca els següents elements en ella:
- La carpeta descomprimida del JRE personalitzat (```custom-jre```).
- La classe ja compilada que vulguis provar, com per exemple, ```HelloWorld.class``` (pots compilar-la a través de la terminal amb la instrucció ```javac```).
- El Dockerfile creat anteriorment.

L'estructura de la teva carpeta local hauria de quedar així:

```bash
test/
├── custom-jre/
├── HelloWorld.class
├── Dockerfile
```

#### 5.3. Construeix el contenidor

Navega a la carpeta local ```test``` des de la teva terminal i executa la següent ordre per a construir la imatge de Docker:

```bash
docker build -t custom-alpine-jre-test .
```

Aquesta instrucció crea una nova imatge de Docker anomenada ```custom-alpine-jre-test``` basada en el teu JRE personalitzat.

#### 5.4. Executa i prova el JRE personalitzat

Una vegada que la imatge s'hagi construït correctament, executa la següent ordre per a fer la prova:

```bash
docker run --rm custom-alpine-jre
```

Si el JRE funciona correctament, hauries de veure en la terminal la sortida del programa, en aquest cas:

```bash
Hello world!
```

Tingues en compte que, si la classe que estàs provant conté una classe o funció potencialment perillosa d'un mòdul que hagis exclòs durant la creació del JRE, el resultat esperat d'aquesta prova seria un error d'execució del tipus:

```bash
Error: Could not find or load main class <ClasseExclosa>
Caused by: java.lang.NoClassDefFoundError: <NomDeLaClasse>
```

Aquest missatge t'indicarà específicament quina classe està faltant. Per exemple: si falta una classe com ```javax.transaction.xa.XAResource```, significa que s'ha exclòs el mòdul ```java.transaction.xa```.


Nota: si utilitzes el compilador del JDK de la teva màquina en lloc d'un compilador personalitzat, pot ser que algunes classes de mòduls exclosos en la JRE personalitzada s'executin de totes maneres.

## **Notes addicionals**

- Assegura’t de tenir els permisos adequats al teu sistema per poder executar les instruccions de Docker i descomprimir els fitxers correctament.

- Aquest procés es pot repetir tantes vegades com sigui necessari en cas que calgui crear un nou JRE o si el resultat no ha estat l’esperat.

- Si insereixes el JRE personalitzat al projecte, recorda revisar tots els fitxers del directori i subdirectoris del JRE, ja que és possible que algun d’ells s’hagi afegit automàticament al ```.gitignore```. Si és el cas, des de l’IDE, fes clic dret sobre el fitxer en qüestió i selecciona "Git > Add" per afegir-lo al repositori.

- Recorda compilar la classe de prova amb ```javac``` abans de fer el test i adaptar el Dockerfile a la classe que estiguis provant.

- Pots provar diferents classes compilades repetint aquests passos, simplement reemplaçant ```HelloWorld.class``` amb una altra classe ```.class``` de la teva preferència.

- Assegura't que les rutes en el Dockerfile coincideixin amb els noms i ubicacions dels teus arxius.