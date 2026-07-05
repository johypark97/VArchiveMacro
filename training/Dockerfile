FROM debian:trixie

RUN apt-get update && apt-get install -y curl make python3 python3-pil tesseract-ocr unzip wget

ARG WORKING_DIR=/training
WORKDIR $WORKING_DIR

COPY ./init/clone-tesstrain.sh ./init/
RUN ./init/clone-tesstrain.sh
