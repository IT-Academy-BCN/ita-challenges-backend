
## ITA Challenge

* Para habilitar / deshabilitar el registro Consul, modificar el valor de la propiedad `spring.cloud.consul.enabled` en el fichero `bootstrap.yml` (true/false)


##### Spring Boot Actuator

- http://localhost:8762/actuator/health (debe responder {"status":"UP"})
- http://localhost:8762/actuator/auditevents
- http://localhost:8762/actuator/beans
- http://localhost:8762/actuator/conditions
- http://localhost:8762/actuator/configprops
- http://localhost:8762/actuator/env
- http://localhost:8762/actuator/heapdump (genera volcado de heap para descarga)
- http://localhost:8762/actuator/httptrace
- http://localhost:8762/actuator/info
- http://localhost:8762/actuator/loggers
- http://localhost:8762/actuator/metrics
- http://localhost:8762/actuator/mappings
- http://localhost:8762/actuator/scheduledtasks
- http://localhost:8762/actuator/threaddump