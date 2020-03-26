package greens.amitech.com.greens.adapter;

import android.content.Context;
import android.content.Intent;
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
import greens.amitech.com.greens.model.MyOrders;
import greens.amitech.com.greens.MyOrdersActivity;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.MyViewHolder> {

    public Context mcontext;
    public List<MyOrders> myOrdersList;



    public MyOrdersAdapter(MyOrdersActivity mcontext, List<MyOrders> myOrdersList) {

        this.mcontext=mcontext;
        this.myOrdersList = myOrdersList;


    }


    @NonNull
    @Override
    public MyOrdersAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item_view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_layout, parent, false);


        return new MyOrdersAdapter.MyViewHolder(item_view);

    }


    @Override
    public void onBindViewHolder(@NonNull final MyOrdersAdapter.MyViewHolder holder, final int position) {

        final MyOrders item_model = myOrdersList.get(position);
        holder.order_id.setText(item_model.getOrder_number());

        holder.total_amount.setText(String.format("%s%s", mcontext.getString(R.string.rupee_symbol), item_model.getTotal_amt()));
        holder.purchase_date.setText(item_model.getOrder_date());
        holder.delivery_date.setText(item_model.getDdate());
        holder.item_total.setText(String.format("%s%s", mcontext.getString(R.string.rupee_symbol),item_model.getItemTotal()));
        holder.offer_amt.setText(String.format("%s%s",item_model.getItemOffer(), mcontext.getString(R.string.percentage_symbol)));
        holder.discount_total.setText(String.format("%s%s%s", "-", mcontext.getString(R.string.rupee_symbol),
                item_model.getTotalDiscount()));

        final Intent intent=new Intent(mcontext, OrderDetailsActivity.class);
        intent.putExtra("oid",item_model.getOrder_number());

        holder.view_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mcontext.startActivity(intent);

            }
        });
    }




    @Override
    public int getItemCount() {
        return myOrdersList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView order_id,total_amount,purchase_date,delivery_date, offer_amt, discount_total, item_total;
        public Button view_items;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            order_id=itemView.findViewById(R.id.order_number);
            total_amount=itemView.findViewById(R.id.total_amount);
            purchase_date=itemView.findViewById(R.id.purchase_date);
            delivery_date=itemView.findViewById(R.id.delivery_date);
            view_items=itemView.findViewById(R.id.view_items);

            item_total = itemView.findViewById(R.id.item_total);
            offer_amt = itemView.findViewById(R.id.offer_price);
            discount_total = itemView.findViewById(R.id.total_discount);

        }

    }
}
