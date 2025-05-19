namespace OrderGraphQLApi.models;

public class OrderConnection
{
    public List<OrderEdge> Edges { get; set; }
    public PageInfo PageInfo { get; set; }
}

public class OrderEdge
{
    public Order Node { get; set; }  // The actual order object
    public string Cursor { get; set; }  // The cursor for pagination
}

public class PageInfo
{
    public bool HasNextPage { get; set; }
    public string EndCursor { get; set; }  // The cursor for the last item on the current page
}
