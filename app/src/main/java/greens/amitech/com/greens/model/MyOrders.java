package greens.amitech.com.greens.model;

public class MyOrders {

    private String order_number;
    private String total_amt;
    private String order_date;
    private String ddate;
    private String itemTotal;
    private String itemOffer;
    private String totalDiscount;


    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public String getTotal_amt() {
        return total_amt;
    }

    public void setTotal_amt(String total_amt) {
        this.total_amt = total_amt;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getDdate() {
        return ddate;
    }

    public void setDdate(String ddate) {
        this.ddate = ddate;
    }

    public MyOrders(String order_number, String total_amt, String order_date, String ddate, String itemTotal, String itemOffer,
                    String totalDiscount){

       this.order_date=order_date;
       this.order_number = order_number;
       this.ddate=ddate;
       this.total_amt=total_amt;
       this.itemTotal = itemTotal;
       this.itemOffer = itemOffer;
       this.totalDiscount = totalDiscount;

    }


    public String getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(String itemTotal) {
        this.itemTotal = itemTotal;
    }

    public String getItemOffer() {
        return itemOffer;
    }

    public void setItemOffer(String itemOffer) {
        this.itemOffer = itemOffer;
    }

    public String getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(String totalDiscount) {
        this.totalDiscount = totalDiscount;
    }
}
