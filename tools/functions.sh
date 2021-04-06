CACHE_DIR=$HOME/.cache/pixelwheels-dev

die() {
    echo "$PROGNAME: $*" >&2
    exit 1
}

# Download a file to the cache dir.
# Sets $LOCAL_PATH to the path to the file in the cache dir.
# params:
# - url
# - sha256sum of the file to download
# - local name (optional)
download_to_cache() {
    local url=$1
    local url_sha256sum=$2
    local local_name=${3:-}

    if [ -z "$local_name" ] ; then
        local_name=$(basename $url)
    fi

    LOCAL_PATH=$CACHE_DIR/$local_name

    if [ -e "$LOCAL_PATH" ] ; then
        echo "$local_name has already been downloaded"
    else
        echo "Downloading $local_name from $url..."
        mkdir -p $CACHE_DIR
        curl --location "$url" > "$LOCAL_PATH"
    fi

    echo "Verifying checksum"
    echo "$url_sha256sum  $LOCAL_PATH" | sha256sum --check
}
