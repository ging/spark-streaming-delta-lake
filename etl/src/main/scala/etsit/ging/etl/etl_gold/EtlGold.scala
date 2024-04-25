package etsit.ging.etl.etl_gold

// https://medium.com/expedia-group-tech/apache-spark-structured-streaming-operations-5-of-6-40d907866fa7

import org.apache.log4j.Level
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object EtlGold {
  def main(args: Array[String]): Unit = {

    /** SparkSession builder
      * For using DeltaLake connector, mind the configurations
      */
    val spark = SparkSession
      .builder()
      .appName("ETLGold")
      .master("local[*]")
      .config("spark.sql.streaming.forceDeleteTempCheckpointLocation", "true")
      .config("spark.sql.extensions", "io.delta.sql.DeltaSparkSessionExtension")
      .config(
        "spark.sql.catalog.spark_catalog",
        "org.apache.spark.sql.delta.catalog.DeltaCatalog"
      )
      .getOrCreate()

    import spark.implicits._

    spark.sparkContext.setLogLevel(Level.WARN.toString)

    val df = spark.readStream
      .format("delta")
      .load("../data/delta_historical")

    val df_max_value = df
      .withColumn(
        "timestamp_as_timestamp",
        to_timestamp(from_unixtime($"timestamp"))
      )
      .withWatermark("timestamp_as_timestamp", "10 seconds")
      .groupBy(
        window($"timestamp_as_timestamp", "5 seconds")
      )
      .agg(
        max($"altitude").as("max_altitude"),
        min($"altitude").as("min_altitude")
      )
      .select(
        $"window.start".as("start"),
        $"window.end".as("end"),
        $"max_altitude",
        $"min_altitude"
      )

    val console_stream = df_max_value.writeStream
      .format("console")
      .start

    val delta_stream = df_max_value.writeStream
      .format("delta")
      .outputMode("append")
      .option("checkpointLocation", "/tmp/delta/gold/_checkpoints/")
      .start("../data/delta_aggregation")

    console_stream.awaitTermination
    delta_stream.awaitTermination
  }
}
