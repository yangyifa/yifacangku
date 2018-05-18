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
    public MyLocationListener locListener = new MyLocationListener();//��λSDK��������
    
    //��ǰλ�þ�γ��  
    private double latitude;  
    private double longitude; 
    
  //�Ƿ��״ζ�λ  
    private boolean isFirstLoc = true;  
    
  //��ť ��Ӹ�����  
    private Button addOverlayBtn;  
    //�Ƿ���ʾ������ 1-��ʾ 0-����ʾ  
    private int isShowOverlay = 1;  
    //��ť ��λ��ǰλ��  
    private Button locCurplaceBtn;  
    
    //��λͼ������  
    private BitmapDescriptor mCurrentMarker = null;
    //��λͼ����ʾģʽ (��ͨ-����-����)  
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
		baiduMap.setMyLocationEnabled(true);  //������λͼ��
		baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(16.0f);    //���õ�ͼ���ż���16 ������ͨ��ͼ  
        baiduMap.setMapStatus(msu);
        
		mLocClient = new LocationClient(getApplicationContext());
		mLocClient.registerLocationListener(locListener);
		LocationClientOption option=new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd0911");
		option.setScanSpan(5000);
		mLocClient.setLocOption(option);//���ö�λ����
		mLocClient.start();//���ô˷�����ʼ��λ	
		
		
		addOverlayBtn = (Button) findViewById(R.id.btn_add_overlay);  
	    locCurplaceBtn = (Button) findViewById(R.id.btn_cur_place);  
	    addOverlayBtn.setEnabled(false);  
		 //Button ��Ӹ�����  
        addOverlayBtn.setOnClickListener(new OnClickListener() {      
            @Override      
            public void onClick(View v) {  
                addCircleOverlay();  
            }      
        });    
  
        //Button ��λ��ǰλ��  
        locCurplaceBtn.setOnClickListener(new OnClickListener() {      
            @Override      
            public void onClick(View v) {  
                addMyLocation();  
            }      
        });    
	}
	
	//��λSDK������
		public class MyLocationListener implements BDLocationListener{
			public void onReceivePoi(BDLocation location){}
			public void onReceiveLocation(BDLocation location){
				if(location==null||baiduMap==null){
					return;        //MapView���ٺ��ٴ����½��յ�λ��  
				}
				
				//���춨λ���ݣ���
				MyLocationData.Builder locationBuilder=new MyLocationData.Builder();
				locationBuilder.accuracy(location.getRadius());
				locationBuilder.direction(100);
				locationBuilder.latitude(location.getLatitude());
				locationBuilder.longitude(location.getLongitude());
				MyLocationData locationData=locationBuilder.build();
				//���ö�λ����
				baiduMap.setMyLocationData(locationData);
				
				mCurrentMode=LocationMode.NORMAL;
				
				//��ȡ��γ��  
	            latitude = location.getLatitude();  
	            longitude = location.getLongitude();  
				
	            if (isFirstLoc) {  
	                isFirstLoc = false;  
	                //��������������ݽṹ  
	                LatLng loc = new LatLng(location.getLatitude(),location.getLongitude());  
	                //MapStatusUpdate������ͼ��Ҫ�����ı仯  
	                //MapStatusUpdateFactory���ɵ�ͼ��Ҫ�����ı仯  
	                MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(loc);  
	                baiduMap.animateMapStatus(msu);  
	                Toast.makeText(getApplicationContext(), location.getAddrStr(),   
	                        Toast.LENGTH_SHORT).show();  
	            }  
			}
		}
		
		private void addMyLocation(){
			//�������ö�λ������
      		MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);  
      		baiduMap.setMyLocationConfiguration(config);
      		baiduMap.clear();
      		addOverlayBtn.setEnabled(true); 
      		
      		//��λMarker�����
      		LatLng point=new LatLng(latitude,longitude);
      		//������λͼ��
      		BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.middle);
      		//��������ʹ��λͼ�걻���
      		OverlayOptions option=new MarkerOptions().position(point).icon(mCurrentMarker);
      		baiduMap.addOverlay(option);
		}
		
		private void addCircleOverlay(){
			if(isShowOverlay==1){
				baiduMap.clear();
				isShowOverlay=0; //����Ϊ����ʾ
				
				LatLng pt=new LatLng(latitude,longitude);
				CircleOptions circleOptions=new CircleOptions();
				circleOptions.center(pt);  //����Բ��ͼ��
				circleOptions.fillColor(0xAAFFFF00);
				circleOptions.radius(250);
				circleOptions.stroke(new Stroke(5,0xAAFFFF00));   // ���ñ߿�
				baiduMap.addOverlay(circleOptions);
			}else{
				baiduMap.clear();
				isShowOverlay=1;  //����Ϊ��ʾ
			}
      		
      		
      		
		}
	
	
		/* ���´������ڸ���ͼ��Ӹ�����
		MyLocationOverlay myLocationOverlay=new MyLocationOverlay(mapView);
		LocationData locationData=new LocationData();
		locationData.latitude=location.getLatitude();
		locationData.longitude=location.getLongitude();
		myLocationOverlay.setData(locationData);
		mapView.getOverlays().add(myLocationOverlay);
		mapView.refresh();//ˢ��ʹ������������Ч
		
		PopupOverlay pop=new PopupOverlay(mapView,new PopupClickListener(){     
			public void onClickedPopup(int index){
				Toast.makeText(MainActivity.this, "�����˰�ť"+index, Toast.LENGTH_SHORT).show();
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
		
		mLocClient.stop();                       //�˳�ʱ���ٶ�λ 
		baiduMap.setMyLocationEnabled(false);//������Ҫ��λͼ��ʱ�رն�λͼ��
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
