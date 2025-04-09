import pino, { Logger } from "pino";

export class ServiceLogger {
    logger: Logger

    constructor() {
        this.logger = pino({
            timestamp: pino.stdTimeFunctions.isoTime,
            level: process.env.PINO_LOG_LEVEL || 'info',
            redact: ['contact_information.customer_name', 'contact_information.email', 'contact_information.phone', 'address.street', 'address.postal_code', 'address.city', 'address.country'],
        })

        process.on('uncaughtException', (err) => {
            this.logger.fatal(err);
            process.exit(1);
        });
    }
}