# Spark Streaming and Delta lake

## Context

We’re big data engineers working in the ESA (European Space Agency). After a technical meeting we’ve been tasked to create a pipeline with real-time data from ISS (International Space Station) involving a data lake creation for historical data and also a service which takes the higher altitude the satellite reaches for aerospacial researching purposes.

Our colleagues from ground control developed a service exposing an REST-API endpoint so that when we send a GET HTTP message, we get current information from the satellite.

After a thorough revision of the briefing, you and your team mates came up with the following service architecture:

![architecture](./assets/architecture.png)

The architecture is not very complicated, but it’s effective and scalable.

- We set up first a script in python which sets a loop that queries the ground control API service each .5 seconds. If the request is successful it produces a kafka message in the “iss” topic.
- Kafka cluster is simple now, is just a broker with a zookeeper together. We have only a only kafka instance, but it could scale up horizontally with more kafka servers.
- In the data lake, we have draw to sections inside following the medallion architecture with just two stages. The first one is the bronze stage, in which we just parse the JSON data coming from the kafka broker and converting it to a spark table with Spark structured streaming API and storing it with deltalake connector.
- The second one is the gold stage in which we aggregate data using again Spark structured API, but this time consuming real time data stream from deltalake.

## Setting up project

Tutorial follows..
