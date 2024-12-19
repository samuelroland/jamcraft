# Script used to generate js files inside src/proto from ../proto
# Must be run from this folder
# This requires the separate installation of protoc, on Fedora it is
# sudo dnf install protobuf protobuf-compiler
set proto_destination src/proto

## SETUP
set path1 $PWD/node_modules/.bin/protoc-gen-js

if ! test -x $path1
    echo Installing protoc-gen-js via pnpm
    echo pnpm install protoc-gen-js
    pnpm install protoc-gen-js
end

set path2 $PWD/node_modules/.bin/protoc-gen-grpc-web
if ! test -x $path2
    echo Installing protoc-gen-grpc-web from Github
    echo "Downloading to $path2"
    curl https://github.com/grpc/grpc-web/releases/download/1.5.0/protoc-gen-grpc-web-1.5.0-linux-x86_64 --location -o $path2
    chmod +x $path2
end

mkdir -p $proto_destination

## GENERATION
echo "Generating proto files"

for file in (fd -e proto --base-directory ../proto/)
    echo "Generating JS files from $file"
    protoc -I ../proto $file \
        --js_out=import_style=commonjs:$proto_destination --grpc-web_out=import_style=commonjs,mode=grpcwebtext:$proto_destination \
        --plugin=protoc-gen-js=$path1 --plugin=protoc-gen-grpc-web=$path2
end

# protoc -I . hello.proto --js_out=import_style=commonjs:generated --grpc-web_out=import_style=commonjs,mode=grpcwebtext:generated --plugin=protoc-gen-js=$path1
# echo yo
# protoc -I . hello.proto --js_out=import_style=commonjs:generated --grpc-web_out=import_style=typescript,mode=grpcweb:. --plugin=protoc-gen-grpc-web=$path2
