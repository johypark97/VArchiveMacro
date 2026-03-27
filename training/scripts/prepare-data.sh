#!/bin/bash

function main()
{
    cd ../tesstrain

    # Update the time of gt files to fix the modification time error. This
    # occurs because the timezone of the dev container is out of sync with the
    # host timezone.
    find data/djmax-ground-truth -exec touch {} \;

    make lists unicharset MODEL_NAME=djmax
}

main
