package com.zejian.myapplication.anima.leochuan.gallery;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.zejian.myapplication.R;

/**
 * Created by Dajavu on 27/10/2017.
 */

public class GalleryLayoutActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerView = findViewById(R.id.recycler);
        viewPagerLayoutManager = createLayoutManager();
        DataAdapter dataAdapter = new DataAdapter();
        dataAdapter.setOnItemClickListener(new DataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Toast.makeText(v.getContext(), "clicked:" + pos, Toast.LENGTH_SHORT).show();
                ScrollHelper.smoothScrollToTargetView(recyclerView, v);
            }
        });
        recyclerView.setAdapter(dataAdapter);
        recyclerView.setLayoutManager(viewPagerLayoutManager);
    }
}
