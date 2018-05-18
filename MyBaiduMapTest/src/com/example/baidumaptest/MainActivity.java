package com.example.baidumaptest;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;


import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends Activity {
	
	//private BMapManager manager;
	private MapView mapView;
	private BaiduMap baiduMap;
	
    
    private LocationClient mLocClient; 
    public MyLocationListener locListener = new MyLocationListener();//定位SDK监听函数
    
    //当前位置经纬度  
    private double latitude;  
    private double longitude; 
    
  //是否首次定位  
    private boolean isFirstLoc = true;  
    
  //按钮 添加覆盖物  
    private Button addOverlayBtn;  
    //是否显示覆盖物 1-显示 0-不显示  
    private int isShowOverlay = 1;  
    //按钮 定位当前位置  
    private Button locCurplaceBtn;  
    
    //定位图标描述  
    private BitmapDescriptor mCurrentMarker = null;
    //定位图层显示模式 (普通-跟随-罗盘)  
    private LocationMode mCurrentMode; 
      
   
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		//manager=new BMapManager(this);
		//manager.init("Bpye4GQwOqAExZgvkflqoo30y79RQATx", null);
		
		SDKInitializer.initialize(getApplicationContext());
		
		setContentView(R.layout.activity_main);
		
		mapView=(MapView)findViewById(R.id.map_view);
		//mapView.setBuiltInZoomControls(true);
		
		baiduMap=mapView.getMap();
		baiduMap.setMyLocationEnabled(true);  //开启定位图层
		baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(16.0f);    //设置地图缩放级别16 类型普通地图  
        baiduMap.setMapStatus(msu);
        
		mLocClient = new LocationClient(getApplicationContext());
		mLocClient.registerLocationListener(locListener);
		LocationClientOption option=new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd0911");
		option.setScanSpan(5000);
		mLocClient.setLocOption(option);//设置定位参数
		mLocClient.start();//调用此方法开始定位	
		
		
		addOverlayBtn = (Button) findViewById(R.id.btn_add_overlay);  
	    locCurplaceBtn = (Button) findViewById(R.id.btn_cur_place);  
	    addOverlayBtn.setEnabled(false);  
		 //Button 添加覆盖物  
        addOverlayBtn.setOnClickListener(new OnClickListener() {      
            @Override      
            public void onClick(View v) {  
                addCircleOverlay();  
            }      
        });    
  
        //Button 定位当前位置  
        locCurplaceBtn.setOnClickListener(new OnClickListener() {      
            @Override      
            public void onClick(View v) {  
                addMyLocation();  
            }      
        });    
	}
	
	//定位SDK监听器
		public class MyLocationListener implements BDLocationListener{
			public void onReceivePoi(BDLocation location){}
			public void onReceiveLocation(BDLocation location){
				if(location==null||baiduMap==null){
					return;        //MapView销毁后不再处理新接收的位置  
				}
				
				//构造定位数据！！
				MyLocationData.Builder locationBuilder=new MyLocationData.Builder();
				locationBuilder.accuracy(location.getRadius());
				locationBuilder.direction(100);
				locationBuilder.latitude(location.getLatitude());
				locationBuilder.longitude(location.getLongitude());
				MyLocationData locationData=locationBuilder.build();
				//设置定位数据
				baiduMap.setMyLocationData(locationData);
				
				mCurrentMode=LocationMode.NORMAL;
				
				//获取经纬度  
	            latitude = location.getLatitude();  
	            longitude = location.getLongitude();  
				
	            if (isFirstLoc) {  
	                isFirstLoc = false;  
	                //地理坐标基本数据结构  
	                LatLng loc = new LatLng(location.getLatitude(),location.getLongitude());  
	                //MapStatusUpdate描述地图将要发生的变化  
	                //MapStatusUpdateFactory生成地图将要反生的变化  
	                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(loc);  
	                baiduMap.animateMapStatus(msu);  
	                Toast.makeText(getApplicationContext(), location.getAddrStr(),   
	                        Toast.LENGTH_SHORT).show();  
	            }  
			}
		}
		
		private void addMyLocation(){
			//用于设置定位的属性
      		MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);  
      		baiduMap.setMyLocationConfiguration(config);
      		baiduMap.clear();
      		addOverlayBtn.setEnabled(true); 
      		
      		//定位Marker坐标点
      		LatLng point=new LatLng(latitude,longitude);
      		//构建定位图标
      		BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.middle);
      		//构建下列使定位图标被添加
      		OverlayOptions option=new MarkerOptions().position(point).icon(mCurrentMarker);
      		baiduMap.addOverlay(option);
		}
		
		private void addCircleOverlay(){
			if(isShowOverlay==1){
				baiduMap.clear();
				isShowOverlay=0; //设置为不显示
				
				LatLng pt=new LatLng(latitude,longitude);
				CircleOptions circleOptions=new CircleOptions();
				circleOptions.center(pt);  //设置圆心图标
				circleOptions.fillColor(0xAAFFFF00);
				circleOptions.radius(250);
				circleOptions.stroke(new Stroke(5,0xAAFFFF00));   // 设置边框
				baiduMap.addOverlay(circleOptions);
			}else{
				baiduMap.clear();
				isShowOverlay=1;  //设置为显示
			}
      		
      		
      		
		}
	
	
		/* 以下代码用于给地图添加覆盖物
		MyLocationOverlay myLocationOverlay=new MyLocationOverlay(mapView);
		LocationData locationData=new LocationData();
		locationData.latitude=location.getLatitude();
		locationData.longitude=location.getLongitude();
		myLocationOverlay.setData(locationData);
		mapView.getOverlays().add(myLocationOverlay);
		mapView.refresh();//刷新使新增覆盖物生效
		
		PopupOverlay pop=new PopupOverlay(mapView,new PopupClickListener(){     
			public void onClickedPopup(int index){
				Toast.makeText(MainActivity.this, "你点击了按钮"+index, Toast.LENGTH_SHORT).show();
				}
             });
		
		Bitmap[] bitmaps=new Bitmap[3];
		try{
			bitmaps[0]=BitmapFactory.decodeResource(getResources(), R.drawable.left);
			bitmaps[1]=BitmapFactory.decodeResource(getResources(), R.drawable.middle);
			bitmaps[2]=BitmapFactory.decodeResource(getResources(), R.drawable.right);
		}catch(Exception e){
			e.printStackTrace();
		}
		pop.showPopup(bitmaps, point, 18);
		*/
			
	
	protected void onResume(){
		super.onResume();
		mapView.onResume();
	/*	if(manager!=null){
			manager.start();
		}
    */
	}
	
	protected void onPause(){
		super.onPause();
		mapView.onPause();
	/*	if(manager!=null){
			manager.stop();
		}
	*/	
	}
	
	protected void onDestroy(){
		
		mLocClient.stop();                       //退出时销毁定位 
		baiduMap.setMyLocationEnabled(false);//当不需要定位图层时关闭定位图层
		mapView.onDestroy();
	/*	if(manager!=null){
			manager.destroy();
			manager=null;
		}
	*/	
		mapView=null;
		super.onDestroy();
	}

	
	
	
}
