package kaist.restaurantownerapp.communication;

public interface ClientData {
    public void handleMenu(String menu);

    public void handleOrderResponse(String msg);

    public void handleConnectionResult(boolean connected);
}
