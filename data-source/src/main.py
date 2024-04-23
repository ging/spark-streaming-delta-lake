import json
import sys
import requests
from time import sleep
import logging
from confluent_kafka import Producer
from uuid import uuid4

root = logging.getLogger()
root.setLevel(logging.DEBUG)
handler = logging.StreamHandler(sys.stdout)
handler.setLevel(logging.DEBUG)
formatter = logging.Formatter('%(asctime)s - %(levelname)s - %(name)s - %(message)s')
handler.setFormatter(formatter)
root.addHandler(handler)

config = {
    'bootstrap.servers': 'localhost:9092',  # Ensure this matches your broker config
}
kafka_producer = Producer(config)


def acked(err, msg):
    if err is not None:
        logging.error("Failed to deliver message: %s: %s" % (str(msg), str(err)))
    else:
        logging.info(f"Message produced: {msg.topic()} [{msg.partition()}] @ {msg.offset()}")

def init_loop():
    loop = True
    while loop:
        try:
            logging.info("requesting data from source...")
            res = requests.get("https://api.wheretheiss.at/v1/satellites/25544") 
            if res.status_code == requests.codes["ok"]:
                logging.info("requesting data from source...")
                data = res.json()      
                kafka_producer.produce(topic="iss", key=str(uuid4()), value=json.dumps(data).rstrip(), callback=acked)
                kafka_producer.flush()
            else: 
                logging.error("Request data went wrong...")
        except KeyboardInterrupt:
            loop = False
            break
        finally:
            sleep(.5)
        
    
    
def main():
    logging.info("Init data source")
    init_loop()


if __name__ == "__main__":
    main()