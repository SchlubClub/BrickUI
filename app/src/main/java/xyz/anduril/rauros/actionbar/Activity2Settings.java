package xyz.anduril.rauros.actionbar;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by Rauros on 7/10/2017.
 */

public class Activity2Settings extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        TextView title = (TextView) findViewById(R.id.settingsTitle);
        title.setText("Placeholder for settings.");

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ic_player:
                        Intent intentMap = new Intent(Activity2Settings.this, MainActivity.class);
                        startActivity(intentMap);
                        break;
                    case R.id.ic_map:
                        Intent intentSettings = new Intent(Activity2Settings.this, Activity1Map.class);
                        startActivity(intentSettings);
                        break;
                    case R.id.ic_settings:

                        break;
                }
                return false;
            }
        });
    }
}
