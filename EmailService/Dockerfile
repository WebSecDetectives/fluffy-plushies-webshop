FROM docker.io/library/node:23-alpine

ARG RABBITMQ_HOST
ARG EMAIL_SERVICE_RABBITMQ_QUEUE
ARG SMTP_HOST
ARG SMTP_PORT
ARG SMTP_USER
ARG SMTP_PASS
ENV RABBITMQ_HOST $RABBITMQ_HOST
ENV EMAIL_SERVICE_RABBITMQ_QUEUE $EMAIL_SERVICE_RABBITMQ_QUEUE
ENV SMTP_HOST $SMTP_HOST
ENV SMTP_PORT $SMTP_PORT
ENV SMTP_USER $SMTP_USER
ENV SMTP_PASS $SMTP_PASS

ENV NODE_ENV development

RUN mkdir -p /home/node/app/node_modules && chown -R node:node /home/node/app
WORKDIR /home/node/app

USER node

COPY --chown=node:node package.json ./
RUN npm install

COPY --chown=node:node tsconfig.json ./
COPY --chown=node:node ./src ./src

CMD [ "npm", "run", "prod" ]