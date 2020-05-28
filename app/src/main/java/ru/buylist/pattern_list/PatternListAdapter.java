package ru.buylist.pattern_list;

import androidx.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.List;

import ru.buylist.R;
import ru.buylist.data.entity.Item;
import ru.buylist.databinding.ItemPatternAndRecipeBinding;

import static ru.buylist.utils.ItemClickCallback.*;

public class PatternListAdapter extends RecyclerSwipeAdapter<PatternListAdapter.PatternListHolder> {

    private final ItemCallback callback;
    private final SwipeLayout.SwipeListener swipeListener;
    private List<Item> items;

    public PatternListAdapter(ItemCallback callback, SwipeLayout.SwipeListener swipeListener) {
        this.callback = callback;
        this.swipeListener = swipeListener;
    }

    @Override
    public PatternListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemPatternAndRecipeBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_pattern_and_recipe,
                parent, false);
        binding.setCallback(callback);
        return new PatternListHolder(binding);
    }

    @Override
    public void onBindViewHolder(PatternListHolder holder, int position) {
        Item item = items.get(position);
        holder.bind(item);

        mItemManger.bindView(holder.itemView, position);
        holder.binding.layoutSwipeItem.addSwipeListener(swipeListener);
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.layout_swipe_item;
    }

    public void setItems(List<Item> items) {
        this.items = items;
        notifyDataSetChanged();
    }


    /**
     * Holder
     */
    static class PatternListHolder extends RecyclerView.ViewHolder {
        ItemPatternAndRecipeBinding binding;
        Item item;

        PatternListHolder(ItemPatternAndRecipeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Item item) {
            this.item = item;
            binding.setItem(item);
//            bindColor(item);
            binding.layoutSwipeItem.setShowMode(SwipeLayout.ShowMode.PullOut);
            binding.layoutSwipeItem.addDrag(SwipeLayout.DragEdge.Right, binding.layoutBottomSwipe);
            binding.cardTopItemSwipe.setBackgroundColor(0);
            binding.executePendingBindings();
        }

//        void bindColor(Item item) {
//            if (item.getCategoryColor() == null) {
//                binding.imgCategoryCircle.setColorFilter(Color.parseColor(CategoryInfo.COLOR));
//            } else {
//                binding.imgCategoryCircle.setColorFilter(Color.parseColor(item.getCategoryColor()));
//            }
//        }

    }
}
