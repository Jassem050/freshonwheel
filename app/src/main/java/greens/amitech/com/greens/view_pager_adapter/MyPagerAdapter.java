package greens.amitech.com.greens.view_pager_adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;


import java.util.List;

import greens.amitech.com.greens.R;
import greens.amitech.com.greens.utils.NetworkUtils;

public class MyPagerAdapter extends PagerAdapter {
    Context mcontext;
    LayoutInflater layoutInflater;
    private Integer[] images = {R.drawable.freshlogo, R.drawable.freshlogo, R.drawable.freshlogo};
    private List<String> images_list;


    public MyPagerAdapter(Context activity, List<String> images) {
        mcontext = activity;
        this.images_list=images;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {


        layoutInflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.image_slider_layout, null);

        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        Log.d("coming_iamge", NetworkUtils.IMAGE_URL+ NetworkUtils.SLIDER_IMAGES_FOLDER);

        Glide.with(mcontext).load(NetworkUtils.IMAGE_URL+ NetworkUtils.SLIDER_IMAGES_FOLDER+images_list.get(position)).into(imageView);
        imageView.setImageResource(images[position]);

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);

//        TextView view = new TextView(mcontext);
//            view.setText("com.royalcart.amits.royalcart.Item "+position);
//            view.setGravity(Gravity.CENTER);
//            view.setBackgroundColor(Color.argb(255, position * 50, position * 10, position * 50));
//
//            container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images_list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public void destroyItem(@NonNull View container, int position, @NonNull Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }
}

