/**
 * @module
 * @mergeModuleWith <project>
 */

/**
 * An interface defining the data contained in an order confirmation message JSON payload.
 */
export interface OrderConfirmation {
    line_items: Array<LineItem>,
    shipping_cost: number,
    total_amount: number,
    status: string,
    address: {
        street: string,
        postal_code: string,
        city: string,
        country: string
    },
    contact_information: {
        customer_name: string,
        email: string,
        phone: string
    }
}

interface LineItem {
    item_id: string,
    item_name: string,
    quantity: bigint,
    price_per_item: number
}