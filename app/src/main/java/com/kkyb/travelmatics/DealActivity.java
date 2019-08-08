package com.kkyb.travelmatics;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import com.squareup.picasso.Picasso;

public class DealActivity extends AppCompatActivity {

  private FirebaseDatabase firebaseDatabase;
  private DatabaseReference databaseReference;

  private EditText txtTitle;
  private EditText txtPrice;
  private EditText txtDescription;

  private TravelDeal deal;

  private static final int PICTURE_RESULT = 42;

  private ImageView imageView;
  private Button btnImage;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_deal);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    firebaseDatabase = FirebaseUtil.firebaseDatabase;
    databaseReference = FirebaseUtil.databaseReference;

    txtTitle = findViewById(R.id.txtTitle);
    txtPrice = findViewById(R.id.txtPrice);
    txtDescription = findViewById(R.id.txtDescription);
    imageView = findViewById(R.id.image);

    Intent intent = getIntent();
    TravelDeal travelDeal = (TravelDeal) intent.getSerializableExtra("Deal");
    deal = travelDeal != null ? travelDeal : new TravelDeal();

    txtTitle.setText(deal.getTitle());
    txtPrice.setText(deal.getPrice());
    txtDescription.setText(deal.getDescription());
    showImage(deal.getImageUrl());

    btnImage = findViewById(R.id.btnImage);
    btnImage.setOnClickListener(view -> {
      Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT);
      imageIntent.setType("image/jpeg");
      imageIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

      this.startActivityForResult(Intent.createChooser(imageIntent,
          "Insert Picture"), PICTURE_RESULT);
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.save_menu, menu);
    menu.findItem(R.id.save_menu).setVisible(FirebaseUtil.isAdmin);
    menu.findItem(R.id.delete_menu).setVisible(FirebaseUtil.isAdmin);
    enableEditTexts(FirebaseUtil.isAdmin);

    if (FirebaseUtil.isAdmin) {
      btnImage.setVisibility(View.VISIBLE);
    } else {
      btnImage.setVisibility(View.INVISIBLE);
    }

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

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK) {
      Uri imageUri = data.getData();
      StorageReference ref = FirebaseUtil.storageReference.child(imageUri.getLastPathSegment());
      ref.putFile(imageUri).addOnSuccessListener(this,
          taskSnapshot -> ref.getDownloadUrl()
              .addOnSuccessListener(uri -> {
                deal.setImageUrl(uri.toString());
                deal.setImageName(taskSnapshot.getStorage().getPath());
                showImage(uri.toString());
              }));
    }
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
    if (deal.getImageName() != null && !deal.getImageName().isEmpty()) {
      Log.d("Delete", deal.getImageName());
      FirebaseUtil.firebaseStorage.getReference().child(deal.getImageName()).delete();
    }
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

  private void enableEditTexts(boolean isEnabled) {
    txtTitle.setEnabled(isEnabled);
    txtDescription.setEnabled(isEnabled);
    txtPrice.setEnabled(isEnabled);
  }

  private void showImage(String url) {
    if (url != null && !url.isEmpty()) {
      int width = Resources.getSystem().getDisplayMetrics().widthPixels;
      Picasso.get()
          .load(url)
          .resize(width, width * 2 / 3)
          .centerCrop()
          .into(imageView);
    }
  }
}
