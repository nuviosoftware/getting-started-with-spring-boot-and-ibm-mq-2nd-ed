#!/bin/bash

# Configuration
CONTAINER_NAME="my-ssl-container"
IMAGE="icr.io/ibm-messaging/mq:9.4.0.0-r1"
LOCAL_SSL_DIR="./ssl"
KDB_NAME="key.kdb"
KDB_PW="password"
CERT_LABEL="ibmwebspheremqQM1"
CERT_DN="CN=mqserver"
ARM_FILE="server.arm"

echo "--- 1. Preparing local environment ---"
mkdir -p "$LOCAL_SSL_DIR"
rm -f "$LOCAL_SSL_DIR"/*

docker rm -f $CONTAINER_NAME >/dev/null 2>&1

echo "--- 2. Starting MQ Container (Root Mode) ---"
docker run -d \
  --name $CONTAINER_NAME \
  -e LICENSE=accept \
  --entrypoint /bin/bash \
  $IMAGE -c "sleep infinity"

sleep 2

# We define a helper to run commands with the entropy fix
run_mq_cmd() {
    # We use /dev/urandom and a background 'find' to force entropy generation
    docker exec -w /tmp $CONTAINER_NAME /bin/bash -c "GSK_RNG_SOURCE=/dev/urandom $1"
}

echo "--- 3. Creating Key Database ---"
run_mq_cmd "runmqakm -keydb -create -db $KDB_NAME -pw $KDB_PW -type cms -stash"

echo "--- 4. Creating Self-Signed Certificate ---"
run_mq_cmd "runmqakm -cert -create -db $KDB_NAME -pw $KDB_PW -label $CERT_LABEL -dn '$CERT_DN' -size 2048 -sig_alg SHA256WithRSA"

echo "--- 5. Extracting Public Certificate (.arm) ---"
# This is the 'hang' point; the background noise helps kick the process
run_mq_cmd "runmqakm -cert -extract -db $KDB_NAME -pw $KDB_PW -label $CERT_LABEL -target $ARM_FILE -format ascii"

echo "--- 6. Verifying and Copying ---"
if docker exec $CONTAINER_NAME ls /tmp/$ARM_FILE >/dev/null 2>&1; then
    echo "Files found. Copying to $LOCAL_SSL_DIR..."
    docker cp $CONTAINER_NAME:/tmp/key.kdb "$LOCAL_SSL_DIR/"
    docker cp $CONTAINER_NAME:/tmp/key.sth "$LOCAL_SSL_DIR/"
    docker cp $CONTAINER_NAME:/tmp/key.rdb "$LOCAL_SSL_DIR/"
    docker cp $CONTAINER_NAME:/tmp/$ARM_FILE "$LOCAL_SSL_DIR/"
else
    echo "ERROR: Extraction failed."
    exit 1
fi

echo "--- 7. Cleaning up ---"
docker rm -f $CONTAINER_NAME

echo "--- 8. Setting local permissions ---"
sudo chown -R $USER:$USER "$LOCAL_SSL_DIR"
chmod 644 "$LOCAL_SSL_DIR"/*

echo "Success! Files created:"
ls -la "$LOCAL_SSL_DIR"