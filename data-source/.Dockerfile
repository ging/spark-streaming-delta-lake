FROM python:3.11-buster

RUN pip install --upgrade pip
WORKDIR /app
COPY ./requirements.txt ./requirements.txt
RUN pip install -r ./requirements.txt
COPY ./src ./src

CMD python ./src/main.py