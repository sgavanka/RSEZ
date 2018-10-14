package app.rsez;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class TabsFragment extends Fragment  {
   private SectionsPageAdapter sectionsPageAdapter;
   private ViewPager viewPager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tabs, container, false);
        sectionsPageAdapter = new SectionsPageAdapter(getChildFragmentManager());
        viewPager = (ViewPager) view.findViewById(R.id.container);
        setupViewPager(viewPager);
        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        return view;
    }
    private void setupViewPager(final ViewPager viewPager) {
        final SectionsPageAdapter adapter = new SectionsPageAdapter(getChildFragmentManager());
        adapter.addFragment(new TabFragment1(), "Hosting");
        adapter.addFragment(new TabFragment2(), " My Events");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                Fragment frag = adapter.getItem(position);
                System.out.println("reload");
                setFragment(frag);
            }
        });
    }


    public void setFragment(Fragment fragment) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.detach(fragment).attach(fragment).commit();
    }
}


