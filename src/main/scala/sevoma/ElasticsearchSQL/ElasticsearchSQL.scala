package sevoma.ElasticsearchSQL

import org.apache.spark.sql.SparkSession
import org.elasticsearch.spark.sql._

object ElasticsearchSQL extends App {


  /**
   * Elasticsearch SQL example
   */

  val sqlContext = SparkSession.builder().appName("lol")
    .master("local[4]")
    .getOrCreate()


  val esFormat = "org.elasticsearch.spark.sql"
  val elasticOptions = Map[String, String](
    "es.nodes" -> "localhost",
    "es.port" -> "9200",
    "es.nodes.wan.only" -> "true",
    "es.query" ->  "?q=*",
    "es.http.timeout" -> "10s",
    "es.scroll.size" -> "5000",
    "es.read.field.as.array.include" -> "",
    "es.mapping.date.rich" -> "false",
    "es.mapping.timestamp" -> "timestamp"
  )

  // DataFrame schema automatically inferred
  // Note: You must specify <index-name>/<document-type>
  val df = sqlContext.read.format(esFormat).options(elasticOptions).load("data-2017.05.*/somedoctype")

  df.createOrReplaceTempView("esindex")

  // Query ES using SQL
  var res = sqlContext.sql("SELECT * FROM esindex WHERE query = 'somequery'")
  println("Elastic count via SQL: " + res.count())
  res.saveToEs("data-summarized/somedoctype", elasticOptions)


//  // Same query against ES using filter
//  val res2 = df.filter(df("proto").equalTo("tcp")
//    .and(df("dst_ip").equalTo("127.0.0.1"))
//    .and(df("src_ip").notEqual("127.0.0.1")))
//  println("Elastic count via filter: " + res2.count())

  sys.exit(0)
}
