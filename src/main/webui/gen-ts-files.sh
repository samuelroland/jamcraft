#!/bin/bash
# Script to download empty.proto and generate all TS files from source proto files out of the webui folder
pushd src &>/dev/null

empty_path=google/protobuf/empty.proto
if ! test -f $empty_path; then
    wget https://raw.githubusercontent.com/protocolbuffers/protobuf/main/src/google/protobuf/empty.proto -O $empty_path
fi
mkdir -p grpc
for p in ../../proto/*.proto; do
    echo "Generating TS files for $p"
    npx protoc -I ../../proto/ -I . --ts_out grpc --ts_opt optimize_code_size $p
done

popd &>/dev/null
