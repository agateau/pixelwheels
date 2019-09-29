FROM ubuntu:18.04

COPY tools/aseprite/install-aseprite-dependencies bin

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

COPY \
    tools/aseprite/download-aseprite \
    tools/aseprite/build-aseprite \
    /bin/

WORKDIR /src/aseprite
RUN download-aseprite . \
    && build-aseprite . /usr/local \
    && rm -rf /src/aseprite

COPY requirements.txt /src
RUN pip3 install -r /src/requirements.txt

WORKDIR /root
ENTRYPOINT ["/bin/bash"]
