package com.d.lib.common.widget.dialog;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.d.lib.common.R;
import com.d.lib.common.widget.toggle.ToggleButton;
import com.d.lib.pulllayout.rv.adapter.CommonAdapter;
import com.d.lib.pulllayout.rv.adapter.CommonHolder;
import com.d.lib.pulllayout.rv.adapter.MultiItemTypeSupport;

import java.util.List;

/**
 * BottomShareSheetDialog
 * Created by D on 2017/7/27.
 */
public class BottomShareSheetDialog extends AbsSheetDialog<BottomShareSheetDialog.Bean> {
    private TextView tv_title;

    public BottomShareSheetDialog(Context context, String title, List<Bean> datas) {
        super(context);
        this.mTitle = title;
        this.mDatas = datas;
        bindView(mRootView);
        init();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.lib_pub_dialog_bottom_style_share;
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        return new SheetAdapter(mContext, mDatas, new MultiItemTypeSupport<Bean>() {
            @Override
            public int getLayoutId(int viewType) {
                if (viewType == Bean.TYPE_CONTENT) {
                    return R.layout.lib_pub_adapter_dlg_bottom_share_content;
                } else if (viewType == Bean.TYPE_PASSWORD) {
                    return R.layout.lib_pub_adapter_dlg_bottom_share_psw;
                }
                return R.layout.lib_pub_adapter_dlg_bottom_share_content;
            }

            @Override
            public int getItemViewType(int position, Bean bean) {
                return bean.type;
            }
        });
    }

    @Override
    protected void bindView(View rootView) {
        tv_title = (TextView) rootView.findViewById(R.id.tv_title);
        TextView tvCancel = (TextView) rootView.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(-1, null);
            }
        });
    }

    @Override
    protected void init() {
        if (!TextUtils.isEmpty(mTitle)) {
            tv_title.setVisibility(View.VISIBLE);
            tv_title.setText(mTitle);
        } else {
            tv_title.setVisibility(View.GONE);
        }
        initRecyclerList(R.id.rv_list, LinearLayoutManager.VERTICAL);
    }

    public class SheetAdapter extends CommonAdapter<Bean> {

        SheetAdapter(Context context, List<Bean> datas, MultiItemTypeSupport<Bean> multiItemTypeSupport) {
            super(context, datas, multiItemTypeSupport);
        }

        @Override
        public void convert(final int position, CommonHolder holder, final Bean item) {
            if (holder.layoutId == R.layout.lib_pub_adapter_dlg_bottom_share_content) {
                holder.setText(R.id.tv_title, item.title);
                holder.setText(R.id.tv_content, item.content);
            } else if (holder.layoutId == R.layout.lib_pub_adapter_dlg_bottom_share_psw) {
                holder.setText(R.id.tv_title, item.title);
                holder.setText(R.id.tv_psw, item.password);
                ToggleButton toggleButton = holder.getView(R.id.tbtn_toggle);
                toggleButton.setOpen(item.isChecked);
            }
        }
    }

    public static class Bean {
        public static final int TYPE_CONTENT = 0;
        public static final int TYPE_PASSWORD = 1;

        public int type;
        public String title;
        public String content;
        public String password;
        public boolean isChecked;

        public Bean(String title, String content) {
            this.type = TYPE_CONTENT;
            this.title = title;
            this.content = content;
        }

        public Bean(String title, String password, boolean isChecked) {
            this.type = TYPE_PASSWORD;
            this.title = title;
            this.password = password;
            this.isChecked = isChecked;
        }
    }
}
