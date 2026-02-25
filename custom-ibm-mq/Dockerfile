FROM icr.io/ibm-messaging/mq:9.4.0.0-r1 AS mq-dev-server

ENV MQ_PASSWORD_FILE=true
ENV MQ_ADDITIONAL_USERS=APPUSER

COPY 20-config.mqsc /etc/mqm/

COPY mq-secrets/APPUSER.pass /etc/mqm/mq-secrets/APPUSER

ENTRYPOINT ["runmqdevserver"]