#!/bin/bash

function main()
{
    # const
    local START_MODEL_PATH="usr/share/tessdata/djmax0.traineddata"
    local VIRTUAL_ENV_PATH=venv

    # activate the virtualenv
    if [[ -z $VIRTUAL_ENV ]]; then
        if [[ ! -e $VIRTUAL_ENV_PATH ]]; then
            echo "virtualenv is not created"
            exit 1
        fi

        source $VIRTUAL_ENV_PATH/bin/activate
    fi

    # run task
    cd tesstrain

    if [[ ! -e $START_MODEL_PATH ]]; then
        echo "cannot find the START_MODEL"
        echo "- $START_MODEL_PATH"
        exit 1
    fi

    cp data/djmax/all-lstmf data/djmax/list.eval
    cp data/djmax/all-lstmf data/djmax/list.train

    make training MAX_ITERATIONS=100000 MODEL_NAME=djmax START_MODEL=djmax0
}

main
