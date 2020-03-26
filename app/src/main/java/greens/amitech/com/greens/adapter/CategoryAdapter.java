package greens.amitech.com.greens.adapter;

import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import greens.amitech.com.greens.fragments.DynamicTabFragment;
import greens.amitech.com.greens.model.Category;

public class CategoryAdapter extends FragmentStateAdapter {
    private static final String TAG = "CategoryAdapter";

    private List<Category> categoryList;
    private TextView totalTextView;
    private RelativeLayout bottomLayoutView;

    public CategoryAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }



    public void setCategoryList(List<Category> categoryList){
        this.categoryList = categoryList;
    }

    public void setTotalTextView(TextView totalTextView){
        this.totalTextView = totalTextView;
    }

    public void setBottomLayoutView(RelativeLayout bottomLayoutView){
        this.bottomLayoutView = bottomLayoutView;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return DynamicTabFragment.newInstance(position, categoryList.get(position).getCategoryId(), totalTextView, bottomLayoutView);
    }

    @Override
    public int getItemCount() {
        if (categoryList != null) {
            return categoryList.size();
        } else {
            return 0;
        }
    }


}
