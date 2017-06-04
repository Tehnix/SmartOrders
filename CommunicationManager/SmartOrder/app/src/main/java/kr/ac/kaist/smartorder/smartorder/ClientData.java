package kr.ac.kaist.smartorder.smartorder;

public interface ClientData {
    public void handleMenu(String menu);

    public void handleOrderResponse(boolean success, String msg);
}
