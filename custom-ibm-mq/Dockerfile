FROM icr.io/ibm-messaging/mq:9.4.0.0-r1

USER root

RUN mkdir -p /etc/mqm /opt/mqm/ssl

COPY 20-config.mqsc /etc/mqm/20-config.mqsc
COPY ssl/key.kdb /opt/mqm/ssl/key.kdb
COPY ssl/key.rdb /opt/mqm/ssl/key.rdb
COPY ssl/key.sth /opt/mqm/ssl/key.sth

RUN chown -R 1001:0 /etc/mqm /opt/mqm/ssl && \
    chmod -R 770 /opt/mqm/ssl

EXPOSE 1414
EXPOSE 9443
EXPOSE 1415

USER 1001

ENV ICC_SHIFT=0

ENTRYPOINT ["runmqdevserver"]