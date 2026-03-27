#!/usr/bin/env bash

# ========================
# -------- string --------
# ========================

readonly STRING_HELP_INFO="Try '$( basename "$0" ) -h' for more information."

readonly STRING_HELP=$( cat << EOF
Usage:
    $( basename "$0" ) (option...) [task]

Tasks:
    build     build image
    run       run image and attach to the container
    attach    attach to the container
    help      print help messages

Options:
    -i    set image name (default: tesstrain)
    -c    set container name (default: tesstrain)
    -d    set data directory path to mount (default: ./data)
    -o    overwrite existing image when building (default: false)
    -r    use --rm option when running the container
    -h    print help messages
EOF
)

# ==========================
# -------- function --------
# ==========================

function getopts_printError()
{
    if [[ -n $2 ]]; then
        case $1 in
            :) echo "option requires an argument: -$2" ;;
            ?) echo "invalid option: -$2" ;;
            *) echo "unknown error" ;;
        esac
    fi
}

function isImageExists()
{
    docker image inspect "$1" > /dev/null 2>&1
}

function isContainerExists()
{
    docker container inspect "$1" > /dev/null 2>&1
}

function buildImage()
{
    local -r WORKING_DIR=$1

    local imageName=${2:?imageName is unset or null}
    local flagOverwrite=${3:-0}

    if (( ! flagOverwrite )) && isImageExists "$imageName"; then
        echo "docker image '$imageName' already exists"
        return
    fi

    set -x
    docker build --build-arg WORKING_DIR=$WORKING_DIR -t "$imageName" .
}

function runImage()
{
    local -r WORKING_DIR=$1

    local imageName=${2:?imageName is unset or null}
    local containerName=${3:?containerName is unset or null}
    local dataDirectoryPath=${4:?dataDirectoryPath is unset or null}
    local flagRun_rm=${5:-0}

    if isContainerExists "$containerName"; then
        echo "docker container '$containerName' already exists"
        return
    elif ! isImageExists "$imageName"; then
        echo "docker image '$imageName' does not exist"
        return
    fi

    local optionList=(
        -it
        --name "$containerName"
        -v ./scripts:$WORKING_DIR/scripts/
        -v "$dataDirectoryPath":$WORKING_DIR/data/
    )
    (( flagRun_rm )) && optionList+=( --rm )

    set -x
    docker run "${optionList[@]}" "$imageName"
}

function attachContainer()
{
    local containerName=${1:?containerName is unset or null}

    if ! isContainerExists "$containerName"; then
        echo "docker container '$containerName' does not exist"
        return
    fi

    set -x
    docker attach "$containerName"
}

# ======================
# -------- main --------
# ======================

function main()
{
    if ! command -v docker > /dev/null; then
        echo "'docker' command is not found"
        exit 1
    fi

    local -r WORKING_DIR=/training

    local imageName=tesstrain
    local containerName=tesstrain
    local dataDirectoryPath=./data
    local flagBuild_overwrite=0
    local flagRun_rm=0

    OPTIND=1
    while getopts :i:c:d:orh opt; do
        case $opt in
            i)
                imageName=$OPTARG
                ;;
            c)
                containerName=$OPTARG
                ;;
            d)
                dataDirectoryPath=$OPTARG
                ;;
            o)
                flagBuild_overwrite=1
                ;;
            r)
                flagRun_rm=1
                ;;
            h)
                echo "$STRING_HELP"
                exit 0
                ;;
            *)
                getopts_printError "$opt" "$OPTARG"
                echo "$STRING_HELP_INFO"
                exit 1
                ;;
        esac
    done
    shift $(( OPTIND - 1 ))

    if (( ! $# )); then
        echo "requires a task"
        echo "$STRING_HELP_INFO"
        exit 1
    fi

    local task=$1
    case $task in
        build)
            buildImage $WORKING_DIR "$imageName" "$flagBuild_overwrite"
            ;;
        run)
            runImage $WORKING_DIR "$imageName" "$containerName" "$dataDirectoryPath" "$flagRun_rm"
            ;;
        attach)
            attachContainer "$containerName"
            ;;
        help)
            echo "$STRING_HELP"
            exit 0
            ;;
        *)
            echo "invalid task: $task"
            echo "$STRING_HELP_INFO"
            exit 1
            ;;
    esac
}

main "$@"
