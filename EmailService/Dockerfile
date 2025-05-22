FROM docker.io/library/node:23-alpine

ENV NODE_ENV development

RUN mkdir -p /home/node/app/node_modules && chown -R node:node /home/node/app
WORKDIR /home/node/app

USER node

COPY --chown=node:node package.json ./
RUN npm install

COPY --chown=node:node tsconfig.json ./
COPY --chown=node:node ./src ./src

CMD [ "npm", "run", "prod" ]
