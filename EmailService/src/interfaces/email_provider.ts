import { OrderConfirmation } from "../models/order_confirmation.js";

export interface EmailProvider {
    sendEmail(order: OrderConfirmation, html: string): void;
}

// https://stackoverflow.com/a/43674389
export interface EmailProviderStatic {
    new(): EmailProvider;
    createInstance(): Promise<EmailProvider>;
}