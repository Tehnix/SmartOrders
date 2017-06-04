package kaist.customerapplication.communicationmanager;

public interface ClientData {
    public void handleMenu(String menu);

    public void handleOrderResponse(boolean success, String msg);
}
