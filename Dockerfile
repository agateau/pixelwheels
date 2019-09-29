FROM ubuntu:18.04

COPY tools/aseprite/install-aseprite-dependencies bin
ENV ASEPRITE_VERSION 1.2.13

RUN apt-get update \
    && install-aseprite-dependencies \
    && apt-get install -y --no-install-recommends \
        git \
        imagemagick \
        make \
        openjdk-8-jdk \
        python3-pip \
        python3-setuptools \
        zip

COPY tools/aseprite/download-aseprite /bin
WORKDIR /src/aseprite
RUN download-aseprite $ASEPRITE_VERSION

COPY tools/aseprite/build-aseprite /bin
RUN build-aseprite

COPY requirements.txt /src
RUN pip3 install -r /src/requirements.txt

WORKDIR /root
ENTRYPOINT ["/bin/bash"]
