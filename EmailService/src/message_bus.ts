import * as amqp from 'amqplib';
import { ServiceLogger } from './logger.js'
import EventEmitter from 'events';
import { OrderConfirmation } from './models/order_confirmation.js'

export class MessageBus extends EventEmitter {
    queue_name!: string;
    logger!: ServiceLogger;
    channel!: amqp.Channel;

    private constructor() {
        super()
    }

    static async createInstance(url: string, queue_name: string): Promise<MessageBus> {
        const message_bus = new MessageBus();

        message_bus.queue_name = queue_name;
        message_bus.logger = new ServiceLogger;

        await message_bus.createChannel(url);

        return message_bus;
    }

    async createChannel(url: string) {
        try {
            const conn = await amqp.connect(url)
            this.channel = await conn.createChannel();
            await this.channel.assertQueue(this.queue_name, { durable: true })
            await this.channel.prefetch(1)
        } catch (error) {
            this.logger.logger.fatal(error)
        }
    }

    listen() {
        this.channel.consume(this.queue_name, (msg) => {
            if (msg) {
                this.channel.ack(msg)
                try {
                    const order_confirmation: OrderConfirmation = JSON.parse(msg.content.toString()) as OrderConfirmation;
                    const hadListener = this.emit('order_confirmation', order_confirmation);
                    if (!hadListener) {
                        throw new Error("NoListeners")
                    }
                } catch (error) {
                    if (error instanceof SyntaxError) {
                        this.logger.logger.error(error, "Could not parse order confirmation JSON object.", msg.content.toString());
                    } else if (error instanceof Error) {
                        if (error.name === "NoListeners") {
                            this.logger.logger.error("Order confirmation was emitted, but no listeners are listening.");
                        }
                    }
                }
            }
        })
    }
}