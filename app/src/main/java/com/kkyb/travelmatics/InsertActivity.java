package com.kkyb.travelmatics;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InsertActivity extends AppCompatActivity {

  private FirebaseDatabase firebaseDatabase;
  private DatabaseReference databaseReference;

  private EditText txtTitle;
  private EditText txtPrice;
  private EditText txtDescription;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_insert);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    firebaseDatabase = FirebaseDatabase.getInstance();
    databaseReference = firebaseDatabase.getReference().child("traveldeals");

    txtTitle = findViewById(R.id.txtTitle);
    txtPrice = findViewById(R.id.txtPrice);
    txtDescription = findViewById(R.id.txtDescription);
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
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void saveDeal() {
    String title = txtTitle.getText().toString();
    String description = txtDescription.getText().toString();
    String price = txtPrice.getText().toString();

    TravelDeal travelDeal = new TravelDeal(title, description, price, "");
    databaseReference.push().setValue(travelDeal);
  }

  private void clean() {
    txtTitle.setText("");
    txtDescription.setText("");
    txtPrice.setText("");
    txtTitle.requestFocus();
  }
}
