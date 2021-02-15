from airflow import DAG
from airflow.operators.dummy_operator import DummyOperator
from airflow.contrib.operators.spark_submit_operator import SparkSubmitOperator
from datetime import datetime, timedelta

###############################################
# Parameters
###############################################
spark_master = "spark://spark:7077"
postgres_driver_jar = "/usr/local/spark/resources/jars/postgresql-42.2.18.jar"
libs_jar = "file:/usr/local/spark/resources/jars/postgresql-42.2.18.jar,file:/usr/local/spark/resources/jars/lift-json_2.13-3.4.3.jar,file:/usr/local/spark/resources/jars/scala-library-2.12.13.jar"
assembly_jar = "/usr/local/spark/sdg-tryout/target/scala-2.12/sdg-tryout-assembly-0.1.jar"

remote = "remote"
dataflows_file = "/usr/local/spark/resources/data/dataflows.json"
postgres_db = "jdbc:postgresql://postgres/test"
postgres_user = "test"
postgres_pwd = "postgres"

###############################################
# DAG Definition
###############################################
now = datetime.now()

default_args = {
    "owner": "airflow",
    "depends_on_past": False,
    "start_date": datetime(now.year, now.month, now.day),
    "email": ["airflow@airflow.com"],
    "email_on_failure": False,
    "email_on_retry": False,
    "retries": 1,
    "retry_delay": timedelta(minutes=1)
}

dag = DAG(
        dag_id="sdg-tryout", 
        description="This dag reads the json files, load them into a PostgresDB and then read them from the same PostgresDB",
        default_args=default_args, 
        schedule_interval=timedelta(1)
    )

start = DummyOperator(task_id="start", dag=dag)

spark_job_transform_json = SparkSubmitOperator(
    task_id="spark_job_transform_json",
    application=assembly_jar,
    name="transform_json",
    conn_id="spark_default",
    verbose=1,
    conf={"spark.master":spark_master},
    application_args=[remote, postgres_db, postgres_user,postgres_pwd, dataflows_file],
    jars=libs_jar,
    driver_class_path=postgres_driver_jar,
    dag=dag)

end = DummyOperator(task_id="end", dag=dag)

start >> spark_job_load_postgres >> spark_job_read_postgres >> end
