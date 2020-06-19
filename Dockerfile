FROM ubuntu:18.04

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        curl \
        git \
        imagemagick \
        make \
        openjdk-8-jdk \
        python3-pip \
        python3-setuptools \
        zip

COPY requirements.txt /src/
RUN pip3 install -r /src/requirements.txt

WORKDIR /root
ENTRYPOINT ["/bin/bash"]
