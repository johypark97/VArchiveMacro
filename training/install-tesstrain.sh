#!/bin/bash

function main()
{
    # const
    local VIRTUAL_ENV_PATH=venv

    # install packages
    sudo apt update
    sudo apt -y install tesseract-ocr python3 python3-venv

    # install the virtualenv and pillow
    if [[ ! -e $VIRTUAL_ENV_PATH ]]; then
        python3 -m venv $VIRTUAL_ENV_PATH
        [[ -z $VIRTUAL_ENV ]] && source $VIRTUAL_ENV_PATH/bin/activate
        pip install pillow
    fi

    # clone tesstrain
    if [[ ! -e tesstrain ]]; then
        git clone https://github.com/tesseract-ocr/tesstrain
        cd tesstrain
        make tesseract-langdata
    fi
}

main
