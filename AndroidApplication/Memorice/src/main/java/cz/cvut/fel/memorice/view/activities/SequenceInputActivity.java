package cz.cvut.fel.memorice.view.activities;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import cz.cvut.fel.memorice.R;
import cz.cvut.fel.memorice.model.database.SQLiteHelper;
import cz.cvut.fel.memorice.model.entities.builders.SequenceBuilder;
import cz.cvut.fel.memorice.model.entities.entries.Entry;
import cz.cvut.fel.memorice.model.entities.entries.SequenceEntry;
import cz.cvut.fel.memorice.model.util.EmptyNameException;
import cz.cvut.fel.memorice.model.util.NameAlreadyUsedException;

/**
 * Created by sheemon on 18.4.16.
 */
public class SequenceInputActivity extends InputActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequence_input);

        Toolbar toolbar =
                (Toolbar) findViewById(R.id.input_toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorInvertDarker));
        }

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_cross_white_24dp);
        prepareInputLabels();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: // Intercept the click on the home button
                finish();
                return true;
            case R.id.action_save:
                try {
                    buildNewSequence();
                    finish();
                } catch (NameAlreadyUsedException e) {
                    showLabelUsedDialog(new AlertDialog.Builder(SequenceInputActivity.this));
                } catch (EmptyNameException e) {
                    showLabelEmptyDialog(new AlertDialog.Builder(SequenceInputActivity.this));
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private void buildNewSequence() throws NameAlreadyUsedException, EmptyNameException {
        EditText labelInput = (EditText) findViewById(R.id.entry_title);
        String label = labelInput.getText().toString();
        SQLiteHelper helper = new SQLiteHelper(getApplicationContext());
        if (helper.getEntity(label) != null || label.length() == 0) {
            throw new NameAlreadyUsedException();
        }
        if (label.length() == 0) {
            throw new EmptyNameException();
        }
        SequenceBuilder builder = SequenceBuilder.getInstance();
        builder.init(label);
        builder.add(new SequenceEntry("A", 0));
        builder.add(new SequenceEntry("A", 1));
        builder.add(new SequenceEntry("A", 2));
        builder.add(new SequenceEntry("A", 3));
        builder.add(new SequenceEntry("A", 4));
        builder.add(new SequenceEntry("A", 5));

        helper.addEntity(builder.wrap());
    }
}
