# Airflow orquestated spark-kafka process
### Infraestructura
Se ha desarrollado un fichero docker-compose que levanta los siguientes contenedores de docker
1. PostgreSQL -> Soporte de Airflow y persistencia de DataFrame procesados
2. Airflow -> Workflow manager que lanza el proceso Spark. Es una imagen custom que incluye las librerias necesarias para ejecutar SparkSubmit
3. Spark -> Cluster spark con un master y dos workers
4. Zookeper -> Zookeper para Karfka
5. Kafka -> Build de un Kafka optimizado para Docker

### Airflow
Airflow UI: http://localhost:8282/admin/
Se ha desarrollado un DAG que crea un workflow con 3 tasks
Start -> Spark_job -> End

Para que el job se ejecutara satisfactoriamente, se ha utilizado el operador SparkSubmit. Tambien se han anadido los jars necesarios para ejecutar el proceso, asi como se han definido los recursos necesarios. 

### Spark
Spark UI: http://localhost:8181/
Spark Master : spark://spark:7077 

El proyecto Spark se ha buildeado con SBT, al cual se le ha agregado el plugin de "Assembly", para poder generar FAT Jars. 
El proceso tiene dos modos de ejecucion: local y cluster. Ambos ejecutan el mismo codigo comun y solo se diferencian en las rutas de input y output: 
- Local utiliza la carpeta src/main/resources como directorio de input/output
- Cluster utiliza la carpeta "resources" agregada a docker como volumen para el input y escribe los resultados en PostgreSQL y Kafka
La estructura del codigo es la siguiente: 
- scala/App: Ejecuta el proceso segun sea LOCAL/CLUSTER
- scala/process: Contiene los flujos LOCAL/CLUSTER
- scala/utils: Contiene el codigo comun para ambos flujos. 
En modo cluster, envia los registros OK/KO a un topic que tiene el nombre indicado para el fichero JSON en dataflow. 


### Kafka
Kafka: INSIDE://kafka:9093
Se ha desplegado una instancia de Kafka sencilla, con un zookeeper asociado. 
