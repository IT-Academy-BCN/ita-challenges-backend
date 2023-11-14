# ita-challenges-backend

### Versiones necesarias

- Gradle 8.1.1
- Java 17.0.1

### Módulos del proyecto

- [itachallenge-challenge](itachallenge-challenge/README.md).
- [itachallenge-score](itachallenge-score/README.md). 
- [itachallenge-user](itachallenge-user/README.md).
- [Consul](consul/README.md). Gestión de microservicios.

### Guidelines de desarrollo

See [GUIDELINES.md](GUIDELINES.md) for more details.

<hr/>

### Contributors

See [contributors.md](contributors.md) for more details.

### Instrucciones para subir la versión

* Asegúrese de tener Docker instalado en su máquina
* Modifique el archivo **build.gradle** del microservicio que quiera subir de versión (atributo 'version')
* Modifique el archivo **Dockerfile** del microservicio para renombrar la versión 
* Modifique la tag correspondiente a la versión del microservicio en el fichero **conf/.env.dev**
* Solicite credenciales al Administrador para subir la imagen a DockerHub
* Desde el directorio raíz del proyecto, **ejecute el siguiente comando** para subir la imagen a DockerHub (**Linux Bash**) 
```
./itachallenge-[nombreDelMicroservicio]/build_Docker.sh conf/.env.dev
```


[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v2.0%20adopted-ff69b4.svg)](CODE_OF_CONDUCT_EN.md)
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v2.0%20adopted-ff69b4.svg)](CODE_OF_CONDUCT_ES.md)
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v2.0%20adopted-ff69b4.svg)](CODE_OF_CONDUCT_CA.md)




