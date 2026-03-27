FROM debian:trixie

ARG WORKING_DIR=/training
WORKDIR $WORKING_DIR

RUN apt update && apt install -y git make python3 python3-pil tesseract-ocr unzip wget
RUN git clone https://github.com/tesseract-ocr/tesstrain
RUN cd tesstrain && make tesseract-langdata
