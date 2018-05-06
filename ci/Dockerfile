FROM ubuntu:18.04

ENV ASEPRITE_VERSION 1.2.13

RUN apt-get update \
    && echo "Aseprite dependencies" \
    && apt-get install -y --no-install-recommends \
        ca-certificates \
        cmake \
        curl \
        g++ \
        libx11-dev \
        libxcursor-dev \
        ninja-build \
        unzip \
    && echo "Other dependencies" \
    && apt-get install -y --no-install-recommends \
        git \
        imagemagick \
        make \
        openjdk-8-jdk \
        python3-pip \
        python3-setuptools \
        zip

COPY download-aseprite /bin
WORKDIR /src/aseprite
RUN download-aseprite $ASEPRITE_VERSION

COPY build-aseprite /bin
RUN build-aseprite

COPY requirements.txt /src
RUN pip3 install -r /src/requirements.txt

WORKDIR /root
ENTRYPOINT ["/bin/bash"]
