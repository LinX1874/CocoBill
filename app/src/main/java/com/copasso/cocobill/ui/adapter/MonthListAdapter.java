package com.copasso.cocobill.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.copasso.cocobill.R;
import com.copasso.cocobill.model.bean.local.BBill;
import com.copasso.cocobill.model.bean.local.MonthListBean;
import com.copasso.cocobill.utils.ImageUtils;
import com.copasso.cocobill.widget.SwipeMenuView;
import com.copasso.cocobill.widget.stickyheader.StickyHeaderGridAdapter;

import java.util.List;

import androidx.appcompat.app.AlertDialog;

/**
 * 悬浮头部项
 * 可侧滑编辑、删除
 */
public class MonthListAdapter extends StickyHeaderGridAdapter {

    private Context mContext;

    private OnStickyHeaderClickListener onStickyHeaderClickListener;

    private List<MonthListBean.DaylistBean> mDatas;

    public MonthListAdapter(Context context, List<MonthListBean.DaylistBean> datas) {
        this.mContext = context;
        this.mDatas = datas;
    }

    public void setmDatas(List<MonthListBean.DaylistBean> mDatas) {
        this.mDatas = mDatas;
    }

    public void setOnStickyHeaderClickListener(OnStickyHeaderClickListener listener) {
        if (onStickyHeaderClickListener == null)
            this.onStickyHeaderClickListener = listener;
    }

    public void remove(int section, int offset) {
        mDatas.get(section).getList().remove(offset);
        notifySectionItemRemoved(section, offset);
    }

    public void clear() {
        this.mDatas = null;
        notifyAllSectionsDataSetChanged();
    }

    @Override
    public int getSectionCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public int getSectionItemCount(int section) {
        return (mDatas == null || mDatas.get(section).getList() == null) ? 0 : mDatas.get(section).getList().size();
    }

    @Override
    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int headerType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_monthlist_header, parent, false);
        return new MyHeaderViewHolder(view);
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent, int itemType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_monthlist_item, parent, false);
        return new MyItemViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderViewHolder viewHolder, int section) {
        final MyHeaderViewHolder holder = (MyHeaderViewHolder) viewHolder;
        holder.header_date.setText(mDatas.get(section).getTime());
        holder.header_money.setText(mDatas.get(section).getMoney());
    }

    @Override
    public void onBindItemViewHolder(ItemViewHolder viewHolder, final int section, final int position) {
        final MyItemViewHolder holder = (MyItemViewHolder) viewHolder;

        BBill bBill = mDatas.get(section).getList().get(position);
        holder.item_title.setText(bBill.getSortName());
        holder.item_img.setImageDrawable(ImageUtils.getDrawable(bBill.getSortImg()));
        if (bBill.isIncome()) {
            holder.item_money.setText("+" + bBill.getCost());
        } else {
            holder.item_money.setText("-" + bBill.getCost());
        }

        //监听侧滑删除事件
        holder.item_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int section = getAdapterPositionSection(holder.getAdapterPosition());
                final int offset = getItemSectionOffset(section, holder.getAdapterPosition());

//                确认删除
                new AlertDialog.Builder(mContext).setTitle("是否删除此条记录")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onStickyHeaderClickListener
                                        .OnDeleteClick(mDatas.get(section).getList().get(offset), section, offset);
                            }
                        })
                        .show();
            }
        });
        //监听侧滑编辑事件
        holder.item_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int section = getAdapterPositionSection(holder.getAdapterPosition());
                final int offset = getItemSectionOffset(section, holder.getAdapterPosition());
                onStickyHeaderClickListener.OnEditClick(
                        mDatas.get(section).getList().get(offset), section, offset);
            }
        });
        //监听单击显示详情事件
        holder.item_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(mContext)
                        .title(bBill.getSortName())
                        .content("\t\t" + Math.abs(bBill.getCost()) + "元\n\t\t" + bBill.getContent())
                        .positiveText("朕知道了")
                        .icon(ImageUtils.getDrawable(bBill.getSortImg()))
                        .limitIconToDefaultSize()
                        .show();
            }
        });
    }

    /**
     * 自定义编辑、删除接口
     */
    public interface OnStickyHeaderClickListener {
        void OnDeleteClick(BBill item, int section, int offset);

        void OnEditClick(BBill item, int section, int offset);
    }

    public static class MyHeaderViewHolder extends HeaderViewHolder {
        TextView header_date;
        TextView header_money;

        MyHeaderViewHolder(View itemView) {
            super(itemView);
            header_date = (TextView) itemView.findViewById(R.id.header_date);
            header_money = (TextView) itemView.findViewById(R.id.header_money);
        }
    }

    public static class MyItemViewHolder extends ItemViewHolder {
        TextView item_title;
        TextView item_money;
        Button item_delete;
        Button item_edit;
        ImageView item_img;
        RelativeLayout item_layout;
        SwipeMenuView mSwipeMenuView;

        MyItemViewHolder(View itemView) {
            super(itemView);
            item_title = (TextView) itemView.findViewById(R.id.item_title);
            item_money = (TextView) itemView.findViewById(R.id.item_money);
            item_delete = (Button) itemView.findViewById(R.id.item_delete);
            item_edit = (Button) itemView.findViewById(R.id.item_edit);
            item_img = (ImageView) itemView.findViewById(R.id.item_img);
            item_layout = (RelativeLayout) itemView.findViewById(R.id.item_layout);
            mSwipeMenuView = (SwipeMenuView) itemView.findViewById(R.id.swipe_menu);
        }
    }
}