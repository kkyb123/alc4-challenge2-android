package com.kkyb.travelmatics;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DealActivity extends AppCompatActivity {

  private FirebaseDatabase firebaseDatabase;
  private DatabaseReference databaseReference;

  private EditText txtTitle;
  private EditText txtPrice;
  private EditText txtDescription;

  private TravelDeal deal;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_insert);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    firebaseDatabase = FirebaseUtil.firebaseDatabase;
    databaseReference = FirebaseUtil.databaseReference;

    txtTitle = findViewById(R.id.txtTitle);
    txtPrice = findViewById(R.id.txtPrice);
    txtDescription = findViewById(R.id.txtDescription);

    Intent intent = getIntent();
    TravelDeal travelDeal = (TravelDeal) intent.getSerializableExtra("Deal");
    deal = travelDeal != null ? travelDeal : new TravelDeal();

    txtTitle.setText(deal.getTitle());
    txtPrice.setText(deal.getPrice());
    txtDescription.setText(deal.getDescription());
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.save_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    if (id == R.id.save_menu) {
      saveDeal();
      Toast.makeText(this, "Deal Saved", Toast.LENGTH_LONG).show();
      clean();
      backToList();
      return true;
    }

    if (id == R.id.delete_menu) {
      deletDeal();
      Toast.makeText(this, "Deal Deleted", Toast.LENGTH_LONG).show();
      backToList();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void saveDeal() {
    deal.setTitle(txtTitle.getText().toString());
    deal.setDescription(txtDescription.getText().toString());
    deal.setPrice(txtPrice.getText().toString());

    if (deal.getId() == null) {
      databaseReference.push().setValue(deal);
      return;
    }

    databaseReference.child(deal.getId()).setValue(deal);
  }

  private void deletDeal() {
    if (deal == null || deal.getId() == null) {
      Toast.makeText(this, "Please save deal before deleting", Toast.LENGTH_LONG).show();
    }

    databaseReference.child(deal.getId()).removeValue();
  }

  private void backToList() {
    startActivity(new Intent(this, ListActivity.class));
  }

  private void clean() {
    txtTitle.setText("");
    txtDescription.setText("");
    txtPrice.setText("");
    txtTitle.requestFocus();
  }
}
