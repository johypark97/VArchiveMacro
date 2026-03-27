#!/bin/bash

function main()
{
    local -r START_MODEL_PATH="usr/share/tessdata/djmax0.traineddata"

    cd ../tesstrain

    if [[ ! -e $START_MODEL_PATH ]]; then
        echo "START_MODEL not found:"
        echo "- $START_MODEL_PATH"
        exit 1
    fi

    cp data/djmax/all-lstmf data/djmax/list.eval
    cp data/djmax/all-lstmf data/djmax/list.train

    make training MAX_ITERATIONS=100000 MODEL_NAME=djmax START_MODEL=djmax0
}

main
