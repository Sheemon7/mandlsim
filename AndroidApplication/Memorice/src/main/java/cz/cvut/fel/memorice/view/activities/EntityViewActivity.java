package cz.cvut.fel.memorice.view.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.logging.Logger;

import cz.cvut.fel.memorice.R;
import cz.cvut.fel.memorice.view.activities.input.DictionaryInputActivity;
import cz.cvut.fel.memorice.view.activities.input.SequenceInputActivity;
import cz.cvut.fel.memorice.view.activities.input.SetInputActivity;
import cz.cvut.fel.memorice.view.fragments.DividerItemDecoration;
import cz.cvut.fel.memorice.view.fragments.EntityListAdapter;

/**
 * Created by sheemon on 14.4.16.
 */
public class EntityViewActivity extends AppCompatActivity {
    private static final Logger LOG = Logger.getLogger(EntityViewActivity.class.getName());

    private static final int ANIMATION_DURATION = 200;
    private static final int FAB_HIDE_DURATION = 3000;
    private static final int FAB_ANIMATION_OFFSET = 150;

    private RecyclerView mRecyclerView;
    private EntityListAdapter mAdapter;
    private FloatingActionsMenu fabMenu;

    private Thread fabHideThread;
    private View shadowView;

    /* listeners */
    private RecyclerView.OnScrollListener onScrollListener = new CustomOnScrollListener();
    private View.OnClickListener onClickListener = new CustomOnClickListener();
    private SearchView.OnCloseListener onCloseListener = new CustomOnCloseListener();
    private SearchView.OnQueryTextListener onQueryTextListener = new CustomOnQueryTextChangeListener();
    private FloatingActionsMenu.OnFloatingActionsMenuUpdateListener onFloatingActionsMenuUpdateListener = new CustomOnFloatingActionsMenuChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity_list);
        Toolbar toolbar =
                (Toolbar) findViewById(R.id.entry_toolbar);
        setSupportActionBar(toolbar);

        prepareSwitch();
        prepareRecyclerView();
        prepareFAB();
        prepareFABHideThread();
        fabHideThread.start();

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.showAll(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sets, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnSearchClickListener(onClickListener);
        searchView.setOnCloseListener(onCloseListener);
        searchView.setOnQueryTextListener(onQueryTextListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent;
        switch (item.getItemId()) {
            case R.id.action_settings:
                myIntent = new Intent(EntityViewActivity.this, SettingsActivity.class);
                EntityViewActivity.this.startActivity(myIntent);
                return true;
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_help:
                myIntent = new Intent(EntityViewActivity.this, HelpActivity.class);
                EntityViewActivity.this.startActivity(myIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void prepareSwitch() {
        final Switch switchFav = (Switch) findViewById(R.id.switch_fav);
        final ImageView indicator = (ImageView) findViewById(R.id.fav_indicator);
        if (switchFav != null) {
            switchFav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mAdapter.showFavorites(getApplicationContext());
                        indicator.setImageResource(R.drawable.ic_fav_white_fill_24dp);
                    } else {
                        mAdapter.showAll(getApplicationContext());
                        indicator.setImageResource(R.drawable.ic_fav_outline_24dp);
                    }
                }
            });
        }
    }

    private void prepareFABHideThread() {
        if (fabHideThread != null && fabHideThread.isAlive()) {
            fabHideThread.interrupt();
        }
        fabHideThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(FAB_HIDE_DURATION);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!fabMenu.isExpanded()) {
                                fadeOut(fabMenu);
                            }
                        }
                    });
                } catch (InterruptedException e) {
                    LOG.info("Interrupting fab hide thread");
                }
            }
        });
    }

    private void prepareShadowView() {
        shadowView = findViewById(R.id.shadowView);
        shadowView.bringToFront();
        shadowView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                fabMenu.collapse();
                return true;
            }
        });
        shadowView.setAlpha(0.6f);
        fabMenu.bringToFront();
    }

    private void prepareRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(onScrollListener);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new EntityListAdapter(mRecyclerView);
        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(getApplicationContext(), R.drawable.separator));
        mAdapter.showAll(getApplicationContext());
    }

    private void prepareFAB() {
        fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        fabMenu.setOnFloatingActionsMenuUpdateListener(onFloatingActionsMenuUpdateListener);
        prepareFABActionsMenu();
    }

    private void prepareFABActionsMenu() {
        findViewById(R.id.fab_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(EntityViewActivity.this, SequenceInputActivity.class);
                EntityViewActivity.this.startActivity(myIntent);
            }
        });
        findViewById(R.id.fab_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(EntityViewActivity.this, SetInputActivity.class);
                EntityViewActivity.this.startActivity(myIntent);
            }
        });
        findViewById(R.id.fab_dictionary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(EntityViewActivity.this, DictionaryInputActivity.class);
                EntityViewActivity.this.startActivity(myIntent);
            }
        });
    }

    private void fadeOut(final View view, final int offset) {
        AlphaAnimation fadeOut = new AlphaAnimation(view.getAlpha(), 0);
        fadeOut.setStartOffset(offset);
        carryOutAnimation(view, fadeOut);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(ANIMATION_DURATION + offset);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view.setEnabled(false);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void fadeIn(View view, int offset) {
        AlphaAnimation fadeIn = new AlphaAnimation(view.getAlpha(), 1);
        fadeIn.setStartOffset(offset);
        carryOutAnimation(view, fadeIn);
        view.setEnabled(true);
    }

    private void fadeIn(View view) {
        fadeIn(view, 0);
    }

    private void fadeOut(View view) {
        fadeOut(view, 0);
    }

    /* listeners */

    private void carryOutAnimation(View view, AlphaAnimation animation) {
        animation.setDuration(ANIMATION_DURATION);
        animation.setFillAfter(true);
        view.startAnimation(animation);
    }

    private class CustomOnScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                fadeIn(fabMenu, FAB_ANIMATION_OFFSET);
                if (fabHideThread.isAlive()) {
                    fabHideThread.interrupt();
                }
            } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                prepareFABHideThread();
                fabHideThread.start();
            }
        }
    }

    private class CustomOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            mRecyclerView.removeOnScrollListener(onScrollListener);
            if (fabHideThread.isAlive()) {
                fabHideThread.interrupt();
            }
            fadeOut(fabMenu);
        }
    }

    private class CustomOnCloseListener implements SearchView.OnCloseListener {

        @Override
        public boolean onClose() {
            mRecyclerView.addOnScrollListener(onScrollListener);
            return false;
        }
    }

    private class CustomOnQueryTextChangeListener implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            return onQueryTextChange(query);
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            mAdapter.filter(newText, getApplicationContext());
            return true;
        }
    }

    private class CustomOnFloatingActionsMenuChangeListener implements FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {

        @Override
        public void onMenuExpanded() {
            prepareShadowView();
            shadowView.setVisibility(View.VISIBLE);
            fadeIn(shadowView);
        }

        @Override
        public void onMenuCollapsed() {
            fadeOut(shadowView);
            shadowView.setVisibility(View.INVISIBLE);
            shadowView.setAlpha(0f);
            if (fabHideThread.isAlive()) {
                fabHideThread.interrupt();
            }
            prepareFABHideThread();
            fabHideThread.start();
        }
    }
}
