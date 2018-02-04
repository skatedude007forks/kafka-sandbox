package com.seigneurin.kafka.scala

import java.util.Properties

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.{Consumed, KafkaStreams, StreamsBuilder, StreamsConfig}

/*
 * Transforms values from 'text-input' to 'text-output':
 * - only keeps text containing the letter 'a'
 * - capitalizes the text
 *
 * Before running this:
 * - kafka-topics --zookeeper localhost:2181 --create --topic text-input --partitions 1 --replication-factor 1
 * - kafka-topics --zookeeper localhost:2181 --create --topic text-output --partitions 1 --replication-factor 1
 *
 * Launch a consumer to display the output:
 * - kafka-console-consumer --zookeeper localhost:2181 --topic text-output
 *
 * Launch a producer and type some text:
 * - kafka-console-producer --broker-list localhost:9092 --topic text-input
 */
object DummyKStream {

  def main(args: Array[String]) {

    val props = new Properties()
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "text-transformer")
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, "8")
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass)
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass)

    val builder = new StreamsBuilder()

    builder.stream("text-input", Consumed.`with`(Serdes.String(), Serdes.String()))
      .filter((key, value) => value.contains("a"))
      .mapValues(_.toUpperCase())
      .to("text-output")

    val streams = new KafkaStreams(builder.build(), props)
    streams.start()

  }
}
