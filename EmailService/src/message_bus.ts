/**
 * @module
 * @mergeModuleWith <project>
 */

import * as amqp from 'amqplib';
import { ServiceLogger } from './logger.js'
import EventEmitter from 'events';
import { OrderConfirmation } from './models/order_confirmation.js'

/**
 * This class handles listening to a queue on the RabbitMQ message bus and emitting payloads when messages are received.
 */
export class MessageBus extends EventEmitter {
    queue_name!: string;
    logger!: ServiceLogger;
    channel!: amqp.Channel;
    queue!: string

    private constructor() {
        super()
    }

    /**
     * Returns an initialized instance of MessageBus.
     * 
     * @remarks
     * It was not possible to use the constructor for initialization purposes, as TypeScript constructors cannot be async.
     * This class extends EventEmitter. When a message is received, we emit the message payload to outside consumers for processing. This ensures decoupling of concerns.
     * 
     * @param url - The hostname and URI for the RabbitMQ instance
     * @param queue_name - The name of the queue that the Email Service needs to subscribe to
     * @returns An initialized instance of MessageBus.
     */
    static async createInstance(url: string, queue_name: string): Promise<MessageBus> {
        const message_bus = new MessageBus();

        message_bus.queue_name = queue_name;
        message_bus.logger = new ServiceLogger;

        await message_bus.createChannel(url);

        return message_bus;
    }

    /**
     * Creates a channel that subscribes to the queue provided to createInstance().
     * @param url - The hostname and URI for the RabbitMQ instance
     */
    async createChannel(url: string) {
        try {
            const conn = await amqp.connect(url)
            this.channel = await conn.createChannel();
            await this.channel.assertExchange("direct", "direct")
            const { queue } = await this.channel.assertQueue(this.queue_name, { durable: true })
            await this.channel.bindQueue(queue, "direct", this.queue_name)
            this.queue = queue
            await this.channel.prefetch(1)
        } catch (error) {
            this.logger.logger.fatal(error)
        }
    }

    /**
     * Starts listening for messages in the queue.
     * When a message is received, an "order_confirmation" event is emitted with the message payload to be caught by a consumer.
     * 
     * @remarks
     * This method never returns, and it should therefore be the last method to call in the main loop.
     */
    listen() {
        this.channel.consume(this.queue, (msg) => {
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