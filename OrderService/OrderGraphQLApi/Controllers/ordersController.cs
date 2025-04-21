using models.order;
using services.ordersService;
using Microsoft.AspNetCore.Mvc;

namespace controllers.ordersController;

  [ApiController]
    [Route("api/[controller]")]
    public class OrdersController : ControllerBase
    {
        private readonly OrdersService _ordersService;

        public OrdersController(OrdersService ordersService){
            _ordersService = ordersService;
        }

        [HttpPost]
        public async Task<IActionResult> CreateAsync([FromBody] Order order)
        {
            try{
                await _ordersService.CreateAsync(order);
                return Ok("order created succesfully");
            }catch(Exception ex){
                return BadRequest(ex.Message);
            }
        }

        [HttpGet]
        public async Task<IActionResult> GetAllAsync()
        {
            var orders = await _ordersService.GetAllAsync();
            if (orders == null){
                return NotFound("No orders found.");
            }else{
                return Ok(orders);
            }
        }

        [HttpGet("{id}")]
        public IActionResult GetbyId(int id)
        {
            return Ok("Hello from OrdersController!");
        }

        [HttpPatch("{id}")] // skal vi bruge put eller patch???
        public IActionResult Update(int id, [FromBody] Order order){
            return Ok("Hello from OrdersController!");
        }

        [HttpDelete("{id}")] // burde det logges i stedet???
        public IActionResult Delete(int id){
            return Ok("Hello from OrdersController!");
        }
    }