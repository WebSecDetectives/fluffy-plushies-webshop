import { MessageBus } from "./message_bus.js";
import { EmailConstructor } from "./email_constructor.js";
import { Emailer } from "./emailer.js";
import { OrderConfirmation } from "./models/order_confirmation.js";

const message_bus = await MessageBus.createInstance(process.env.AMQP_HOST!, "email.send_order_confirmed");
const email_constructor = new EmailConstructor();
const emailer = await Emailer.createInstance();

message_bus.on('order_confirmation', async (order_confirmation: OrderConfirmation) => {
    const html = email_constructor.construct(order_confirmation);

    if (html) {
        emailer.sendEmail(order_confirmation, html);
    }
})

message_bus.listen();

/*const order: OrderConfirmation = {
    status: "idk lmao",
    total_amount: 45749.9,
    shipping_cost: 50,
    contact_information: {
        customer_name: "Martin Niemann Madsen",
        email: "martin@martin.com",
        phone: "73782785"
    },
    address: {
        street: "Hedeboparken 4, st. 105",
        city: "Roskilde",
        country: "Denmark",
        postal_code: "4000"
    },
    line_items: [{
        item_id: "01960bb6-0fbd-7bc1-8a39-d9751abb5949",
        item_name: "Legally Distinct Blåhaj",
        quantity: 100n,
        price_per_item: 450
    },
    {
        item_id: "01961043-e083-7c7d-bc01-f5f249bf33ad",
        item_name: "Kaj fra Kaj og Andrea",
        quantity: 1n,
        price_per_item: 399.95
    }]
}

const email_construct = new EmailConstructor();
email_construct.construct(order);*/