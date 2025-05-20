namespace OrderGraphQLApi.models;

public class ReservationItem
{
    public string item_id { get; set; }
    public int quantity { get; set; }
}

public class ItemsReservationRequestDto
{
    public List<ReservationItem> items { get; set; }
}