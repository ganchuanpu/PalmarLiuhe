package com.liuheonline.la.ui.main.trend;

import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ysyy.aini.palmarliuhe.R;
import com.liuheonline.la.entity.SizeEntity;
import com.liuheonline.la.mvp.presenter.SizePresenter;
import com.liuheonline.la.ui.base.BaseMVPActivity;
import com.liuheonline.la.ui.widget.CustomDatePicker;
import com.yxt.itv.library.base.BaseView;
import com.yxt.itv.library.ioc.BindId;
import com.yxt.itv.library.navigationbar.DefaultNavigationBar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SizeTrend extends BaseMVPActivity<BaseView<List<SizeEntity>>,SizePresenter,List<SizeEntity>> implements  SwipeRefreshLayout.OnRefreshListener,BaseQuickAdapter.RequestLoadMoreListener{

    @BindId(R.id.size_refresh)
    private SwipeRefreshLayout swipeRefreshLayout;
    @BindId(R.id.size_recycler)
    private RecyclerView recyclerView;
    private BaseQuickAdapter<SizeEntity,BaseViewHolder> baseQuickAdapter;
    int p = 1;
    CustomDatePicker customDatePicker;
    int year = Calendar.getInstance().get(Calendar.YEAR);
    @Override
    protected void initPresenter() {
        presenter = new SizePresenter();
    }

    @Override
    protected void fetchData() {
        swipeRefreshLayout.setRefreshing(true);
        presenter.getSize(year,p,10,getIntent().getExtras().getInt("lotteryid"));
    }


    @Override
    protected void initData() {

        baseQuickAdapter = new BaseQuickAdapter<SizeEntity, BaseViewHolder>(R.layout.item_size_trend) {
            @Override
            protected void convert(BaseViewHolder helper, SizeEntity data) {
                helper.setText(R.id.size_periods,data.getPeriods()+"");
                helper.setText(R.id.size_num1,data.getBig()+"");
                helper.setText(R.id.size_num2,data.getSmall()+"");

                helper.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
                if (helper.getAdapterPosition()%2==0){
                    helper.itemView.setBackgroundColor(Color.parseColor("#efefef"));
                }

                helper.setBackgroundRes(R.id.size_num1,R.drawable.text_solid_none_shape);
                helper.setTextColor(R.id.size_num1,getResources().getColor(R.color.deep_gray));
                helper.setBackgroundRes(R.id.size_num2,R.drawable.text_solid_none_shape);
                helper.setTextColor(R.id.size_num2,getResources().getColor(R.color.deep_gray));

                switch (data.getWin_result()){
                    case "big":
                        helper.setBackgroundRes(R.id.size_num1,R.drawable.text_solid_blue_shape);
                        helper.setTextColor(R.id.size_num1,getResources().getColor(R.color.white));
                        break;
                    case "small":
                        helper.setBackgroundRes(R.id.size_num2,R.drawable.text_solid_blue_shape);
                        helper.setTextColor(R.id.size_num2,getResources().getColor(R.color.white));
                        break;

                }
            }
        };
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_sizetrend);
    }

    @Override
    protected void initTitle() {
        initDatePicker();
        new DefaultNavigationBar.Builder(this)
                .setTitle("大小走势")
                .setLeftIconVisibility(false)
                .setRightText("年份")
                .setRightClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        customDatePicker.show();
                    }
                })
                .builder();
    }

    @Override
    protected void initView() {
        swipeRefreshLayout.setOnRefreshListener(this);
        baseQuickAdapter.setOnLoadMoreListener(this,recyclerView);
        baseQuickAdapter.disableLoadMoreIfNotFullPage();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(baseQuickAdapter);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onLoading() {

    }

    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        customDatePicker = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
              String stryear = time.split(" ")[0];
              year = Integer.parseInt(stryear.substring(0,4));
                Log.w("the year",year+"");
                p = 1;
                presenter.getSize(year,p,10,getIntent().getExtras().getInt("lotteryid"));

            }
        }, "2010-01-01 00:00", now); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker.showSpecificTime(false); // 不显示时和分
        customDatePicker.setIsLoop(false); // 不允许循环滚动
    }
    @Override
    public void onLoadFailed(int code, String error) {
        super.onLoadFailed(code, error);
        swipeRefreshLayout.setRefreshing(false);
        baseQuickAdapter.loadMoreFail();
    }

    @Override
    public void successed(List<SizeEntity> sizeEntities) {
        swipeRefreshLayout.setRefreshing(false);
        if(p == 1){
            if(sizeEntities != null && sizeEntities.size() > 0){
                baseQuickAdapter.setNewData(sizeEntities);
               // baseQuickAdapter.replaceData(footerEntities);
                if(sizeEntities.size()  < 10){
                    baseQuickAdapter.loadMoreEnd();
                }
            }else{
                baseQuickAdapter.setNewData(sizeEntities);//空数据
            }
        }else{
            if(sizeEntities != null && sizeEntities.size() == 10){
                baseQuickAdapter.loadMoreComplete();
            }else{
                baseQuickAdapter.loadMoreEnd();
            }
            baseQuickAdapter.addData(sizeEntities);
        }

    }


    @Override
    public void onRefresh() {
        p = 1;
        presenter.getSize(year,p,10,getIntent().getExtras().getInt("lotteryid"));
    }

    @Override
    public void onLoadMoreRequested() {
        p ++;
        presenter.getSize(year,p,10,getIntent().getExtras().getInt("lotteryid"));
    }
}
