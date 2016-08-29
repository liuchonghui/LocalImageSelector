package tool.imagedownloader.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.imageloadercompact.CompactImageView;
import com.android.imageloadercompact.ImageLoaderCompact;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

import tool.imagedownloader.android.ConvertListToGridAdapter;
import tool.imagedownloader.android.PicEnum;
import tool.imagedownloader.test.R;
import tools.android.imagedownloader.ImageDownloadManager;

/**
 * @author liu_chonghui
 */
public class YongLiFragment extends BaseFragment {

    protected int getPageLayout() {
        return R.layout.activity_yangli;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startTransaction();

        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected void startTransaction() {
        getActivity().overridePendingTransition(R.anim.push_left_in,
                R.anim.push_still);
    }

    protected void initData() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (!isViewNull()) {
            return mView;
        }
        mView = inflater.inflate(getPageLayout(), container, false);
        intView(mView);
        return mView;
    }

    ListView mListView;
    PicEnumListAdapter picListAdapter;
    ConvertListToGridAdapter agentAdapter;

    @SuppressLint("InflateParams")
    protected void intView(View view) {
        clearCacheFiles();

        mListView = (ListView) view.findViewById(R.id.list);
        mListView.addHeaderView(LayoutInflater.from(getActivity()).inflate(
                R.layout.header_blank, null));
        picListAdapter = new PicEnumListAdapter(getActivity());
        picListAdapter.setData(Arrays.asList(PicEnum.values()));
        agentAdapter = new ConvertListToGridAdapter(getActivity(),
                ConvertListToGridAdapter.COLUMN.SIZE_3, picListAdapter);
        agentAdapter.setLeftMargin(dp2px(getActivity(), 10));
        agentAdapter.setRightMargin(dp2px(getActivity(), 10));
        mListView.setAdapter(agentAdapter);
    }



    private void clearCacheFiles() {
        String cachePath = ImageDownloadManager.getInstance().getDownloadCacheDir(getActivity());
        File cacheDir = new File(cachePath);
        if (!cacheDir.exists()) {
            return;
        }
        File[] files = cacheDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String filename) {
                return true;
            }
        });
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file != null && file.exists()) {
                    file.delete();
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    boolean firstResume = true;

    @Override
    public void onResume() {
        super.onResume();
        if (firstResume) {
            firstResume = false;
        }
        if (!firstResume) {
        }
    }

    public boolean holdGoBack() {
        // if (myOneKeyShare != null && myOneKeyShare.isShow()) {
        // return true;
        // }
        // if (popupAttacher != null && popupAttacher.isShowing()) {
        // return true;
        // }
        return false;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean flag = false;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (holdGoBack()) {
                // if (myOneKeyShare != null && myOneKeyShare.isShow()) {
                // myOneKeyShare.close();
                // }
                // if (popupAttacher != null && popupAttacher.isShowing()) {
                // popupAttacher.closePop();
                // }
                flag = true;
            }
        }
        return flag;
    }

    public void leaveCurrentPage() {
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.push_still,
                R.anim.push_right_out);
    }

    class PicEnumListAdapter extends BaseAdapter {
        Context ctx;
        int wh[] = new int[2];
        List<PicEnum> dataList;

        public PicEnumListAdapter(Context ctx) {
            this.ctx = ctx;
            DisplayMetrics metrics = ctx.getApplicationContext().getResources()
                    .getDisplayMetrics();
            int screenWidth = metrics.widthPixels;
            int px10 = dp2px(ctx, 10);
            int contentWidth = screenWidth - 4 * px10;
            int singleWidth = (int) (contentWidth / 3.0f + 0.5f);
            int singleHeight = (int) ((149.0f / 106.0f) * singleWidth + 0.5f);
            wh[0] = singleWidth;
            wh[1] = singleHeight;
        }

        public void setData(List<PicEnum> list) {
            this.dataList = list;
        }

        @Override
        public int getCount() {
            if (dataList != null) {
                return dataList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public int getItemLayout() {
            return R.layout.grid_item;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            PicEnum model = dataList.get(position);
            String id = model.name();
            String logo = model.getUrl();
            String title = model.getTitle();

            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(
                        getItemLayout(), null);
                holder.mainlayout = (RelativeLayout) convertView
                        .findViewById(R.id.main_layout);
                holder.logo = (CompactImageView) convertView
                        .findViewById(R.id.list_item_image);
                holder.title = (TextView) convertView
                        .findViewById(R.id.list_item_title);
                if (holder.logo != null) {
                    try {
                        ViewGroup.LayoutParams lp = holder.logo
                                .getLayoutParams();
                        lp.width = wh[0];
                        lp.height = wh[1];
                        holder.logo.setLayoutParams(lp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (holder.logo != null) {
                ImageLoaderCompact.getInstance().displayImage(ctx, logo, holder.logo);
            }
            if (holder.title != null) {
                holder.title.setText(title);
            }

            return convertView;
        }

        class ViewHolder {
            RelativeLayout mainlayout;
            CompactImageView logo;
            TextView title;
        }
    }

    int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
