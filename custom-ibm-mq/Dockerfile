# Add custom configuration
FROM icr.io/ibm-messaging/mq:9.4.0.0-r1

# Copy MQSC config
COPY 20-config.mqsc /etc/mqm/

# Create SSL directory
RUN mkdir -p /var/mqm/qmgrs/QM1/ssl

# Copy prebuilt key database
COPY ssl/key.kdb /var/mqm/qmgrs/QM1/ssl/
COPY ssl/key.rdb /var/mqm/qmgrs/QM1/ssl/
COPY ssl/key.sth /var/mqm/qmgrs/QM1/ssl/

ENTRYPOINT ["runmqdevserver"]