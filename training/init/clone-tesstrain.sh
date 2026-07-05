#!/usr/bin/env bash

function main()
{
    local -r GITHUB_USER=tesseract-ocr
    local -r GITHUB_REPO=tesstrain
    local -r GITHUB_API=https://api.github.com/repos/$GITHUB_USER/$GITHUB_REPO/tarball

    local tempFile=$( mktemp --suffix=.tar.gz )

    curl -L -o $tempFile $GITHUB_API
    mkdir ${GITHUB_REPO}
    tar -xzf $tempFile -C ${GITHUB_REPO} --strip-components=1
}

main "$@"
