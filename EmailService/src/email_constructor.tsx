import fs from 'node:fs';
import path from 'node:path';
import mjml2html from "mjml";
import {
    Mjml,
    MjmlHead,
    MjmlTitle,
    MjmlPreview,
    MjmlBody,
    MjmlSection,
    MjmlColumn,
    MjmlText,
    MjmlTable,
    MjmlDivider,
} from "@faire/mjml-react";
import { MJMLParseResults } from 'mjml-core';
import { renderToMjml } from "@faire/mjml-react/utils/renderToMjml.js";
import { ServiceLogger } from './logger.js'
import { OrderConfirmation } from "./models/order_confirmation.js";
import React from 'react';

export class EmailConstructor {
    logger: ServiceLogger;
    template!: string;

    constructor() {
        this.logger = new ServiceLogger;

        try {
            this.template = fs.readFileSync(path.join(process.cwd(), './src/templates/order_confirmation.mjml'), 'utf8')
        } catch (error) {
            this.logger.logger.fatal(error, "Could not read email template file: order_confirmation.mjml.")
        }
    }

    renderReactToHTML(email: React.ReactElement): MJMLParseResults {
        return mjml2html(renderToMjml(email));
    }

    construct(order: OrderConfirmation): string | null {
        const { html, errors } = this.renderReactToHTML(
            <Mjml>

                <MjmlHead>
                    <MjmlTitle>Order Confirmation</MjmlTitle>
                    <MjmlPreview>Order Confirmation</MjmlPreview>
                </MjmlHead>

                < MjmlBody>
                    <MjmlSection>
                        <MjmlColumn>

                            <MjmlSection paddingBottom="0px">
                                <MjmlColumn>
                                    <MjmlText fontSize="20px">Fluffy Plushies A/S</MjmlText>
                                </MjmlColumn>
                                <MjmlColumn>
                                    <MjmlText fontSize="14px" paddingTop="16px" paddingLeft="119px">The last word in plushies</MjmlText>
                                </MjmlColumn>
                            </MjmlSection>

                            <MjmlSection>
                                <MjmlColumn>
                                    <MjmlText fontSize="16px" fontWeight='700' fontFamily='helvetica'>Order Confirmation</MjmlText>
                                    <MjmlText fontSize="12px" fontFamily='helvetica'>Status: {order.status}</MjmlText>
                                </MjmlColumn>
                                <MjmlColumn>
                                    <MjmlText fontSize="12px" fontFamily='helvetica' align='right'>Name: {order.contact_information.customer_name}</MjmlText>
                                    <MjmlText fontSize="12px" fontFamily='helvetica' align='right'>Email: {order.contact_information.email}</MjmlText>
                                    <MjmlText fontSize="12px" fontFamily='helvetica' align='right'>Phone: {order.contact_information.phone}</MjmlText>
                                </MjmlColumn>
                            </MjmlSection>

                            <MjmlTable>
                                <tr style={{ textAlign: "left" }}>
                                    <th style={{ padding: "0 160px 0 0" }}>Item</th>
                                    <th style={{ padding: "0 0px 0 0" }}>Quantity</th>
                                    <th style={{ padding: "0 0 0 0px" }}>Price</th>
                                </tr>

                                {order.line_items.map(({ item_id, item_name, price_per_item, quantity }) => (
                                    <tr key={item_id} style={{ textAlign: "left" }}>
                                        <td style={{ padding: "15 160px 0 0" }}>{item_name}</td>
                                        <td style={{ padding: "15 50px 0 0" }}>{quantity}</td>
                                        <td style={{ padding: "15 0 0 0px" }}>{price_per_item}</td>
                                    </tr>
                                ))}
                            </MjmlTable>

                            <MjmlDivider borderWidth="1px"></MjmlDivider>

                            <MjmlTable>
                                <tr style={{ textAlign: "left" }}>
                                    <td style={{ paddingLeft: "366px" }}>Shipping</td>
                                    <td style={{ paddingRight: "19px" }}>{order.shipping_cost}</td>
                                </tr>
                                <tr style={{ textAlign: "left" }}>
                                    <td style={{ paddingLeft: "366px", fontWeight: "bold", fontSize: "16px" }}>Total</td>
                                    <td style={{ paddingRight: "19px" }}>{order.total_amount}</td>
                                </tr>
                            </MjmlTable>

                            <MjmlDivider borderWidth="2px"></MjmlDivider>

                        </MjmlColumn>
                    </MjmlSection>
                </MjmlBody>
            </Mjml>
        )

        if (errors) {
            // MJML likes to complain about bad composition of components,
            // but it renders correct HTML regardless, so we ignore it.
            this.logger.logger.error(errors);
            return html
        } else {
            return html
        }

        /*console.log("Ready")
    
        http.createServer(function (_, response) {
            response.writeHead(200, { "Content-Type": "text/html" });
            response.write(html);
            response.end();
        }).listen(8080);*/
    }
}