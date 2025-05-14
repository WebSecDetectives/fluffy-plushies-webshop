/**
 * @module
 * @mergeModuleWith <project>
 */

import { ServiceLogger } from "./logger.js";
import { OrderConfirmation } from "./models/order_confirmation.js";
import nodemailer from "nodemailer";
import SMTPTransport from "nodemailer/lib/smtp-transport/index.js";

/**
 * This class handles sending email with an HTML payload using an external SMTP server.
 */
export class Emailer {
    logger!: ServiceLogger;
    transporter!: nodemailer.Transporter<SMTPTransport.SentMessageInfo, SMTPTransport.Options>;

    /**
     * Returns an initialized instance of EmailProvider.
     * 
     * @remarks
     * It was not possible to use the constructor for initialization purposes, as TypeScript constructors cannot be async.
     * 
     * @returns An initialized instance of EmailProvider.
     */
    static async createInstance(): Promise<Emailer> {
        const emailer = new Emailer();

        emailer.logger = new ServiceLogger;

        try {
            emailer.transporter = nodemailer.createTransport({
                host: process.env.SMTP_HOST,
                port: Number(process.env.SMTP_PORT),
                auth: {
                    user: process.env.SMTP_USER,
                    pass: process.env.SMTP_PASS
                }
            });
        } catch (error: any) {
            const message = await error.text();
            emailer.logger.logger.fatal(message, "Nodemailer failed to create SMTP transport.")
        }

        return emailer;
    }

    /**
     * Tells the SMTP server to send an email containing the provided HTML payload to the recipient in the provided order confirmation.
     * @param order - A parsed order confirmation inside an OrderConfirmation object.
     * @param html - A stringified HTML payload.
     */
    async sendEmail(order: OrderConfirmation, html: string) {
        try {
            const info = await this.transporter.sendMail({
                from: `"Fluffy Plushies A/S" <${process.env.SMTP_USER}>`,
                to: order.contact_information.email,
                subject: `${order.contact_information.customer_name}, your order confirmation for ${order.line_items.length} items from Fluffy Plushies A/S.`,
                text: "This email contains HTML. Please view this message in a mail client that supports HTML.",
                html: html
            });
            this.logger.logger.info(`Sent ID: ${info.messageId} - Sent email to "${info.envelope.to}".`)
        } catch (error: any) {
            const message = await error.text();
            this.logger.logger.error(`Nodemailer sendMail failed: ${message}.`)
        }
    }
}