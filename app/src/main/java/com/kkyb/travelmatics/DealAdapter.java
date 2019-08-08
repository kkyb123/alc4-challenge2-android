package com.kkyb.travelmatics;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder> {

  private FirebaseDatabase firebaseDatabase;
  private DatabaseReference databaseReference;
  private ChildEventListener childEventListener;

  private List<TravelDeal> deals;

  public DealAdapter() {

    firebaseDatabase = FirebaseUtil.firebaseDatabase;
    databaseReference = FirebaseUtil.databaseReference;
    deals = FirebaseUtil.deals;
    childEventListener = new ChildEventListener() {
      @Override
      public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        TravelDeal travelDeal = dataSnapshot.getValue(TravelDeal.class);
        if (travelDeal == null) {
          return;
        }
        travelDeal.setId(dataSnapshot.getKey());
        deals.add(travelDeal);
        notifyItemInserted(deals.size() - 1);

      }

      @Override
      public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

      }

      @Override
      public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

      }

      @Override
      public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    };

    databaseReference.addChildEventListener(childEventListener);
  }

  @NonNull
  @Override
  public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_row, parent, false);
    return new DealViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {
    holder.bind(deals.get(position));
  }

  @Override
  public int getItemCount() {
    return deals.size();
  }

  public class DealViewHolder extends RecyclerView.ViewHolder {

    TextView tvTitle;
    TextView tvDescription;
    TextView tvPrice;

    public DealViewHolder(@NonNull View itemView) {
      super(itemView);

      tvTitle = itemView.findViewById(R.id.tvTitle);
      tvDescription = itemView.findViewById(R.id.tvDescription);
      tvPrice = itemView.findViewById(R.id.tvPrice);
      itemView.setOnClickListener(this::onClick);
    }

    public void bind(TravelDeal deal) {
      tvTitle.setText(deal.getTitle());
      tvDescription.setText(deal.getDescription());
      tvPrice.setText(deal.getPrice());
    }

    private void onClick(View view) {
      TravelDeal deal = deals.get(getAdapterPosition());
      Intent intent = new Intent(view.getContext(), DealActivity.class);
      intent.putExtra("Deal", deal);
      view.getContext().startActivity(intent);
    }
  }
}
