package greens.amitech.com.greens.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import greens.amitech.com.greens.OrderDetailsActivity;
import greens.amitech.com.greens.R;
import greens.amitech.com.greens.model.MyOrdersDetail;
import greens.amitech.com.greens.utils.NetworkUtils;

public class MyOrdersDetailAdapter extends RecyclerView.Adapter<MyOrdersDetailAdapter.MyViewHolder> {

    public Context mcontext;
    public List<MyOrdersDetail> my_orders_models;
    public NetworkUtils NetworkUtils;


    public MyOrdersDetailAdapter(OrderDetailsActivity mcontext, List<MyOrdersDetail> MyOrdersDetails) {

        this.mcontext=mcontext;
        this.my_orders_models= MyOrdersDetails;

    }


    @NonNull
    @Override
    public MyOrdersDetailAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item_view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_details_layout, parent, false);


        return new MyOrdersDetailAdapter.MyViewHolder(item_view);

    }


    @Override
    public void onBindViewHolder(@NonNull final MyOrdersDetailAdapter.MyViewHolder holder, final int position) {

        final MyOrdersDetail MyOrdersDetail = my_orders_models.get(position);


        holder.item_name.setText(MyOrdersDetail.getItem_name());
        holder.qty.setText(MyOrdersDetail.getItem_qty());
        holder.subtotal.setText("₹"+ MyOrdersDetail.getSubtotal());



    }


    @Override
    public int getItemCount() {
        return my_orders_models.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView item_name, qty, subtotal;
        public Button view_items;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            NetworkUtils = new NetworkUtils();

            item_name = itemView.findViewById(R.id.item_name);
            qty = itemView.findViewById(R.id.quantity);
            subtotal = itemView.findViewById(R.id.subtotal);


        }

    }
}
