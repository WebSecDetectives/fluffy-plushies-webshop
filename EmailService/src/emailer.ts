import { EmailProvider, EmailProviderStatic } from "./interfaces/email_provider.js";
import { ServiceLogger } from "./logger.js";
import { OrderConfirmation } from "./models/order_confirmation.js";
import { staticImplements } from "./helpers/static_implements.js";
import nodemailer from "nodemailer";
import SMTPTransport from "nodemailer/lib/smtp-transport/index.js";

@staticImplements<EmailProviderStatic>()
export class Emailer implements EmailProvider {
    logger!: ServiceLogger;
    transporter!: nodemailer.Transporter<SMTPTransport.SentMessageInfo, SMTPTransport.Options>;

    static async createInstance(): Promise<EmailProvider> {
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