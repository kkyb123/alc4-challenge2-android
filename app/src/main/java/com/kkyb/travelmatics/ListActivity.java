package com.kkyb.travelmatics;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;

public class ListActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    FirebaseUtil.openFbReference("traveldeals", this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (!FirebaseUtil.isAdmin) {
      invalidateOptionsMenu();
    }

    FirebaseUtil.attachAuthListener();

    RecyclerView recyclerView = findViewById(R.id.rvDeals);

    final DealAdapter dealAdapter = new DealAdapter();
    recyclerView.setAdapter(dealAdapter);

    LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
    recyclerView.setLayoutManager(layoutManager);
  }

  @Override
  protected void onPause() {
    super.onPause();
    FirebaseUtil.detachAuthListener();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.list_activity_menu, menu);
    MenuItem insertMenu = menu.findItem(R.id.insert_menu);
    insertMenu.setVisible(FirebaseUtil.isAdmin);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    if (id == R.id.insert_menu) {
      startActivity(new Intent(this, DealActivity.class));
      return true;
    }

    if (id == R.id.logout_menu) {
      AuthUI.getInstance()
          .signOut(this)
          .addOnCompleteListener(task -> FirebaseUtil.attachAuthListener());
      FirebaseUtil.detachAuthListener();
    }

    return super.onOptionsItemSelected(item);
  }

  public void showMenu() {
    invalidateOptionsMenu();
  }

}
