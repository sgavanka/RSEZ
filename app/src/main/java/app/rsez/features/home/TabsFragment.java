package app.rsez.features.home;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.rsez.R;

public class TabsFragment extends Fragment  {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tabs, container, false);

        ViewPager viewPager;
        setupViewPager(viewPager = view.findViewById(R.id.container));
        ((TabLayout) view.findViewById(R.id.tabs)).setupWithViewPager(viewPager);

        return view;
    }

    private void setupViewPager(final ViewPager viewPager) {
        final SectionsPageAdapter adapter = new SectionsPageAdapter(getChildFragmentManager());
        adapter.addFragment(new HostingTabFragment(), "Hosting");
        adapter.addFragment(new AttendingTabFragment(), " Attending");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) { }
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageSelected(int position) { }
        });
    }
}


