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
Per exemple, aquests són els mòduls seleccionats per crear el primer JRE personalitzat del projecte, tot i que potser hauràs d’afegir-ne o excloure’n algun més:

- java.base
- java.logging
- java.prefs
- java.rmi
- java.sql
- jdk.charsets
- java.desktop

Nota: altres mòduls no especificats durant el procés també poden incloure's automàticament. Això pot deure's a mòduls predeterminats i essencials o a mòduls inclosos que tinguin dependències internes que requereixin d'altres mòduls.

#### 2.2. Executa l'ordre jlink

Fes servir ```jlink``` per generar el JRE personalitzat executant aquesta instrucció dins del contenidor:

```bash
jlink --module-path $JAVA_HOME/jmods \
--add-modules java.base,java.logging,java.prefs,java.rmi,java.sql,jdk.charsets,java.desktop \
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

## **Notes addicionals**

- Assegura’t de tenir els permisos adequats al teu sistema per poder executar les instruccions de Docker i descomprimir els fitxers correctament.

- Aquest procés es pot repetir tantes vegades com sigui necessari en cas que calgui crear un nou JRE o si el resultat no ha estat l’esperat.

- Si insereixes el JRE personalitzat al projecte, recorda revisar tots els fitxers del directori i subdirectoris del JRE, ja que és possible que algun d’ells s’hagi afegit automàticament al ```.gitignore```. Si és el cas, des de l’IDE, fes clic dret sobre el fitxer en qüestió i selecciona "Git > Add" per afegir-lo al repositori.