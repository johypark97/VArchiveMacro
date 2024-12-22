#!/bin/bash

function main()
{
    # const
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

    # Update the time of gt files to fix the modification time error. This
    # occurs because the timezone of the dev container is out of sync with the
    # host timezone.
    find data/djmax-ground-truth -exec touch {} \;

    make lists unicharset MODEL_NAME=djmax
}

main
